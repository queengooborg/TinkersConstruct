package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.data.NamedComponentRegistry;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static slimeknights.tconstruct.TConstruct.getResource;

/**
 * Standard recipe to add a modifier
 */
public class SwappableModifierRecipe extends ModifierRecipe {

  private static final String ALREADY_PRESENT = TConstruct.makeTranslationKey("recipe", "swappable.already_present");

  /** Value of the modifier being swapped, distinguishing this recipe from others for the same modifier */
  private final String value;
  /** Logic to format the variant, needed for syncing */
  private final VariantFormatter variantFormatter;
  /** Display component for the variant string */
  @Getter
  private final Component variant;

  public SwappableModifierRecipe(ResourceLocation id, List<SizedIngredient> inputs, Ingredient toolRequirement, int maxToolSize, ModifierMatch requirements, String requirementsError, ModifierId result, String value, VariantFormatter variantFormatter, @Nullable SlotCount slots, boolean allowCrystal) {
    super(id, inputs, toolRequirement, maxToolSize, requirements, requirementsError, new ModifierEntry(result, 1), 1, slots, allowCrystal);
    this.value = value;
    this.variantFormatter = variantFormatter;
    this.variant = variantFormatter.format(result, value);
  }

  /** @deprecated use {@link #SwappableModifierRecipe(ResourceLocation, List, Ingredient, int, ModifierMatch, String, ModifierId, String, VariantFormatter, SlotCount, boolean)}} */
  @Deprecated
  public SwappableModifierRecipe(ResourceLocation id, List<SizedIngredient> inputs, Ingredient toolRequirement, int maxToolSize, ModifierMatch requirements, String requirementsError, ModifierId result, String value, @Nullable SlotCount slots, boolean allowCrystal) {
    this(id, inputs, toolRequirement, maxToolSize, requirements, requirementsError, result, value, VariantFormatter.NONE, slots, allowCrystal);
  }

  /** @deprecated use {@link #SwappableModifierRecipe(ResourceLocation, List, Ingredient, int, ModifierMatch, String, ModifierId, String, VariantFormatter, SlotCount, boolean)}} */
  @Deprecated
  public SwappableModifierRecipe(ResourceLocation id, List<SizedIngredient> inputs, Ingredient toolRequirement, int maxToolSize, ModifierMatch requirements, String requirementsError, ModifierId result, String value, @Nullable SlotCount slots) {
    this(id, inputs, toolRequirement, maxToolSize, requirements, requirementsError, result, value, slots, false);
  }

    /**
     * Gets the recipe result, or an object containing an error message if the recipe matches but cannot be applied.
     * @return Validated result
     */
  @Override
  public ValidatedResult getValidatedResult(ITinkerStationContainer inv) {
    ToolStack tool = inv.getTinkerable();

    // if the tool has the modifier already, can skip most requirements
    ModifierId modifier = result.getId();

    ValidatedResult commonError;
    boolean needsModifier;
    if (tool.getUpgrades().getLevel(modifier) == 0) {
      needsModifier = true;
      commonError = validatePrerequisites(tool);
    } else {
      needsModifier = false;
      commonError = validateRequirements(tool);
    }
    if (commonError.hasError()) {
      return commonError;
    }

    // do not allow adding the modifier if this variant is already present
    if (tool.getPersistentData().getString(modifier).equals(value)) {
      return ValidatedResult.failure(ALREADY_PRESENT, result.getModifier().getDisplayName(), variant);
    }

    // consume slots
    tool = tool.copy();
    ModDataNBT persistentData = tool.getPersistentData();
    if (needsModifier) {
      SlotCount slots = getSlots();
      if (slots != null) {
        persistentData.addSlots(slots.getType(), -slots.getCount());
      }
    }

    // set the new value to the modifier
    persistentData.putString(modifier, value);

    // add modifier if needed
    if (needsModifier) {
      tool.addModifier(result.getId(), 1);
    } else {
      tool.rebuildStats();
    }

    // ensure no modifier problems
    ValidatedResult toolValidation = tool.validate();
    if (toolValidation.hasError()) {
      return toolValidation;
    }

    return ValidatedResult.success(tool.createStack(Math.min(inv.getTinkerableSize(), shrinkToolSlotBy())));
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.swappableModifierSerializer.get();
  }


