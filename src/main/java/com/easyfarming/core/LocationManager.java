package com.easyfarming.core;

import net.runelite.api.gameval.ItemID;
import net.runelite.api.coords.WorldPoint;
import java.util.*;

/**
 * Manages all farming locations and their teleport options
 * Based on the original Farming-Helper plugin structure
 */
public class LocationManager
{
	private final Map<String, Location> locations;
	
	public LocationManager()
	{
		this.locations = new HashMap<>();
		initializeLocations();
	}
	
	/**
	 * Initialize all farming locations with their teleport options
	 */
	private void initializeLocations()
	{
		// Ardougne Herb Patch
		Location ardougne = new Location("ardougne", new WorldPoint(2670, 3374, 0), false);
		
		// Ardougne Cloak (Farm teleport) - any tier 2+
		ardougne.addTeleportOption(new Teleport(
			"ARDOUGNE_CLOAK", Teleport.TeleportCategory.ITEM,
			"Use Ardougne cloak Farm teleport to go directly to the herb patch",
			ItemID.ARDY_CAPE_MEDIUM, "Farm Teleport", 0,
			new WorldPoint(2667, 3375, 0),
			Arrays.asList(new ItemRequirement(ItemID.ARDY_CAPE_MEDIUM, 1, true))
		));
		
		// Ardougne Teleport Spell
		ardougne.addTeleportOption(new Teleport(
			"ARDOUGNE_TELEPORT", Teleport.TeleportCategory.SPELLBOOK,
			"Cast Ardougne Teleport, then run north to the herb patch",
			0, null, 30, // Spell ID 30 for Ardougne Teleport
			new WorldPoint(2662, 3305, 0),
			Arrays.asList(
				new ItemRequirement(ItemID.LAWRUNE, 2),
				new ItemRequirement(ItemID.WATERRUNE, 2)
			)
		));
		
		// Ardougne Teleport Tab
		ardougne.addTeleportOption(new Teleport(
			"ARDOUGNE_TELE_TAB", Teleport.TeleportCategory.ITEM,
			"Use Ardougne teleport tab, then run north to the herb patch",
			ItemID._51_ARDOUGNE_TELEPORT, null, 0,
			new WorldPoint(2662, 3305, 0),
			Arrays.asList(new ItemRequirement(ItemID._51_ARDOUGNE_TELEPORT, 1))
		));
		
		// Skills Necklace (Fishing Guild)
		ardougne.addTeleportOption(new Teleport(
			"JEWL_NECKLACE_OF_SKILLS_1_FISHING", Teleport.TeleportCategory.ITEM,
			"Use Skills necklace to Fishing Guild, then run north to the herb patch",
			ItemID.JEWL_NECKLACE_OF_SKILLS_1, "Fishing Guild", 0,
			new WorldPoint(2611, 3391, 0),
			Arrays.asList(new ItemRequirement(ItemID.JEWL_NECKLACE_OF_SKILLS_1, 1))
		));
		
		// Combat Bracelet (Ranging Guild)
		ardougne.addTeleportOption(new Teleport(
			"COMBAT_BRACELET_RANGING", Teleport.TeleportCategory.ITEM,
			"Use Combat bracelet to Ranging Guild, then run north to the herb patch",
			ItemID.JEWL_BRACELET_OF_COMBAT_1, "Ranging Guild", 0,
			new WorldPoint(2657, 3439, 0),
			Arrays.asList(new ItemRequirement(ItemID.JEWL_BRACELET_OF_COMBAT_1, 1))
		));
		
		// Quest Point Cape
		ardougne.addTeleportOption(new Teleport(
			"QUEST_POINT_CAPE", Teleport.TeleportCategory.ITEM,
			"Use Quest point cape teleport, then run north to the herb patch",
			ItemID.SKILLCAPE_QP, null, 0,
			new WorldPoint(2662, 3305, 0),
			Arrays.asList(new ItemRequirement(ItemID.SKILLCAPE_QP, 1))
		));
		
		// Fairy Ring BLR
		List<Integer> fairyRingStaffs = Arrays.asList(ItemID.DRAMEN_STAFF, ItemID.LUNAR_MOONCLAN_LIMINAL_STAFF);
		ardougne.addTeleportOption(new Teleport(
			"FAIRY_RING_BLR", Teleport.TeleportCategory.ITEM,
			"Use Fairy ring code BLR, then run north to the herb patch",
			ItemID.DRAMEN_STAFF, null, 0,
			new WorldPoint(2650, 3230, 0),
			Arrays.asList(new ItemRequirement(fairyRingStaffs, 1, true))
		));
		
		locations.put("ardougne", ardougne);
		
		// Catherby Herb Patch
		Location catherby = new Location("catherby", new WorldPoint(2813, 3463, 0), false);
		
		// Catherby Teleport Tab
		catherby.addTeleportOption(new Teleport(
			"CATHERBY_TELE_TAB", Teleport.TeleportCategory.ITEM,
			"Use Catherby teleport tab to go directly to the herb patch",
			ItemID.LUNAR_TABLET_CATHERBY_TELEPORT, null, 0,
			new WorldPoint(2808, 3451, 0),
			Arrays.asList(new ItemRequirement(ItemID.LUNAR_TABLET_CATHERBY_TELEPORT, 1))
		));
		
		// Camelot Teleport Spell
		catherby.addTeleportOption(new Teleport(
			"CAMELOT_TELEPORT", Teleport.TeleportCategory.SPELLBOOK,
			"Cast Camelot Teleport, then run south to the herb patch",
			0, null, 29, // Spell ID 29 for Camelot Teleport
			new WorldPoint(2757, 3478, 0),
			Arrays.asList(
				new ItemRequirement(ItemID.LAWRUNE, 1),
				new ItemRequirement(ItemID.AIRRUNE, 5)
			)
		));
		
		// Camelot Teleport Tab
		catherby.addTeleportOption(new Teleport(
			"CAMELOT_TELE_TAB", Teleport.TeleportCategory.ITEM,
			"Use Camelot teleport tab, then run south to the herb patch",
			ItemID.POH_TABLET_CAMELOTTELEPORT, null, 0,
			new WorldPoint(2757, 3478, 0),
			Arrays.asList(new ItemRequirement(ItemID.POH_TABLET_CAMELOTTELEPORT, 1))
		));
		
		locations.put("catherby", catherby);
		
		// Falador Herb Patch
		Location falador = new Location("falador", new WorldPoint(3058, 3311, 0), false);
		
		// Explorer's Ring (Farm teleport) - any tier 2+
		falador.addTeleportOption(new Teleport(
			"EXPLORERS_RING", Teleport.TeleportCategory.ITEM,
			"Use Explorer's ring teleport to go directly to the Falador herb patch",
			ItemID.LUMBRIDGE_RING_MEDIUM, "Teleport", 0,
			new WorldPoint(3055, 3308, 0),
			Arrays.asList(new ItemRequirement(ItemID.LUMBRIDGE_RING_MEDIUM, 1, true))
		));
		
		// Falador Teleport Spell
		falador.addTeleportOption(new Teleport(
			"FALADOR_TELEPORT", Teleport.TeleportCategory.SPELLBOOK,
			"Cast Falador Teleport, then run south to the herb patch",
			0, null, 28, // Spell ID 28 for Falador Teleport
			new WorldPoint(2966, 3403, 0),
			Arrays.asList(
				new ItemRequirement(ItemID.LAWRUNE, 1),
				new ItemRequirement(ItemID.AIRRUNE, 3),
				new ItemRequirement(ItemID.WATERRUNE, 1)
			)
		));
		
		// Falador Teleport Tab
		falador.addTeleportOption(new Teleport(
			"FALADOR_TELE_TAB", Teleport.TeleportCategory.ITEM,
			"Use Falador teleport tab, then run south to the herb patch",
			ItemID.POH_TABLET_FALADORTELEPORT, null, 0,
			new WorldPoint(2966, 3403, 0),
			Arrays.asList(new ItemRequirement(ItemID.POH_TABLET_FALADORTELEPORT, 1))
		));
		
		// Ring of Elements (Air Altar)
		falador.addTeleportOption(new Teleport(
			"RING_OF_ELEMENTS_AIR", Teleport.TeleportCategory.ITEM,
			"Use Ring of the elements to Air Altar, then run south to the herb patch",
			ItemID.RING_OF_ELEMENTS, null, 0,
			new WorldPoint(2983, 3296, 0),
			Arrays.asList(new ItemRequirement(ItemID.RING_OF_ELEMENTS, 1))
		));
		
		// Spirit Tree (Port Sarim)
		falador.addTeleportOption(new Teleport(
			"SPIRIT_TREE_PORT_SARIM", Teleport.TeleportCategory.SPIRIT_TREE,
			"Use Spirit tree to Port Sarim, then run north to the herb patch",
			0, null, 0,
			new WorldPoint(3054, 3256, 0),
			Arrays.asList() // No items needed for spirit tree teleports
		));
		
		// Draynor Manor Teleport
		falador.addTeleportOption(new Teleport(
			"DRAYNOR_MANOR_TELEPORT", Teleport.TeleportCategory.SPELLBOOK,
			"Cast Draynor Manor Teleport, then run north to the herb patch",
			0, null, 0, // Custom spell ID needed
			new WorldPoint(3108, 3350, 0),
			Arrays.asList(
				new ItemRequirement(ItemID.AIRRUNE, 1),
				new ItemRequirement(ItemID.LAWRUNE, 1),
				new ItemRequirement(ItemID.EARTHRUNE, 1)
			)
		));
		
		// Amulet of Glory (Draynor)
		falador.addTeleportOption(new Teleport(
			"AMULET_OF_GLORY_DRAYNOR", Teleport.TeleportCategory.ITEM,
			"Use Amulet of glory to Draynor Village, then run north to the herb patch",
			ItemID.AMULET_OF_GLORY, "Draynor Village", 0,
			new WorldPoint(3105, 3251, 0),
			Arrays.asList(new ItemRequirement(ItemID.AMULET_OF_GLORY, 1))
		));
		
		// Skills Necklace (Mining Guild)
		falador.addTeleportOption(new Teleport(
			"JEWL_NECKLACE_OF_SKILLS_1_MINING", Teleport.TeleportCategory.ITEM,
			"Use Skills necklace to Mining Guild, then run north to the herb patch",
			ItemID.JEWL_NECKLACE_OF_SKILLS_1, "Mining Guild", 0,
			new WorldPoint(3046, 9756, 0),
			Arrays.asList(new ItemRequirement(ItemID.JEWL_NECKLACE_OF_SKILLS_1, 1))
		));
		
		// Ring of Wealth (Falador Park)
		falador.addTeleportOption(new Teleport(
			"RING_OF_WEALTH_FALADOR", Teleport.TeleportCategory.ITEM,
			"Use Ring of Wealth to Falador Park, then run south to the herb patch",
			ItemID.RING_OF_WEALTH, "Falador Park", 0,
			new WorldPoint(2994, 3375, 0),
			Arrays.asList(new ItemRequirement(ItemID.RING_OF_WEALTH, 1))
		));
		
		locations.put("falador", falador);
		
		// Morytania Herb Patch
		Location morytania = new Location("morytania", new WorldPoint(3601, 3525, 0), false);
		
		// Ectophial
		morytania.addTeleportOption(new Teleport(
			"ECTOPHIAL", Teleport.TeleportCategory.ITEM,
			"Use Ectophial to go directly to the herb patch",
			ItemID.ECTOPHIAL, null, 0,
			new WorldPoint(3659, 3524, 0),
			Arrays.asList(new ItemRequirement(ItemID.ECTOPHIAL, 1))
		));
		
		// Burgh de Rott Teleport
		morytania.addTeleportOption(new Teleport(
			"BURGH_DE_ROTT_TELEPORT", Teleport.TeleportCategory.SPELLBOOK,
			"Cast Burgh de Rott Teleport, then run north to the herb patch",
			0, null, 0, // Custom spell ID needed
			new WorldPoint(3488, 3181, 0),
			Arrays.asList(
				new ItemRequirement(ItemID.LAWRUNE, 2),
				new ItemRequirement(ItemID.SOULRUNE, 2),
				new ItemRequirement(ItemID.EARTHRUNE, 2)
			)
		));
		
		locations.put("morytania", morytania);
		
		// Troll Stronghold Herb Patch
		Location trollStronghold = new Location("trollStronghold", new WorldPoint(2820, 3694, 0), false);
		
		// Trollheim Teleport Spell
		trollStronghold.addTeleportOption(new Teleport(
			"TROLLHEIM_TELEPORT", Teleport.TeleportCategory.SPELLBOOK,
			"Cast Trollheim Teleport, then run north to the herb patch",
			0, null, 31, // Spell ID 31 for Trollheim Teleport
			new WorldPoint(2893, 3678, 0),
			Arrays.asList(
				new ItemRequirement(ItemID.LAWRUNE, 2),
				new ItemRequirement(ItemID.FIRERUNE, 2)
			)
		));
		
		// Stony Basalt
		trollStronghold.addTeleportOption(new Teleport(
			"STRONGHOLD_TELEPORT_BASALT", Teleport.TeleportCategory.ITEM,
			"Use Stony basalt to go directly to the herb patch",
			ItemID.STRONGHOLD_TELEPORT_BASALT, null, 0,
			new WorldPoint(2820, 3694, 0),
			Arrays.asList(new ItemRequirement(ItemID.STRONGHOLD_TELEPORT_BASALT, 1))
		));
		
		locations.put("trollStronghold", trollStronghold);
		
		// Kourend Herb Patch
		Location kourend = new Location("kourend", new WorldPoint(1739, 3550, 0), false);
		
		// Xeric's Talisman
		kourend.addTeleportOption(new Teleport(
			"XERICS_TALISMAN", Teleport.TeleportCategory.ITEM,
			"Teleport to Hosidius with Xeric's talisman.",
			ItemID.XERIC_TALISMAN, null, 0,
			new WorldPoint(1739, 3550, 0),
			Arrays.asList(new ItemRequirement(ItemID.XERIC_TALISMAN, 1))
		));
		
		// Mounted Xeric's (POH)
		kourend.addTeleportOption(new Teleport(
			"MOUNTED_XERICS", Teleport.TeleportCategory.PORTAL_NEXUS,
			"Use Mounted Xeric's talisman in your POH, then run to the herb patch",
			0, null, 0,
			new WorldPoint(1739, 3550, 0),
			Arrays.asList(new ItemRequirement(0, 0)) // Requires 75 Construction and built furniture
		));
		
		locations.put("kourend", kourend);
		
		// Farming Guild Herb Patch
		Location farmingGuild = new Location("farmingGuild", new WorldPoint(1249, 3719, 0), false);
		
		// Skills Necklace
		farmingGuild.addTeleportOption(new Teleport(
			"JEWL_NECKLACE_OF_SKILLS_1", Teleport.TeleportCategory.ITEM,
			"Use Skills necklace to Farming Guild, then run to the herb patch",
			ItemID.JEWL_NECKLACE_OF_SKILLS_1, "Farming Guild", 0,
			new WorldPoint(1248, 3721, 0),
			Arrays.asList(new ItemRequirement(ItemID.JEWL_NECKLACE_OF_SKILLS_1, 1))
		));
		
		// Farming Cape
		farmingGuild.addTeleportOption(new Teleport(
			"SKILLCAPE_FARMING", Teleport.TeleportCategory.ITEM,
			"Use Farming cape to go directly to the herb patch",
			ItemID.SKILLCAPE_FARMING, null, 0,
			new WorldPoint(1249, 3719, 0),
			Arrays.asList(new ItemRequirement(ItemID.SKILLCAPE_FARMING, 1))
		));
		
		locations.put("farmingGuild", farmingGuild);
		
		// Harmony Island Herb Patch
		Location harmony = new Location("harmony", new WorldPoint(3784, 2838, 0), false);
		
		// Harmony Teleport Tab
		harmony.addTeleportOption(new Teleport(
			"HARMONY_TELE_TAB", Teleport.TeleportCategory.ITEM,
			"Use Harmony teleport tab to go directly to the herb patch",
			ItemID.TELETAB_HARMONY, null, 0,
			new WorldPoint(3784, 2838, 0),
			Arrays.asList(new ItemRequirement(ItemID.TELETAB_HARMONY, 1))
		));
		
		locations.put("harmony", harmony);
		
		// Weiss Herb Patch
		Location weiss = new Location("weiss", new WorldPoint(2849, 3932, 0), false);
		
		// Icy Basalt
		weiss.addTeleportOption(new Teleport(
			"WEISS_TELEPORT_BASALT", Teleport.TeleportCategory.ITEM,
			"Use Icy basalt to go directly to the herb patch",
			ItemID.WEISS_TELEPORT_BASALT, null, 0,
			new WorldPoint(2849, 3932, 0),
			Arrays.asList(new ItemRequirement(ItemID.WEISS_TELEPORT_BASALT, 1))
		));
		
		locations.put("weiss", weiss);
	}
	
