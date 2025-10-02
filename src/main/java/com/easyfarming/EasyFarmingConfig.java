package com.easyfarming;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("easyfarming")
public interface EasyFarmingConfig extends Config
{
	// ============================================================================
	// RUN TYPES SECTION
	// ============================================================================
	@ConfigSection(
		name = "Run Types",
		description = "Select which types of farming runs to enable",
		position = 1
	)
	String runTypesSection = "runTypes";

	@ConfigItem(
		keyName = "enableHerbRuns",
		name = "Herb Runs",
		description = "Enable herb farming runs",
		section = runTypesSection
	)
	default boolean enableHerbRuns() { return true; }

	@ConfigItem(
		keyName = "enableTreeRuns",
		name = "Tree Runs",
		description = "Enable tree farming runs",
		section = runTypesSection
	)
	default boolean enableTreeRuns() { return false; }

	@ConfigItem(
		keyName = "enableFruitTreeRuns",
		name = "Fruit Tree Runs",
		description = "Enable fruit tree farming runs",
		section = runTypesSection
	)
	default boolean enableFruitTreeRuns() { return false; }

	@ConfigItem(
		keyName = "enableAllotmentRuns",
		name = "Allotment Runs",
		description = "Enable allotment farming runs",
		section = runTypesSection
	)
	default boolean enableAllotmentRuns() { return false; }

	// ============================================================================
	// HERB PATCHES SECTION
	// ============================================================================
	@ConfigSection(
		name = "Herb Patches",
		description = "Select which herb patches to include in your farming runs",
		position = 2
	)
	String herbPatchesSection = "herbPatches";

	@ConfigItem(
		keyName = "ardougneHerb",
		name = "Ardougne",
		description = "Include Ardougne herb patch",
		section = herbPatchesSection
	)
	default boolean ardougneHerb() { return true; }

	@ConfigItem(
		keyName = "catherbyHerb",
		name = "Catherby",
		description = "Include Catherby herb patch",
		section = herbPatchesSection
	)
	default boolean catherbyHerb() { return true; }

	@ConfigItem(
		keyName = "faladorHerb",
		name = "Falador",
		description = "Include Falador herb patch",
		section = herbPatchesSection
	)
	default boolean faladorHerb() { return true; }

	@ConfigItem(
		keyName = "morytaniaHerb",
		name = "Morytania",
		description = "Include Morytania herb patch",
		section = herbPatchesSection
	)
	default boolean morytaniaHerb() { return true; }

	@ConfigItem(
		keyName = "trollStrongholdHerb",
		name = "Troll Stronghold",
		description = "Include Troll Stronghold herb patch",
		section = herbPatchesSection
	)
	default boolean trollStrongholdHerb() { return true; }

	@ConfigItem(
		keyName = "kourendHerb",
		name = "Kourend",
		description = "Include Kourend herb patch",
		section = herbPatchesSection
	)
	default boolean kourendHerb() { return true; }

	@ConfigItem(
		keyName = "farmingGuildHerb",
		name = "Farming Guild",
		description = "Include Farming Guild herb patch",
		section = herbPatchesSection
	)
	default boolean farmingGuildHerb() { return true; }

	@ConfigItem(
		keyName = "harmonyHerb",
		name = "Harmony Island",
		description = "Include Harmony Island herb patch",
		section = herbPatchesSection
	)
	default boolean harmonyHerb() { return true; }

	@ConfigItem(
		keyName = "weissHerb",
		name = "Weiss",
		description = "Include Weiss herb patch",
		section = herbPatchesSection
	)
	default boolean weissHerb() { return true; }

	// ============================================================================
	// TELEPORT OPTIONS SECTION
	// ============================================================================
	@ConfigSection(
		name = "Teleport Options",
		description = "Select your preferred teleport method for each location",
		position = 3
	)
	String teleportSection = "teleports";

	@ConfigItem(
		keyName = "ardougneTeleport",
		name = "Ardougne Teleport",
		description = "Choose teleport method for Ardougne",
		section = teleportSection
	)
	default ArdougneTeleportOption ardougneTeleport() { return ArdougneTeleportOption.ARDOUGNE_CLOAK; }

