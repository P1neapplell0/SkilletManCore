package com.p1nero.smc.client.gui.screen;

import com.p1nero.smc.SMCConfig;
import com.p1nero.smc.client.gui.screen.component.DialogueAnswerComponent;
import com.p1nero.smc.client.gui.screen.component.DialogueChoiceComponent;
import com.p1nero.smc.entity.api.NpcDialogue;
import com.p1nero.smc.entity.custom.npc.SMCNpc;
import com.p1nero.smc.mixin.MobInvoker;
import com.p1nero.smc.network.SMCPacketHandler;
import com.p1nero.smc.network.PacketRelay;
import com.p1nero.smc.network.packet.serverbound.AddDialogPacket;
import com.p1nero.smc.network.packet.serverbound.NpcBlockPlayerInteractPacket;
import com.p1nero.smc.network.packet.serverbound.NpcPlayerInteractPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 改编自theAether 的 ValkyrieQueenDialogueScreen
 * 搬运了相关类
 */
public class DialogueScreen extends Screen {
    protected ResourceLocation PICTURE_LOCATION = null;
    private int picHeight = 144, picWidth = 256;
    private int picShowHeight = 144, picShowWidth = 256;
    private int yOffset = 0;
    private boolean isSilent;
    protected final DialogueAnswerComponent dialogueAnswer;
    @Nullable
    protected Entity entity;

    @Nullable
    protected BlockPos pos;
    public final int typewriterInterval;
    private int typewriterTimer = 0;
    @Nullable
    EntityType<?> entityType;

    public DialogueScreen(@NotNull Entity entity) {
        super(entity.getDisplayName());
        typewriterInterval = SMCConfig.TYPEWRITER_EFFECT_INTERVAL.get();
        this.dialogueAnswer = new DialogueAnswerComponent(this.buildDialogueAnswerName(entity.getDisplayName().copy().withStyle(ChatFormatting.YELLOW)).append(": "));
        this.entity = entity;
        this.entityType = entity.getType();
    }

    public DialogueScreen(Component name, @Nullable Entity entity) {
        super(name);
        typewriterInterval = SMCConfig.TYPEWRITER_EFFECT_INTERVAL.get();
        this.dialogueAnswer = new DialogueAnswerComponent(name);
        this.entity = entity;
        if(entity != null) {
            this.entityType = entity.getType();
        }
    }

    public DialogueScreen(BlockEntity blockEntity) {
        super(blockEntity.getBlockState().getBlock().getName());
        typewriterInterval = SMCConfig.TYPEWRITER_EFFECT_INTERVAL.get();
        this.dialogueAnswer = new DialogueAnswerComponent(blockEntity.getBlockState().getBlock().getName());
        this.pos = blockEntity.getBlockPos();
    }

    /**
     * 在这里实现对话逻辑调用
     */
    @Override
    protected void init() {
        positionDialogue();//不填的话用builder创造出来的对话框第一个对话会错误显示
    }

    public void setPicture(ResourceLocation resourceLocation) {
        this.PICTURE_LOCATION = resourceLocation;
        if(Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            Minecraft.getInstance().level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }
    }

