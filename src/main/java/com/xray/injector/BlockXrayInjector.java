package com.xray.injector;

import com.xray.XRay;
import com.xray.reference.Reference;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockXrayInjector extends BlockContainer {
    public BlockXrayInjector() {
        super(Material.IRON);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, "xray_injector"));
        setUnlocalizedName("rioutouscraftxray.xray_injector");
        setCreativeTab(net.minecraft.creativetab.CreativeTabs.REDSTONE);
        setHardness(3.5F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    @Override public TileEntity createNewTileEntity(World world, int metadata) { return new TileXrayInjector(); }

    @Override public boolean isOpaqueCube(IBlockState state) { return true; }
    @Override public boolean isFullCube(IBlockState state) { return true; }
    @Override public BlockRenderLayer getBlockLayer() { return BlockRenderLayer.SOLID; }

    @Override public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                               EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) player.openGui(XRay.instance, Reference.GUI_INJECTOR, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileXrayInjector) InventoryHelper.dropInventoryItems(world, pos, (TileXrayInjector) tile);
        super.breakBlock(world, pos, state);
    }
}
