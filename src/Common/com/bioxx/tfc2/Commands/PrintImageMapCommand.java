package com.bioxx.tfc2.Commands;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.royawesome.jlibnoise.NoiseQuality;
import net.royawesome.jlibnoise.model.Plane;
import net.royawesome.jlibnoise.module.Cache;
import net.royawesome.jlibnoise.module.modifier.Clamp;
import net.royawesome.jlibnoise.module.modifier.Curve;
import net.royawesome.jlibnoise.module.modifier.ScaleBias;
import net.royawesome.jlibnoise.module.modifier.ScalePoint;
import net.royawesome.jlibnoise.module.source.Perlin;

import com.bioxx.tfc2.World.ChunkManager;
import com.bioxx.tfc2.World.Biome.TerrainType;

public class PrintImageMapCommand extends CommandBase
{
	static Color[] colorMap = new Color[256];
	public PrintImageMapCommand()
	{
		for(int i = 0; i < 256; i++)
		{
			colorMap[i] = Color.getColor("", (i << 16) + (i << 8) + i);
		}
	}
	@Override
	public String getCommandName()
	{
		return "pi";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params)
	{
		MinecraftServer server = MinecraftServer.getServer();
		EntityPlayerMP player = null;
		try {
			player = getCommandSenderAsPlayer(sender);
		} catch (PlayerNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WorldServer world = server.worldServerForDimension(player.getEntityWorld().provider.getDimensionId());

		if(params.length >= 2)
		{
			String name = params[1];
			if(params[0].equals("terrain"))
			{
				int size = params.length >= 3 ? Integer.parseInt(params[2]) : 512;
				drawTerrainImage((int)Math.floor(player.posX), (int)Math.floor(player.posZ), size, world, name);
			}
			else if(params[0].equals("biome"))
			{
				int size = params.length >= 3 ? Integer.parseInt(params[2]) : 512;
				TerrainType biome = ((ChunkManager)world.getWorldChunkManager()).getBiomeAt((int)Math.floor(player.posX), (int)Math.floor(player.posZ));
				if(biome != null)
					drawBiomeImage((int)Math.floor(player.posX), (int)Math.floor(player.posZ), size, world, name, biome);
			}
			else if(params[0].equals("chunk"))
			{
				drawChunkBiomeImage((int)Math.floor(player.posX), (int)Math.floor(player.posZ), world, name);
			}
			else if(params[0].equals("noise"))
			{
				int size = params.length >= 3 ? Integer.parseInt(params[2]) : 512;
				drawNoiseImage((int)Math.floor(player.posX), (int)Math.floor(player.posZ), size, world, name);
			}
		}
	}
	public static void drawTerrainImage(int xCoord, int zCoord, int size, World world, String name)
	{
		try 
		{
			File outFile = new File(name+".bmp");
			BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
			graphics.clearRect(0, 0, size, size);
			System.out.println(name+".bmp");
			float perc = 0.1f;
			float count = 0;
			TerrainType[] b = ((ChunkManager)world.getWorldChunkManager()).getBiomeData(xCoord-size/2, zCoord-size/2, size, size);
			for(int z = 0; z < size; z++)
			{
				for(int x = 0; x < size; x++)
				{
					count++;
					graphics.setColor(b[z+x*size].getMapColor());	
					graphics.drawRect(z, x, 1, 1);
					if(count / (size*size) > perc)
					{
						System.out.println((int)(perc*100)+"%");
						perc+=0.1f;
					}
				}
			}
			System.out.println(name+".bmp Done!");
			ImageIO.write(outBitmap, "BMP", outFile);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static void drawChunkBiomeImage(int xCoord, int zCoord, World world, String name)
	{
		try 
		{
			int chunkX = xCoord >> 4;
			int chunkZ = zCoord >> 4;
			File outFile = new File(name+".bmp");
			BufferedImage outBitmap = new BufferedImage(16,16,BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
			graphics.clearRect(0, 0, 16, 16);
			System.out.println(name+".bmp");
			float perc = 0.1f;
			float count = 0;
			TerrainType[] b = ((ChunkManager)world.getWorldChunkManager()).getBiomeData(chunkX << 4, chunkZ << 4, 16, 16);
			for(int z = 0; z < 16; z++)
			{
				for(int x = 0; x < 16; x++)
				{
					count++;
					graphics.setColor(b[z+x*16].getMapColor());	
					graphics.drawRect(z, x, 1, 1);
					if(count / (16*16) > perc)
					{
						System.out.println((int)(perc*100)+"%");
						perc+=0.1f;
					}
				}
			}
			System.out.println(name+".bmp Done!");
			ImageIO.write(outBitmap, "BMP", outFile);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static void drawBiomeImage(int xCoord, int zCoord, int size, World world, String name, TerrainType biome)
	{
		try 
		{
			File outFile = new File(name+".bmp");
			BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
			graphics.clearRect(0, 0, size, size);
			System.out.println(name+".bmp");
			float perc = 0.1f;
			float count = 0;
			for(int z = 0; z < size; z++)
			{
				for(int x = 0; x < size; x++)
				{
					count++;
					double n = biome.getHeightPlane().GetValue(xCoord-size/2+x, zCoord-size/2+z);
					int h = (int)(255*n);
					graphics.setColor(colorMap[h]);	
					graphics.drawRect(z, x, 1, 1);
					if(count / (size*size) > perc)
					{
						System.out.println((int)(perc*100)+"%");
						perc+=0.1f;
					}
				}
			}
			System.out.println(name+".bmp Done!");
			ImageIO.write(outBitmap, "BMP", outFile);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static void drawNoiseImage(int xCoord, int zCoord, int size, World world, String name)
	{
		try 
		{
			File outFile = new File(name+".bmp");
			BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
			graphics.clearRect(0, 0, size, size);
			System.out.println(name+".bmp");
			float perc = 0.1f;
			float count = 0;

			Perlin pe = new Perlin();
			pe.setSeed (0);
			pe.setFrequency (0.03125);
			pe.setOctaveCount (3);
			pe.setNoiseQuality (NoiseQuality.BEST);



			ScalePoint sp = new ScalePoint();
			sp.setSourceModule(0, pe);
			sp.setxScale(.33);
			sp.setyScale(.33);
			sp.setzScale(.33);

			//The scalebias makes our noise fit the range 0-1
			ScaleBias sb = new ScaleBias(sp);
			//Noise is normally +-2 so we scale by 0.25 to make it +-0.5
			sb.setScale(0.25);
			//Next we offset by +0.5 which makes the noise 0-1
			sb.setBias(0.5);

			Curve curveModule = new Curve();
			curveModule.setSourceModule(0, sb);
			curveModule.AddControlPoint(0, 0);
			curveModule.AddControlPoint(0.35, 0.1);
			curveModule.AddControlPoint(0.75, 0.9);
			curveModule.AddControlPoint(1, 1);

			Clamp clampModule = new Clamp();
			clampModule.setSourceModule(0, curveModule);
			clampModule.setLowerBound(0);
			clampModule.setUpperBound(1);

			Cache cacheModule = new Cache();
			cacheModule.setSourceModule(0, clampModule);
			Plane heightPlane = new Plane(cacheModule);

			for(int z = 0; z < size; z++)
			{
				for(int x = 0; x < size; x++)
				{
					count++;
					double n = heightPlane.GetValue(xCoord-(size/2)+x, zCoord-(size/2)+z);
					if(n >= -2 && n <= 2)
					{
						int h = (int)(255*n);
						graphics.setColor(colorMap[h]);	
						graphics.drawRect(z, x, 1, 1);
						if(count / (size*size) > perc)
						{
							System.out.println((int)(perc*100)+"%");
							perc+=0.1f;
						}
					}
					else
					{
						//System.out.println("Error");
					}
				}
			}
			System.out.println(name+".bmp Done!");
			ImageIO.write(outBitmap, "BMP", outFile);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "";
	}

}
