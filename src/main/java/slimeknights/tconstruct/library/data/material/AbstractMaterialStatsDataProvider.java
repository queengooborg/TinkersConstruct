package slimeknights.tconstruct.library.data.material;

import com.google.gson.JsonElement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.server.packs.PackType;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.json.MaterialStatJson;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsManager;
import slimeknights.tconstruct.tools.item.ArmorSlotType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/** Base data generator for use in addons, depends on the regular material provider */
public abstract class AbstractMaterialStatsDataProvider extends GenericDataProvider {
  /** All material stats generated so far */
  private final Map<MaterialId,List<IMaterialStats>> allMaterialStats = new HashMap<>();
  /* Materials data provider for validation */
  private final AbstractMaterialDataProvider materials;

  public AbstractMaterialStatsDataProvider(DataGenerator gen, AbstractMaterialDataProvider materials) {
    super(gen, PackType.SERVER_DATA, MaterialStatsManager.FOLDER);
    this.materials = materials;
  }

  /** Adds all relevant material stats */
  protected abstract void addMaterialStats();

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    return CompletableFuture.runAsync(() -> {
      addMaterialStats();

      // ensure we have stats for all materials
      Set<MaterialId> materialsGenerated = materials.getAllMaterials();
      for (MaterialId material : materialsGenerated) {
        if (!allMaterialStats.containsKey(material)) {
          throw new IllegalStateException(String.format("Missing material stats for '%s'", material));
        }
      }
      // does not ensure we have materials for all stats, we may be adding stats for another mod
      // generate finally
      allMaterialStats.forEach((materialId, materialStats) -> saveJson(cache, materialId, convert(materialStats)));
    });
  }


  /* Helpers */

  /**
   * Adds a set of material stats for the given material ID
   * @param location  Material ID
   * @param stats     Stats to add
   */
  protected void addMaterialStats(MaterialId location, IMaterialStats... stats) {
    allMaterialStats.computeIfAbsent(location, materialId -> new ArrayList<>())
                    .addAll(Arrays.asList(stats));
  }

  /**
   * Adds material stats from the given armor builder
   * @param location     Material ID
   * @param statBuilder  Stat builder
   * @param otherStats   Other stat types to add after the builder
   */
  protected void addArmorStats(MaterialId location, ArmorSlotType.ArmorBuilder<? extends IMaterialStats> statBuilder, IMaterialStats... otherStats) {
    IMaterialStats[] stats = new IMaterialStats[4];
    for (ArmorSlotType slotType : ArmorSlotType.values()) {
      stats[slotType.getIndex()] = statBuilder.build(slotType);
    }
    addMaterialStats(location, stats);
    if (otherStats.length > 0) {
      addMaterialStats(location, otherStats);
    }
  }

  /**
   * Adds material stats from the given armor and shield builder
   * @param location     Material ID
   * @param statBuilder  Stat builder
   * @param otherStats   Other stat types to add after the builder
   */
  protected void addArmorShieldStats(MaterialId location, ArmorSlotType.ArmorShieldBuilder<? extends IMaterialStats> statBuilder, IMaterialStats... otherStats) {
    addArmorStats(location, statBuilder, otherStats);
    addMaterialStats(location, statBuilder.buildShield());
  }

  /* Internal */

  /** Converts a material and stats list to a JSON */
  private MaterialStatJson convert(List<IMaterialStats> stats) {
    return new MaterialStatJson(stats.stream()
      .collect(Collectors.toMap(
        IMaterialStats::getIdentifier,
        stat -> encodeStats(stat, stat.getType()))));
  }

  /** Deals with generics for the stat encoder */
  @SuppressWarnings("unchecked")
  private static <T extends IMaterialStats> JsonElement encodeStats(IMaterialStats stats, MaterialStatType<T> type) {
    return type.getLoadable().serialize((T)stats);
  }
}
