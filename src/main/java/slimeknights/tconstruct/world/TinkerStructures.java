package slimeknights.tconstruct.world;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration.TreeConfigurationBuilder;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AttachedToLeavesDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.UpwardsBranchingTrunkPlacer;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.block.FoliageType;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock.VineStage;
import slimeknights.tconstruct.world.data.StructureRepalleter;
import slimeknights.tconstruct.world.data.WorldgenDatapackRegistryProvider;
import slimeknights.tconstruct.world.worldgen.islands.IslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.IslandStructure;
import slimeknights.tconstruct.world.worldgen.trees.ExtraRootVariantPlacer;
import slimeknights.tconstruct.world.worldgen.trees.LeaveVineDecorator;
import slimeknights.tconstruct.world.worldgen.trees.SupplierBlockStateProvider;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeFungusConfig;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeTreeConfig;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeFungusFeature;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeature;

import java.util.List;

import static slimeknights.tconstruct.TConstruct.getResource;

/**
 * Contains any logic relevant to structure generation, including trees and islands
 */
@SuppressWarnings("unused")
public final class TinkerStructures extends TinkerModule {
  static final Logger log = Util.getLogger("tinker_structures");
  private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, TConstruct.MOD_ID);
    private static final DeferredRegister<StructureType<?>> STRUCTURE_TYPE = DeferredRegister.create(Registries.STRUCTURE_TYPE, TConstruct.MOD_ID);
  private static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE = DeferredRegister.create(Registries.STRUCTURE_PIECE, TConstruct.MOD_ID);
  private static final DeferredRegister<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_STATE_PROVIDER_TYPES, TConstruct.MOD_ID);
  private static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS = DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, TConstruct.MOD_ID);
  private static final DeferredRegister<RootPlacerType<?>> ROOT_PLACERS = DeferredRegister.create(Registries.ROOT_PLACER_TYPE, TConstruct.MOD_ID);


  public TinkerStructures() {
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    FEATURES.register(bus);
    STRUCTURE_TYPE.register(bus);
    STRUCTURE_PIECE.register(bus);
    BLOCK_STATE_PROVIDER_TYPES.register(bus);
    TREE_DECORATORS.register(bus);
    ROOT_PLACERS.register(bus);
  }


  /*
   * Misc
   */
  public static final RegistryObject<BlockStateProviderType<SupplierBlockStateProvider>> supplierBlockstateProvider = BLOCK_STATE_PROVIDER_TYPES.register("supplier_state_provider", () -> new BlockStateProviderType<>(SupplierBlockStateProvider.CODEC));
  public static final RegistryObject<TreeDecoratorType<LeaveVineDecorator>> leaveVineDecorator = TREE_DECORATORS.register("leave_vines", () -> new TreeDecoratorType<>(LeaveVineDecorator.CODEC));
  public static final RegistryObject<RootPlacerType<ExtraRootVariantPlacer>> extraRootVariantPlacer = ROOT_PLACERS.register("extra_root_variants", () -> new RootPlacerType<>(ExtraRootVariantPlacer.CODEC));

  /*
   * Features
   */
  /** Overworld variant of slimy trees */
  public static final RegistryObject<SlimeTreeFeature> slimeTree = FEATURES.register("slime_tree", () -> new SlimeTreeFeature(SlimeTreeConfig.CODEC));
  /** Nether variant of slimy trees */
  public static final RegistryObject<SlimeFungusFeature> slimeFungus = FEATURES.register("slime_fungus", () -> new SlimeFungusFeature(SlimeFungusConfig.CODEC));

  /** Greenheart tree variant */
  public static final RegistryObject<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> earthSlimeTree = CONFIGURED_FEATURES.registerStatic(
    "earth_slime_tree", slimeTree,
    new SlimeTreeConfig.Builder()
      .planted()
      .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState())
      .leaves(() -> TinkerWorld.slimeLeaves.get(FoliageType.EARTH).defaultBlockState())
      .baseHeight(4).randomHeight(3)
      .build());
  /** Greenheart tree variant on islands */
  public static final RegistryObject<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> earthSlimeIslandTree = CONFIGURED_FEATURES.registerStatic(
    "earth_slime_island_tree", slimeTree,
    new SlimeTreeConfig.Builder()
      .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState())
      .leaves(() -> TinkerWorld.slimeLeaves.get(FoliageType.EARTH).defaultBlockState())
      .baseHeight(4).randomHeight(3)
      .build());

  /** Skyroot tree variant */
  public static final RegistryObject<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> skySlimeTree = CONFIGURED_FEATURES.registerStatic(
    "sky_slime_tree", slimeTree,
    new SlimeTreeConfig.Builder()
      .planted().canDoubleHeight()
      .trunk(() -> TinkerWorld.skyroot.getLog().defaultBlockState())
      .leaves(() -> TinkerWorld.slimeLeaves.get(FoliageType.SKY).defaultBlockState())
      .build());
  /** Skyroot tree variant on islands */
  public static final RegistryObject<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> skySlimeIslandTree = CONFIGURED_FEATURES.registerStatic(
    "sky_slime_island_tree", slimeTree,
    new SlimeTreeConfig.Builder()
      .canDoubleHeight()
      .trunk(() -> TinkerWorld.skyroot.getLog().defaultBlockState())
      .leaves(() -> TinkerWorld.slimeLeaves.get(FoliageType.SKY).defaultBlockState())
      .vines(() -> TinkerWorld.skySlimeVine.get().defaultBlockState().setValue(SlimeVineBlock.STAGE, VineStage.MIDDLE))
      .build());

  /** Enderslime short tree variant */
  public static final RegistryObject<ConfiguredFeature<TreeConfiguration,Feature<TreeConfiguration>>> enderSlimeTree = CONFIGURED_FEATURES.registerSupplier(
    "ender_slime_tree", () -> Feature.TREE,
    () -> new TreeConfigurationBuilder(BlockStateProvider.simple(TinkerWorld.enderbark.getLog()),
                                 new UpwardsBranchingTrunkPlacer(2, 1, 4, UniformInt.of(1, 4), 0.5F, UniformInt.of(0, 1), BuiltInRegistries.BLOCK.getOrCreateTag(TinkerTags.Blocks.ENDERBARK_LOGS_CAN_GROW_THROUGH)),
                                 BlockStateProvider.simple(TinkerWorld.slimeLeaves.get(FoliageType.ENDER)),
                                 new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 70),
                                 ExtraRootVariantPlacer.builder()
                                                       .trunkOffset(UniformInt.of(1, 3))
                                                       .rootBlock(TinkerWorld.enderbarkRoots.get())
                                                       .canGrowThroughTag(TinkerTags.Blocks.ENDERBARK_ROOTS_CAN_GROW_THROUGH)
                                                       .slimyRoots(TinkerWorld.slimyEnderbarkRoots)
                                                       .buildOptional(),
                                 new TwoLayersFeatureSize(2, 0, 2))
      .decorators(List.of(new LeaveVineDecorator(TinkerWorld.enderSlimeVine.get(), 0.125F), new AttachedToLeavesDecorator(0.14F, 1, 0, new RandomizedIntStateProvider(BlockStateProvider.simple(TinkerWorld.slimeSapling.get(FoliageType.ENDER).defaultBlockState().setValue(BlockStateProperties.HANGING, true)), MangrovePropaguleBlock.AGE, UniformInt.of(0, 4)), 2, List.of(Direction.DOWN))))
      .ignoreVines()
      .build());
  /** Enderslime tall tree variant */
  public static final RegistryObject<ConfiguredFeature<TreeConfiguration,Feature<TreeConfiguration>>> enderSlimeTreeTall = CONFIGURED_FEATURES.registerSupplier(
    "ender_slime_tree_tall", () -> Feature.TREE,
    () -> new TreeConfigurationBuilder(BlockStateProvider.simple(TinkerWorld.enderbark.getLog()),
                                       new UpwardsBranchingTrunkPlacer(4, 1, 9, UniformInt.of(1, 6), 0.5F, UniformInt.of(0, 1), BuiltInRegistries.BLOCK.getOrCreateTag(TinkerTags.Blocks.ENDERBARK_LOGS_CAN_GROW_THROUGH)),
                                       BlockStateProvider.simple(TinkerWorld.slimeLeaves.get(FoliageType.ENDER)),
                                       new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 70),
                                       ExtraRootVariantPlacer.builder()
                                                             .trunkOffset(UniformInt.of(3, 7))
                                                             .rootBlock(TinkerWorld.enderbarkRoots.get())
                                                             .canGrowThroughTag(TinkerTags.Blocks.ENDERBARK_ROOTS_CAN_GROW_THROUGH)
                                                             .slimyRoots(TinkerWorld.slimyEnderbarkRoots)
                                                             .buildOptional(),
                                       new TwoLayersFeatureSize(3, 0, 2))
      .decorators(List.of(new LeaveVineDecorator(TinkerWorld.enderSlimeVine.get(), 0.125F), new AttachedToLeavesDecorator(0.14F, 1, 0, new RandomizedIntStateProvider(BlockStateProvider.simple(TinkerWorld.slimeSapling.get(FoliageType.ENDER).defaultBlockState().setValue(BlockStateProperties.HANGING, true)), MangrovePropaguleBlock.AGE, UniformInt.of(0, 4)), 2, List.of(Direction.DOWN))))
      .ignoreVines()
      .build());
  /** Bloodshroom tree variant */
  public static final RegistryObject<ConfiguredFeature<HugeFungusConfiguration,SlimeFungusFeature>> bloodSlimeFungus = CONFIGURED_FEATURES.registerSupplier(
    "blood_slime_fungus", slimeFungus,
    () -> new SlimeFungusConfig(
      TinkerTags.Blocks.SLIMY_SOIL,
      TinkerWorld.bloodshroom.getLog().defaultBlockState(),
      TinkerWorld.slimeLeaves.get(FoliageType.BLOOD).defaultBlockState(),
      TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
      true));
  /** Bloodshroom island tree variant */
  public static final RegistryObject<ConfiguredFeature<HugeFungusConfiguration,SlimeFungusFeature>> bloodSlimeIslandFungus = CONFIGURED_FEATURES.registerSupplier(
    "blood_slime_island_fungus", slimeFungus,
    () -> new SlimeFungusConfig(
      TinkerTags.Blocks.SLIMY_NYLIUM,
      TinkerWorld.bloodshroom.getLog().defaultBlockState(),
      TinkerWorld.slimeLeaves.get(FoliageType.BLOOD).defaultBlockState(),
      TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
      false));
  /* Deprecated ichor tree */
  public static final RegistryObject<ConfiguredFeature<HugeFungusConfiguration,SlimeFungusFeature>> ichorSlimeFungus = CONFIGURED_FEATURES.registerSupplier(
    "ichor_slime_fungus", slimeFungus,
    () -> new SlimeFungusConfig(
      TinkerTags.Blocks.SLIMY_SOIL,
      TinkerWorld.bloodshroom.getLog().defaultBlockState(),
      TinkerWorld.slimeLeaves.get(FoliageType.ICHOR).defaultBlockState(),
      TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
      false));

  /*
   * Structures
   */
  public static final RegistryObject<StructurePieceType> islandPiece = STRUCTURE_PIECE.register("island", () -> IslandPiece::new);
  public static final RegistryObject<StructureType<IslandStructure>> island = STRUCTURE_TYPE.register("island", () -> () -> IslandStructure.CODEC);
  // island keys, they are registered in JSON
  public static final ResourceKey<Structure> earthSlimeIsland = ResourceKey.create(Registries.STRUCTURE, getResource("earth_slime_island"));
  public static final ResourceKey<Structure> skySlimeIsland = ResourceKey.create(Registries.STRUCTURE, getResource("sky_slime_island"));
  public static final ResourceKey<Structure> clayIsland = ResourceKey.create(Registries.STRUCTURE, getResource("clay_island"));
  public static final ResourceKey<Structure> bloodIsland = ResourceKey.create(Registries.STRUCTURE, getResource("blood_island"));
  public static final ResourceKey<Structure> endSlimeIsland = ResourceKey.create(Registries.STRUCTURE, getResource("end_slime_island"));

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator datagenerator = event.getGenerator();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    boolean server = event.includeServer();
    datagenerator.addProvider(server, new StructureRepalleter(datagenerator, existingFileHelper));
    datagenerator.addProvider(server, new WorldgenDatapackRegistryProvider(datagenerator, existingFileHelper));
    //    datagenerator.addProvider(server, new StructureUpdater(datagenerator, existingFileHelper, TConstruct.MOD_ID, PackType.SERVER_DATA, "structures"));
    //    datagenerator.addProvider(event.includeClient(), new StructureUpdater(datagenerator, existingFileHelper, TConstruct.MOD_ID, PackType.CLIENT_RESOURCES, "book/structures"));
  }
}
