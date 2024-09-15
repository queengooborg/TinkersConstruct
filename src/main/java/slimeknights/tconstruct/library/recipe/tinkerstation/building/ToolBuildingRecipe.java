package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.layout.LayoutSlot;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * This recipe is used for crafting a set of parts into a tool
 */

@RequiredArgsConstructor
public class ToolBuildingRecipe implements ITinkerStationRecipe {
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final IModifiable output;
  protected final int outputCount;
  @Nullable
  protected final ResourceLocation layoutSlot;
  protected final List<Ingredient> ingredients;
  protected List<LayoutSlot> layoutSlots;
  protected List<List<ItemStack>> allToolParts;
  public static final int X_OFFSET = -6;
  public static final int Y_OFFSET = -15;
  public static final int SLOT_SIZE = 18;

  /** @deprecated Use {@link #ToolBuildingRecipe(ResourceLocation, String, IModifiable, int, ResourceLocation, List)} */
  @Deprecated
  public ToolBuildingRecipe(ResourceLocation id, String group, IModifiable output, int outputCount, List<Ingredient> ingredients) {
    this(id, group, output, outputCount, null, ingredients);
  }

  /**
   * Gets the ID of the station slot layout for displaying this recipe.
   * Typically matches the output definition ID, but some tool recipes share a single layout.
   */
  public ResourceLocation getLayoutSlotId() {
    return Objects.requireNonNullElse(layoutSlot, getOutput().getToolDefinition().getId());
  }

  /** Gets the layout slots so we know where go position item slots for guis */
  public List<LayoutSlot> getLayoutSlots() {
    if (layoutSlots == null) {
      layoutSlots = StationSlotLayoutLoader.getInstance().get(getLayoutSlotId()).getInputSlots();
      if (layoutSlots.isEmpty()) {
        // fallback to tinker station or anvil
        layoutSlots = StationSlotLayoutLoader.getInstance().get(TConstruct.getResource(requiresAnvil() ? "tinkers_anvil" : "tinker_station")).getInputSlots();
      }
      int missingSlots = getAllToolParts().size() + getExtraRequirements().size() - layoutSlots.size();
      // check layout slots if its too small
      if (missingSlots > 0) {
        TConstruct.LOG.error(String.format("Tool part count is greater than layout slot count for %s!", getId()));
        layoutSlots = new ArrayList<>(layoutSlots);
        for (int additionalSlot = 0; additionalSlot < missingSlots; additionalSlot++) {
          layoutSlots.add(new LayoutSlot(null, null, additionalSlot * SLOT_SIZE - X_OFFSET, -Y_OFFSET, null));
        }
      }
    }
    return layoutSlots;
  }

  /** Gets the tool parts for this tool */
  public List<PartRequirement> getToolParts() {
    return getOutput().getToolDefinition().getData().getParts();
  }

  /**
   * Gets all tool parts as and all its variants for JEI input lookups.
   */
  public List<List<ItemStack>> getAllToolParts() {
    if (allToolParts == null) {
      allToolParts = getToolParts().stream()
        .map(PartRequirement::getPart)
        .map(part -> part == null
                     ? List.of(new ItemStack(Items.BARRIER))
                     : MaterialRegistry.getInstance().getVisibleMaterials().stream()
                                       .filter(part::canUseMaterial)
                                       .map(mat -> part.withMaterial(mat.getIdentifier()))
                                       .toList())
        .toList();
    }
    return allToolParts;
  }

  /** @deprecated use {@link #ToolBuildingRecipe(ResourceLocation, String, IModifiable, int, List)} */
  @Deprecated
  public ToolBuildingRecipe(ResourceLocation id, String group, IModifiable output) {
    this(id, group, output, 1, Collections.emptyList());
  }

  /** Gets the additional recipe requirements beyond the tool parts */
  public List<Ingredient> getExtraRequirements() {
    return ingredients;
  }

  /** Helper to determine if an anvil is required */
  public boolean requiresAnvil() {
    return getToolParts().size() + getExtraRequirements().size() >= 4;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.toolBuildingRecipeSerializer.get();
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level worldIn) {
    if (!inv.getTinkerableStack().isEmpty()) {
      return false;
    }
    List<PartRequirement> parts = getToolParts();
    int requiredInputs = parts.size() + ingredients.size();
    int maxInputs = inv.getInputCount();
    // disallow if we have no inputs, or if we have too few slots
    if (requiredInputs == 0 || requiredInputs > maxInputs) {
      return false;
    }
    // each part must match the given slot
    int i;
    int partSize = parts.size();
    for (i = 0; i < partSize; i++) {
      if (!parts.get(i).matches(inv.getInput(i).getItem())) {
        return false;
      }
    }
    // remaining slots must match extra requirements
    for (; i < maxInputs; i++) {
      Ingredient ingredient = LogicHelper.getOrDefault(ingredients, i - partSize, Ingredient.EMPTY);
      if (!ingredient.test(inv.getInput(i))) {
        return false;
      }
    }

    return true;
  }

  @Override
  public ItemStack assemble(ITinkerStationContainer inv) {
    // first n slots contain parts
    List<MaterialVariant> materials = IntStream.range(0, output.getToolDefinition().getData().getParts().size())
                                               .mapToObj(i -> MaterialVariant.of(IMaterialItem.getMaterialFromStack(inv.getInput(i))))
                                               .toList();
    return ToolStack.createTool(output.asItem(), output.getToolDefinition(), new MaterialNBT(materials)).createStack(outputCount);
  }

  /** @deprecated Use {@link #assemble(ITinkerStationContainer)} */
  @Deprecated
  @Override
  public ItemStack getResultItem() {
    return new ItemStack(this.output);
  }
}
