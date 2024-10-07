package slimeknights.tconstruct.common.data.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.FenceBuildingBlockObject;
import slimeknights.mantle.registration.object.WoodBlockObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Function;

import static slimeknights.mantle.util.IdExtender.INSTANCE;

@SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
public class TinkerBlockStateProvider extends BlockStateProvider {
  private final UncheckedModelFile GENERATED = new UncheckedModelFile("item/generated");

  public TinkerBlockStateProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
    super(generator, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    addFenceBuildingBlock(TinkerMaterials.blazewood, "block/wood/blazewood/", "planks", blockTexture("wood/blazewood"));
    addFenceBuildingBlock(TinkerMaterials.nahuatl, "block/wood/nahuatl/", "planks", blockTexture("wood/nahuatl"));
    addWood(TinkerWorld.greenheart, false);
    addWood(TinkerWorld.skyroot, true);
    addWood(TinkerWorld.bloodshroom, true);
    addWood(TinkerWorld.enderbark, true);
    basicBlock(TinkerWorld.enderbarkRoots.get(), models().withExistingParent("block/wood/enderbark/roots/empty", "block/mangrove_roots")
                                                         .texture("side", blockTexture("wood/enderbark/roots"))
                                                         .texture("top", blockTexture("wood/enderbark/roots_top")));
    TinkerWorld.slimyEnderbarkRoots.forEach((type, block) -> {
      String name = type.getSerializedName();
      cubeColumn(block, "block/wood/enderbark/roots/" + name, blockTexture("wood/enderbark/roots/" + name), blockTexture("wood/enderbark/roots/" + name + "_top"));
    });
  }


  /* Helpers */

  /** Creates a texture in the block folder */
  protected ResourceLocation blockTexture(String path) {
    return new ResourceLocation(TConstruct.MOD_ID, ModelProvider.BLOCK_FOLDER + "/" + path);
  }

  /** Creates a texture in the block folder */
  protected ResourceLocation itemTexture(String path) {
    return new ResourceLocation(TConstruct.MOD_ID, ModelProvider.ITEM_FOLDER + "/" + path);
  }

  /** Creates all models for a building block object */
  protected void addBuildingBlock(BuildingBlockObject block, String folder, String name, ResourceLocation texture) {
    ModelFile blockModel = basicBlock(block.get(), folder + name, texture);
    slab(block.getSlab(), folder + "slab", blockModel, texture, texture, texture);
    stairs(block.getStairs(), folder + "stairs", texture, texture, texture);
  }

  /** Creates all models for a building block object */
  protected void addFenceBuildingBlock(FenceBuildingBlockObject block, String folder, String name, ResourceLocation texture) {
    addBuildingBlock(block, folder, name, texture);
    fence(block.getFence(), folder + "fence/", texture);
  }

  /** Creates all models for the given wood block object */
  protected void addWood(WoodBlockObject wood, boolean trapdoorOrientable) {
    String plankPath = wood.getId().getPath();
    String name = plankPath.substring(0, plankPath.length() - "_planks".length());
    String folder = "block/wood/" + name + "/"; // forge model providers do not prefix with block if you have / in the path
    // helper to get textures for wood, since we put them in a nice folder
    Function<String,ResourceLocation> texture = suffix -> blockTexture("wood/" + name + "/" + suffix);
    ResourceLocation planks = texture.apply("planks");

    // planks and fences
    addFenceBuildingBlock(wood, folder, "planks", planks);
    fenceGate(wood.getFenceGate(), folder + "fence/gate", planks);
    // logs
    axisBlock(wood.getLog(),          folder + "log/log",           texture.apply("log"), true);
    axisBlock(wood.getStrippedLog(),  folder + "log/stripped",      texture.apply("stripped_log"), true);
    axisBlock(wood.getWood(),         folder + "log/wood",          texture.apply("log"), false);
    axisBlock(wood.getStrippedWood(), folder + "log/wood_stripped", texture.apply("stripped_log"), false);
    // doors
    door(wood.getDoor(), folder, texture.apply("door_bottom"), texture.apply("door_top"));
    basicItem(wood.getDoor(), "wood/");
    trapdoor(wood.getTrapdoor(), folder + "trapdoor_", texture.apply("trapdoor"), trapdoorOrientable);
    // redstone
    pressurePlate(wood.getPressurePlate(), folder + "pressure_plate", planks);
    button(wood.getButton(), folder + "button", planks);
    // sign
    signBlock(wood.getSign(), wood.getWallSign(), models().sign(folder + "sign", planks));
    basicItem(wood.getSign(), "wood/");
  }


