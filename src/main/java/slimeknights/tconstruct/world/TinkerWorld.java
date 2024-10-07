package slimeknights.tconstruct.world;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.crafting.FireworkStarRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent.Operation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.registration.object.WoodBlockObject;
import slimeknights.mantle.registration.object.WoodBlockObject.WoodVariant;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.GeodeItemObject;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.block.CongealedSlimeBlock;
import slimeknights.tconstruct.world.block.DirtType;
import slimeknights.tconstruct.world.block.FoliageType;
import slimeknights.tconstruct.world.block.PiglinHeadBlock;
import slimeknights.tconstruct.world.block.PiglinWallHeadBlock;
import slimeknights.tconstruct.world.block.SlimeDirtBlock;
import slimeknights.tconstruct.world.block.SlimeFungusBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.block.SlimeLeavesBlock;
import slimeknights.tconstruct.world.block.SlimeNyliumBlock;
import slimeknights.tconstruct.world.block.SlimePropaguleBlock;
import slimeknights.tconstruct.world.block.SlimePropaguleLeavesBlock;
import slimeknights.tconstruct.world.block.SlimeRootsBlock;
import slimeknights.tconstruct.world.block.SlimeSaplingBlock;
import slimeknights.tconstruct.world.block.SlimeTallGrassBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.block.SlimeWartBlock;
import slimeknights.tconstruct.world.block.StickySlimeBlock;
import slimeknights.tconstruct.world.data.WorldRecipeProvider;
import slimeknights.tconstruct.world.entity.EnderSlimeEntity;
import slimeknights.tconstruct.world.entity.SkySlimeEntity;
import slimeknights.tconstruct.world.entity.SlimePlacementPredicate;
import slimeknights.tconstruct.world.entity.TerracubeEntity;
import slimeknights.tconstruct.world.item.SlimeGrassSeedItem;
import slimeknights.tconstruct.world.worldgen.trees.SlimeTree;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Contains blocks and items relevant to structures and world gen
 */
@SuppressWarnings("unused")
public final class TinkerWorld extends TinkerModule {
	static final Logger log = Util.getLogger("tinker_world");

  public static final PlantType SLIME_PLANT_TYPE = PlantType.get("slime");

  /*
   * Block base properties
   */
  private static final Item.Properties HEAD_PROPS = new Item.Properties().rarity(Rarity.UNCOMMON);

  /*
   * Blocks
   */
  // ores
  public static final ItemObject<Block> cobaltOre = BLOCKS.register("cobalt_ore", () -> new Block(builder(Material.STONE, MaterialColor.NETHER, SoundType.NETHER_ORE).requiresCorrectToolForDrops().strength(10.0F)), (b) -> new BlockItem(b, new Item.Properties()));
  public static final ItemObject<Block> rawCobaltBlock = BLOCKS.register("raw_cobalt_block", () -> new Block(builder(Material.STONE, MaterialColor.COLOR_BLUE, SoundType.NETHER_ORE).requiresCorrectToolForDrops().strength(6.0f, 7.0f)), (b) -> new BlockItem(b, new Item.Properties()));
  public static final ItemObject<Item> rawCobalt = ITEMS.register("raw_cobalt", new Item.Properties());

