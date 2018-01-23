package com.crypticmushroom.planetbound.blocks.oreblock;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class FortiumOreBlock extends Block
{
    public FortiumOreBlock()
    {
        super(Material.ROCK, MapColor.GRAY);
        
        setHarvestLevel("pickaxe", 2);
        setHardness(5);
        setResistance(10);
        setSoundType(SoundType.METAL);
    }
}