  /* Display */

  @Override
  public List<ItemStack> getToolWithModifier() {
    if (toolWithModifier == null) {
      ResourceLocation id = result.getId();
      toolWithModifier = getToolInputs().stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, requirements, result, data -> data.putString(id, value))).collect(Collectors.toList());
      toolWithModifier = getToolInputs().stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, requirements, result, data -> data.putString(id, value))).collect(Collectors.toList());
    }
    return toolWithModifier;
  }

  public static class Serializer extends AbstractModifierRecipe.Serializer<SwappableModifierRecipe> {
    @Override
    protected ModifierEntry readResult(JsonObject json) {
      JsonObject result = GsonHelper.getAsJsonObject(json, "result");
      return new ModifierEntry(ModifierId.getFromJson(result, "name"), 1);
    }

    @Override
    public SwappableModifierRecipe fromJson(ResourceLocation id, JsonObject json, Ingredient toolRequirement, int maxToolSize, ModifierMatch requirements,
                                            String requirementsError, ModifierEntry result, int maxLevel, @Nullable SlotCount slots) {
      List<SizedIngredient> ingredients = JsonHelper.parseList(json, "inputs", SizedIngredient::deserialize);
      String value = GsonHelper.getAsString(GsonHelper.getAsJsonObject(json, "result"), "value");
      boolean allowCrystal = GsonHelper.getAsBoolean(json, "allow_crystal", false);
      VariantFormatter variantFormatter = VariantFormatter.LOADER.deserialize(json, "variant_formatter");
      return new SwappableModifierRecipe(id, ingredients, toolRequirement, maxToolSize, requirements, requirementsError, result.getId(), value, variantFormatter, slots, allowCrystal);
    }

    @Override
    public SwappableModifierRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer, Ingredient toolRequirement, int maxToolSize, ModifierMatch requirements,
                                               String requirementsError, ModifierEntry result, int maxLevel, @Nullable SlotCount slots) {
      int size = buffer.readVarInt();
      ImmutableList.Builder<SizedIngredient> builder = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        builder.add(SizedIngredient.read(buffer));
      }
      String value = buffer.readUtf();
      boolean allowCrystal = buffer.readBoolean();
      VariantFormatter variantFormatter = VariantFormatter.LOADER.fromNetwork(buffer);
      return new SwappableModifierRecipe(id, builder.build(), toolRequirement, maxToolSize, requirements, requirementsError, result.getId(), value, variantFormatter, slots, allowCrystal);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, SwappableModifierRecipe recipe) {
      super.toNetworkSafe(buffer, recipe);
      buffer.writeVarInt(recipe.inputs.size());
      for (SizedIngredient ingredient : recipe.inputs) {
        ingredient.write(buffer);
      }
      buffer.writeUtf(recipe.value);
      buffer.writeBoolean(recipe.allowCrystal);
      VariantFormatter.LOADER.toNetwork(recipe.variantFormatter, buffer);
    }
  }

  /** Methods of formatting the variant string */
  @FunctionalInterface
  public interface VariantFormatter {
    NamedComponentRegistry<VariantFormatter> LOADER = new NamedComponentRegistry<>("Unknown variant formatter");

    /** Formats the variant given the modifier */
    Component format(ModifierId modifier, String variant);


    /* Formatters */
    /** Formats using the raw variant name. Exists for minimal migration on legacy recipes, will get removed in 1.20. */
    @Deprecated
    VariantFormatter NONE = LOADER.register(getResource("none"), (modifier, variant) -> new TextComponent(variant));
    /** Formats using the modifier ID as a base translation key, default method in 1.20. */
    VariantFormatter DEFAULT = LOADER.register(getResource("default"), (modifier, variant) -> new TranslatableComponent(Util.makeTranslationKey("modifier", modifier) + "." + variant));
    /** Formats using the material translation key */
    VariantFormatter MATERIAL = LOADER.register(getResource("material"), (modifier, variant) -> MaterialTooltipCache.getDisplayName(Objects.requireNonNullElse(MaterialVariantId.tryParse(variant), IMaterial.UNKNOWN_ID)));
  }
}