  // slime
  public static final EnumObject<SlimeType, SlimeBlock> slime = Util.make(() -> {
    Function<SlimeType,BlockBehaviour.Properties> slimeProps = type -> builder(Material.CLAY, type.getMapColor(), SoundType.SLIME_BLOCK).friction(0.8F).sound(SoundType.SLIME_BLOCK).noOcclusion();
    return new EnumObject.Builder<SlimeType, SlimeBlock>(SlimeType.class)
      .put(SlimeType.EARTH, () -> (SlimeBlock)Blocks.SLIME_BLOCK)
      // sky slime: sticks to anything, but will not pull back
      .put(SlimeType.SKY,   BLOCKS.register("sky_slime", () -> new StickySlimeBlock(slimeProps.apply(SlimeType.SKY), (state, other) -> true), (b) -> new BlockTooltipItem(b, new Item.Properties())))
      // ichor: does not stick to self, but sticks to anything else
      .put(SlimeType.ICHOR, BLOCKS.register("ichor_slime", () -> new StickySlimeBlock(slimeProps.apply(SlimeType.ICHOR).lightLevel(s -> SlimeType.ICHOR.getLightLevel()),
                                                                                      (state, other) -> other.getBlock() != state.getBlock()), (b) -> new BlockTooltipItem(b, new Item.Properties())))
      // ender: only sticks to self
      .put(SlimeType.ENDER, BLOCKS.register("ender_slime", () -> new StickySlimeBlock(slimeProps.apply(SlimeType.ENDER), (state, other) -> other.getBlock() == state.getBlock()), (b) -> new BlockTooltipItem(b, new Item.Properties())))
      // blood slime: not sticky, and honey won't stick to it, good for bounce pads
      .build();
  });
  public static final EnumObject<SlimeType, CongealedSlimeBlock> congealedSlime = BLOCKS.registerEnum(SlimeType.values(), "congealed_slime", type -> new CongealedSlimeBlock(builder(Material.CLAY, type.getMapColor(), SoundType.SLIME_BLOCK).strength(0.5F).friction(0.5F).lightLevel(s -> type.getLightLevel())), (b) -> new BlockTooltipItem(b, new Item.Properties()));

  // island blocks
  public static final EnumObject<DirtType, Block> slimeDirt = BLOCKS.registerEnum(DirtType.TINKER, "slime_dirt", (type) -> new SlimeDirtBlock(builder(Material.DIRT, type.getMapColor(), SoundType.SLIME_BLOCK).strength(1.9f)), (b) -> new BlockTooltipItem(b, new Item.Properties()));
  public static final EnumObject<DirtType, Block> allDirt = new EnumObject.Builder<DirtType, Block>(DirtType.class).put(DirtType.VANILLA, () -> Blocks.DIRT).putAll(slimeDirt).build();

  /** Grass variants, the name represents the dirt type */
  public static final EnumObject<FoliageType, Block> vanillaSlimeGrass, earthSlimeGrass, skySlimeGrass, enderSlimeGrass, ichorSlimeGrass;
  /** Map of dirt type to slime grass type. Each slime grass is a map from foliage to grass type */
  public static final Map<DirtType, EnumObject<FoliageType, Block>> slimeGrass = new EnumMap<>(DirtType.class);

	static {
    Function<FoliageType,BlockBehaviour.Properties> slimeGrassProps = type -> builder(Material.GRASS, type.getMapColor(), SoundType.SLIME_BLOCK).strength(2.0f).requiresCorrectToolForDrops().randomTicks();
    Function<FoliageType, Block> slimeGrassRegister = type -> type.isNether() ? new SlimeNyliumBlock(slimeGrassProps.apply(type), type) : new SlimeGrassBlock(slimeGrassProps.apply(type), type);
    // blood is not an exact match for vanilla, but close enough
    FoliageType[] values = FoliageType.values();
    vanillaSlimeGrass = BLOCKS.registerEnum(values, "vanilla_slime_grass", slimeGrassRegister, (b) -> new BlockTooltipItem(b, new Item.Properties()));
    earthSlimeGrass   = BLOCKS.registerEnum(values, "earth_slime_grass",   slimeGrassRegister, (b) -> new BlockTooltipItem(b, new Item.Properties()));
    skySlimeGrass     = BLOCKS.registerEnum(values, "sky_slime_grass",     slimeGrassRegister, (b) -> new BlockTooltipItem(b, new Item.Properties()));
    enderSlimeGrass   = BLOCKS.registerEnum(values, "ender_slime_grass",   slimeGrassRegister, (b) -> new BlockTooltipItem(b, new Item.Properties()));
    ichorSlimeGrass   = BLOCKS.registerEnum(values, "ichor_slime_grass",   slimeGrassRegister, (b) -> new BlockTooltipItem(b, new Item.Properties()));
    slimeGrass.put(DirtType.VANILLA, vanillaSlimeGrass);
    slimeGrass.put(DirtType.EARTH, earthSlimeGrass);
    slimeGrass.put(DirtType.SKY,   skySlimeGrass);
    slimeGrass.put(DirtType.ENDER, enderSlimeGrass);
    slimeGrass.put(DirtType.ICHOR, ichorSlimeGrass);
  }
  public static final EnumObject<FoliageType, SlimeGrassSeedItem> slimeGrassSeeds = ITEMS.registerEnum(FoliageType.values(), "slime_grass_seeds", type -> new SlimeGrassSeedItem(new Item.Properties(), type));

