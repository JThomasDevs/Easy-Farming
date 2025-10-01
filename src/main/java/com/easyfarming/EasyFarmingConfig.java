package com.easyfarming;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("easyfarming")
public interface EasyFarmingConfig extends Config
{
	@ConfigSection(
		name = "Herb Patches",
		description = "Configure which herb patches to include in your runs",
		position = 1
	)
	String herbPatchesSection = "herbPatches";

	@ConfigItem(
		keyName = "ardougne",
		name = "Ardougne",
		description = "Include Ardougne herb patch in herb runs",
		section = herbPatchesSection
	)
	default boolean ardougne() { return true; }

	@ConfigItem(
		keyName = "catherby",
		name = "Catherby",
		description = "Include Catherby herb patch in herb runs",
		section = herbPatchesSection
	)
	default boolean catherby() { return true; }

	@ConfigItem(
		keyName = "falador",
		name = "Falador",
		description = "Include Falador herb patch in herb runs",
		section = herbPatchesSection
	)
	default boolean falador() { return true; }

	@ConfigItem(
		keyName = "morytania",
		name = "Morytania",
		description = "Include Morytania herb patch in herb runs",
		section = herbPatchesSection
	)
	default boolean morytania() { return true; }

	@ConfigItem(
		keyName = "trollStronghold",
		name = "Troll Stronghold",
		description = "Include Troll Stronghold herb patch in herb runs",
		section = herbPatchesSection
	)
	default boolean trollStronghold() { return true; }

	@ConfigItem(
		keyName = "kourend",
		name = "Kourend",
		description = "Include Kourend herb patch in herb runs",
		section = herbPatchesSection
	)
	default boolean kourend() { return true; }

	@ConfigItem(
		keyName = "farmingGuild",
		name = "Farming Guild",
		description = "Include Farming Guild herb patch in herb runs",
		section = herbPatchesSection
	)
	default boolean farmingGuild() { return true; }

	@ConfigItem(
		keyName = "harmony",
		name = "Harmony Island",
		description = "Include Harmony Island herb patch in herb runs",
		section = herbPatchesSection
	)
	default boolean harmony() { return true; }

	@ConfigItem(
		keyName = "weiss",
		name = "Weiss",
		description = "Include Weiss herb patch in herb runs",
		section = herbPatchesSection
	)
	default boolean weiss() { return true; }

	@ConfigSection(
		name = "Supplies",
		description = "Configure what supplies to include",
		position = 2
	)
	String suppliesSection = "supplies";

	@ConfigItem(
		keyName = "useCompost",
		name = "Use Compost",
		description = "Include compost in herb run supplies",
		section = suppliesSection
	)
	default boolean useCompost() { return true; }

	@ConfigItem(
		keyName = "useWateringCan",
		name = "Use Watering Can",
		description = "Include watering can in herb run supplies",
		section = suppliesSection
	)
	default boolean useWateringCan() { return true; }
}
