package slimeknights.tconstruct.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraftforge.event.CreativeModeTabEvent;
import slimeknights.mantle.registration.object.WoodBlockObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.item.ArmorSlotType;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.DirtType;
import slimeknights.tconstruct.world.block.FoliageType;

import java.util.ArrayList;
import java.util.List;

public class CreativeModeTabs {
  public static void makeTabs(CreativeModeTabEvent.Register event) {
    /** Tab for all special tools added by the mod */
    event.registerCreativeModeTab(new ResourceLocation(TConstruct.MOD_ID, "gadgets"), builder ->
        // Set name of tab to display
        builder.title(Component.translatable("itemGroup." + TConstruct.MOD_ID + ".gadgets"))
          // Set icon of creative tab
          .icon(() -> new ItemStack(TinkerGadgets.itemFrame.get(FrameType.CLEAR)))
          // Add default items to tab
          .displayItems((enabledFlags, populator, hasPermissions) -> {
            populator.accept(TinkerGadgets.punji);
            populator.accept(TinkerGadgets.piggyBackpack);
            for (FrameType type : FrameType.values()) {
              populator.accept(TinkerGadgets.itemFrame.get(type));
            }
            populator.accept(TinkerGadgets.glowBall);
            populator.accept(TinkerGadgets.efln);
            populator.accept(TinkerGadgets.magmaCake);
            populator.accept(TinkerGadgets.quartzShuriken);
            populator.accept(TinkerGadgets.flintShuriken);
          })
    );

    /** Creative tab for items that do not fit in another tab */
    event.registerCreativeModeTab(new ResourceLocation(TConstruct.MOD_ID, "general"), builder ->
      // Set name of tab to display
      builder.title(Component.translatable("itemGroup." + TConstruct.MOD_ID + ".general"))
        // Set icon of creative tab
        .icon(() -> new ItemStack(TinkerCommons.slimeball.get(SlimeType.SKY)))
        // Add default items to tab
        .displayItems((enabledFlags, populator, hasPermissions) -> {
          // Commons
          populator.accept(TinkerCommons.glow.get());
          populator.accept(TinkerCommons.clearGlass.get());
          populator.accept(TinkerCommons.clearTintedGlass.get());
          populator.accept(TinkerCommons.clearGlassPane.get());
          populator.accept(TinkerCommons.soulGlass.get());
          populator.accept(TinkerCommons.soulGlassPane.get());
          populator.accept(TinkerCommons.goldBars.get());
          populator.accept(TinkerCommons.obsidianPane.get());
          populator.accept(TinkerCommons.goldPlatform.get());
          populator.accept(TinkerCommons.ironPlatform.get());
          populator.accept(TinkerCommons.cobaltPlatform.get());
          for (WeatheringCopper.WeatherState state : WeatheringCopper.WeatherState.values()) {
            populator.accept(TinkerCommons.copperPlatform.get(state));
            populator.accept(TinkerCommons.waxedCopperPlatform.get(state));
          }
          populator.accept(TinkerCommons.bacon);
          populator.accept(TinkerCommons.jeweledApple);
          populator.accept(TinkerCommons.cheeseIngot);
          populator.accept(TinkerCommons.cheeseBlock);
          populator.accept(TinkerCommons.materialsAndYou);
          populator.accept(TinkerCommons.punySmelting);
          populator.accept(TinkerCommons.mightySmelting);
          populator.accept(TinkerCommons.tinkersGadgetry);
          populator.accept(TinkerCommons.fantasticFoundry);
          populator.accept(TinkerCommons.encyclopedia);
          for (SlimeType type : SlimeType.values()) {
            populator.accept(TinkerCommons.slimeball.get(type));
          }

          // Fluids
          populator.accept(TinkerFluids.venomBottle);
          for (SlimeType type : SlimeType.values()) {
            populator.accept(TinkerFluids.slimeBottle.get(type));
          }
          populator.accept(TinkerFluids.magmaBottle);
          populator.accept(TinkerFluids.meatSoupBowl);
          populator.accept(TinkerFluids.splashBottle);
          populator.accept(TinkerFluids.lingeringBottle);

          // Modifiers
          populator.accept(TinkerModifiers.silkyCloth);
          populator.accept(TinkerModifiers.dragonScale);
          populator.accept(TinkerModifiers.emeraldReinforcement);
          populator.accept(TinkerModifiers.slimesteelReinforcement);
          populator.accept(TinkerModifiers.ironReinforcement);
          populator.accept(TinkerModifiers.searedReinforcement);
          populator.accept(TinkerModifiers.goldReinforcement);
          populator.accept(TinkerModifiers.cobaltReinforcement);
          populator.accept(TinkerModifiers.obsidianReinforcement);

          // Materials
          populator.accept(TinkerMaterials.cobalt);
          populator.accept(TinkerMaterials.slimesteel);
          populator.accept(TinkerMaterials.amethystBronze);
          populator.accept(TinkerMaterials.roseGold);
          populator.accept(TinkerMaterials.pigIron);
          populator.accept(TinkerMaterials.queensSlime);
          populator.accept(TinkerMaterials.manyullyn);
          populator.accept(TinkerMaterials.hepatizon);
          populator.accept(TinkerMaterials.copperNugget);
          populator.accept(TinkerMaterials.netheriteNugget);
          populator.accept(TinkerMaterials.debrisNugget);
          populator.accept(TinkerMaterials.necroticBone);
          populator.accept(TinkerMaterials.venombone);
          populator.accept(TinkerMaterials.blazingBone);
          populator.accept(TinkerMaterials.necroniumBone);
          populator.accept(TinkerMaterials.nahuatl);
          populator.accept(TinkerMaterials.blazewood);

          // Tables
          populator.accept(TinkerTables.craftingStation);
          populator.accept(TinkerTables.tinkerStation);
          populator.accept(TinkerTables.partBuilder);
          populator.accept(TinkerTables.tinkersChest);
          populator.accept(TinkerTables.partChest);
          populator.accept(TinkerTables.modifierWorktable);
          populator.accept(TinkerTables.tinkersAnvil);
          populator.accept(TinkerTables.scorchedAnvil);
          populator.accept(TinkerTables.castChest);
          populator.accept(TinkerTables.pattern);
        })
    );

    /** Tab for all blocks related to the smeltery */
    event.registerCreativeModeTab(new ResourceLocation(TConstruct.MOD_ID, "smeltery"), builder ->
      // Set name of tab to display
      builder.title(Component.translatable("itemGroup." + TConstruct.MOD_ID + ".smeltery"))
        // Set icon of creative tab
        .icon(() -> new ItemStack(TinkerSmeltery.smelteryController))
        // Add default items to tab
        .displayItems((enabledFlags, populator, hasPermissions) -> {
          populator.accept(TinkerSmeltery.grout);
          populator.accept(TinkerSmeltery.netherGrout);
          populator.accept(TinkerSmeltery.searedStone);
          populator.accept(TinkerSmeltery.searedCobble);
          populator.accept(TinkerSmeltery.searedPaver);
          populator.accept(TinkerSmeltery.searedBricks);
          populator.accept(TinkerSmeltery.searedCrackedBricks);
          populator.accept(TinkerSmeltery.searedFancyBricks);
          populator.accept(TinkerSmeltery.searedTriangleBricks);
          populator.accept(TinkerSmeltery.searedLadder);
          populator.accept(TinkerSmeltery.searedGlass);
          populator.accept(TinkerSmeltery.searedSoulGlass);
          populator.accept(TinkerSmeltery.searedTintedGlass);
          populator.accept(TinkerSmeltery.searedGlassPane);
          populator.accept(TinkerSmeltery.searedSoulGlassPane);
          populator.accept(TinkerSmeltery.searedDrain);
          populator.accept(TinkerSmeltery.searedDuct);
          populator.accept(TinkerSmeltery.searedChute);
          populator.accept(TinkerSmeltery.scorchedStone);
          populator.accept(TinkerSmeltery.polishedScorchedStone);
          populator.accept(TinkerSmeltery.scorchedBricks);
          populator.accept(TinkerSmeltery.scorchedRoad);
          populator.accept(TinkerSmeltery.chiseledScorchedBricks);
          populator.accept(TinkerSmeltery.scorchedLadder);
          populator.accept(TinkerSmeltery.scorchedGlass);
          populator.accept(TinkerSmeltery.scorchedSoulGlass);
          populator.accept(TinkerSmeltery.scorchedTintedGlass);
          populator.accept(TinkerSmeltery.scorchedGlassPane);
          populator.accept(TinkerSmeltery.scorchedSoulGlassPane);
          populator.accept(TinkerSmeltery.scorchedDrain);
          populator.accept(TinkerSmeltery.scorchedDuct);
          populator.accept(TinkerSmeltery.scorchedChute);
          for (SearedTankBlock.TankType type : SearedTankBlock.TankType.values()) {
            populator.accept(TinkerSmeltery.searedTank.get(type));
          }
          populator.accept(TinkerSmeltery.searedLantern);
          populator.accept(TinkerSmeltery.searedFaucet);
          populator.accept(TinkerSmeltery.searedChannel);
          populator.accept(TinkerSmeltery.searedBasin);
          populator.accept(TinkerSmeltery.searedTable);
          for (SearedTankBlock.TankType type : SearedTankBlock.TankType.values()) {
            populator.accept(TinkerSmeltery.scorchedTank.get(type));
          }
          populator.accept(TinkerSmeltery.scorchedLantern);
          populator.accept(TinkerSmeltery.scorchedFaucet);
          populator.accept(TinkerSmeltery.scorchedChannel);
          populator.accept(TinkerSmeltery.scorchedBasin);
          populator.accept(TinkerSmeltery.scorchedTable);
          populator.accept(TinkerSmeltery.smelteryController);
          populator.accept(TinkerSmeltery.foundryController);
          populator.accept(TinkerSmeltery.searedMelter);
          populator.accept(TinkerSmeltery.searedHeater);
          populator.accept(TinkerSmeltery.scorchedAlloyer);
          populator.accept(TinkerSmeltery.searedBrick);
          populator.accept(TinkerSmeltery.scorchedBrick);
          populator.accept(TinkerSmeltery.copperCan);
          populator.accept(TinkerSmeltery.blankSandCast);
          populator.accept(TinkerSmeltery.blankRedSandCast);
          populator.accept(TinkerSmeltery.ingotCast);
          populator.accept(TinkerSmeltery.nuggetCast);
          populator.accept(TinkerSmeltery.gemCast);
          populator.accept(TinkerSmeltery.rodCast);
          populator.accept(TinkerSmeltery.repairKitCast);
          populator.accept(TinkerSmeltery.plateCast);
          populator.accept(TinkerSmeltery.gearCast);
          populator.accept(TinkerSmeltery.coinCast);
          populator.accept(TinkerSmeltery.wireCast);
          populator.accept(TinkerSmeltery.pickHeadCast);
          populator.accept(TinkerSmeltery.smallAxeHeadCast);
          populator.accept(TinkerSmeltery.smallBladeCast);
          populator.accept(TinkerSmeltery.hammerHeadCast);
          populator.accept(TinkerSmeltery.broadBladeCast);
          populator.accept(TinkerSmeltery.broadAxeHeadCast);
          populator.accept(TinkerSmeltery.toolBindingCast);
          populator.accept(TinkerSmeltery.roundPlateCast);
          populator.accept(TinkerSmeltery.largePlateCast);
          populator.accept(TinkerSmeltery.toolHandleCast);
          populator.accept(TinkerSmeltery.toughHandleCast);
          populator.accept(TinkerSmeltery.bowLimbCast);
          populator.accept(TinkerSmeltery.bowGripCast);
          populator.accept(TinkerSmeltery.helmetPlatingCast);
          populator.accept(TinkerSmeltery.chestplatePlatingCast);
          populator.accept(TinkerSmeltery.leggingsPlatingCast);
          populator.accept(TinkerSmeltery.bootsPlatingCast);
          populator.accept(TinkerSmeltery.mailleCast);
          for (ArmorSlotType type : ArmorSlotType.values()) {
            populator.accept(TinkerSmeltery.dummyPlating.get(type));
          }
        })
    );

    /** Tab for all tool parts */
    event.registerCreativeModeTab(new ResourceLocation(TConstruct.MOD_ID, "tool_parts"), builder ->
      // Set name of tab to display
      builder.title(Component.translatable("itemGroup." + TConstruct.MOD_ID + ".tool_parts"))
        // Set icon of creative tab
//        .icon(() -> {
//          List<IMaterial> materials = new ArrayList<>(MaterialRegistry.getInstance().getVisibleMaterials());
//          return new TinkerToolParts.pickHead.get().withMaterial(materials.get(TConstruct.RANDOM.nextInt(materials.size())).getIdentifier();
//        }))
        .icon(() -> new ItemStack(TinkerToolParts.pickHead))
        // Add default items to tab
        .displayItems((enabledFlags, populator, hasPermissions) -> {
          populator.accept(TinkerToolParts.repairKit);
          populator.accept(TinkerToolParts.pickHead);
          populator.accept(TinkerToolParts.hammerHead);
          populator.accept(TinkerToolParts.smallAxeHead);
          populator.accept(TinkerToolParts.broadAxeHead);
          populator.accept(TinkerToolParts.smallBlade);
          populator.accept(TinkerToolParts.broadBlade);
          populator.accept(TinkerToolParts.roundPlate);
          populator.accept(TinkerToolParts.largePlate);
          populator.accept(TinkerToolParts.bowLimb);
          populator.accept(TinkerToolParts.bowGrip);
          populator.accept(TinkerToolParts.bowstring);
          populator.accept(TinkerToolParts.toolBinding);
          populator.accept(TinkerToolParts.toolHandle);
          populator.accept(TinkerToolParts.toughHandle);
          for (ArmorSlotType type : ArmorSlotType.values()) {
            populator.accept(TinkerToolParts.plating.get(type));
          }
          populator.accept(TinkerToolParts.maille);
          populator.accept(TinkerToolParts.shieldCore);
        })
    );

    /** Creative tab for all tool items */
    event.registerCreativeModeTab(new ResourceLocation(TConstruct.MOD_ID, "tools"), builder ->
      // Set name of tab to display
      builder.title(Component.translatable("itemGroup." + TConstruct.MOD_ID + ".tools"))
        // Set icon of creative tab
        .icon(() -> new ItemStack(TinkerGadgets.itemFrame.get(FrameType.CLEAR)))
        // Add default items to tab
        .displayItems((enabledFlags, populator, hasPermissions) -> {
          populator.accept(TinkerTools.pickaxe);
          populator.accept(TinkerTools.sledgeHammer);
          populator.accept(TinkerTools.veinHammer);
          populator.accept(TinkerTools.mattock);
          populator.accept(TinkerTools.pickadze);
          populator.accept(TinkerTools.excavator);
          populator.accept(TinkerTools.handAxe);
          populator.accept(TinkerTools.broadAxe);
          populator.accept(TinkerTools.kama);
          populator.accept(TinkerTools.scythe);
          populator.accept(TinkerTools.dagger);
          populator.accept(TinkerTools.sword);
          populator.accept(TinkerTools.cleaver);
          populator.accept(TinkerTools.crossbow);
          populator.accept(TinkerTools.longbow);
          populator.accept(TinkerTools.flintAndBrick);
          populator.accept(TinkerTools.skyStaff);
          populator.accept(TinkerTools.earthStaff);
          populator.accept(TinkerTools.ichorStaff);
          populator.accept(TinkerTools.enderStaff);
          for (ArmorSlotType type : ArmorSlotType.values()) {
            populator.accept(TinkerTools.travelersGear.get(type));
            populator.accept(TinkerTools.plateArmor.get(type));
            populator.accept(TinkerTools.slimesuit.get(type));
          }
          populator.accept(TinkerTools.travelersShield);
          populator.accept(TinkerTools.plateShield);
          populator.accept(TinkerTools.crystalshotItem);
          populator.accept(TinkerModifiers.modifierCrystal);
          populator.accept(TinkerModifiers.creativeSlotItem);
        })
    );

    /** Tab for anything generated in the world */
    event.registerCreativeModeTab(new ResourceLocation(TConstruct.MOD_ID, "world"), builder ->
      // Set name of tab to display
      builder.title(Component.translatable("itemGroup." + TConstruct.MOD_ID + ".world"))
        // Set icon of creative tab
        .icon(() -> new ItemStack(TinkerWorld.cobaltOre))
        // Add default items to tab
        .displayItems((enabledFlags, populator, hasPermissions) -> {
          populator.accept(TinkerWorld.cobaltOre);
          populator.accept(TinkerWorld.rawCobaltBlock);
          populator.accept(TinkerWorld.rawCobalt);
          for (SlimeType type : SlimeType.values()) {
            populator.accept(TinkerWorld.slime.get(type));
            populator.accept(TinkerWorld.congealedSlime.get(type));
          }

          for (DirtType type : DirtType.TINKER) {
            populator.accept(TinkerWorld.slimeDirt.get(type));
          }
          for (FoliageType foliage : FoliageType.values()) {
            populator.accept(TinkerWorld.vanillaSlimeGrass.get(foliage));
            populator.accept(TinkerWorld.earthSlimeGrass.get(foliage));
            populator.accept(TinkerWorld.skySlimeGrass.get(foliage));
            populator.accept(TinkerWorld.enderSlimeGrass.get(foliage));
            populator.accept(TinkerWorld.ichorSlimeGrass.get(foliage));
          }
          for (FoliageType foliage : FoliageType.values()) {
            populator.accept(TinkerWorld.slimeGrassSeeds.get(foliage));
          }
          for (WoodBlockObject wood : new WoodBlockObject[]{
            TinkerWorld.greenheart,
            TinkerWorld.skyroot,
            TinkerWorld.bloodshroom,
            TinkerWorld.enderbark
          }) {
            populator.accept(wood);
            populator.accept(wood.getLog());
            populator.accept(wood.getStrippedLog());
            populator.accept(wood.getWood());
            populator.accept(wood.getStrippedWood());
            populator.accept(wood.getSlab());
            populator.accept(wood.getStairs());
            populator.accept(wood.getFence());
            populator.accept(wood.getFenceGate());
            populator.accept(wood.getDoor());
            populator.accept(wood.getTrapdoor());
            populator.accept(wood.getPressurePlate());
            populator.accept(wood.getButton());
            populator.accept(wood.getSign());
            populator.accept(wood.getWallSign());
          }
          for (SlimeType type : SlimeType.values()) {
            populator.accept(TinkerWorld.slimyEnderbarkRoots.get(type));
          }
          for (FoliageType foliage : FoliageType.values()) {
            populator.accept(TinkerWorld.slimeFern.get(foliage));
            populator.accept(TinkerWorld.slimeTallGrass.get(foliage));
            populator.accept(TinkerWorld.pottedSlimeFern.get(foliage));
          }
          for (FoliageType foliage : FoliageType.values()) {
            populator.accept(TinkerWorld.slimeSapling.get(foliage));
            populator.accept(TinkerWorld.pottedSlimeSapling.get(foliage));
            populator.accept(TinkerWorld.slimeLeaves.get(foliage));
          }
          populator.accept(TinkerWorld.skySlimeVine);
          populator.accept(TinkerWorld.enderSlimeVine);
          populator.accept(TinkerWorld.earthGeode);
          populator.accept(TinkerWorld.skyGeode);
          populator.accept(TinkerWorld.ichorGeode);
          populator.accept(TinkerWorld.enderGeode);
          for (TinkerHeadType type : TinkerHeadType.values()) {
            populator.accept(TinkerWorld.headItems.get(type));
          }
        })
    );
  }
}
