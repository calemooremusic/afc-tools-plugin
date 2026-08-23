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
		return "<font color='red'>Destroy looting bag in bank</font>\nSkullable Ranged gear\nXbow and ammo\nPhoenix necklace\nPots/food\nAnti Venom\nKnife\n150k for gate";
	}

	@ConfigItem(
			keyName = "customSettingsList",
			name = "Custom Settings List",
			description = "Enter custom runner settings separated by commas or newlines",
			section = checklistSection,
			position = 2
	)
	default String customSettingsList()
	{
		return "Entity Hider: ON";
	}

	@ConfigItem(
			keyName = "tileMarkerEnabled",
			name = "Enable Course Tile Markers",
			description = "Highlights key Wilderness Agility course tiles",
			position = 3
	)
	default boolean tileMarkerEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "pkLogEnabled",
			name = "Enable PvP Tracker",
			description = "Tracks damage dealt/taken against opponents",
			position = 4
	)
	default boolean pkLogEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "fallTrackerEnabled",
			name = "Enable Fall Tracker",
			description = "Tracks and displays agility course falls",
			position = 5
	)
	default boolean fallTrackerEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "streamerModeLoot",
			name = "Streamer Mode: Hide Loot",
			description = "Hides the looting bag value in the panel to prevent stream sniping",
			position = 6
	)
	default boolean streamerModeLoot()
	{
		return false;
	}
}