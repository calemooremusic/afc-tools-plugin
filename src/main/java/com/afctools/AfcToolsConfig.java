package com.afctools;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("afctools")
public interface AfcToolsConfig extends Config
{
	@ConfigSection(name = "HUD & View", description = "Toggle visual elements", position = 0)
	String viewSection = "viewSection";

	@ConfigSection(name = "Checklist Settings", description = "Customize pre-run checklist", position = 1)
	String checklistSection = "checklistSection";

	@ConfigSection(name = "Safety Settings", description = "Preferred game settings", position = 2)
	String safetySection = "safetySection";

	@ConfigItem(keyName = "enableHud", name = "Enable On-Screen HUD", description = "Shows the glass tracker overlay", section = viewSection, position = 1)
	default boolean enableHud() { return true; }

	@Range(min = 50, max = 200)
	@ConfigItem(keyName = "hudScale", name = "HUD Scale (%)", description = "Scale the size of the glass tracker overlay", section = viewSection, position = 2)
	default int hudScale() { return 100; }

	@ConfigItem(keyName = "showPanelStats", name = "Show Panel Stats", description = "Displays trackers in the side panel", section = viewSection, position = 3)
	default boolean showPanelStats() { return true; }

	@ConfigItem(keyName = "showHiscores", name = "Show Lap Hiscores", description = "Displays the global lap leaderboard in the side panel", section = viewSection, position = 4)
	default boolean showHiscores() { return true; }

	@ConfigItem(keyName = "customGearList", name = "Custom Gear List", description = "Enter custom gear/items separated by commas or newlines", section = checklistSection, position = 1)
	default String customGearList() { return "<font color='red'>Destroy looting bag in bank</font>\nSkullable Ranged gear\nXbow and ammo\nPhoenix necklace\nPots/food\nAnti Venom\nKnife\n150k for gate"; }

	@ConfigItem(keyName = "customSettingsList", name = "Custom Settings List", description = "Enter custom runner settings separated by commas or newlines", section = checklistSection, position = 2)
	default String customSettingsList() { return "Entity Hider: ON"; }

	@ConfigItem(keyName = "tileMarkerEnabled", name = "Enable Course Tile Markers", description = "Highlights key Wilderness Agility course tiles", position = 3)
	default boolean tileMarkerEnabled() { return true; }

	@ConfigItem(keyName = "pkLogEnabled", name = "Enable PvP Tracker", description = "Tracks damage dealt/taken against opponents", position = 4)
	default boolean pkLogEnabled() { return true; }

	@ConfigItem(keyName = "fallTrackerEnabled", name = "Enable Fall Tracker", description = "Tracks and displays agility course falls", position = 5)
	default boolean fallTrackerEnabled() { return true; }

	@ConfigItem(keyName = "streamerModeLoot", name = "Streamer Mode: Hide Loot", description = "Hides the looting bag value in the panel", position = 6)
	default boolean streamerModeLoot() { return false; }

	@ConfigItem(keyName = "prefAutoRetaliate", name = "Auto Retaliate", description = "Preferred Auto Retaliate setting", section = safetySection, position = 1)
	default RetaliateOption prefAutoRetaliate() { return RetaliateOption.OFF; }

	@ConfigItem(keyName = "prefPlayerAttack", name = "Player Attack", description = "Preferred Player Attack option", section = safetySection, position = 2)
	default AttackOption prefPlayerAttack() { return AttackOption.HIDDEN; }

	@ConfigItem(keyName = "prefNpcAttack", name = "NPC Attack", description = "Preferred NPC Attack option", section = safetySection, position = 3)
	default AttackOption prefNpcAttack() { return AttackOption.HIDDEN; }

	@ConfigItem(keyName = "prefSkullPrevention", name = "Skull Prevention", description = "Preferred Skull Prevention setting", section = safetySection, position = 4)
	default SkullPreventionOption prefSkullPrevention() { return SkullPreventionOption.ON; }

	enum AttackOption { HIDDEN, RIGHT_CLICK, LEFT_CLICK, IGNORE }
	enum RetaliateOption { ON, OFF, IGNORE }
	enum SkullPreventionOption { ON, OFF, IGNORE }
}