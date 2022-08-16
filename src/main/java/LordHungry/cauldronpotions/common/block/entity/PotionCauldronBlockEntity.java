package LordHungry.cauldronpotions.common.block.entity;

import LordHungry.cauldronpotions.CauldronPotions;
import LordHungry.cauldronpotions.common.block.PotionCauldronBlock;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PotionCauldronBlockEntity extends BlockEntity {

	private String effect = "empty";

	private static final int FUEL_VALUE = 20;
	private static final int BREWTIME = 100;

	public int FUEL_AMOUNT = 0;
	public int CURRENT_BREW = 0;
	public boolean IS_BREWING = false;

	public PotionCauldronBlockEntity(BlockEntityType<PotionCauldronBlockEntity> potionCauldronBlockEntity, BlockPos blockPos, BlockState blockState) {
		super(CauldronPotions.POTION_CAULDRON_BLOCK_ENTITY, blockPos, blockState);
	}

	public static void tick(World world1, BlockPos pos, BlockState state1, PotionCauldronBlockEntity be) {
		if (be.CURRENT_BREW > 0) {
			be.IS_BREWING = true;
			--be.CURRENT_BREW;
			world1.setBlockState(pos, state1.with(PotionCauldronBlock.IS_BREWING, true));
			CauldronPotions.LOGGER.info("setting potion to brew!");
		} else if (be.CURRENT_BREW == 0 && be.IS_BREWING) {
			be.IS_BREWING = false;
			world1.setBlockState(pos, state1.with(PotionCauldronBlock.IS_BREWING, false));
			CauldronPotions.LOGGER.info("setting potion to not brew!");
		}
	}

	public void setEffect(String string) {
		effect = string;
		markDirty();
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		nbt.putString("effect", effect);
		nbt.putInt("fuel_amount", FUEL_AMOUNT);
		nbt.putInt("current_brew", CURRENT_BREW);
		nbt.putBoolean("is_brewing", IS_BREWING);

		super.writeNbt(nbt);
	}

	public String getEffect() {
		return effect;
	}

	public Potion getPotion() {
		return Potion.byId(effect);
	}

	public void setPotion(Potion potion) {
		ItemStack Ipotion = PotionUtil.setPotion(new ItemStack(Items.POTION), potion);
		NbtCompound NBTpotion = Ipotion.getNbt();
		assert NBTpotion != null;
		String PotionString = NBTpotion.getString("Potion");
		effect = PotionString.substring(PotionString.indexOf(":") + 1);
	}

	public void takeFuel(ItemStack fuel) {
		FUEL_AMOUNT = FUEL_AMOUNT + FUEL_VALUE;
	}

	public void beginBrew() {
		CURRENT_BREW = BREWTIME;
		FUEL_AMOUNT = FUEL_AMOUNT - 1;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);

		effect = nbt.getString("effect");
		FUEL_AMOUNT = nbt.getInt("fuel_amount");
		CURRENT_BREW = nbt.getInt("current_brew");
		IS_BREWING = nbt.getBoolean("is_brewing");
		markDirty();
	}
}
