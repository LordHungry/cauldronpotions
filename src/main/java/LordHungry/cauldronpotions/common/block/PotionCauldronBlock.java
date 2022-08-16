package LordHungry.cauldronpotions.common.block;

import LordHungry.cauldronpotions.CauldronPotions;
import LordHungry.cauldronpotions.common.block.entity.PotionCauldronBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Random;

import static LordHungry.cauldronpotions.common.block.actions.PotionCauldron.BrewPotion.*;
import static LordHungry.cauldronpotions.common.block.actions.PotionCauldron.PotionHelper.isPotionCauldron;

public class PotionCauldronBlock extends BlockWithEntity implements BlockEntityProvider {

	public static final int MIN_FILL_LEVEL = 0;
	public static final int MAX_FILL_LEVEL = 5;
	public static final IntProperty LEVEL = IntProperty.of("level", MIN_FILL_LEVEL, MAX_FILL_LEVEL);
	public static final BooleanProperty IS_BREWING = BooleanProperty.of("is_brewing");
	private static final int BASE_CONTENT_HEIGHT = 6;
	private static final double HEIGHT_PER_LEVEL = 3.0;

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
		builder.add(IS_BREWING);
	}


	public boolean isFull(BlockState state) {
		return state.get(LEVEL) == MAX_FILL_LEVEL;
	}

	public boolean isEmpty(BlockState state) {
		return state.get(LEVEL) == MIN_FILL_LEVEL;
	}

	public PotionCauldronBlock(Settings settings) {
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(LEVEL, 0));
		setDefaultState(getStateManager().getDefaultState().with(IS_BREWING, false));
	}

	public static void decrementFluidLevel(BlockState state, World world, BlockPos pos) {
		PotionCauldronBlockEntity potionCauldron = isPotionCauldron(world, pos);
		int i = state.get(LEVEL) - 1;
		world.setBlockState(pos, state.with(LEVEL, i));
		if (state.get(PotionCauldronBlock.LEVEL) == 0) {
			assert potionCauldron != null;
			potionCauldron.setEffect("empty");
		}
	}

	public static void incrementFluidLevel(BlockState state, World world, BlockPos pos) {
		PotionCauldronBlockEntity potionCauldron = isPotionCauldron(world, pos);
		int i = state.get(LEVEL) + 1;
		world.setBlockState(pos, state.with(LEVEL, i));
		assert potionCauldron != null;
		if (Objects.equals(potionCauldron.getEffect(), "empty")) {
			potionCauldron.setEffect("water");
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new PotionCauldronBlockEntity(CauldronPotions.POTION_CAULDRON_BLOCK_ENTITY, pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, CauldronPotions.POTION_CAULDRON_BLOCK_ENTITY, PotionCauldronBlockEntity::tick);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (state.get(IS_BREWING)) {
			CauldronPotions.LOGGER.info("i am brewing!!!");
			double d = (double) pos.getX() + 0.5;
			double e = (double) pos.getY();
			double f = (double) pos.getZ() + 0.5;
			if (random.nextDouble() < 0.1) {
				world.playSound(d, e, f, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}
			double g = 0.52;
			double h = random.nextDouble() * 0.6 - 0.3;
			double j = random.nextDouble() * 6.0 / 16.0;
			world.addParticle(ParticleTypes.SMOKE, d, e + j, f, 0.0, 0.0, 0.0);
			world.addParticle(ParticleTypes.FLAME, d, e + j, f, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack items = player.getStackInHand(hand);
		Item item = items.getItem();
		PotionCauldronBlockEntity potionCauldron = isPotionCauldron(world, pos);
		if (!world.isClient) {
			assert potionCauldron != null;
			if (state.get(PotionCauldronBlock.LEVEL) != 0 && !potionCauldron.IS_BREWING) {
				if (BrewingRecipeRegistry.hasRecipe(PotionUtil.setPotion(new ItemStack(Items.POTION), potionCauldron.getPotion()), items) && potionCauldron.FUEL_AMOUNT != 0) {
					brewPotion(world, pos, potionCauldron, items, state);
				} else if (item == Items.GLASS_BOTTLE) {
					takePotion(world, pos, potionCauldron, items, player, hand, state);
				}else if (item == Items.POTION && state.get(PotionCauldronBlock.LEVEL) != 5 && PotionUtil.getPotion(items) == potionCauldron.getPotion()) {
					drainPotion(world, pos, items, player, hand, state, potionCauldron);
				} else if (item == Items.WOODEN_SHOVEL) {
					destroyPotion(world, pos, potionCauldron);
				}
			} if (item == Items.WATER_BUCKET) {
				drainBucket(world, pos, items, player, hand, state, potionCauldron);
			}else if (item == Items.POTION && state.get(PotionCauldronBlock.LEVEL) == 0) {
				drainPotion(world, pos, items, player, hand, state, potionCauldron);
			} else if (item == Items.BLAZE_POWDER && potionCauldron.FUEL_AMOUNT == 0) {
				potionCauldron.takeFuel(items);
				items.decrement(1);
			} else if (item == Items.AIR) {
				checkPotion(world, pos, potionCauldron);
			}
		}
		return ActionResult.success(world.isClient);
	}
}
