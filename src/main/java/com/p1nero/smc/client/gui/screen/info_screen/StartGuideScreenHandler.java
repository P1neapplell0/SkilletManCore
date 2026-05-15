package com.p1nero.smc.client.gui.screen.info_screen;

import com.p1nero.smc.client.gui.TreeNode;
import com.p1nero.smc.client.gui.screen.DialogueScreen;
import com.p1nero.smc.client.gui.screen.LinkListStreamDialogueScreenBuilder;
import com.p1nero.smc.client.sound.SMCSounds;
import com.p1nero.smc.datagen.lang.SMCLangGenerator;
import com.p1nero.smc.network.packet.serverbound.NpcPlayerInteractPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StartGuideScreenHandler {
    public static final String name = "start_guide";
    @OnlyIn(Dist.CLIENT)
    public static void addStartGuideScreen() {
        DialogueScreen.ScreenDialogueBuilder builder = new DialogueScreen.ScreenDialogueBuilder(name);

        LinkListStreamDialogueScreenBuilder screenBuilder = new LinkListStreamDialogueScreenBuilder(null, Component.literal("").append(builder.name().copy().withStyle(ChatFormatting.AQUA)).append(": \n"));

        TreeNode root = new TreeNode(builder.ans(0))
                .addExecutable(dialogueScreen -> dialogueScreen.playSound(SMCSounds.TALKING.get()))
                .addChild(new TreeNode(builder.ans(1), builder.opt(0))
                        .addExecutable(dialogueScreen -> dialogueScreen.playSound(SMCSounds.TALKING.get()))
                        .addChild(new TreeNode(builder.ans(2), builder.opt(2))
                                .addExecutable(dialogueScreen -> dialogueScreen.playSound(SMCSounds.TALKING.get()))
                                .addLeaf(builder.opt(4), (byte) NpcPlayerInteractPacket.DO_NOTHING))
                        .addChild(new TreeNode(builder.ans(2), builder.opt(3))
                                .addExecutable(dialogueScreen -> dialogueScreen.playSound(SMCSounds.TALKING.get()))
                                .addLeaf(builder.opt(4), (byte) NpcPlayerInteractPacket.DO_NOTHING))
                )
                .addChild(new TreeNode(builder.ans(3), builder.opt(1))
                        .addExecutable(dialogueScreen -> dialogueScreen.playSound(SMCSounds.TALKING.get()))
                        .addLeaf(builder.opt(5), (byte) NpcPlayerInteractPacket.DO_NOTHING)
                        .addChild(new TreeNode(builder.ans(4), builder.opt(6))
                                .addExecutable(dialogueScreen -> dialogueScreen.playSound(SMCSounds.TALKING.get()))
                                .addLeaf(builder.opt(7), (byte) NpcPlayerInteractPacket.DO_NOTHING)));

        screenBuilder.setAnswerRoot(root);
        Minecraft.getInstance().setScreen(screenBuilder.build());
    }

    public static void onGenerate(SMCLangGenerator generator) {
        generator.addScreenName(name, "背景故事");
        generator.addScreenAns(name, 0, "你是一个来自天外的旅人，受§c『终界』§r侵扰，你失去了力量。因此你暂居此处，白天做饭维生，夜里抵御来自§c『终界』§r的袭击，保卫村庄。你在做菜的时候，不断从翻炒中领悟武学之道，同时可能邂逅神秘村民，传授你§e『秘笈』§r或§e『神兵』§r。§c[语速过快或过慢可在config/skillet_man_core-client.toml调节]");
        generator.addScreenOpt(name, 0, "继续");
        generator.addScreenOpt(name, 1, "跳过");
        generator.addScreenAns(name, 1, "有朝一日，你觉得力量渐渐回来了，准备前往§c『终界』§r，击败恶龙，夺回属于你的力量，换得主世界永恒的安宁。最终将成为一代宗师，村民们因此称你为——§e平 底 锅 侠§r。");
        generator.addScreenOpt(name, 2, "平底锅侠...好俗的名字...");
        generator.addScreenOpt(name, 3, "这像是作者一拍大腿就想出来的整合包");
        generator.addScreenAns(name, 2, "没错。这个整合包就是作者一拍大腿想出来的。但这一拍大腿就换来了两三个月的爆肝...建议给作者三连。废话少说，快去村庄里寻找落脚点吧！一定要在下一个冬季到来之前击败恶龙！");
        generator.addScreenOpt(name, 4, "我去，还能吐槽");
        generator.addScreenAns(name, 3, "你确定要跳过这么精彩的对话吗？一旦跳过或按ECS退出后，将只有重建存档才会弹出本对话哦~");
        generator.addScreenOpt(name, 5, "确定");
        generator.addScreenOpt(name, 6, "取消");
        generator.addScreenAns(name, 4, "哈哈，还想再看一遍？想得美，自己在村子里逛逛吧。");
        generator.addScreenOpt(name, 7, "...");
    }

}