  /** Creates a wood variant properties function */
  private static Function<WoodVariant,BlockBehaviour.Properties> createSlimewood(MaterialColor planks, MaterialColor bark) {
    return type -> switch (type) {
      case WOOD -> BlockBehaviour.Properties.of(Material.NETHER_WOOD, bark).sound(SoundType.WOOD).requiresCorrectToolForDrops();
      case LOG -> BlockBehaviour.Properties.of(Material.NETHER_WOOD, state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? planks : bark).sound(SoundType.WOOD).requiresCorrectToolForDrops();
      default -> BlockBehaviour.Properties.of(Material.NETHER_WOOD, planks).sound(SoundType.SLIME_BLOCK);
    };
  }

  // wood
  public static final WoodBlockObject greenheart  = BLOCKS.registerWood("greenheart",  createSlimewood(MaterialColor.COLOR_LIGHT_GREEN, MaterialColor.COLOR_GREEN),     false);
  public static final WoodBlockObject skyroot     = BLOCKS.registerWood("skyroot",     createSlimewood(MaterialColor.COLOR_CYAN,        MaterialColor.TERRACOTTA_CYAN), false);
  public static final WoodBlockObject bloodshroom = BLOCKS.registerWood("bloodshroom", createSlimewood(MaterialColor.COLOR_RED,         MaterialColor.COLOR_ORANGE),    false);
  public static final WoodBlockObject enderbark   = BLOCKS.registerWood("enderbark",   createSlimewood(MaterialColor.COLOR_BLACK,       MaterialColor.COLOR_BLACK),     false);
  public static final ItemObject<Block> enderbarkRoots = BLOCKS.register("enderbark_roots", () -> new SlimeRootsBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK).strength(0.7F).randomTicks().sound(SoundType.MANGROVE_ROOTS).noOcclusion().isSuffocating(Blocks::never).isViewBlocking(Blocks::never).noOcclusion()), (b) -> new BlockItem(b, new Item.Properties()));
  public static final EnumObject<SlimeType,Block> slimyEnderbarkRoots = BLOCKS.registerEnum(SlimeType.values(), "enderbark_roots", type -> new SlimeDirtBlock(BlockBehaviour.Properties.of(Material.DIRT, type.getMapColor()).strength(0.7F).sound(SoundType.MUDDY_MANGROVE_ROOTS).lightLevel(s -> type.getLightLevel())), (b) -> new BlockItem(b, new Item.Properties()));

  // plants
  public static final EnumObject<FoliageType, SlimeTallGrassBlock> slimeFern, slimeTallGrass;
  static {
    Function<FoliageType,BlockBehaviour.Properties> props = type -> {
      BlockBehaviour.Properties properties;
      if (type.isNether()) {
        properties = builder(Material.REPLACEABLE_FIREPROOF_PLANT, type.getMapColor(), SoundType.ROOTS);
      } else {
        properties = builder(Material.REPLACEABLE_PLANT, type.getMapColor(), SoundType.GRASS);
      }
      return properties.instabreak().noCollission().offsetType(Block.OffsetType.XYZ);
    };
    slimeFern = BLOCKS.registerEnum(FoliageType.values(), "slime_fern", type -> new SlimeTallGrassBlock(props.apply(type), type), (b) -> new BlockItem(b, new Item.Properties()));
    slimeTallGrass = BLOCKS.registerEnum(FoliageType.values(), "slime_tall_grass", type -> new SlimeTallGrassBlock(props.apply(type), type), (b) -> new BlockItem(b, new Item.Properties()));
  }
  public static final EnumObject<FoliageType,FlowerPotBlock> pottedSlimeFern = BLOCKS.registerPottedEnum(FoliageType.values(), "slime_fern", slimeFern);

  // trees
  public static final EnumObject<FoliageType, Block> slimeSapling = Util.make(() -> {
    Function<FoliageType,BlockBehaviour.Properties> props = type -> builder(Material.PLANT, type.getMapColor(), type.isNether() ? SoundType.FUNGUS : SoundType.GRASS).instabreak().noCollission();
    return new EnumObject.Builder<FoliageType,Block>(FoliageType.class)
      .putAll(BLOCKS.registerEnum(FoliageType.OVERWORLD, "slime_sapling", (type) -> new SlimeSaplingBlock(new SlimeTree(type), type, props.apply(type).randomTicks()), (b) -> new BlockTooltipItem(b, new Item.Properties())))
      .put(FoliageType.BLOOD, BLOCKS.register("blood_slime_sapling", () -> new SlimeFungusBlock(props.apply(FoliageType.BLOOD), () -> Holder.hackyErase(TinkerStructures.bloodSlimeFungus.getHolder().orElseThrow())), (b) -> new BlockTooltipItem(b, new Item.Properties())))
      .put(FoliageType.ICHOR, BLOCKS.register("ichor_slime_sapling", () -> new SlimeFungusBlock(props.apply(FoliageType.ICHOR), () -> Holder.hackyErase(TinkerStructures.ichorSlimeFungus.getHolder().orElseThrow())), (b) -> new BlockItem(b, new Item.Properties())))
      .put(FoliageType.ENDER, BLOCKS.register("ender_slime_sapling", () -> new SlimePropaguleBlock(new SlimeTree(FoliageType.ENDER), FoliageType.ENDER, props.apply(FoliageType.ENDER)), (b) -> new BlockTooltipItem(b, new Item.Properties())))
      .build();
  });
  public static final EnumObject<FoliageType,FlowerPotBlock> pottedSlimeSapling = BLOCKS.registerPottedEnum(FoliageType.values(), "slime_sapling", slimeSapling);
  public static final EnumObject<FoliageType, Block> slimeLeaves = new EnumObject.Builder<FoliageType, Block>(FoliageType.class)
    .putAll(BLOCKS.registerEnum(FoliageType.OVERWORLD, "slime_leaves", type -> new SlimeLeavesBlock(builder(Material.LEAVES, type.getMapColor(), SoundType.GRASS).strength(1.0f).randomTicks().noOcclusion().isValidSpawn(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never), type), (b) -> new BlockItem(b, new Item.Properties())))
    .putAll(BLOCKS.registerEnum(FoliageType.NETHER, "slime_leaves", type -> new SlimeWartBlock(builder(Material.GRASS, type.getMapColor(), SoundType.WART_BLOCK).strength(1.5F).isValidSpawn((s, w, p, e) -> false), type), (b) -> new BlockItem(b, new Item.Properties())))
    .put(FoliageType.ENDER, BLOCKS.register("ender_slime_leaves", () -> new SlimePropaguleLeavesBlock(builder(Material.LEAVES, FoliageType.ENDER.getMapColor(), SoundType.GRASS).strength(1.0f).randomTicks().noOcclusion().isValidSpawn(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never), FoliageType.ENDER), (b) -> new BlockItem(b, new Item.Properties())))
    .build();

  // slime vines
  public static final ItemObject<SlimeVineBlock> skySlimeVine, enderSlimeVine;
  static {
    Function<SlimeType,BlockBehaviour.Properties> props = type -> builder(Material.REPLACEABLE_PLANT, type.getMapColor(), SoundType.GRASS).strength(0.75F).noCollission().randomTicks();
    skySlimeVine = BLOCKS.register("sky_slime_vine", () -> new SlimeVineBlock(props.apply(SlimeType.SKY), SlimeType.SKY), (b) -> new BlockItem(b, new Item.Properties()));
    enderSlimeVine = BLOCKS.register("ender_slime_vine", () -> new SlimeVineBlock(props.apply(SlimeType.ENDER), SlimeType.ENDER), (b) -> new BlockItem(b, new Item.Properties()));
  }

  // geodes
  // earth
  public static final GeodeItemObject earthGeode = BLOCKS.registerGeode("earth_slime_crystal", MaterialColor.COLOR_LIGHT_GREEN, Sounds.EARTH_CRYSTAL, Sounds.EARTH_CRYSTAL_CHIME.getSound(), Sounds.EARTH_CRYSTAL_CLUSTER,  3, new Item.Properties());
  public static final RegistryObject<ConfiguredFeature<GeodeConfiguration,Feature<GeodeConfiguration>>> configuredEarthGeode = CONFIGURED_FEATURES.registerGeode(
    "earth_geode", earthGeode, BlockStateProvider.simple(Blocks.CALCITE), BlockStateProvider.simple(Blocks.CLAY),
    new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 5.2D), new GeodeCrackSettings(0.95D, 2.0D, 2), UniformInt.of(6, 9), UniformInt.of(3, 4), UniformInt.of(1, 2), 16, 1);
  public static final RegistryObject<PlacedFeature> placedEarthGeode = PLACED_FEATURES.registerGeode("earth_geode", configuredEarthGeode, RarityFilter.onAverageOnceEvery(128), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.aboveBottom(54)));
  // sky
  public static final GeodeItemObject skyGeode   = BLOCKS.registerGeode("sky_slime_crystal",   MaterialColor.COLOR_BLUE,        Sounds.SKY_CRYSTAL,   Sounds.SKY_CRYSTAL_CHIME.getSound(),   Sounds.SKY_CRYSTAL_CLUSTER,    0, new Item.Properties());
  public static final RegistryObject<ConfiguredFeature<GeodeConfiguration,Feature<GeodeConfiguration>>> configuredSkyGeode = CONFIGURED_FEATURES.registerGeode(
    "sky_geode", skyGeode, BlockStateProvider.simple(Blocks.CALCITE), BlockStateProvider.simple(Blocks.MOSSY_COBBLESTONE),
    new GeodeLayerSettings(1.5D, 2.0D, 3.0D, 4.5D), new GeodeCrackSettings(0.55D, 0.5D, 2), UniformInt.of(3, 4), ConstantInt.of(2), ConstantInt.of(1), 8, 3);
  public static final RegistryObject<PlacedFeature> placedSkyGeode = PLACED_FEATURES.registerGeode("sky_geode", configuredSkyGeode, RarityFilter.onAverageOnceEvery(64), HeightRangePlacement.uniform(VerticalAnchor.absolute(16), VerticalAnchor.absolute(54)));
  // ichor
  public static final GeodeItemObject ichorGeode = BLOCKS.registerGeode("ichor_slime_crystal", MaterialColor.COLOR_ORANGE,      Sounds.ICHOR_CRYSTAL, Sounds.ICHOR_CRYSTAL_CHIME.getSound(), Sounds.ICHOR_CRYSTAL_CLUSTER, 10, new Item.Properties());
  public static final RegistryObject<ConfiguredFeature<GeodeConfiguration,Feature<GeodeConfiguration>>> configuredIchorGeode = CONFIGURED_FEATURES.registerGeode(
    "ichor_geode", ichorGeode, BlockStateProvider.simple(Blocks.CALCITE), BlockStateProvider.simple(Blocks.NETHERRACK),
    new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 4.2D), new GeodeCrackSettings(0.75D, 2.0D, 2), UniformInt.of(4, 6), UniformInt.of(3, 4), UniformInt.of(1, 2), 24, 20);
  public static final RegistryObject<PlacedFeature> placedIchorGeode = PLACED_FEATURES.registerGeode("ichor_geode", configuredIchorGeode, RarityFilter.onAverageOnceEvery(52), HeightRangePlacement.uniform(VerticalAnchor.belowTop(48), VerticalAnchor.belowTop(16)));
  // ender
  public static final GeodeItemObject enderGeode = BLOCKS.registerGeode("ender_slime_crystal", MaterialColor.COLOR_PURPLE,      Sounds.ENDER_CRYSTAL, Sounds.ENDER_CRYSTAL_CHIME.getSound(), Sounds.ENDER_CRYSTAL_CLUSTER,  7, new Item.Properties());
  public static final RegistryObject<ConfiguredFeature<GeodeConfiguration,Feature<GeodeConfiguration>>> configuredEnderGeode = CONFIGURED_FEATURES.registerGeode(
    "ender_geode", enderGeode, BlockStateProvider.simple(Blocks.CALCITE), BlockStateProvider.simple(Blocks.END_STONE),
    new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 5.2D), new GeodeCrackSettings(0.45, 1.0D, 2), UniformInt.of(4, 10), UniformInt.of(3, 4), UniformInt.of(1, 2), 16, 10000);
  public static final RegistryObject<PlacedFeature> placedEnderGeode = PLACED_FEATURES.registerGeode("ender_geode", configuredEnderGeode, RarityFilter.onAverageOnceEvery(256), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(16), VerticalAnchor.aboveBottom(64)));

  // heads
  public static final EnumObject<TinkerHeadType,SkullBlock>               heads     = BLOCKS.registerEnumNoItem(TinkerHeadType.values(), "head", TinkerWorld::makeHead);
  public static final EnumObject<TinkerHeadType,WallSkullBlock>           wallHeads = BLOCKS.registerEnumNoItem(TinkerHeadType.values(), "wall_head", TinkerWorld::makeWallHead);
  public static final EnumObject<TinkerHeadType,StandingAndWallBlockItem> headItems = ITEMS.registerEnum(TinkerHeadType.values(), "head", type -> new StandingAndWallBlockItem(heads.get(type), wallHeads.get(type), HEAD_PROPS));

  /*
   * Entities
   */
  // our own copy of the slime to make spawning a bit easier
  public static final RegistryObject<EntityType<SkySlimeEntity>> skySlimeEntity = ENTITIES.registerWithEgg("sky_slime", () ->
    EntityType.Builder.of(SkySlimeEntity::new, MobCategory.MONSTER)
                      .setShouldReceiveVelocityUpdates(true)
                      .setTrackingRange(20)
                      .sized(2.04F, 2.04F)
                      .setCustomClientFactory((spawnEntity, world) -> TinkerWorld.skySlimeEntity.get().create(world)), 0x47eff5, 0xacfff4);
  public static final RegistryObject<EntityType<EnderSlimeEntity>> enderSlimeEntity = ENTITIES.registerWithEgg("ender_slime", () ->
    EntityType.Builder.of(EnderSlimeEntity::new, MobCategory.MONSTER)
                      .setShouldReceiveVelocityUpdates(true)
                      .setTrackingRange(32)
                      .sized(2.04F, 2.04F)
                      .setCustomClientFactory((spawnEntity, world) -> TinkerWorld.enderSlimeEntity.get().create(world)), 0x6300B0, 0xD37CFF);
  public static final RegistryObject<EntityType<TerracubeEntity>> terracubeEntity = ENTITIES.registerWithEgg("terracube", () ->
    EntityType.Builder.of(TerracubeEntity::new, MobCategory.MONSTER)
                      .setShouldReceiveVelocityUpdates(true)
                      .setTrackingRange(8)
                      .sized(2.04F, 2.04F)
                      .setCustomClientFactory((spawnEntity, world) -> TinkerWorld.terracubeEntity.get().create(world)), 0xAFB9D6, 0xA1A7B1);

  /*
   * Particles
   */
  public static final RegistryObject<SimpleParticleType> skySlimeParticle = PARTICLE_TYPES.register("sky_slime", () -> new SimpleParticleType(false));
  public static final RegistryObject<SimpleParticleType> enderSlimeParticle = PARTICLE_TYPES.register("ender_slime", () -> new SimpleParticleType(false));
  public static final RegistryObject<SimpleParticleType> terracubeParticle = PARTICLE_TYPES.register("terracube", () -> new SimpleParticleType(false));

  /*
   * Features
   */
  // small veins, standard distribution
  public static RegistryObject<ConfiguredFeature<OreConfiguration,Feature<OreConfiguration>>> configuredSmallCobaltOre = CONFIGURED_FEATURES.registerSupplier("cobalt_ore_small", () -> Feature.ORE, () -> new OreConfiguration(OreFeatures.ORE_ANCIENT_DEBRIS_SMALL, cobaltOre.get().defaultBlockState(), 4));
  public static RegistryObject<PlacedFeature> placedSmallCobaltOre = PLACED_FEATURES.register("cobalt_ore_small", configuredSmallCobaltOre, CountPlacement.of(5), InSquarePlacement.spread(), PlacementUtils.RANGE_8_8, BiomeFilter.biome());
  // large veins, around y=16, up to 48
  public static RegistryObject<ConfiguredFeature<OreConfiguration,Feature<OreConfiguration>>> configuredLargeCobaltOre = CONFIGURED_FEATURES.registerSupplier("cobalt_ore_large", () -> Feature.ORE, () -> new OreConfiguration(OreFeatures.ORE_ANCIENT_DEBRIS_LARGE, cobaltOre.get().defaultBlockState(), 6));
  public static RegistryObject<PlacedFeature> placedLargeCobaltOre = PLACED_FEATURES.register("cobalt_ore_large", configuredLargeCobaltOre, CountPlacement.of(3), InSquarePlacement.spread(), HeightRangePlacement.triangle(VerticalAnchor.absolute(8), VerticalAnchor.absolute(32)), BiomeFilter.biome());


  /*
   * Events
   */

  @SubscribeEvent
  void entityAttributes(EntityAttributeCreationEvent event) {
    event.put(skySlimeEntity.get(), Monster.createMonsterAttributes().build());
    event.put(enderSlimeEntity.get(), Monster.createMonsterAttributes().build());
    event.put(terracubeEntity.get(), Monster.createMonsterAttributes().build());
  }

  /** Sets all fire info for the given wood */
  private static void setWoodFireInfo(FireBlock fireBlock, WoodBlockObject wood) {
    // planks
    fireBlock.setFlammable(wood.get(), 5, 20);
    fireBlock.setFlammable(wood.getSlab(), 5, 20);
    fireBlock.setFlammable(wood.getStairs(), 5, 20);
    fireBlock.setFlammable(wood.getFence(), 5, 20);
    fireBlock.setFlammable(wood.getFenceGate(), 5, 20);
    // logs
    fireBlock.setFlammable(wood.getLog(), 5, 5);
    fireBlock.setFlammable(wood.getStrippedLog(), 5, 5);
    fireBlock.setFlammable(wood.getWood(), 5, 5);
    fireBlock.setFlammable(wood.getStrippedWood(), 5, 5);
  }

  @SubscribeEvent
  void registerSpawnPlacement(SpawnPlacementRegisterEvent event) {
    event.register(EntityType.SLIME, null, null, new SlimePlacementPredicate<>(TinkerTags.Blocks.EARTH_SLIME_SPAWN), Operation.OR);
    event.register(skySlimeEntity.get(),   SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new SlimePlacementPredicate<>(TinkerTags.Blocks.SKY_SLIME_SPAWN), Operation.OR);
    event.register(enderSlimeEntity.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new SlimePlacementPredicate<>(TinkerTags.Blocks.ENDER_SLIME_SPAWN), Operation.OR);
    event.register(terracubeEntity.get(),  SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TerracubeEntity::canSpawnHere, Operation.OR);

  }

  @SubscribeEvent
  void commonSetup(final FMLCommonSetupEvent event) {
    // compostables
    event.enqueueWork(() -> {
      slimeLeaves.forEach((type, block) -> ComposterBlock.add(type.isNether() ? 0.85f : 0.35f, block));
      slimeSapling.forEach(block -> ComposterBlock.add(0.35f, block));
      slimeTallGrass.forEach(block -> ComposterBlock.add(0.35f, block));
      slimeFern.forEach(block -> ComposterBlock.add(0.65f, block));
      slimeGrassSeeds.forEach(block -> ComposterBlock.add(0.35F, block));
      ComposterBlock.add(0.5f, skySlimeVine);
      ComposterBlock.add(0.5f, enderSlimeVine);
      ComposterBlock.add(0.4f, enderbarkRoots);

      // head equipping
      DispenseItemBehavior dispenseArmor = new OptionalDispenseItemBehavior() {
        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack) {
          this.setSuccess(ArmorItem.dispenseArmor(source, stack));
          return stack;
        }
      };
      TinkerWorld.heads.forEach(head -> DispenserBlock.registerBehavior(head, dispenseArmor));
      // heads in firework stars
      TinkerWorld.heads.forEach(head -> FireworkStarRecipe.SHAPE_BY_ITEM.put(head.asItem(), FireworkRocketItem.Shape.CREEPER));
      // inject heads into the tile entity type
      event.enqueueWork(() -> {
        ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
        builder.addAll(BlockEntityType.SKULL.validBlocks);
        TinkerWorld.heads.forEach(head -> builder.add(head));
        TinkerWorld.wallHeads.forEach(head -> builder.add(head));
        BlockEntityType.SKULL.validBlocks = builder.build();
      });
    });

    // flammability
    event.enqueueWork(() -> {
      FireBlock fireblock = (FireBlock)Blocks.FIRE;
      // plants
      BiConsumer<FoliageType, Block> plantFireInfo = (type, block) -> {
        if (!type.isNether()) {
          fireblock.setFlammable(block, 30, 60);
        }
      };
      slimeLeaves.forEach(plantFireInfo);
      slimeTallGrass.forEach(plantFireInfo);
      slimeFern.forEach(plantFireInfo);
      // vines
      fireblock.setFlammable(skySlimeVine.get(), 15, 100);
      fireblock.setFlammable(enderSlimeVine.get(), 15, 100);
    });
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator datagenerator = event.getGenerator();
    datagenerator.addProvider(event.includeServer(), new WorldRecipeProvider(datagenerator));
  }


  /* helpers */

  /** Creates a skull block for the given head type */
  private static SkullBlock makeHead(TinkerHeadType type) {
    BlockBehaviour.Properties props = BlockBehaviour.Properties.of(Material.DECORATION).strength(1.0F);
    if (type == TinkerHeadType.PIGLIN || type == TinkerHeadType.PIGLIN_BRUTE || type == TinkerHeadType.ZOMBIFIED_PIGLIN) {
      return new PiglinHeadBlock(type, props);
    }
    return new SkullBlock(type, props);
  }

  /** Creates a skull wall block for the given head type */
  private static WallSkullBlock makeWallHead(TinkerHeadType type) {
    BlockBehaviour.Properties props = BlockBehaviour.Properties.of(Material.DECORATION).strength(1.0F).lootFrom(() -> heads.get(type));
    if (type == TinkerHeadType.PIGLIN || type == TinkerHeadType.PIGLIN_BRUTE || type == TinkerHeadType.ZOMBIFIED_PIGLIN) {
      return new PiglinWallHeadBlock(type, props);
    }
    return new WallSkullBlock(type, props);
  }
}
