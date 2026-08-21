package com.afctools;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("afctools")
public interface AfcToolsConfig extends Config
{
	@ConfigSection(
			name = "Checklist Settings",
			description = "Customize pre-run checklist",
			position = 0
	)
	String checklistSection = "checklistSection";

	@ConfigItem(
			keyName = "customGearList",
			name = "Custom Gear List",
			description = "Enter custom gear/items separated by commas or newlines",
			section = checklistSection,
			position = 1
	)
	default String customGearList()
	{
		return "Skullable Ranged gear\nXbow and ammo\nPhoenix necklace\nPots/food\nAnti Venom\nKnife\nLooting bag\n150k for gate";
	}

	@ConfigItem(
			keyName = "tileMarkerEnabled",
			name = "Enable Course Tile Markers",
			description = "Highlights key Wilderness Agility course tiles",
			position = 2
	)
	default boolean tileMarkerEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "pkLogEnabled",
			name = "Enable PvP Tracker",
			description = "Tracks damage dealt/taken against opponents",
			position = 3
	)
	default boolean pkLogEnabled()
	{
		return true;
	}
}