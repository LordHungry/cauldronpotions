package LordHungry.cauldronpotions.common.block.actions.PotionCauldron;

import LordHungry.cauldronpotions.common.block.entity.PotionCauldronBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface PotionHelper {

	static PotionCauldronBlockEntity isPotionCauldron(World world, BlockPos pos) {
		var blockEntity = world.getBlockEntity(pos);
		if(blockEntity instanceof PotionCauldronBlockEntity potionCauldron) {
			return potionCauldron;
		} else return null;
	}

	// WILL CHECK WHAT THE INGREDIENT MAKES IF MIXED WITH CURRENTPOTION
	// ONLY USE IF YOU KNOW IT DOES MAKE SOMETHING
	// EX: PotionRecipeChecker(Items.MAGMA_CREAM.getDefaultStack(), Potions.AWKWARD) returns string FIRE_RESISTANCE
	static String PotionRecipeChecker(ItemStack ingredient, Potion currentPotion) {
		NbtCompound NBTpotion = BrewingRecipeRegistry.craft(ingredient, PotionUtil.setPotion(new ItemStack(Items.POTION), currentPotion)).getNbt();
		assert NBTpotion != null;
		String PotionString = NBTpotion.getString("Potion");
		return PotionString.substring(PotionString.indexOf(":") + 1);
	}
}