  /* forge seems to think all block models should be in the root folder. That's dumb, so we get to copy and paste their builders when its not practical to adapt */

  /** Gets the resource location key for a block */
  @SuppressWarnings("deprecation")
  private ResourceLocation key(Block block) {
    return BuiltInRegistries.BLOCK.getKey(block);
  }

  /** Gets the resource path for a block */
  private String name(Block block) {
    return key(block).getPath();
  }

  /** Gets the resource location key for a block */
  @SuppressWarnings("deprecation")
  private ResourceLocation itemKey(ItemLike item) {
    return BuiltInRegistries.ITEM.getKey(item.asItem());
  }

  /** Gets the resource location key for a block */
  private String itemName(ItemLike item) {
    return itemKey(item).getPath();
  }

  /** Creates a model for a generated item with 1 layer */
  protected ItemModelBuilder basicItem(ItemLike item, String texturePrefix) {
    return basicItem(itemKey(item), texturePrefix);
  }

  /** Creates a model for a generated item with 1 layer */
  protected ItemModelBuilder basicItem(ResourceLocation item, String texturePrefix) {
    return itemModels().getBuilder(item.toString()).parent(GENERATED).texture("layer0", itemTexture(texturePrefix + item.getPath()));
  }

  /**
   * Creates a model for a block with a simple model
   * @param block   Block
   * @param model   Model to use for the block and item
   * @return model file for the basic block
   */
  public ModelFile basicBlock(Block block, ModelFile model) {
    simpleBlock(block, model);
    simpleBlockItem(block, model);
    return model;
  }

  /**
   * Creates a model for a cube with the same texture on all sides and an item form
   * @param block     Block
   * @param location  Location for the block model
   * @param texture   Texture for all sides
   * @return model file for the basic block
   */
  public ModelFile basicBlock(Block block, String location, ResourceLocation texture) {
    return basicBlock(block, models().cubeAll(location, texture));
  }

  /**
   * Creates a model for a cube with the same texture on all sides and an item form
   * @param block     Block
   * @param location  Location for the block model
   * @param side      Texture for sides
   * @param top       Texture for top
   * @return model file for the basic block
   */
  public ModelFile cubeColumn(Block block, String location, ResourceLocation side, ResourceLocation top) {
    return basicBlock(block, models().cubeColumn(location, side, top));
  }

  /**
   * Adds a block with axis textures
   * @param block        Block to add, expected to be instance of RotatedPillarBlock
   * @param location     Location for the model
   * @param texture      Side texture
   * @param horizontal   If true, makes a top texture by suffixing the side texture and includes a horizontal model.
   *                     If false, uses the side for the top
   */
  public void axisBlock(Block block, String location, ResourceLocation texture, boolean horizontal) {
    ResourceLocation endTexture = horizontal ? INSTANCE.suffix(texture, "_top") : texture;
    ModelFile model = models().cubeColumn(TConstruct.resourceString(location), texture, endTexture);
    axisBlock((RotatedPillarBlock)block, model,
              horizontal ? models().cubeColumnHorizontal(TConstruct.resourceString(location + "_horizontal"), texture, endTexture) : model);
    simpleBlockItem(block, model);
  }

  /**
   * Creates block and item model for a slab
   * @param block           Slab block
   * @param location        Location for slab models, top slab will suffix top
   * @param doubleModel     Model for the double slab
   * @param sideTexture     Side texture
   * @param bottomTexture   Bottom texture
   * @param topTexture      Top texture
   */
  public void slab(SlabBlock block, String location, ModelFile doubleModel, ResourceLocation sideTexture, ResourceLocation bottomTexture, ResourceLocation topTexture) {
    ModelFile slab = models().slab(location, sideTexture, bottomTexture, topTexture);
    slabBlock(
      block, slab,
      models().slabTop(location + "_top", sideTexture, bottomTexture, topTexture),
      doubleModel);
    simpleBlockItem(block, slab);
  }

