package slimeknights.tconstruct.world.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSet.StructureSelectionEntry;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride.BoundingBoxType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddFeaturesBiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddSpawnsBiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.holdersets.AndHolderSet;
import net.minecraftforge.registries.holdersets.NotHolderSet;
import net.minecraftforge.registries.holdersets.OrHolderSet;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.FoliageType;
import slimeknights.tconstruct.world.worldgen.islands.IslandStructure;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.minecraft.core.HolderSet.direct;
import static slimeknights.tconstruct.TConstruct.getResource;
import static slimeknights.tconstruct.world.TinkerStructures.bloodIsland;
import static slimeknights.tconstruct.world.TinkerStructures.bloodSlimeIslandFungus;
import static slimeknights.tconstruct.world.TinkerStructures.clayIsland;
import static slimeknights.tconstruct.world.TinkerStructures.earthSlimeIsland;
import static slimeknights.tconstruct.world.TinkerStructures.earthSlimeIslandTree;
import static slimeknights.tconstruct.world.TinkerStructures.endSlimeIsland;
import static slimeknights.tconstruct.world.TinkerStructures.enderSlimeTree;
import static slimeknights.tconstruct.world.TinkerStructures.enderSlimeTreeTall;
import static slimeknights.tconstruct.world.TinkerStructures.skySlimeIsland;
import static slimeknights.tconstruct.world.TinkerStructures.skySlimeIslandTree;

/** Provider for all our worldgen datapack registry stuff */
@SuppressWarnings("SameParameterValue")
@RequiredArgsConstructor
public class WorldgenDatapackRegistryProvider implements DataProvider {
  private final DataGenerator generator;
  private final ExistingFileHelper existingFileHelper;
  private final RegistryAccess registryAccess = RegistryAccess.builtinCopy();
  private final RegistryOps<JsonElement> registryOps = RegistryOps.create(JsonOps.INSTANCE, registryAccess);

