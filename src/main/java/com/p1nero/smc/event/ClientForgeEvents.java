package com.p1nero.smc.event;

import com.p1nero.smc.SkilletManCoreMod;
import com.p1nero.smc.client.gui.BossBarHandler;
import com.p1nero.smc.client.gui.hud.CustomGuiRenderer;
import com.p1nero.smc.client.gui.hud.HealthBarRenderer;
import com.p1nero.smc.client.gui.hud.RenderSpatulaBar;
import com.p1nero.smc.client.gui.screen.DialogueScreen;
import com.simibubi.create.AllItems;
import dev.xkmc.cuisinedelight.content.client.CookingOverlay;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.forgeevent.UpdatePlayerMotionEvent;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = SkilletManCoreMod.MOD_ID, value = Dist.CLIENT)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onRenderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        if(event.getBossEvent().getName().contains(Component.translatable("entity.minecraft.ender_dragon"))){
            event.setCanceled(true);
        }
        if(BossBarHandler.renderBossBar(event.getGuiGraphics(), event.getBossEvent(), event.getX(), event.getY())){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        if(Minecraft.getInstance().screen instanceof DialogueScreen) {
            event.setCanceled(true);
            return;
        }
        if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type()) {
            event.setCanceled(true);
            HealthBarRenderer.renderHealthBar(event.getGuiGraphics(), event.getWindow(), event.getPartialTick());
        }
        if (event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type()) {
            event.setCanceled(true);
        }

        if(event.getOverlay().overlay() instanceof CookingOverlay) {
            RenderSpatulaBar.render(event.getGuiGraphics(), event.getWindow(), event.getPartialTick());
        }
    }

    /**
     * 画任务的UI
     */
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent event) {
        GuiGraphics guiGraphics = event.getGuiGraphics();
        CustomGuiRenderer.renderCustomGui(guiGraphics);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        CustomGuiRenderer.tick();
    }

    public static final List<String> TIPS = new ArrayList<>();
    static {
        TIPS.add("袭击会随天数和等级的增加而变难，尽早升级并挑战最终boss吧！");
        TIPS.add("大部分的动物都可以对话哦");
        TIPS.add("多次和无业游民对话可使其转化成理想职业");
        TIPS.add("不要吝啬于升级你的武器，多抽取武器盔甲和技能书以获取更好的体验！");
        TIPS.add("炒混合菜品时，可先查看烹饪时间区间，计算好下锅顺序再开始！");
        TIPS.add("顾客村民初始一分钟生成一次，随店铺等级升高，客流量会增加");
        TIPS.add("默认 Z + 鼠标和滚轮可调节视角 —— Leawind Third Person");
        TIPS.add("任何时候开始学机械动力都不晚！");
        TIPS.add("我知道你根本就不会看作者在左上角给的提示，对吧~");
        TIPS.add("[提示框广告招租，五元一条不限时！]");
        TIPS.add("玩家被最终boss击败时，最终boss将恢复最大生命值1/10的血量。");
    }
    /**
     * 画提示
     */
    @SubscribeEvent
    public static void onRenderScreen(ScreenEvent.Render.Post event){
        Screen screen = event.getScreen();
        if(screen instanceof GenericDirtMessageScreen || screen instanceof LevelLoadingScreen || screen instanceof PauseScreen){
            GuiGraphics guiGraphics = event.getGuiGraphics();
            int tipIndex = (int) (System.currentTimeMillis() / 5000) % TIPS.size();
            guiGraphics.drawString(screen.getMinecraft().font, Component.translatable("screen_tips.smc.tip" + tipIndex).withStyle(ChatFormatting.GOLD), 10, 10, 16777215, true);
        }
    }

}