	@ConfigItem(
		keyName = "catherbyTeleport",
		name = "Catherby Teleport",
		description = "Choose teleport method for Catherby",
		section = teleportSection
	)
	default CatherbyTeleportOption catherbyTeleport() { return CatherbyTeleportOption.CATHERBY_TELE_TAB; }

	@ConfigItem(
		keyName = "faladorTeleport",
		name = "Falador Teleport",
		description = "Choose teleport method for Falador",
		section = teleportSection
	)
	default FaladorTeleportOption faladorTeleport() { return FaladorTeleportOption.EXPLORERS_RING; }

	@ConfigItem(
		keyName = "morytaniaTeleport",
		name = "Morytania Teleport",
		description = "Choose teleport method for Morytania",
		section = teleportSection
	)
	default MorytaniaTeleportOption morytaniaTeleport() { return MorytaniaTeleportOption.ECTOPHIAL; }

	@ConfigItem(
		keyName = "trollStrongholdTeleport",
		name = "Troll Stronghold Teleport",
		description = "Choose teleport method for Troll Stronghold",
		section = teleportSection
	)
	default TrollStrongholdTeleportOption trollStrongholdTeleport() { return TrollStrongholdTeleportOption.TROLLHEIM_TELEPORT; }

	@ConfigItem(
		keyName = "kourendTeleport",
		name = "Kourend Teleport",
		description = "Choose teleport method for Kourend",
		section = teleportSection
	)
	default KourendTeleportOption kourendTeleport() { return KourendTeleportOption.XERICS_TALISMAN; }

	@ConfigItem(
		keyName = "farmingGuildTeleport",
		name = "Farming Guild Teleport",
		description = "Choose teleport method for Farming Guild",
		section = teleportSection
	)
	default FarmingGuildTeleportOption farmingGuildTeleport() { return FarmingGuildTeleportOption.JEWL_NECKLACE_OF_SKILLS_1; }

	@ConfigItem(
		keyName = "harmonyTeleport",
		name = "Harmony Island Teleport",
		description = "Choose teleport method for Harmony Island",
		section = teleportSection
	)
	default HarmonyTeleportOption harmonyTeleport() { return HarmonyTeleportOption.HARMONY_TELE_TAB; }

	@ConfigItem(
		keyName = "weissTeleport",
		name = "Weiss Teleport",
		description = "Choose teleport method for Weiss",
		section = teleportSection
	)
	default WeissTeleportOption weissTeleport() { return WeissTeleportOption.WEISS_TELEPORT_BASALT; }

	// ============================================================================
	// SUPPLIES SECTION
	// ============================================================================
	@ConfigSection(
		name = "Supplies",
		description = "Configure what farming supplies to include in your runs",
		position = 4
	)
	String suppliesSection = "supplies";

	@ConfigItem(
		keyName = "useCompost",
		name = "Supercompost",
		description = "Include supercompost in farming run supplies (1 per patch)",
		section = suppliesSection
	)
	default boolean useCompost() { return true; }

	@ConfigItem(
		keyName = "useWateringCan",
		name = "Watering Can",
		description = "Include watering can in farming run supplies (for disease prevention)",
		section = suppliesSection
	)
	default boolean useWateringCan() { return true; }

	@ConfigItem(
		keyName = "useSecateurs",
		name = "Secateurs",
		description = "Include secateurs in farming run supplies (for disease prevention)",
		section = suppliesSection
	)
	default boolean useSecateurs() { return true; }

	// ============================================================================
	// DISPLAY OPTIONS SECTION
	// ============================================================================
	@ConfigSection(
		name = "Display Options",
		description = "Configure how the plugin displays information",
		position = 5
	)
	String displaySection = "display";

	@ConfigItem(
		keyName = "showInstructions",
		name = "Show Instructions",
		description = "Display written instructions for each step",
		section = displaySection
	)
	default boolean showInstructions() { return true; }

	@ConfigItem(
		keyName = "showItemCounts",
		name = "Show Item Counts",
		description = "Display remaining item counts in info boxes",
		section = displaySection
	)
	default boolean showItemCounts() { return true; }