  @Override
  public void run(CachedOutput cache) throws IOException {
    Map<ResourceKey<Structure>,Structure> structures = new LinkedHashMap<>();
    // earthslime island
    structures.put(earthSlimeIsland, IslandStructure.seaBuilder()
      .addDefaultTemplates(getResource("islands/earth/"))
      .addTree(reference(earthSlimeIslandTree), 1)
      .addSlimyGrass(FoliageType.EARTH)
      .build(new StructureSettings(tag(TinkerTags.Biomes.EARTHSLIME_ISLANDS), monsterOverride(EntityType.SLIME, 4, 4), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
    // skyslime island
    structures.put(skySlimeIsland, IslandStructure.skyBuilder()
      .addDefaultTemplates(getResource("islands/sky/"))
      .addTree(reference(skySlimeIslandTree), 1)
      .addSlimyGrass(FoliageType.SKY)
      .vines(TinkerWorld.skySlimeVine.get())
      .build(new StructureSettings(tag(TinkerTags.Biomes.SKYSLIME_ISLANDS), monsterOverride(TinkerWorld.skySlimeEntity.get(), 3, 4), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
    // clay island
    structures.put(clayIsland, IslandStructure.skyBuilder().addDefaultTemplates(getResource("islands/dirt/"))
      .addTree(reference(TreeFeatures.OAK), 4)
      .addTree(reference(TreeFeatures.BIRCH), 3)
      .addTree(reference(TreeFeatures.SPRUCE), 2)
      .addTree(reference(TreeFeatures.ACACIA), 1)
      .addTree(reference(TreeFeatures.JUNGLE_TREE_NO_VINE), 1)
      .addGrass(Blocks.GRASS, 7)
      .addGrass(Blocks.FERN, 1)
      .build(new StructureSettings(tag(TinkerTags.Biomes.CLAY_ISLANDS), monsterOverride(TinkerWorld.terracubeEntity.get(), 2, 4), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
    // blood island
    structures.put(bloodIsland, IslandStructure.seaBuilder().addDefaultTemplates(getResource("islands/blood/"))
      .addTree(reference(bloodSlimeIslandFungus), 1)
      .addSlimyGrass(FoliageType.BLOOD)
      .build(new StructureSettings(tag(TinkerTags.Biomes.BLOOD_ISLANDS), monsterOverride(EntityType.MAGMA_CUBE, 4, 6), Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.NONE)));
    // enderslime
    structures.put(endSlimeIsland, IslandStructure.skyBuilder().addDefaultTemplates(getResource("islands/ender/"))
      .addTree(reference(enderSlimeTree), 3)
      .addTree(reference(enderSlimeTreeTall), 17)
      .addSlimyGrass(FoliageType.ENDER)
      .vines(TinkerWorld.enderSlimeVine.get())
      .build(new StructureSettings(tag(TinkerTags.Biomes.ENDERSLIME_ISLANDS), monsterOverride(TinkerWorld.enderSlimeEntity.get(), 4, 4), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));

    // structure sets
    Map<String,StructureSet> structureSets = new LinkedHashMap<>();
    structureSets.put("overworld_ocean_island", structureSet(new RandomSpreadStructurePlacement(35, 25, RandomSpreadType.LINEAR, 25988585),  entry(earthSlimeIsland, 1)));
    structureSets.put("overworld_sky_island",   structureSet(new RandomSpreadStructurePlacement(40, 15, RandomSpreadType.LINEAR, 14357800),  entry(skySlimeIsland,   4), entry(clayIsland, 1)));
    structureSets.put("nether_ocean_island",    structureSet(new RandomSpreadStructurePlacement(15, 10, RandomSpreadType.LINEAR, 65245622),  entry(bloodIsland,      1)));
    structureSets.put("end_sky_island",         structureSet(new RandomSpreadStructurePlacement(25, 12, RandomSpreadType.LINEAR, 368963602), entry(endSlimeIsland,   1)));

    // biome modifiers
    Map<String,BiomeModifier> biomeModifiers = new LinkedHashMap<>();
    HolderSet<Biome> overworld = tag(BiomeTags.IS_OVERWORLD);
    HolderSet<Biome> nether = tag(BiomeTags.IS_NETHER);
    HolderSet<Biome> end = tag(BiomeTags.IS_END);

    biomeModifiers.put("cobalt_ore", new AddFeaturesBiomeModifier(nether, direct(reference(TinkerWorld.placedSmallCobaltOre), reference(TinkerWorld.placedLargeCobaltOre)), Decoration.UNDERGROUND_DECORATION));
    // geodes
    biomeModifiers.put("earth_geode", new AddFeaturesBiomeModifier(overworld, direct(reference(TinkerWorld.placedEarthGeode)), Decoration.LOCAL_MODIFICATIONS));
    biomeModifiers.put("sky_geode", new AddFeaturesBiomeModifier(and(overworld, not(Registries.BIOME, or(tag(BiomeTags.IS_OCEAN), tag(BiomeTags.IS_DEEP_OCEAN), tag(BiomeTags.IS_BEACH), tag(BiomeTags.IS_RIVER)))), direct(reference(TinkerWorld.placedSkyGeode)), Decoration.LOCAL_MODIFICATIONS));
    biomeModifiers.put("ichor_geode", new AddFeaturesBiomeModifier(nether, direct(reference(TinkerWorld.placedIchorGeode)), Decoration.LOCAL_MODIFICATIONS));
    biomeModifiers.put("ender_geode", new AddFeaturesBiomeModifier(and(end, not(Registries.BIOME, direct(reference(Biomes.THE_END)))), direct(reference(TinkerWorld.placedEnderGeode)), Decoration.LOCAL_MODIFICATIONS));
    // spawns
    biomeModifiers.put("spawn_overworld_slime", new AddSpawnsBiomeModifier(overworld, List.of(new SpawnerData(TinkerWorld.skySlimeEntity.get(), 100, 2, 4))));
    biomeModifiers.put("spawn_end_slime", new AddSpawnsBiomeModifier(end, List.of(new SpawnerData(TinkerWorld.enderSlimeEntity.get(), 10, 2, 4))));

    // run final loading
    registryName(Registries.STRUCTURE_SET, structureSets).run(cache);
    registryKey(Registries.STRUCTURE, structures).run(cache);
    registryName(ForgeRegistries.Keys.BIOME_MODIFIERS, biomeModifiers).run(cache);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Worldgen Datapack Registries";
  }


  /* Registry helpers */

  /** Creates a reference to the given registry object */
  private <T> Holder<T> reference(ResourceKey<T> key) {
    ResourceKey<Registry<T>> registry = ResourceKey.createRegistryKey(key.registry());
    return registryAccess.registryOrThrow(registry).getOrCreateHolderOrThrow(Objects.requireNonNull(key));
  }

  /** Creates a reference to the given registry object */
  private <T> Holder<T> reference(Holder<T> object) {
    return reference(object.unwrapKey().orElseThrow());
  }

  /** Creates a reference to the given registry object */
  private <T> Holder<T> reference(RegistryObject<T> object) {
    return reference(Objects.requireNonNull(object.getKey()));
  }


  /* Holder sets */

  /** Creates a holder set tag for the given registry */
  private <T> HolderSet<T> tag(TagKey<T> key) {
    return registryAccess.registryOrThrow(key.registry()).getOrCreateTag(key);
  }

  /** Ands the holder sets together */
  @SafeVarargs
  private <T> AndHolderSet<T> and(HolderSet<T>... sets) {
    return new AndHolderSet<>(List.of(sets));
  }

  /** Ors the holder sets together */
  @SafeVarargs
  private <T> OrHolderSet<T> or(HolderSet<T>... sets) {
    return new OrHolderSet<>(List.of(sets));
  }

  private <T> NotHolderSet<T> not(ResourceKey<Registry<T>> key, HolderSet<T> set) {
    return new NotHolderSet<>(registryAccess.registryOrThrow(key), set);
  }


  /* Datapack helpers */

  /** Creates a datapack registry with the given entries */
  private <T> DataProvider registryRL(ResourceKey<Registry<T>> registry, Map<ResourceLocation, T> entries) {
    return JsonCodecProvider.forDatapackRegistry(generator, existingFileHelper, TConstruct.MOD_ID, registryOps, registry, entries);
  }

  /** Creates a datapack registry with the given entries */
  private <T> DataProvider registryName(ResourceKey<Registry<T>> registry, Map<String, T> entries) {
    return registryRL(registry, entries.entrySet().stream().collect(Collectors.toMap(entry -> TConstruct.getResource(entry.getKey()), Entry::getValue)));
  }

  /** Creates a datapack registry with the given entries */
  private <T> DataProvider registryKey(ResourceKey<Registry<T>> registry, Map<ResourceKey<T>, T> entries) {
    return registryRL(registry, entries.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().location(), Entry::getValue)));
  }


  /* Object creation helpers */

  /** Saves a structure set */
  private StructureSet structureSet(StructurePlacement placement, StructureSelectionEntry... structures) {
    return new StructureSet(List.of(structures), placement);
  }

  /** Creates an entry for a registry object */
  private StructureSelectionEntry entry(ResourceKey<Structure> structure, int weight) {
    return new StructureSelectionEntry(reference(structure), weight);
  }

  /** Creates a spawn override for a single mob */
  private static Map<MobCategory,StructureSpawnOverride> monsterOverride(EntityType<?> entity, int min, int max) {
    return Map.of(MobCategory.MONSTER, new StructureSpawnOverride(BoundingBoxType.STRUCTURE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(entity, 1, min, max))));
  }
}
