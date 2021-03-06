package com.crypticmushroom.planetbound.world.gen;

import com.crypticmushroom.planetbound.world.planet.Planet;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

import java.util.List;
import java.util.Random;

public class ChunkGeneratorPlanet implements IChunkGenerator {

    private final Random random;
    private final double[] heightMap;
    public NoiseGeneratorOctaves scaleNoise;
    public NoiseGeneratorOctaves depthNoise;
    double[] mainNoiseRegion;
    double[] minLimitRegion;
    double[] maxLimitRegion;
    double[] depthRegion;
    private World world;
    private NoiseGeneratorOctaves minLimitPerlinNoise;
    private NoiseGeneratorOctaves maxLimitPerlinNoise;
    private NoiseGeneratorOctaves mainPerlinNoise;
    private NoiseGeneratorPerlin surfaceNoise;
    private double[] depthBuffer = new double[256];
    private Planet planetInstance;

    public ChunkGeneratorPlanet(World world, long seed, Planet planet) {
        this.heightMap = new double[planet.getXSize() * planet.getYSize() * planet.getZSize()];

        this.planetInstance = planet;

        this.world = world;
        this.random = new Random(seed);
        this.minLimitPerlinNoise = new NoiseGeneratorOctaves(this.random, 16);
        this.maxLimitPerlinNoise = new NoiseGeneratorOctaves(this.random, 16);
        this.mainPerlinNoise = new NoiseGeneratorOctaves(this.random, 8);
        this.surfaceNoise = new NoiseGeneratorPerlin(this.random, 4);
        this.scaleNoise = new NoiseGeneratorOctaves(this.random, 10);
        this.depthNoise = new NoiseGeneratorOctaves(this.random, 16);
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        ChunkPrimer primer = new ChunkPrimer();

        this.setBlocksInChunk(x, z, primer);
        this.generateTerrainBlocks(x, z, primer);

        Chunk chunk = new Chunk(this.world, primer, x, z);
        chunk.generateSkylightMap();

        return chunk;
    }

    @Override
    public void populate(int x, int z) {
        BlockFalling.fallInstantly = true;
        int i = x * 16;
        int j = z * 16;
        BlockPos blockpos = new BlockPos(i, 0, j);
        Biome biome = this.world.getBiome(blockpos.add(16, 0, 16));
        this.random.setSeed(this.world.getSeed());
        long k = this.random.nextLong() / 2L * 2L + 1L;
        long l = this.random.nextLong() / 2L * 2L + 1L;
        this.random.setSeed((long) x * k + (long) z * l ^ this.world.getSeed());

        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.world, this.random, x, z, false);

        biome.decorate(this.world, this.random, blockpos);

