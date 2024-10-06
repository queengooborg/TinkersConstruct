package slimeknights.tconstruct.shared;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegisterEvent;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.item.TooltipItem;
import slimeknights.mantle.registration.object.FenceBuildingBlockObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.registration.object.MetalItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.shared.block.OrientableBlock;
import slimeknights.tconstruct.shared.block.SlimesteelBlock;

/**
 * Contains bommon blocks and items used in crafting materials
 */
@SuppressWarnings("unused")
public final class TinkerMaterials extends TinkerModule {
  // ores
  public static final MetalItemObject cobalt = BLOCKS.registerMetal("cobalt", metalBuilder(MaterialColor.COLOR_BLUE), (b) -> new BlockTooltipItem(b, new Item.Properties()), new Item.Properties());
  // tier 3
  public static final MetalItemObject slimesteel     = BLOCKS.registerMetal("slimesteel", () -> new SlimesteelBlock(metalBuilder(MaterialColor.WARPED_WART_BLOCK).noOcclusion()), (b) -> new BlockTooltipItem(b, new Item.Properties()), new Item.Properties());
  public static final MetalItemObject amethystBronze = BLOCKS.registerMetal("amethyst_bronze", metalBuilder(MaterialColor.COLOR_PURPLE), (b) -> new BlockTooltipItem(b, new Item.Properties()), new Item.Properties());
  public static final MetalItemObject roseGold       = BLOCKS.registerMetal("rose_gold", metalBuilder(MaterialColor.TERRACOTTA_WHITE), (b) -> new BlockTooltipItem(b, new Item.Properties()), new Item.Properties());
  public static final MetalItemObject pigIron        = BLOCKS.registerMetal("pig_iron", () -> new OrientableBlock(metalBuilder(MaterialColor.COLOR_PINK)), (b) -> new BlockTooltipItem(b, new Item.Properties()), new Item.Properties());
  // tier 4
  public static final MetalItemObject queensSlime = BLOCKS.registerMetal("queens_slime", metalBuilder(MaterialColor.COLOR_GREEN), (b) -> new BlockTooltipItem(b, new Item.Properties()), new Item.Properties());
  public static final MetalItemObject manyullyn   = BLOCKS.registerMetal("manyullyn", metalBuilder(MaterialColor.COLOR_PURPLE), (b) -> new BlockTooltipItem(b, new Item.Properties()), new Item.Properties());
  public static final MetalItemObject hepatizon   = BLOCKS.registerMetal("hepatizon", metalBuilder(MaterialColor.TERRACOTTA_BLUE), (b) -> new BlockTooltipItem(b, new Item.Properties()), new Item.Properties());
  public static final MetalItemObject soulsteel   = BLOCKS.registerMetal("soulsteel", metalBuilder(MaterialColor.COLOR_BROWN).noOcclusion(), (b) -> new BlockItem(b, new Item.Properties()), new Item.Properties());
  public static final ItemObject<Item> copperNugget = ITEMS.register("copper_nugget", new Item.Properties());
  public static final ItemObject<Item> netheriteNugget = ITEMS.register("netherite_nugget", new Item.Properties());
  public static final ItemObject<Item> debrisNugget = ITEMS.register("debris_nugget", () -> new TooltipItem(new Item.Properties()));
  // tier 5
  public static final MetalItemObject knightslime = BLOCKS.registerMetal("knightslime", metalBuilder(MaterialColor.COLOR_MAGENTA), (b) -> new BlockItem(b, new Item.Properties()), new Item.Properties());

  // non-metal
  public static final ItemObject<Item> necroticBone = ITEMS.register("necrotic_bone", () -> new TooltipItem(new Item.Properties()));
  public static final ItemObject<Item> venombone = ITEMS.register("venombone", () -> new TooltipItem(new Item.Properties()));
  public static final ItemObject<Item> blazingBone = ITEMS.register("blazing_bone", () -> new TooltipItem(new Item.Properties()));
  public static final ItemObject<Item> necroniumBone = ITEMS.register("necronium_bone", () -> new TooltipItem(new Item.Properties()));
  public static final FenceBuildingBlockObject nahuatl = BLOCKS.registerFenceBuilding("nahuatl", builder(Material.NETHER_WOOD, MaterialColor.PODZOL, SoundType.WOOD).requiresCorrectToolForDrops().strength(25f, 300f), (b) -> new BlockItem(b, new Item.Properties()));
  public static final FenceBuildingBlockObject blazewood = BLOCKS.registerFenceBuilding("blazewood", woodBuilder(MaterialColor.TERRACOTTA_RED).requiresCorrectToolForDrops().strength(25f, 300f).lightLevel(s -> 7), (b) -> new BlockItem(b, new Item.Properties()));

  /*
   * Serializers
   */
  @SubscribeEvent
  void registerSerializers(RegisterEvent event) {
    if (event.getRegistryKey() == Registries.RECIPE_SERIALIZER) {
      CraftingHelper.register(MaterialIngredient.Serializer.ID, MaterialIngredient.Serializer.INSTANCE);
    }
  }
}
