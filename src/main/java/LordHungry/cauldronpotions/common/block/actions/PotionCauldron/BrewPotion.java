package LordHungry.cauldronpotions.common.block.actions.PotionCauldron;

import LordHungry.cauldronpotions.CauldronPotions;
import LordHungry.cauldronpotions.common.block.PotionCauldronBlock;
import LordHungry.cauldronpotions.common.block.entity.PotionCauldronBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import static LordHungry.cauldronpotions.common.block.PotionCauldronBlock.LEVEL;
import static LordHungry.cauldronpotions.common.block.actions.PotionCauldron.PotionHelper.PotionRecipeChecker;

public interface BrewPotion {

	static void brewPotion(World world, BlockPos pos, PotionCauldronBlockEntity potionCauldron, ItemStack handItems, BlockState state) {
		String wantedPotion = PotionRecipeChecker(handItems, potionCauldron.getPotion());
		potionCauldron.setEffect(wantedPotion);
		handItems.decrement(1);
		world.playSound(null, pos, SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundCategory.BLOCKS, 1.0F, 1.0F);
		potionCauldron.beginBrew();
	}

	static void takePotion(World world, BlockPos pos, PotionCauldronBlockEntity potionCauldron, ItemStack handItems, PlayerEntity player, Hand hand, BlockState state) {
		player.setStackInHand(hand, ItemUsage.exchangeStack(handItems, player, PotionUtil.setPotion(new ItemStack(Items.POTION), Potion.byId(potionCauldron.getEffect()))));
		player.incrementStat(Stats.USE_CAULDRON);
		PotionCauldronBlock.decrementFluidLevel(state, world, pos);
		world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
		world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
	}

	static void destroyPotion(World world, BlockPos pos, PotionCauldronBlockEntity potionCauldron) {
		potionCauldron.setEffect("water");
		world.playSound(null, pos, SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}

	static void drainBucket(World world, BlockPos pos, ItemStack handItems, PlayerEntity player, Hand hand, BlockState state, PotionCauldronBlockEntity potionCauldron) {
		world.setBlockState(pos, state.with(LEVEL, 5));
		potionCauldron.setEffect("water");
		player.setStackInHand(hand, ItemUsage.exchangeStack(handItems, player, new ItemStack(Items.BUCKET)));
		player.incrementStat(Stats.USE_CAULDRON);
		player.incrementStat(Stats.USED.getOrCreateStat(handItems.getItem()));
		world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
		world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
	}

	static void drainPotion(World world, BlockPos pos, ItemStack handItems, PlayerEntity player, Hand hand, BlockState state, PotionCauldronBlockEntity potionCauldron) {
		PotionCauldronBlock.incrementFluidLevel(state, world, pos);
		potionCauldron.setPotion(PotionUtil.getPotion(handItems));
		player.setStackInHand(hand, ItemUsage.exchangeStack(handItems, player, new ItemStack(Items.GLASS_BOTTLE)));
		player.incrementStat(Stats.USE_CAULDRON);
		player.incrementStat(Stats.USED.getOrCreateStat(handItems.getItem()));
		world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
		world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
	}

	static void checkPotion(World world, BlockPos pos, PotionCauldronBlockEntity potionCauldron) {
		CauldronPotions.LOGGER.info("Potion type in the cauldron is {}.", potionCauldron.getEffect());
		CauldronPotions.LOGGER.info("i have {} fuel left.", potionCauldron.FUEL_AMOUNT);
		CauldronPotions.LOGGER.info("current brewtime is {}.", potionCauldron.CURRENT_BREW);
		CauldronPotions.LOGGER.info("am i brewing? {}", potionCauldron.IS_BREWING);
		world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}
}