        this.planetInstance.generate(blockpos, biome);

        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.world, this.random, x, z, false);

        BlockFalling.fallInstantly = false;
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return null;
    }

    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {

    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }

    public void setBlocksInChunk(int x, int z, ChunkPrimer primer) {
        this.generateHeightmap(x * 4, z * 4);

        for (int i = 0; i < 4; ++i) {
            int j = i * 5;
            int k = (i + 1) * 5;

            for (int l = 0; l < 4; ++l) {
                int i1 = (j + l) * 33;
                int j1 = (j + l + 1) * 33;
                int k1 = (k + l) * 33;
                int l1 = (k + l + 1) * 33;

                for (int i2 = 0; i2 < 32; ++i2) {
                    double d1 = this.heightMap[i1 + i2];
                    double d2 = this.heightMap[j1 + i2];
                    double d3 = this.heightMap[k1 + i2];
                    double d4 = this.heightMap[l1 + i2];
                    double d5 = (this.heightMap[i1 + i2 + 1] - d1) * 0.125D;
                    double d6 = (this.heightMap[j1 + i2 + 1] - d2) * 0.125D;
                    double d7 = (this.heightMap[k1 + i2 + 1] - d3) * 0.125D;
                    double d8 = (this.heightMap[l1 + i2 + 1] - d4) * 0.125D;

                    for (int j2 = 0; j2 < 8; ++j2) {
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * 0.25D;
                        double d13 = (d4 - d2) * 0.25D;

                        for (int k2 = 0; k2 < 4; ++k2) {
                            double d16 = (d11 - d10) * 0.25D;
                            double lvt_45_1_ = d10 - d16;

                            for (int l2 = 0; l2 < 4; ++l2) {
                                if ((lvt_45_1_ += d16) > 0.0D) {
                                    primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, this.planetInstance.getFillerBlock());
                                }
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }

    public void generateTerrainBlocks(int chunkX, int chunkZ, ChunkPrimer primer) {
        this.depthBuffer = this.surfaceNoise.getRegion(this.depthBuffer, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.0625D, 0.0625D, 1.0D);

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                int chance = -1;
                int randomizedNumber = (int) (this.depthBuffer[z + x * 16] / 3.0D + 3.0D + this.random.nextDouble() * 0.25D);

                IBlockState topState = this.planetInstance.getTopBlock();
                IBlockState bottomState = this.planetInstance.getBottomBlock();

                for (int y = 255; y >= 0; --y) {
                    if (y <= this.random.nextInt(5)) {
                        primer.setBlockState(x, y, z, Blocks.BEDROCK.getDefaultState());
                    } else {
                        IBlockState state = primer.getBlockState(x, y, z);

                        if (state.getMaterial() == Material.AIR) {
                            chance = -1;
                        } else if (state.getBlock() == this.planetInstance.getFillerBlock().getBlock()) {
                            if (chance == -1) {
                                if (randomizedNumber <= 0) {
                                    topState = Blocks.AIR.getDefaultState();
                                    bottomState = this.planetInstance.getFillerBlock();
                                }

                                chance = randomizedNumber;

                                if (y >= 0) {
                                    primer.setBlockState(x, y, z, topState);
                                } else {
                                    primer.setBlockState(x, y, z, bottomState);
                                }
                            } else if (chance > 0) {
                                --chance;
                                primer.setBlockState(x, y, z, bottomState);
                            }
                        }
                    }
                }
            }
        }
    }

    public void generateHeightmap(int chunkX, int chunkZ) {
        this.generateHeightmap(chunkX, 0, chunkZ);
    }

    private void generateHeightmap(int chunkX, int yOffset, int chunkZ) {
        this.depthRegion = this.depthNoise.generateNoiseOctaves(this.depthRegion, chunkX, chunkZ, this.planetInstance.getXSize(), this.planetInstance.getZSize(), 80.0D, 80.0D, 0.0D);
        float f = 684.412F;
        float f1 = 684.412F;
        this.mainNoiseRegion = this.mainPerlinNoise.generateNoiseOctaves(this.mainNoiseRegion, chunkX, yOffset, chunkZ, this.planetInstance.getXSize(), this.planetInstance.getYSize(), this.planetInstance.getZSize(), (double) (f / 80.0D), (double) (f1 / 160.0D), (double) (f / 80.0D));
        this.minLimitRegion = this.minLimitPerlinNoise.generateNoiseOctaves(this.minLimitRegion, chunkX, yOffset, chunkZ, this.planetInstance.getXSize(), this.planetInstance.getYSize(), this.planetInstance.getZSize(), (double) f, (double) f1, (double) f);
        this.maxLimitRegion = this.maxLimitPerlinNoise.generateNoiseOctaves(this.maxLimitRegion, chunkX, yOffset, chunkZ, this.planetInstance.getXSize(), this.planetInstance.getYSize(), this.planetInstance.getZSize(), (double) f, (double) f1, (double) f);
        int i = 0;
        int j = 0;

        for (int k = 0; k < 5; ++k) {
            for (int l = 0; l < 5; ++l) {
                float f2 = 0.0F;
                float f3 = 0.0F;
                float f4 = 0.0F;

                for (int j1 = -2; j1 <= 2; ++j1) {
                    for (int k1 = -2; k1 <= 2; ++k1) {
                        float f5 = this.planetInstance.getMinHeight();
                        float f6 = this.planetInstance.getMaxHeight();

                        f2 += f6;
                        f3 += f5;
                        f4 += 1.0F;
                    }
                }

                f2 = f2 / f4;
                f3 = f3 / f4;
                f2 = f2 * 0.9F + 0.1F;
                f3 = (f3 * 4.0F - 1.0F) / 8.0F;
                double d7 = this.depthRegion[j] / 8000.0D;

                if (d7 < 0.0D) {
                    d7 = -d7 * 0.3D;
                }

                d7 = d7 * 3.0D - 2.0D;

                if (d7 < 0.0D) {
                    d7 = d7 / 2.0D;

                    if (d7 < -1.0D) {
                        d7 = -1.0D;
                    }

                    d7 = d7 / 1.4D;
                    d7 = d7 / 2.0D;
                } else {
                    if (d7 > 1.0D) {
                        d7 = 1.0D;
                    }

                    d7 = d7 / 8.0D;
                }

                ++j;
                double d8 = (double) f3;
                double d9 = (double) f2;
                d8 = d8 + d7 * 0.2D;
                d8 = d8 * (double) 8.5F / 8.0D;
                double d0 = (double) 8.5F + d8 * 4.0D;

                for (int l1 = 0; l1 < 33; ++l1) {
                    double d1 = ((double) l1 - d0) * (double) 12.0F * 128.0D / 256.0D / d9;

                    if (d1 < 0.0D) {
                        d1 *= 4.0D;
                    }

                    double d2 = this.minLimitRegion[i] / (double) 512.0F;
                    double d3 = this.maxLimitRegion[i] / (double) 512.0F;
                    double d4 = (this.mainNoiseRegion[i] / 10.0D + 1.0D) / 2.0D;
                    double d5 = MathHelper.clampedLerp(d2, d3, d4) - d1;

                    if (l1 > 29) {
                        double d6 = (double) ((float) (l1 - 29) / 3.0F);
                        d5 = d5 * (1.0D - d6) + -10.0D * d6;
                    }

                    this.heightMap[i] = d5;
                    ++i;
                }
            }
        }
    }

}