    public void setSilent(boolean silent) {
        isSilent = silent;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public void setPicHeight(int picHeight) {
        this.picHeight = picHeight;
    }

    public void setPicWidth(int picWidth) {
        this.picWidth = picWidth;
    }

    public void setPicShowHeight(int picShowHeight) {
        this.picShowHeight = picShowHeight;
    }

    public void setPicShowWidth(int picShowWidth) {
        this.picShowWidth = picShowWidth;
    }

    public void setupDialogueChoices(List<DialogueChoiceComponent> options) {
        this.clearWidgets();
        for (DialogueChoiceComponent option : options) {
            this.addRenderableWidget(option);
        }
        this.positionDialogue();
    }

    /**
     * Repositions the dialogue answer and the player's dialogue choices based on the amount of choices.
     */
    protected void positionDialogue() {
        // Dialogue answer.
        this.dialogueAnswer.reposition(this.width, this.height * 5 / 4, yOffset);//相较于天堂的下移了一点，因为是中文
        // Dialogue choices.
        int lineNumber = this.dialogueAnswer.height / 12 + 1;
        Iterator<Renderable> iterator = this.renderables.iterator();
        while (iterator.hasNext()) {
            Renderable renderable = iterator.next();
            if (renderable instanceof DialogueChoiceComponent option) {
                option.setX(this.width / 2 - option.getWidth() / 2);
                int y = this.height / 2 * 5 / 4 + 12 * lineNumber + yOffset;
                option.setY(y);
                lineNumber++;
                int h = option.getHeight() + 2;
                if (!iterator.hasNext() && y + h > this.height && typewriterTimer < 0) {
                    yOffset -= h;
                    this.dialogueAnswer.reposition(this.width, this.height * 5 / 4, yOffset);
                    y = this.height / 2 * 5 / 4 + 12 * lineNumber + yOffset;
                    option.setY(y);//调低一点
                }
            }
        }
    }

    /**
     * 顺便发包同步记录，以及全服广播对话
     * Sets what message to display for a dialogue answer.
     * @param component The message {@link Component}.
     */
    protected void setDialogueAnswer(Component component) {
        PacketRelay.sendToServer(SMCPacketHandler.INSTANCE, new AddDialogPacket(entity == null ? title : entity.getDisplayName(), component, SMCConfig.BROADCAST_DIALOG.get()));
        if(SMCConfig.ENABLE_TYPEWRITER_EFFECT.get()){
            this.dialogueAnswer.updateTypewriterDialogue(component);
        }else {
            this.dialogueAnswer.updateDialogue(component);
        }

    }

    /**
     * Sets up the formatting for the Valkyrie Queen's key in the {@link DialogueAnswerComponent} widget.
     * @param component The key {@link Component}.
     * @return The formatted {@link MutableComponent}.
     */
    public MutableComponent buildDialogueAnswerName(Component component) {
        return Component.literal("[").append(component.copy().withStyle(ChatFormatting.YELLOW)).append("]");
    }

    /**
     * Sends an NPC interaction to the server, which is sent through a packet to be handled in {@link NpcDialogue#handleNpcInteraction(ServerPlayer, byte)}.
     * @param interactionID A code for which interaction was performed on the client.<br>
     *                      0 - "What can you tell me about this place?"<br>
     *                      1 - "I wish to fight you!"<br>
     *                      2 - "On second thought, I'd rather not."<br>
     *                      3 - "Nevermind."<br>
     * @see NpcPlayerInteractPacket
     */
    protected void finishChat(byte interactionID) {
        if(pos != null) {
            PacketRelay.sendToServer(SMCPacketHandler.INSTANCE, new NpcBlockPlayerInteractPacket(pos, interactionID));
        }
        PacketRelay.sendToServer(SMCPacketHandler.INSTANCE, new NpcPlayerInteractPacket(this.entity == null ? NpcPlayerInteractPacket.NO_ENTITY :this.entity.getId(), interactionID));
        PICTURE_LOCATION = null;
        yOffset = 0;
        picHeight = 144;
        picWidth = 256;
        picShowHeight = 256;
        picShowWidth = 144;
        super.onClose();
    }

    /**
     * 默认对话翻页的时候播放声音
     */
    public void playSound() {
        if(this.isSilent || this.entity == null) {
            return;
        }
        if(this.entity instanceof Mob mob && ((MobInvoker)mob).smc$invokeGetAmbientSound() != null) {
            if(this.entity instanceof SMCNpc smcNpc) {
                smcNpc.setTalkingAnimTimer(30);
            }
            mob.level().playLocalSound(mob.getX(), mob.getY(), mob.getZ(),  ((MobInvoker)mob).smc$invokeGetAmbientSound(), mob.getSoundSource(), 1.0F, 1.0F, false);
        }
    }

    public void playSound(SoundEvent soundEvent) {
        LocalPlayer player = Minecraft.getInstance().player;
        if(Minecraft.getInstance().level != null && player != null) {
            Minecraft.getInstance().level.playLocalSound(player.getX(), player.getY(), player.getZ(),  soundEvent, player.getSoundSource(), 1.0F, 1.0F, false);
        }
    }

    /**
     * 发包但不关闭窗口
    * */
    protected void execute(byte interactionID) {
        PacketRelay.sendToServer(SMCPacketHandler.INSTANCE, new NpcPlayerInteractPacket(this.entity == null ? NpcPlayerInteractPacket.NO_ENTITY :this.entity.getId(), interactionID));
    }

    /**
     * 单机后可提前显示
     */
    @Override
    public boolean mouseClicked(double v, double v1, int i) {
        if(this.dialogueAnswer.index < dialogueAnswer.max - 3) {
            this.dialogueAnswer.index = dialogueAnswer.max - 3;
        }
        return super.mouseClicked(v, v1, i);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        this.renderPicture(guiGraphics);
        if(SMCConfig.ENABLE_TYPEWRITER_EFFECT.get() && typewriterTimer < 0) {
            this.dialogueAnswer.updateTypewriterDialogue();
            positionDialogue();
            typewriterTimer = typewriterInterval;
        } else {
            typewriterTimer--;
        }

        this.dialogueAnswer.render(guiGraphics);

        //如果回答还没显示完则不渲染选项
        for(Renderable renderable : this.renderables) {
            if(renderable instanceof DialogueChoiceComponent && !dialogueAnswer.shouldRenderOption()){
                continue;
            }
            renderable.render(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    private void renderPicture(GuiGraphics guiGraphics) {
        if(PICTURE_LOCATION != null){
            guiGraphics.blit(PICTURE_LOCATION, this.width/2 - picShowWidth/2, (int) (this.height/2 - picShowHeight / 1.3F), picShowWidth, picShowHeight, 0, 0, picWidth, picHeight, picWidth, picHeight);
        }
    }

    /**
     * [CODE COPY] - {@link Screen#renderBackground(GuiGraphics)}.<br><br>
     * Remove code for dark gradient and dirt background.
     */
    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics) {
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        this.width = width;
        this.height = height;
        this.positionDialogue();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        this.finishChat((byte) 0);
    }

    public static Component screen(String name, Object ...objects) {
        return Component.translatable("screen.smc." + name, objects);
    }

    public static Component screenAns(String name, int id, Object ...objects) {
        return Component.translatable("screen.smc.ans." + name + "_" + id, objects);
    }
    public static Component screenOpt(String name, int id, Object ...objects) {
        return Component.translatable("screen.smc.opt." + name + "_" + id, objects);
    }

    public static class ScreenDialogueBuilder{
        private final String name;
        private final Set<ChatFormatting> defaultAnsFormats = new HashSet<>();
        private final Set<ChatFormatting> defaultOptFormats = new HashSet<>();
        public ScreenDialogueBuilder(String name){
            this.name = name;
        }

        public void setDefaultAnsFormat(ChatFormatting... formatting) {
            defaultAnsFormats.addAll(List.of(formatting));
        }
        public void setDefaultOptFormat(ChatFormatting... formatting) {
            defaultOptFormats.addAll(List.of(formatting));
        }

        public Component ans(int id, Object ...objects) {
            Component ans = DialogueScreen.screenAns(name, id, objects);
            if(!defaultAnsFormats.isEmpty()) {
                return ans.copy().withStyle(defaultAnsFormats.toArray(new ChatFormatting[]{}));
            }
            return ans;
        }

        public Component opt(int id, Object ...objects) {
            Component opt = DialogueScreen.screenOpt(name, id, objects);
            if(!defaultOptFormats.isEmpty()){
                return opt.copy().withStyle(defaultOptFormats.toArray(new ChatFormatting[]{}));
            }
            return opt;
        }

        public Component name(Object ...objects){
            return DialogueScreen.screen(name, objects);
        }

    }

}