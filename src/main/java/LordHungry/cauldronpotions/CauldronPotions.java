package LordHungry.cauldronpotions;

import LordHungry.cauldronpotions.common.block.PotionCauldronBlock;
import LordHungry.cauldronpotions.common.block.actions.PotionCauldron.PotionHelper;
import LordHungry.cauldronpotions.common.block.entity.PotionCauldronBlockEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CauldronPotions implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod name as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("Cauldron Potions");
	public static final String MOD_ID = "cauldronpotions";
	public static final Block POTION_CAULDRON_BLOCK = new PotionCauldronBlock(AbstractBlock.Settings.copy(Blocks.CAULDRON));
	public static final BlockEntityType<PotionCauldronBlockEntity> POTION_CAULDRON_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "potion_cauldron"), FabricBlockEntityTypeBuilder.create((blockPos, blockState) -> new PotionCauldronBlockEntity(CauldronPotions.POTION_CAULDRON_BLOCK_ENTITY, blockPos, blockState), POTION_CAULDRON_BLOCK).build());

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Hello Quilt world from {}!", mod.metadata().name());
		LOGGER.info("Potion checker result is {}! should be FIRE RESISTANCE.", PotionHelper.PotionRecipeChecker(Items.MAGMA_CREAM.getDefaultStack(), Potions.AWKWARD));
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "potion_cauldron"), POTION_CAULDRON_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "potion_cauldron"), new BlockItem(POTION_CAULDRON_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));
	}
}