	@ConfigItem(
		keyName = "highlightNextAction",
		name = "Highlight Next Action",
		description = "Highlight the next action the player needs to take",
		section = displaySection
	)
	default boolean highlightNextAction() { return true; }

	// ============================================================================
	// TELEPORT OPTION ENUMS
	// ============================================================================

	enum ArdougneTeleportOption
	{
		ARDOUGNE_CLOAK("Ardougne Cloak (Farm teleport)"),
		ARDOUGNE_TELEPORT("Ardougne Teleport Spell"),
		ARDOUGNE_TELE_TAB("Ardougne Teleport Tab"),
		JEWL_NECKLACE_OF_SKILLS_1("Skills Necklace (Fishing Guild)"),
		COMBAT_BRACELET_RANGING("Combat Bracelet (Ranging Guild)"),
		QUEST_POINT_CAPE("Quest Point Cape"),
		FAIRY_RING_BLR("Fairy Ring BLR");

		private final String displayName;

		ArdougneTeleportOption(String displayName)
		{
			this.displayName = displayName;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

	enum CatherbyTeleportOption
	{
		CATHERBY_TELE_TAB("Catherby Teleport Tab"),
		CAMELOT_TELEPORT("Camelot Teleport Spell"),
		CAMELOT_TELE_TAB("Camelot Teleport Tab");

		private final String displayName;

		CatherbyTeleportOption(String displayName)
		{
			this.displayName = displayName;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

	enum FaladorTeleportOption
	{
		EXPLORERS_RING("Explorer's Ring (Farm teleport)"),
		FALADOR_TELEPORT("Falador Teleport Spell"),
		FALADOR_TELE_TAB("Falador Teleport Tab"),
		RING_OF_ELEMENTS_AIR("Ring of Elements (Air Altar)"),
		SPIRIT_TREE_PORT_SARIM("Spirit Tree (Port Sarim)"),
		DRAYNOR_MANOR_TELEPORT("Draynor Manor Teleport"),
		AMULET_OF_GLORY_DRAYNOR("Amulet of Glory (Draynor)"),
		JEWL_NECKLACE_OF_SKILLS_1_MINING("Skills Necklace (Mining Guild)"),
		RING_OF_WEALTH_FALADOR("Ring of Wealth (Falador Park)");

		private final String displayName;

		FaladorTeleportOption(String displayName)
		{
			this.displayName = displayName;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

	enum MorytaniaTeleportOption
	{
		ECTOPHIAL("Ectophial"),
		BURGH_DE_ROTT_TELEPORT("Burgh de Rott Teleport");

		private final String displayName;

		MorytaniaTeleportOption(String displayName)
		{
			this.displayName = displayName;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

	enum TrollStrongholdTeleportOption
	{
		TROLLHEIM_TELEPORT("Trollheim Teleport Spell"),
		STRONGHOLD_TELEPORT_BASALT("Stony Basalt");

		private final String displayName;

		TrollStrongholdTeleportOption(String displayName)
		{
			this.displayName = displayName;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

	enum KourendTeleportOption
	{
		XERICS_TALISMAN("Xeric's Talisman"),
		XERICS_HEART("Xeric's Heart"),
		MOUNTED_XERICS("Mounted Xeric's (POH)");

		private final String displayName;

		KourendTeleportOption(String displayName)
		{
			this.displayName = displayName;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

	enum FarmingGuildTeleportOption
	{
		JEWL_NECKLACE_OF_SKILLS_1("Skills Necklace"),
		SKILLCAPE_FARMING("Farming Cape");

		private final String displayName;

		FarmingGuildTeleportOption(String displayName)
		{
			this.displayName = displayName;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

	enum HarmonyTeleportOption
	{
		HARMONY_TELE_TAB("Harmony Teleport Tab");

		private final String displayName;

		HarmonyTeleportOption(String displayName)
		{
			this.displayName = displayName;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

	enum WeissTeleportOption
	{
		WEISS_TELEPORT_BASALT("Icy Basalt");

		private final String displayName;

		WeissTeleportOption(String displayName)
		{
			this.displayName = displayName;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}
}