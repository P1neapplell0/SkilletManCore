package com.p1nero.smc.event;

import com.merlin204.supergolem.client.SGPatchedRenderer;
import com.p1nero.smc.SkilletManCoreMod;
import com.p1nero.smc.block.SMCBlockEntities;
import com.p1nero.smc.block.renderer.BetterStructureBlockRenderer;
import com.p1nero.smc.client.gui.hud.CustomGuiRenderer;
import com.p1nero.smc.entity.SMCEntities;
import com.p1nero.smc.entity.custom.boss.goldenflame.client.BlackHoleRenderer;
import com.p1nero.smc.entity.custom.boss.goldenflame.client.FlameCircleRenderer;
import com.p1nero.smc.entity.custom.boss.goldenflame.client.GoldenFlamePatchedRenderer;
import com.p1nero.smc.entity.custom.boss.goldenflame.client.GoldenFlameRenderer;
import com.p1nero.smc.entity.custom.npc.customer.client.CustomerRenderer;
import com.p1nero.smc.entity.custom.npc.me.P1neroRenderer;
import com.p1nero.smc.entity.custom.npc.special.client.SpecialNpcRenderer;
import com.p1nero.smc.entity.custom.npc.special.virgil.client.VirgilVillagerRenderer;
import com.p1nero.smc.entity.custom.npc.special.zombie_man.client.ZombieManRenderer;
import com.p1nero.smc.entity.custom.npc.start_npc.client.StartNpcPlusRenderer;
import com.p1nero.smc.entity.custom.super_golem.client.SuperGolemRenderer;
import com.p1nero.smc.item.SMCItems;
import com.p1nero.smc.item.custom.client.LeftSkilletRightSpatulaRenderer;
import com.p1nero.smc.item.custom.client.PotatoCannonRenderer;
import com.p1nero.smc.item.model.SMCSkilletBEWLR;
import com.simibubi.create.AllItems;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;
import yesman.epicfight.api.forgeevent.ModelBuildEvent;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;
import yesman.epicfight.client.renderer.patched.entity.PVindicatorRenderer;

@SuppressWarnings("unchecked")
@Mod.EventBusSubscriber(modid = SkilletManCoreMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    public static final ModelResourceLocation GOLDEN_SKILLET_MODEL = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(SkilletManCoreMod.MOD_ID, "golden_cuisine_skillet_base"), "inventory");
    public static final ModelResourceLocation DIAMOND_SKILLET_MODEL = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(SkilletManCoreMod.MOD_ID, "diamond_cuisine_skillet_base"), "inventory");

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            //BOSS

            EntityRenderers.register(SMCEntities.GOLDEN_FLAME.get(), GoldenFlameRenderer::new);
            EntityRenderers.register(SMCEntities.BLACK_HOLE.get(), BlackHoleRenderer::new);
            EntityRenderers.register(SMCEntities.FLAME_CIRCLE.get(), FlameCircleRenderer::new);

            EntityRenderers.register(SMCEntities.SUPER_BAD_GOLEM.get(), SuperGolemRenderer::new);
            EntityRenderers.register(SMCEntities.SUPER_GOOD_GOLEM.get(), SuperGolemRenderer::new);

            //NPC
            EntityRenderers.register(SMCEntities.VILLAGER_NO_BRAIN.get(), VillagerRenderer::new);
            EntityRenderers.register(SMCEntities.START_NPC.get(), VillagerRenderer::new);
            EntityRenderers.register(SMCEntities.START_NPC_PLUS.get(), StartNpcPlusRenderer::new);
            EntityRenderers.register(SMCEntities.START_NPC_BBQ.get(), StartNpcPlusRenderer::new);
            EntityRenderers.register(SMCEntities.ZOMBIE_MAN.get(), ZombieManRenderer::new);
            EntityRenderers.register(SMCEntities.CUSTOMER.get(), CustomerRenderer::new);
            EntityRenderers.register(SMCEntities.FAKE_CUSTOMER.get(), CustomerRenderer::new);
            EntityRenderers.register(SMCEntities.HE_SHEN.get(), SpecialNpcRenderer::new);
            EntityRenderers.register(SMCEntities.TWO_KID.get(), SpecialNpcRenderer::new);
            EntityRenderers.register(SMCEntities.THIEF1.get(), SpecialNpcRenderer::new);
            EntityRenderers.register(SMCEntities.THIEF2.get(), SpecialNpcRenderer::new);
            EntityRenderers.register(SMCEntities.VIRGIL_VILLAGER.get(), VirgilVillagerRenderer::new);

            EntityRenderers.register(SMCEntities.P1NERO.get(), P1neroRenderer::new);

            //MISC
            EntityRenderers.register(SMCEntities.CUSTOM_COLOR_ITEM.get(), ItemEntityRenderer::new);
            CustomGuiRenderer.init();
        });

    }

    @SubscribeEvent
    public static void onRendererSetup(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(SMCBlockEntities.BETTER_STRUCTURE_BLOCK_ENTITY.get(), BetterStructureBlockRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
    }

    @SubscribeEvent
    public static void onResourceReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(SMCSkilletBEWLR.INSTANCE.get());
    }

    @SubscribeEvent
    public static void onModelRegister(ModelEvent.RegisterAdditional event) {
        event.register(GOLDEN_SKILLET_MODEL);
        event.register(DIAMOND_SKILLET_MODEL);
    }

    /**
     * 需要先绑定Patch和 Armature
     * {@link SMCEntities#setArmature(ModelBuildEvent.ArmatureBuild)}
     * {@link SMCEntities#setPatch(EntityPatchRegistryEvent)}
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderPatched(PatchedRenderersEvent.Add event) {
        EntityRendererProvider.Context context = event.getContext();
        event.addPatchedEntityRenderer(SMCEntities.GOLDEN_FLAME.get(), (entityType) -> new GoldenFlamePatchedRenderer(() -> Meshes.SKELETON, context, entityType).initLayerLast(context, entityType));
        event.addPatchedEntityRenderer(SMCEntities.SUPER_BAD_GOLEM.get(), (entityType) -> new SGPatchedRenderer(context, entityType).initLayerLast(context, entityType));
        event.addPatchedEntityRenderer(SMCEntities.SUPER_GOOD_GOLEM.get(), (entityType) -> new SGPatchedRenderer(context, entityType).initLayerLast(context, entityType));
        event.addPatchedEntityRenderer(SMCEntities.VIRGIL_VILLAGER.get(), (entityType) -> new PVindicatorRenderer(context, entityType).initLayerLast(context, entityType));
        event.addPatchedEntityRenderer(SMCEntities.P1NERO.get(), (entityType) -> new PHumanoidRenderer<>(() -> Meshes.ALEX, context, entityType).initLayerLast(context, entityType));
        event.addPatchedEntityRenderer(SMCEntities.ZOMBIE_MAN.get(), (entityType) -> new PHumanoidRenderer<>(() -> Meshes.BIPED_OLD_TEX, context, entityType).initLayerLast(context, entityType));

        //Item
        event.addItemRenderer(SMCItems.LEFT_SKILLET_RIGHT_SPATULA.get(), new LeftSkilletRightSpatulaRenderer());
        event.addItemRenderer(AllItems.POTATO_CANNON.asItem(), new PotatoCannonRenderer());

    }

}