  /**
   * Creates block and item model for stairs
   * @param block           Stairs block
   * @param location        Location for stair models, inner and outer will suffix
   * @param sideTexture     Side texture
   * @param bottomTexture   Bottom texture
   * @param topTexture      Top texture
   */
  public void stairs(StairBlock block, String location, ResourceLocation sideTexture, ResourceLocation bottomTexture, ResourceLocation topTexture) {
    ModelFile stairs = models().stairs(location, sideTexture, bottomTexture, topTexture);
    stairsBlock(
      block, stairs,
      models().stairsInner(location + "_inner", sideTexture, bottomTexture, topTexture),
      models().stairsOuter(location + "_outer", sideTexture, bottomTexture, topTexture));
    simpleBlockItem(block, stairs);
  }
  /**
   * Adds a fence block with an item model
   * @param block    Fence block
   * @param prefix   Prefix for block files
   * @param texture  Fence texture
   */
  public void fence(FenceBlock block, String prefix, ResourceLocation texture) {
    fourWayBlock(
      block,
      models().fencePost(prefix + "post", texture),
      models().fenceSide(prefix + "side", texture));
    itemModels().withExistingParent(itemName(block), "minecraft:block/fence_inventory").texture("texture", texture);
  }

  public void fenceGate(FenceGateBlock block, String baseName, ResourceLocation texture) {
    ModelFile model = models().fenceGate(baseName, texture);
    fenceGateBlock(
      block, model,
      models().fenceGateOpen(baseName + "_open", texture),
      models().fenceGateWall(baseName + "_wall", texture),
      models().fenceGateWallOpen(baseName + "_wall_open", texture));
    simpleBlockItem(block, model);
  }

  /**
   * Adds a door block without an item model
   * @param block           Door block
   * @param prefix          Prefix for model files
   * @param bottomTexture   Bottom door texture
   * @param topTexture      Top door texture
   */
  public void door(DoorBlock block, String prefix, ResourceLocation bottomTexture, ResourceLocation topTexture) {
    doorBlock(
      block,
      models().doorBottomLeft(     prefix + "door/bottom_left",       bottomTexture, topTexture),
      models().doorBottomLeftOpen( prefix + "door/bottom_left_open",  bottomTexture, topTexture),
      models().doorBottomRight(    prefix + "door/bottom_right",      bottomTexture, topTexture),
      models().doorBottomRightOpen(prefix + "door/bottom_right_open", bottomTexture, topTexture),
      models().doorTopLeft(        prefix + "door/top_left",          bottomTexture, topTexture),
      models().doorTopLeftOpen(    prefix + "door/top_left_open",     bottomTexture, topTexture),
      models().doorTopRight(       prefix + "door/top_right",         bottomTexture, topTexture),
      models().doorTopRightOpen(   prefix + "door/top_right_open",    bottomTexture, topTexture));
  }

  /**
   * Adds a trapdoor block with an item model
   * @param block    Trapdoor block
   * @param prefix   Model location prefix
   * @param texture  Trapdoor texture
   * @param orientable  If true, its an oriented model.
   */
  public void trapdoor(TrapDoorBlock block, String prefix, ResourceLocation texture, boolean orientable) {
    ModelFile bottom = orientable ? models().trapdoorOrientableBottom(prefix + "bottom", texture) : models().trapdoorBottom(prefix + "bottom", texture);
    trapdoorBlock(
      block, bottom,
      orientable ? models().trapdoorOrientableTop(prefix + "top", texture) : models().trapdoorTop(prefix + "top", texture),
      orientable ? models().trapdoorOrientableOpen(prefix + "open", texture) : models().trapdoorOpen(prefix + "open", texture),
      orientable);
    simpleBlockItem(block, bottom);
  }

  /**
   * Adds a pressure plate with item model
   * @param block     Pressure plate block
   * @param location  Location for the model, pressed will be the location suffixed with down
   * @param texture   Texture for the plate
   */
  public void pressurePlate(PressurePlateBlock block, String location, ResourceLocation texture) {
    ModelFile pressurePlate = models().pressurePlate(location, texture);
    pressurePlateBlock(block, pressurePlate, models().pressurePlateDown(location + "_down", texture));
    simpleBlockItem(block, pressurePlate);
  }

  /**
   * Adds a button with item model
   * @param block     Button block
   * @param location  Location for the model, pressed will be the location suffixed with down
   * @param texture   Texture for the button
   */
  public void button(ButtonBlock block, String location, ResourceLocation texture) {
    ModelFile button = models().button(location, texture);
    buttonBlock(block, button, models().buttonPressed(location + "_pressed", texture));
    itemModels().withExistingParent(itemName(block), "minecraft:block/button_inventory").texture("texture", texture);
  }
}
