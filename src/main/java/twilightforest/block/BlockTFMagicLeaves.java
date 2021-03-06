package twilightforest.block;

import com.google.common.collect.ImmutableList;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.block.enums.MagicWoodVariant;
import twilightforest.client.ModelRegisterCallback;
import twilightforest.client.ModelUtils;
import twilightforest.client.particle.TFParticleType;
import twilightforest.item.TFItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockTFMagicLeaves extends BlockLeaves implements ModelRegisterCallback {

	protected BlockTFMagicLeaves() {
		this.setHardness(0.2F);
		this.setLightOpacity(2);
		this.setCreativeTab(TFItems.creativeTab);
		this.setDefaultState(blockState.getBaseState().withProperty(CHECK_DECAY, true).withProperty(DECAYABLE, true)
				.withProperty(BlockTFMagicLog.VARIANT, MagicWoodVariant.TIME));
	}

	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BlockTFMagicLog.VARIANT, CHECK_DECAY, DECAYABLE);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = state.getValue(BlockTFMagicLog.VARIANT).ordinal();
		if (state.getValue(CHECK_DECAY))
			meta |= 0b1000;
		if (state.getValue(DECAYABLE))
			meta |= 0b100;
		return meta;
	}

	@Override
	@Deprecated
	public IBlockState getStateFromMeta(int meta) {
		int variant = meta & 0b11;
		boolean checkDecay = (meta & 0b1000) > 0;
		boolean decayable = (meta & 0b100) > 0;
		return getDefaultState().withProperty(CHECK_DECAY, checkDecay)
				.withProperty(DECAYABLE, decayable)
				.withProperty(BlockTFMagicLog.VARIANT, MagicWoodVariant.values()[variant]);
	}

	@Override
	public void getSubBlocks(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
		par3List.add(new ItemStack(this, 1, 0));
		par3List.add(new ItemStack(this, 1, 1));
		par3List.add(new ItemStack(this, 1, 2));
		par3List.add(new ItemStack(this, 1, 3));
	}

	@Override
	public void randomDisplayTick(IBlockState state, World par1World, BlockPos pos, Random par5Random) {
		if (state.getValue(BlockTFMagicLog.VARIANT) == MagicWoodVariant.TRANS) {
			for (int i = 0; i < 1; ++i) {
				this.sparkleRunes(par1World, pos, par5Random);
			}
		}
	}

	@Override
	public BlockPlanks.EnumType getWoodType(int meta) {
		return BlockPlanks.EnumType.OAK;
	}

	private void sparkleRunes(World world, BlockPos pos, Random rand) {
		double offset = 0.0625D;

		EnumFacing side = EnumFacing.random(rand);
		double rx = pos.getX() + rand.nextFloat();
		double ry = pos.getY() + rand.nextFloat();
		double rz = pos.getZ() + rand.nextFloat();

		if (side == EnumFacing.DOWN && world.isAirBlock(pos.up())) {
			ry = pos.getY() + 1 + offset;
		}

		if (side == EnumFacing.UP && world.isAirBlock(pos.down())) {
			ry = pos.getY() + 0 - offset;
		}

		if (side == EnumFacing.NORTH && world.isAirBlock(pos.south())) {
			rz = pos.getZ() + 1 + offset;
		}

		if (side == EnumFacing.SOUTH && world.isAirBlock(pos.north())) {
			rz = pos.getZ() + 0 - offset;
		}

		if (side == EnumFacing.WEST && world.isAirBlock(pos.east())) {
			rx = pos.getX() + 1 + offset;
		}

		if (side == EnumFacing.EAST && world.isAirBlock(pos.west())) {
			rx = pos.getX() + 0 - offset;
		}

		if (rx < pos.getX() || rx > pos.getX() + 1 || ry < pos.getY() || ry > pos.getY() + 1 || rz < pos.getZ() || rz > pos.getZ() + 1) {
			TwilightForestMod.proxy.spawnParticle(world, TFParticleType.LEAF_RUNE, rx, ry, rz, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(); // todo 1.9
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel() {
		ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(CHECK_DECAY).ignore(DECAYABLE).build());
		ModelUtils.registerToStateSingleVariant(this, BlockTFMagicLog.VARIANT);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(state.getBlock(), 1, state.getValue(BlockTFMagicLog.VARIANT).ordinal());
	}
}
