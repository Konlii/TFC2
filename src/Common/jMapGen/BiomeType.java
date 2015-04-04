package jMapGen;

import java.awt.Color;

public enum BiomeType 
{
	OCEAN(0x44447a),
	LAKESHORE(0x225588),
	LAKE(0x336699),
	BEACH(0xa09077),
	SCORCHED(0x555555),
	TEMPERATE_DESERT(0xc9d29b),
	TROPICAL_DESERT(0xd2b98b),
	MARSH(0x2f6666),
	ICE(0x99ffff),
	SNOW(0xffffff),
	TUNDRA(0xbbbbaa),
	BARE(0x888888),
	TAIGA(0x99aa77),
	SHRUBLAND(0x889977),
	TEMPERATE_RAIN_FOREST(0x448855),
	TEMPERATE_DECIDUOUS_FOREST(0x679459),
	GRASSLAND(0x88aa55),
	TROPICAL_RAIN_FOREST(0x337755),
	TROPICAL_SEASONAL_FOREST(0x559944),
	SUBTROPICAL_DESERT(0xd2b98b),
	RIVER(0x225588);

	public Color color;

	private BiomeType(int c)
	{
		color = Color.getColor("", c);
	}
}
