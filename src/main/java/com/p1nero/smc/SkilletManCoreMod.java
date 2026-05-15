package com.p1nero.smc;

import com.mojang.logging.LogUtils;
import com.p1nero.smc.block.SMCBlockEntities;
import com.p1nero.smc.block.SMCBlocks;
import com.p1nero.smc.client.sound.SMCSounds;
import com.p1nero.smc.effect.SMCEffects;
import com.p1nero.smc.entity.SMCEntities;
import com.p1nero.smc.entity.SMCVillagers;
import com.p1nero.smc.condition.SMCConditions;
import com.p1nero.smc.gameasset.SMCLivingMotions;
import com.p1nero.smc.item.SMCItemTabs;
import com.p1nero.smc.item.SMCItems;
import com.p1nero.smc.network.SMCPacketHandler;
import com.p1nero.smc.registrate.SMCRegistrateBlocks;
import com.p1nero.smc.registrate.SMCRegistrateItems;
import com.p1nero.smc.util.gacha.ArmorGachaSystem;
import com.p1nero.smc.util.gacha.SkillBookGachaSystem;
import com.p1nero.smc.util.gacha.WeaponGachaSystem;
import dev.xkmc.l2library.base.L2Registrate;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import yesman.epicfight.api.animation.LivingMotion;

import java.util.Locale;

@Mod(SkilletManCoreMod.MOD_ID)
public class SkilletManCoreMod {
    public static final String MUL = "mul";
    public static final String GUO_CHAO = "guo_chao";
    public static final String POISONED_SKILLET = "poisoned_skillet";
    public static final String WEAPON_LEVEL_KEY = "smc_weapon_level";
    public static final String MOD_ID = "skillet_man_core";
    public static final L2Registrate REGISTRATE = new L2Registrate(MOD_ID);
    public static final String REGISTRY_NAMESPACE = "smc";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SkilletManCoreMod(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();

        SMCSounds.REGISTRY.register(bus);
        SMCItems.REGISTRY.register(bus);
        SMCBlocks.REGISTRY.register(bus);
        SMCBlockEntities.REGISTRY.register(bus);
        SMCEntities.REGISTRY.register(bus);
        SMCItemTabs.REGISTRY.register(bus);
        SMCEffects.REGISTRY.register(bus);
//        SMCStructurePlacementTypes.STRUCTURE_PLACEMENT_TYPES.register(bus);
        SMCVillagers.register(bus);
        bus.addListener(this::commonSetup);
        bus.addListener(this::registerExtraStuff);
        bus.addListener(this::addPackFindersEvent);
        LivingMotion.ENUM_MANAGER.registerEnumCls(MOD_ID, SMCLivingMotions.class);
        SMCConditions.CONDITIONS.register(bus);
        MinecraftForge.EVENT_BUS.register(this);

        SMCRegistrateItems.register();
        SMCRegistrateBlocks.register();

        context.registerConfig(ModConfig.Type.COMMON, SMCConfig.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, SMCConfig.CLIENT_SPEC);
    }

    public static MutableComponent getInfo(String key) {
        return Component.translatable("info.smc." + key);
    }

    public static String getInfoKey(String key) {
        return "info.smc." + key;
    }

    public static MutableComponent getInfo(String key, Object... objects) {
        return Component.translatable("info.smc." + key, objects);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        SMCPacketHandler.register();
        WeaponGachaSystem.initItemList();
        SkillBookGachaSystem.initItemList();
        ArmorGachaSystem.initItemList();
    }

    private void addPackFindersEvent(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            String name = "i18n";
            var resourcePath = ModList.get().getModFileById(MOD_ID).getFile().findResource("packs/" + name);
            var pack = Pack.readMetaAndCreate(name, SkilletManCoreMod.getInfo("i18n_pack"), true,
                    (path) -> new PathPackResources(path, resourcePath, false), PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN);
            event.addRepositorySource((packConsumer) -> packConsumer.accept(pack));
        }
    }

    public void registerExtraStuff(RegisterEvent evt) {
//        if (evt.getRegistryKey().equals(Registries.BIOME_SOURCE)) {
//            Registry.register(BuiltInRegistries.BIOME_SOURCE, SkilletManCoreMod.prefix("smc_biomes"), SMCBiomeProvider.SMC_BIOME_CODEC);
//
//        }else if (evt.getRegistryKey().equals(Registries.CHUNK_GENERATOR)) {
//            Registry.register(BuiltInRegistries.CHUNK_GENERATOR, SkilletManCoreMod.prefix("structure_locating_wrapper"), SMCChunkGenerator.CODEC);
//        }
    }

    public static ResourceLocation prefix(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name.toLowerCase(Locale.ROOT));
    }

    public static ResourceLocation namedRegistry(String name) {
        return ResourceLocation.fromNamespaceAndPath(REGISTRY_NAMESPACE, name.toLowerCase(Locale.ROOT));
    }

}
