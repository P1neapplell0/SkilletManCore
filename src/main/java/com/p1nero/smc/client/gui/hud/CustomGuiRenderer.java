package com.p1nero.smc.client.gui.hud;

import com.mojang.blaze3d.platform.Window;
import com.p1nero.smc.SMCConfig;
import com.p1nero.smc.SkilletManCoreMod;
import com.p1nero.smc.archive.DataManager;
import com.p1nero.smc.capability.SMCCapabilityProvider;
import com.p1nero.smc.capability.SMCPlayer;
import com.p1nero.smc.client.gui.screen.DialogueScreen;
import com.p1nero.smc.client.keymapping.KeyMappings;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import dev.xkmc.cuisinedelight.init.CuisineDelight;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class CustomGuiRenderer {
    public static final ResourceLocation SPATULA_TEXTURE = ResourceLocation.fromNamespaceAndPath(CuisineDelight.MODID, "textures/item/spatula.png");
    public static final ResourceLocation SPATULA_TEXTURE2 = ResourceLocation.fromNamespaceAndPath(SkilletManCoreMod.MOD_ID, "textures/item/golden_spatula.png");
    public static final ResourceLocation SPATULA_TEXTURE3 = ResourceLocation.fromNamespaceAndPath(SkilletManCoreMod.MOD_ID, "textures/item/diamond_spatula.png");
    public static final ResourceLocation MONEY_TEXTURE = ResourceLocation.parse("textures/item/emerald.png");
    public static final ResourceLocation TERM_ICON = SolarTerm.getFontIcon().withPrefix("textures/").withSuffix(".png");

    private static final int HUD_TEXT_OFFSET = 4;
    private static final int HUD_ICON_OFFSET = 22;
    private static final int TUTORIAL_X = 6;
    private static final float HINT_FADE_STEP = 0.12F;

    private static boolean initialized;
    private static List<TutorialCondition> tutorialConditions = List.of();
    private static Component noTaskComponent = Component.empty();

    private static Component cachedMoneyCount = Component.empty();
    private static Component cachedTerm = Component.empty();
    private static Component cachedLevel = Component.empty();
    private static Component cachedWorkingState = Component.empty();
    private static Component cachedFormattedTime = Component.empty();
    private static Component cachedShowHintText = Component.empty();
    private static List<FormattedCharSequence> cachedTutorialLines = List.of();
    private static ResourceLocation cachedSpatulaTexture = SPATULA_TEXTURE;
    private static int cachedStageColor = 0xFFFFFF;
    private static int cachedTermX;
    private static int cachedTermY;
    private static int cachedScreenWidth;
    private static int cachedLeftInfoY;
    private static int cachedRightInfoY;
    private static int cachedLineHeight;
    private static int cachedTutorialLineHeight;
    private static int cachedInterval;
    private static int cachedRightTextX;
    private static int cachedRightIconX;
    private static int cachedHeaderWidth;
    private static int cachedHeaderHeight;
    private static int cachedTutorialPanelWidth;
    private static int cachedTutorialPanelHeight;
    private static int cachedTutorialAccentColor = 0xF2C14E;
    private static float hintVisibilityProgress = 1.0F;

    public static void init() {
        if (initialized) {
            return;
        }
        tutorialConditions = List.of(
                new TutorialCondition(
                        player -> !DataManager.firstGiftGot.get(player),
                        mergeComponents(
                                SkilletManCoreMod.getInfo("find_villager_first").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD),
                                SkilletManCoreMod.getInfo("find_villager_first2").withStyle(ChatFormatting.GRAY),
                                SkilletManCoreMod.getInfo("find_villager_first3").withStyle(ChatFormatting.GRAY),
                                SkilletManCoreMod.getInfo("find_villager_first4").withStyle(ChatFormatting.GRAY)
                        ),
                        0xF2C14E
                ),
                new TutorialCondition(
                        player -> !DataManager.firstWork.get(player),
                        mergeComponents(
                                SkilletManCoreMod.getInfo("first_work").withStyle(ChatFormatting.BOLD, ChatFormatting.GREEN),
                                SkilletManCoreMod.getInfo("first_work2").withStyle(ChatFormatting.GRAY)
                        ),
                        0x6BE675
                ),
                new TutorialCondition(
                        player -> !DataManager.firstStopWork.get(player),
                        mergeComponents(
                                SkilletManCoreMod.getInfo("first_stop_work").withStyle(ChatFormatting.BOLD, ChatFormatting.RED)
                                        .append(SkilletManCoreMod.getInfo("first_stop_work2").withStyle(ChatFormatting.GRAY))
                        ),
                        0xFF7676
                ),
                new TutorialCondition(
                        player -> !DataManager.firstGachaGot.get(player),
                        mergeComponents(
                                SkilletManCoreMod.getInfo("find_villager_gacha").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA),
                                SkilletManCoreMod.getInfo("find_villager_gacha2").withStyle(ChatFormatting.GRAY),
                                SkilletManCoreMod.getInfo("find_villager_gacha3").withStyle(ChatFormatting.GRAY)
                        ),
                        0x6FD7FF
                ),
                new TutorialCondition(
                        player -> DataManager.trailRequired.get(player),
                        mergeComponents(
                                SkilletManCoreMod.getInfo("trial_required").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD),
                                SkilletManCoreMod.getInfo("trial_required2").withStyle(ChatFormatting.GRAY),
                                SkilletManCoreMod.getInfo("trial_required3").withStyle(ChatFormatting.GRAY)
                        ),
                        0xF7D56B
                ),
                new TutorialCondition(
                        player -> DataManager.showFirstPlaceWirelessTerminal.get(player),
                        mergeComponents(
                                SkilletManCoreMod.getInfo("first_place_wireless_terminal").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_GREEN),
                                SkilletManCoreMod.getInfo("first_place_wireless_terminal1").withStyle(ChatFormatting.GRAY),
                                SkilletManCoreMod.getInfo("first_place_wireless_terminal2").withStyle(ChatFormatting.GRAY),
                                SkilletManCoreMod.getInfo("first_place_wireless_terminal3").withStyle(ChatFormatting.GRAY),
                                SkilletManCoreMod.getInfo("first_place_wireless_terminal4").withStyle(ChatFormatting.GRAY)
                        ),
                        0x4FB783
                ),
                new TutorialCondition(
                        player -> DataManager.shouldShowMachineTicketHint.get(player),
                        mergeComponents(
                                SkilletManCoreMod.getInfo("should_trade_machine_ticket").withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW),
                                SkilletManCoreMod.getInfo("should_trade_machine_ticket2").withStyle(ChatFormatting.GRAY)
                        ),
                        0xFFD86B
                ),
                new TutorialCondition(
                        player -> DataManager.findBBQHint.get(player),
                        mergeComponents(
                                SkilletManCoreMod.getInfo("find_bbq").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD),
                                SkilletManCoreMod.getInfo("find_bbq1").withStyle(ChatFormatting.GRAY)
                        ),
                        0xF29D52
                )
        );
        noTaskComponent = mergeComponents(
                SkilletManCoreMod.getInfo("no_task").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_GRAY),
                SkilletManCoreMod.getInfo("no_task1").withStyle(ChatFormatting.GRAY),
                SkilletManCoreMod.getInfo("no_task2").withStyle(ChatFormatting.GRAY)
        );
        initialized = true;
    }

    public static void tick() {
        init();
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        if (localPlayer == null) {
            return;
        }

        Font font = minecraft.font;
        Window window = minecraft.getWindow();
        SMCPlayer smcPlayer = SMCCapabilityProvider.getSMCPlayer(localPlayer);

        cachedLineHeight = font.lineHeight + 6;
        cachedTutorialLineHeight = font.lineHeight + 4;
        cachedScreenWidth = window.getGuiScaledWidth();
        cachedLeftInfoY = (int) (SMCConfig.INFO_Y_L.get() * cachedScreenWidth) + (int) (cachedLineHeight * 1.5F);
        cachedRightInfoY = (int) (SMCConfig.INFO_Y_R.get() * cachedScreenWidth) + (int) (cachedLineHeight * 1.5F);
        cachedInterval = SMCConfig.INTERVAL.get();

        cachedMoneyCount = Component.literal(": " + smcPlayer.getMoneyCount());
        cachedLevel = Component.literal(": " + smcPlayer.getLevel());
        cachedRightTextX = cachedScreenWidth - font.width(cachedMoneyCount) - HUD_TEXT_OFFSET;
        cachedRightIconX = cachedRightTextX - HUD_ICON_OFFSET;

        SolarTerm solarTerm = EclipticUtil.getNowSolarTerm(localPlayer.clientLevel);
        cachedTerm = Component.literal("[")
                .append(solarTerm.getSeason().getTranslation())
                .append("] ")
                .append(solarTerm.getTranslation())
                .withStyle(ChatFormatting.BOLD, solarTerm.getSeason().getColor());
        cachedTermX = solarTerm.getIconPosition().getKey() * 30;
        cachedTermY = solarTerm.getIconPosition().getValue() * 30;

        cachedStageColor = switch (smcPlayer.getStage()) {
            case 1 -> 0x84FBFF;
            case 2 -> 0x40FF5F;
            case 3 -> 0xFB4EE9;
            default -> 0xFFFFFF;
        };
        cachedSpatulaTexture = switch (smcPlayer.getStage()) {
            case 0 -> SPATULA_TEXTURE;
            case 1 -> SPATULA_TEXTURE2;
            default -> SPATULA_TEXTURE3;
        };

        boolean working = smcPlayer.isWorking();
        cachedWorkingState = (working ? SkilletManCoreMod.getInfo("working") : SkilletManCoreMod.getInfo("resting"))
                .withStyle(ChatFormatting.BOLD, working ? ChatFormatting.GREEN : ChatFormatting.GOLD);
        cachedFormattedTime = Component.literal(convertMinecraftTime(localPlayer.level().getDayTime()))
                .withStyle(working ? ChatFormatting.GREEN : ChatFormatting.GOLD);

        updateTutorialCache(localPlayer, font);

        float targetVisibility = SMCConfig.SHOW_HINT.get() ? 1.0F : 0.0F;
        if (hintVisibilityProgress < targetVisibility) {
            hintVisibilityProgress = Math.min(targetVisibility, hintVisibilityProgress + HINT_FADE_STEP);
        } else if (hintVisibilityProgress > targetVisibility) {
            hintVisibilityProgress = Math.max(targetVisibility, hintVisibilityProgress - HINT_FADE_STEP);
        }
    }

    public static boolean shouldRender() {
        if (Minecraft.getInstance().screen instanceof DialogueScreen) {
            return true;
        }
        return Minecraft.getInstance().screen == null;
    }

    public static void renderCustomGui(GuiGraphics guiGraphics) {
        if (!shouldRender()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        if (localPlayer == null) {
            return;
        }
        if (!initialized || cachedScreenWidth == 0) {
            tick();
        }
        Font font = minecraft.font;

        //日历
        guiGraphics.blit(TERM_ICON, 2, 2, 16, 16, cachedTermX, cachedTermY, 30, 30, 180, 120);
        guiGraphics.drawString(font, cachedTerm, 24, 7, cachedStageColor, true);

        //任务提示
        if (minecraft.screen == null) {
            renderTutorial(guiGraphics, font);
        }

        //声望等级
        guiGraphics.blit(cachedSpatulaTexture, cachedRightIconX, cachedRightInfoY - 65, 20, 20, 0.0F, 0.0F, 1, 1, 1, 1);
        guiGraphics.drawString(font, cachedLevel, cachedRightTextX, cachedRightInfoY - 60, cachedStageColor, true);
        //金币
        guiGraphics.blit(MONEY_TEXTURE, cachedRightIconX, cachedRightInfoY - 40, 20, 20, 0.0F, 0.0F, 1, 1, 1, 1);
        guiGraphics.drawString(font, cachedMoneyCount, cachedRightTextX, cachedRightInfoY - 35, 0xFFFFFF, true);
        //工作状态
        guiGraphics.drawString(font, cachedWorkingState, cachedRightIconX, cachedRightInfoY + font.lineHeight + cachedInterval - 40, 0x00FF00, true);
        guiGraphics.drawString(font, cachedFormattedTime, cachedRightIconX, cachedRightInfoY + font.lineHeight * 2 + cachedInterval - 40, 0x00FF00, true);
    }

    public static String convertMinecraftTime(long time) {
        // 调整时间到一天范围内 [0, 23999]
        long adjustedTime = time % 24000;
        if (adjustedTime < 0) {
            adjustedTime += 24000; // 处理负值
        }

        // 计算总小时数（包括小数），加上6小时的偏移（因为0刻对应6:00）
        double totalHours = (double) adjustedTime / 1000.0 + 6.0;
        totalHours %= 24; // 确保在24小时内

        // 提取小时和分钟
        int hours = (int) totalHours;
        int minutes = (int) Math.round((totalHours - hours) * 60);

        // 处理分钟进位（例如 23.999小时 -> 24:00 应转为 00:00）
        if (minutes >= 60) {
            hours = (hours + 1) % 24;
            minutes = 0;
        }

        // 格式化为两位数
        return String.format("%02d:%02d", hours, minutes);
    }

    public static void renderTutorial(GuiGraphics guiGraphics, Font font) {
        int headerY = cachedLeftInfoY + cachedLineHeight - 4;
        int alpha = Math.max(0, Math.min(255, (int) (hintVisibilityProgress * 255.0F)));
        drawHintPanel(guiGraphics, TUTORIAL_X - 4, headerY - 4, cachedHeaderWidth, cachedHeaderHeight, 0xD8FFFFFF, 0xC814171C, withAlpha(alpha, cachedTutorialAccentColor));
        guiGraphics.drawString(font, cachedShowHintText, TUTORIAL_X + 4, headerY + 2, 0xF4F6F8, false);

        if (hintVisibilityProgress <= 0.0F || cachedTutorialLines.isEmpty()) {
            return;
        }

        int panelY = headerY + cachedHeaderHeight + 4;
        drawHintPanel(guiGraphics, TUTORIAL_X - 4, panelY, cachedTutorialPanelWidth, cachedTutorialPanelHeight,
                withAlpha((int) (alpha * 0.92F), 0xFFFFFF),
                withAlpha((int) (alpha * 0.82F), 0x14171C),
                withAlpha(alpha, cachedTutorialAccentColor));

        int textX = TUTORIAL_X + 8;
        int textY = panelY + 8;
        int textColor = withAlpha(alpha, 0xF5F7FA);
        for (FormattedCharSequence line : cachedTutorialLines) {
            guiGraphics.drawString(font, line, textX, textY, textColor, false);
            textY += cachedTutorialLineHeight;
        }
    }

    private static void updateTutorialCache(LocalPlayer localPlayer, Font font) {
        TutorialCondition activeCondition = null;
        for (TutorialCondition condition : tutorialConditions) {
            if (condition.condition.test(localPlayer)) {
                activeCondition = condition;
                break;
            }
        }

        boolean hasTodo = activeCondition != null;
        MutableComponent show;
        if (SMCConfig.SHOW_HINT.get()) {
            show = SkilletManCoreMod.getInfo("press_x_to_show_hint",
                    KeyMappings.SHOW_HINT.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_GREEN))
                    .copy()
                    .withStyle(ChatFormatting.BOLD);
        } else {
            show = Component.literal("[")
                    .withStyle(ChatFormatting.BOLD)
                    .append(KeyMappings.SHOW_HINT.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_GREEN))
                    .append(Component.literal("] ").withStyle(ChatFormatting.BOLD));
            if (hasTodo) {
                show.append(SkilletManCoreMod.getInfo("task_todo_tip"));
            }
        }

        if (DataManager.hintUpdated.get(localPlayer)) {
            show.append(SkilletManCoreMod.getInfo("hint_update_tip"));
            if ((localPlayer.tickCount / 10) % 2 == 0) {
                show.append(Component.literal("⭐").withStyle(ChatFormatting.GOLD));
            }
        } else {
            show.append(Component.literal("⭐").withStyle(ChatFormatting.GOLD));
        }

        cachedShowHintText = show;
        cachedHeaderWidth = font.width(cachedShowHintText) + 12;
        cachedHeaderHeight = font.lineHeight + 12;

        Component tutorialText = activeCondition != null ? activeCondition.component : noTaskComponent;
        cachedTutorialAccentColor = activeCondition != null ? activeCondition.accentColor : 0x8F98A4;
        int wrapWidth = Mth.clamp(cachedScreenWidth / 3, 160, 300);
        cachedTutorialLines = font.split(tutorialText, wrapWidth);
        int maxLineWidth = 0;
        for (FormattedCharSequence line : cachedTutorialLines) {
            maxLineWidth = Math.max(maxLineWidth, font.width(line));
        }
        cachedTutorialPanelWidth = maxLineWidth + 18;
        cachedTutorialPanelHeight = cachedTutorialLines.size() * cachedTutorialLineHeight + 16;
    }

    private static MutableComponent mergeComponents(Component... components) {
        MutableComponent merged = Component.empty();
        for (int i = 0; i < components.length; i++) {
            if (i == 1) {
                merged.append(Component.literal("\n"));
            }
            if(i > 0) {
                if(i > 1) {
                    merged.append(" ");
                }
                merged.append(i + ".");
            }
            merged.append(components[i].copy());
        }
        return merged;
    }

    private static void drawHintPanel(GuiGraphics guiGraphics, int x, int y, int width, int height, int borderColor, int backgroundColor, int accentColor) {
        int x2 = x + width;
        int y2 = y + height;
        guiGraphics.fill(x + 1, y + 1, x2 - 1, y2 - 1, backgroundColor);
        guiGraphics.fillGradient(x + 1, y + 1, x2 - 1, y2 - 1,
                withAlpha(Math.min(255, alphaOf(backgroundColor) + 18), 0x232830),
                backgroundColor);
        guiGraphics.fill(x, y, x2, y + 1, borderColor);
        guiGraphics.fill(x, y2 - 1, x2, y2, withAlpha(Math.max(0, alphaOf(borderColor) - 36), 0xFFFFFF));
        guiGraphics.fill(x, y, x + 1, y2, borderColor);
        guiGraphics.fill(x2 - 1, y, x2, y2, withAlpha(Math.max(0, alphaOf(borderColor) - 36), 0xFFFFFF));
        guiGraphics.fill(x + 2, y + 2, x + 4, y2 - 2, accentColor);
    }

    private static int withAlpha(int alpha, int rgb) {
        return (Mth.clamp(alpha, 0, 255) << 24) | (rgb & 0xFFFFFF);
    }

    private static int alphaOf(int color) {
        return color >>> 24;
    }

    private record TutorialCondition(Predicate<LocalPlayer> condition, Component component, int accentColor) {
    }

}