	/**
	 * Get a location by its ID
	 */
	public Location getLocation(String locationId)
	{
		return locations.get(locationId);
	}
	
	/**
	 * Get all locations
	 */
	public Map<String, Location> getAllLocations()
	{
		return new HashMap<>(locations);
	}
	
	/**
	 * Get all enabled locations
	 * TODO: Implement proper filtering logic based on:
	 * - Quest completion requirements
	 * - Skill level requirements  
	 * - User configuration preferences
	 * - Location-specific unlock conditions
	 */
	public List<Location> getEnabledLocations()
	{
		// TODO: Replace with actual filtering logic
		// For now, return all locations as a placeholder
		// This should be updated to check:
		// - Quest completion status (e.g., Recipe for Disaster for Trollheim)
		// - Skill requirements (e.g., Construction level for POH teleports)
		// - User config settings for which locations to include
		// - Diary completion status for enhanced teleports
		return new ArrayList<>(locations.values());
	}
	
	/**
	 * Get the selected teleport option for a location based on config
	 */
	public Teleport getSelectedTeleportOption(String locationId, String selectedOptionName)
	{
		Location location = locations.get(locationId);
		if (location == null)
		{
			return null;
		}
		
		return location.getTeleportOptions().stream()
			.filter(teleport -> teleport.getName().equals(selectedOptionName))
			.findFirst()
			.orElse(null);
	}
	
	/**
	 * Get all herb locations
	 */
	public List<Location> getHerbLocations()
	{
		List<Location> herbLocations = new ArrayList<>();
		// Add all herb patch locations
		String[] herbLocationIds = {"ardougne", "catherby", "falador", "morytania", 
								   "trollStronghold", "kourend", "farmingGuild", 
								   "harmonyIsland", "weiss"};
		
		for (String locationId : herbLocationIds)
		{
			Location location = locations.get(locationId);
			if (location != null)
			{
				herbLocations.add(location);
			}
		}
		
		return herbLocations;
	}
	
	/**
	 * Get all tree locations
	 */
	public List<Location> getTreeLocations()
	{
		// TODO: Implement tree locations when tree farming is added
		return new ArrayList<>();
	}
	
	/**
	 * Get all fruit tree locations
	 */
	public List<Location> getFruitTreeLocations()
	{
		// TODO: Implement fruit tree locations when fruit tree farming is added
		return new ArrayList<>();
	}
	
	/**
	 * Get all allotment locations
	 */
	public List<Location> getAllotmentLocations()
	{
		// TODO: Implement allotment locations when allotment farming is added
		return new ArrayList<>();
	}
}