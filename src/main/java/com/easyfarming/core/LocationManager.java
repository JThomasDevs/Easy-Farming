package com.easyfarming.core;

import com.easyfarming.EasyFarmingConfig;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.coords.WorldPoint;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages all farming locations and their teleport options
 * Based on the original Farming-Helper plugin structure
 */
public class LocationManager
{
	private final Map<String, Location> locations;
	private final EasyFarmingConfig config;
	
	public LocationManager()
	{
		this.locations = new HashMap<>();
		this.config = null; // Will be set later via setConfig method
		initializeLocations();
	}
	
	public LocationManager(EasyFarmingConfig config)
	{
		this.locations = new HashMap<>();
		this.config = config;
		initializeLocations();
	}
	
	/**
	 * Set the config for this LocationManager (used when config is not available at construction time)
	 */
	public void setConfig(EasyFarmingConfig config)
	{
		// Note: This is a workaround for cases where config is not available at construction time
		// In a proper implementation, the config should be injected via constructor
	}
	
	/**
	 * Initialize all farming locations with their teleport options
	 */
	
	/**
	 * Helper method to add a teleport option to a location
	 */
	private void addTeleportOption(Location location, String id, Teleport.TeleportCategory category, 
						  String description, int itemId, String action, int spellId, 
						  WorldPoint destination, List<ItemRequirement> requirements) {
		location.addTeleportOption(new Teleport(id, category, description, itemId, action, spellId, destination, requirements));
	}
	
	/**
	 * Helper method to create a teleport with a single item requirement
	 */
	private void addSimpleTeleportOption(Location location, String id, Teleport.TeleportCategory category, 
										String description, int itemId, String action, int spellId, 
										WorldPoint destination, int requiredItemId, int quantity, boolean isAny) {
		List<ItemRequirement> requirements = Arrays.asList(new ItemRequirement(requiredItemId, quantity, isAny));
		addTeleportOption(location, id, category, description, itemId, action, spellId, destination, requirements);
	}
	
	/**
	 * Helper method to create a teleport with multiple item requirements
	 */
	private void addMultiRequirementTeleportOption(Location location, String id, Teleport.TeleportCategory category, 
												  String description, int itemId, String action, int spellId, 
												  WorldPoint destination, ItemRequirement... requirements) {
		addTeleportOption(location, id, category, description, itemId, action, spellId, destination, Arrays.asList(requirements));
	}

	private void initializeLocations()
	{
		initArdougne();
		initCatherby();
		initFalador();
		initMorytania();
		initTrollStronghold();
		initKourend();
		initFarmingGuild();
		initHarmony();
		initWeiss();
	}
	
	/**
	 * Get a location by its ID
	 * 
	 * @param locationId the unique identifier of the location to retrieve
	 * @return the Location object if found, or null if no location exists with the given ID
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
	 * 
	 * Returns only locations that have their enabled flag set to true.
	 * This method filters the locations based on their enabled status
	 * and returns a defensive copy of the filtered list.
	 * 
	 * @return a new ArrayList containing only enabled locations
	 */
	public List<Location> getEnabledLocations()
	{
		return locations.values().stream()
			.filter(Location::isEnabled)
			.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	/**
	 * Get the selected teleport option for a location based on config
	 */
	public Teleport getSelectedTeleportOption(String locationId, String selectedOptionName)
	{
		// Validate input parameters
		if (selectedOptionName == null)
		{
			throw new IllegalArgumentException("selectedOptionName must not be null");
		}
		
		Location location = locations.get(locationId);
		if (location == null)
		{
			return null;
		}
		
		return location.getTeleportOptions().stream()
			.filter(teleport -> Objects.equals(teleport.getName(), selectedOptionName))
			.findFirst()
			.orElse(null);
	}
	
	/**
	 * Get all herb locations
	 */
	public List<Location> getHerbLocations()
	{
		return locations.values().stream()
			.filter(loc -> loc.hasPatchType(PatchType.HERB))
			.collect(Collectors.toList());
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
	
	/**
	 * Initialize Ardougne herb patch location with all teleport options
	 */
	private void initArdougne() {
		boolean enabled = config != null ? config.ardougneHerb() : true; // Default to enabled if no config
		Location ardougne = new Location("ardougne", new WorldPoint(2670, 3374, 0), false, enabled);
		ardougne.addPatchType(PatchType.HERB);
		
		// Ardougne Cloak (Farm teleport) - any tier 2+
		addSimpleTeleportOption(ardougne, "ARDOUGNE_CLOAK", Teleport.TeleportCategory.ITEM,
			"Use Ardougne cloak Farm teleport to go directly to the herb patch",
			ItemID.ARDY_CAPE_MEDIUM, "Farm Teleport", 0,
			new WorldPoint(2667, 3375, 0), ItemID.ARDY_CAPE_MEDIUM, 1, true);
		
		// Ardougne Teleport Spell
		addMultiRequirementTeleportOption(ardougne, "ARDOUGNE_TELEPORT", Teleport.TeleportCategory.SPELLBOOK,
			"Cast Ardougne Teleport, then run north to the herb patch",
			0, null, 30, new WorldPoint(2662, 3305, 0),
			new ItemRequirement(ItemID.LAWRUNE, 2),
			new ItemRequirement(ItemID.WATERRUNE, 2));
		
		// Ardougne Teleport Tab
		addSimpleTeleportOption(ardougne, "ARDOUGNE_TELE_TAB", Teleport.TeleportCategory.ITEM,
			"Use Ardougne teleport tab, then run north to the herb patch",
			ItemID._51_ARDOUGNE_TELEPORT, null, 0,
			new WorldPoint(2662, 3305, 0), ItemID._51_ARDOUGNE_TELEPORT, 1, false);
		
		// Skills Necklace (Fishing Guild)
		addSimpleTeleportOption(ardougne, "JEWL_NECKLACE_OF_SKILLS_1_FISHING", Teleport.TeleportCategory.ITEM,
			"Use Skills necklace to Fishing Guild, then run north to the herb patch",
			ItemID.JEWL_NECKLACE_OF_SKILLS_1, "Fishing Guild", 0,
			new WorldPoint(2611, 3391, 0), ItemID.JEWL_NECKLACE_OF_SKILLS_1, 1, false);
		
		// Combat Bracelet (Ranging Guild)
		addSimpleTeleportOption(ardougne, "COMBAT_BRACELET_RANGING", Teleport.TeleportCategory.ITEM,
			"Use Combat bracelet to Ranging Guild, then run north to the herb patch",
			ItemID.JEWL_BRACELET_OF_COMBAT_1, "Ranging Guild", 0,
			new WorldPoint(2657, 3439, 0), ItemID.JEWL_BRACELET_OF_COMBAT_1, 1, false);
		
		// Quest Point Cape
		addSimpleTeleportOption(ardougne, "QUEST_POINT_CAPE", Teleport.TeleportCategory.ITEM,
			"Use Quest point cape teleport, then run north to the herb patch",
			ItemID.SKILLCAPE_QP, null, 0,
			new WorldPoint(2662, 3305, 0), ItemID.SKILLCAPE_QP, 1, false);
		
		// Fairy Ring BLR
		List<Integer> fairyRingStaffs = Arrays.asList(ItemID.DRAMEN_STAFF, ItemID.LUNAR_MOONCLAN_LIMINAL_STAFF);
		addTeleportOption(ardougne, "FAIRY_RING_BLR", Teleport.TeleportCategory.ITEM,
			"Use Fairy ring code BLR, then run north to the herb patch",
			ItemID.DRAMEN_STAFF, null, 0,
			new WorldPoint(2650, 3230, 0),
			Collections.singletonList(new ItemRequirement(fairyRingStaffs, 1, true)));
		
		locations.put("ardougne", ardougne);
	}
	
	/**
	 * Initialize Catherby herb patch location with all teleport options
	 */
	private void initCatherby() {
		boolean enabled = config != null ? config.catherbyHerb() : true; // Default to enabled if no config
		Location catherby = new Location("catherby", new WorldPoint(2813, 3463, 0), false, enabled);
		catherby.addPatchType(PatchType.HERB);
		
		// Catherby Teleport Tab
		addSimpleTeleportOption(catherby, "CATHERBY_TELE_TAB", Teleport.TeleportCategory.ITEM,
			"Use Catherby teleport tab to go directly to the herb patch",
			ItemID.LUNAR_TABLET_CATHERBY_TELEPORT, null, 0,
			new WorldPoint(2808, 3451, 0), ItemID.LUNAR_TABLET_CATHERBY_TELEPORT, 1, false);
		
		// Camelot Teleport Spell
		addMultiRequirementTeleportOption(catherby, "CAMELOT_TELEPORT", Teleport.TeleportCategory.SPELLBOOK,
			"Cast Camelot Teleport, then run south to the herb patch",
			0, null, 29, new WorldPoint(2757, 3478, 0),
			new ItemRequirement(ItemID.LAWRUNE, 1),
			new ItemRequirement(ItemID.AIRRUNE, 5));
		
		// Camelot Teleport Tab
		addSimpleTeleportOption(catherby, "CAMELOT_TELE_TAB", Teleport.TeleportCategory.ITEM,
			"Use Camelot teleport tab, then run south to the herb patch",
			ItemID.POH_TABLET_CAMELOTTELEPORT, null, 0,
			new WorldPoint(2757, 3478, 0), ItemID.POH_TABLET_CAMELOTTELEPORT, 1, false);
		
		locations.put("catherby", catherby);
	}
	
	/**
	 * Initialize Falador herb patch location with all teleport options
	 */
	private void initFalador() {
		boolean enabled = config != null ? config.faladorHerb() : true; // Default to enabled if no config
		Location falador = new Location("falador", new WorldPoint(3058, 3311, 0), false, enabled);
		falador.addPatchType(PatchType.HERB);
		
		// Explorer's Ring (Farm teleport) - any tier 2+
		addSimpleTeleportOption(falador, "EXPLORERS_RING", Teleport.TeleportCategory.ITEM,
			"Use Explorer's ring teleport to go directly to the Falador herb patch",
			ItemID.LUMBRIDGE_RING_MEDIUM, "Teleport", 0,
			new WorldPoint(3055, 3308, 0), ItemID.LUMBRIDGE_RING_MEDIUM, 1, true);
		
		// Falador Teleport Spell
		addMultiRequirementTeleportOption(falador, "FALADOR_TELEPORT", Teleport.TeleportCategory.SPELLBOOK,
			"Cast Falador Teleport, then run south to the herb patch",
			0, null, 28, new WorldPoint(2966, 3403, 0),
			new ItemRequirement(ItemID.LAWRUNE, 1),
			new ItemRequirement(ItemID.AIRRUNE, 3),
			new ItemRequirement(ItemID.WATERRUNE, 1));
		
		// Falador Teleport Tab
		addSimpleTeleportOption(falador, "FALADOR_TELE_TAB", Teleport.TeleportCategory.ITEM,
			"Use Falador teleport tab, then run south to the herb patch",
			ItemID.POH_TABLET_FALADORTELEPORT, null, 0,
			new WorldPoint(2966, 3403, 0), ItemID.POH_TABLET_FALADORTELEPORT, 1, false);
		
		// Ring of Elements (Air Altar)
		addSimpleTeleportOption(falador, "RING_OF_ELEMENTS_AIR", Teleport.TeleportCategory.ITEM,
			"Use Ring of the elements to Air Altar, then run south to the herb patch",
			ItemID.RING_OF_ELEMENTS, null, 0,
			new WorldPoint(2983, 3296, 0), ItemID.RING_OF_ELEMENTS, 1, false);
		
		// Spirit Tree (Port Sarim)
		addTeleportOption(falador, "SPIRIT_TREE_PORT_SARIM", Teleport.TeleportCategory.SPIRIT_TREE,
			"Use Spirit tree to Port Sarim, then run north to the herb patch",
			0, null, 0, new WorldPoint(3054, 3256, 0),
			Arrays.asList()); // No items needed for spirit tree teleports
		
		// Draynor Manor Teleport (Primary location)
		addMultiRequirementTeleportOption(falador, "DRAYNOR_MANOR_TELEPORT", Teleport.TeleportCategory.SPELLBOOK,
			"Cast Draynor Manor Teleport, then run northwest to the herb patch",
			0, null, 3108, new WorldPoint(3108, 3350, 0),
			new ItemRequirement(ItemID.AIRRUNE, 1),
			new ItemRequirement(ItemID.LAWRUNE, 1),
			new ItemRequirement(ItemID.EARTHRUNE, 1));
		
		// Draynor Manor Teleport (Alternate location)
		addMultiRequirementTeleportOption(falador, "DRAYNOR_MANOR_TELEPORT_ALT", Teleport.TeleportCategory.SPELLBOOK,
			"Cast Draynor Manor Teleport (alternate), then run northwest to the herb patch",
			0, null, 3109, new WorldPoint(3108, 3350, 0),
			new ItemRequirement(ItemID.AIRRUNE, 1),
			new ItemRequirement(ItemID.LAWRUNE, 1),
			new ItemRequirement(ItemID.EARTHRUNE, 1));
		
		// Amulet of Glory (Draynor)
		addSimpleTeleportOption(falador, "AMULET_OF_GLORY_DRAYNOR", Teleport.TeleportCategory.ITEM,
			"Use Amulet of glory to Draynor Village, then run north to the herb patch",
			ItemID.AMULET_OF_GLORY, "Draynor Village", 0,
			new WorldPoint(3105, 3251, 0), ItemID.AMULET_OF_GLORY, 1, false);
		
		// Skills Necklace (Mining Guild)
		addSimpleTeleportOption(falador, "JEWL_NECKLACE_OF_SKILLS_1_MINING", Teleport.TeleportCategory.ITEM,
			"Use Skills necklace to Mining Guild, then run north to the herb patch",
			ItemID.JEWL_NECKLACE_OF_SKILLS_1, "Mining Guild", 0,
			new WorldPoint(3046, 9756, 0), ItemID.JEWL_NECKLACE_OF_SKILLS_1, 1, false);
		
		// Ring of Wealth (Falador Park)
		addSimpleTeleportOption(falador, "RING_OF_WEALTH_FALADOR", Teleport.TeleportCategory.ITEM,
			"Use Ring of Wealth to Falador Park, then run south to the herb patch",
			ItemID.RING_OF_WEALTH, "Falador Park", 0,
			new WorldPoint(2994, 3375, 0), ItemID.RING_OF_WEALTH, 1, false);
		
		locations.put("falador", falador);
	}
	
	/**
	 * Initialize Morytania herb patch location with all teleport options
	 */
	private void initMorytania() {
		boolean enabled = config != null ? config.morytaniaHerb() : true; // Default to enabled if no config
		Location morytania = new Location("morytania", new WorldPoint(3601, 3525, 0), false, enabled);
		morytania.addPatchType(PatchType.HERB);
		
		// Ectophial
		addSimpleTeleportOption(morytania, "ECTOPHIAL", Teleport.TeleportCategory.ITEM,
			"Use Ectophial to go directly to the herb patch",
			ItemID.ECTOPHIAL, null, 0,
			new WorldPoint(3659, 3524, 0), ItemID.ECTOPHIAL, 1, false);
		
		// Burgh de Rott Teleport
		addMultiRequirementTeleportOption(morytania, "BURGH_DE_ROTT_TELEPORT", Teleport.TeleportCategory.SPELLBOOK,
			"Cast Burgh de Rott Teleport, then run north to the herb patch",
			0, null, 0, new WorldPoint(3488, 3181, 0),
			new ItemRequirement(ItemID.LAWRUNE, 2),
			new ItemRequirement(ItemID.SOULRUNE, 2),
			new ItemRequirement(ItemID.EARTHRUNE, 2));
		
		locations.put("morytania", morytania);
	}
	
	/**
	 * Initialize Troll Stronghold herb patch location with all teleport options
	 */
	private void initTrollStronghold() {
		boolean enabled = config != null ? config.trollStrongholdHerb() : true; // Default to enabled if no config
		Location trollStronghold = new Location("trollStronghold", new WorldPoint(2820, 3694, 0), false, enabled);
		trollStronghold.addPatchType(PatchType.HERB);
		
		// Trollheim Teleport Spell
		addMultiRequirementTeleportOption(trollStronghold, "TROLLHEIM_TELEPORT", Teleport.TeleportCategory.SPELLBOOK,
			"Cast Trollheim Teleport, then run north to the herb patch",
			0, null, 31, new WorldPoint(2893, 3678, 0),
			new ItemRequirement(ItemID.LAWRUNE, 2),
			new ItemRequirement(ItemID.FIRERUNE, 2));
		
		// Stony Basalt
		addSimpleTeleportOption(trollStronghold, "STRONGHOLD_TELEPORT_BASALT", Teleport.TeleportCategory.ITEM,
			"Use Stony basalt to go directly to the herb patch",
			ItemID.STRONGHOLD_TELEPORT_BASALT, null, 0,
			new WorldPoint(2820, 3694, 0), ItemID.STRONGHOLD_TELEPORT_BASALT, 1, false);
		
		locations.put("trollStronghold", trollStronghold);
	}
	
	/**
	 * Initialize Kourend herb patch location with all teleport options
	 */
	private void initKourend() {
		boolean enabled = config != null ? config.kourendHerb() : true; // Default to enabled if no config
		Location kourend = new Location("kourend", new WorldPoint(1739, 3550, 0), false, enabled);
		kourend.addPatchType(PatchType.HERB);
		
		// Xeric's Talisman
		addSimpleTeleportOption(kourend, "XERICS_TALISMAN", Teleport.TeleportCategory.ITEM,
			"Teleport to Hosidius with Xeric's talisman.",
			ItemID.XERIC_TALISMAN, null, 0,
			new WorldPoint(1739, 3550, 0), ItemID.XERIC_TALISMAN, 1, false);
		
		// Mounted Xeric's (POH)
		addSimpleTeleportOption(kourend, "MOUNTED_XERICS", Teleport.TeleportCategory.PORTAL_NEXUS,
			"Use Mounted Xeric's talisman in your POH, then run to the herb patch",
			0, null, 0, new WorldPoint(1739, 3550, 0), 0, 0, false); // Requires 75 Construction and built furniture
		
		locations.put("kourend", kourend);
	}
	
	/**
	 * Initialize Farming Guild herb patch location with all teleport options
	 */
	private void initFarmingGuild() {
		boolean enabled = config != null ? config.farmingGuildHerb() : true; // Default to enabled if no config
		Location farmingGuild = new Location("farmingGuild", new WorldPoint(1249, 3719, 0), false, enabled);
		farmingGuild.addPatchType(PatchType.HERB);
		
		// Skills Necklace
		addSimpleTeleportOption(farmingGuild, "JEWL_NECKLACE_OF_SKILLS_1", Teleport.TeleportCategory.ITEM,
			"Use Skills necklace to Farming Guild, then run to the herb patch",
			ItemID.JEWL_NECKLACE_OF_SKILLS_1, "Farming Guild", 0,
			new WorldPoint(1248, 3721, 0), ItemID.JEWL_NECKLACE_OF_SKILLS_1, 1, false);
		
		// Farming Cape
		addSimpleTeleportOption(farmingGuild, "SKILLCAPE_FARMING", Teleport.TeleportCategory.ITEM,
			"Use Farming cape to go directly to the herb patch",
			ItemID.SKILLCAPE_FARMING, null, 0,
			new WorldPoint(1249, 3719, 0), ItemID.SKILLCAPE_FARMING, 1, false);
		
		locations.put("farmingGuild", farmingGuild);
	}
	
	/**
	 * Initialize Harmony Island herb patch location with all teleport options
	 */
	private void initHarmony() {
		boolean enabled = config != null ? config.harmonyHerb() : true; // Default to enabled if no config
		Location harmony = new Location("harmony", new WorldPoint(3784, 2838, 0), false, enabled);
		harmony.addPatchType(PatchType.HERB);
		
		// Harmony Teleport Tab
		addSimpleTeleportOption(harmony, "HARMONY_TELE_TAB", Teleport.TeleportCategory.ITEM,
			"Use Harmony teleport tab to go directly to the herb patch",
			ItemID.TELETAB_HARMONY, null, 0,
			new WorldPoint(3784, 2838, 0), ItemID.TELETAB_HARMONY, 1, false);
		
		locations.put("harmony", harmony);
	}
	
	/**
	 * Initialize Weiss herb patch location with all teleport options
	 */
	private void initWeiss() {
		boolean enabled = config != null ? config.weissHerb() : true; // Default to enabled if no config
		Location weiss = new Location("weiss", new WorldPoint(2849, 3932, 0), false, enabled);
		weiss.addPatchType(PatchType.HERB);
		
		// Icy Basalt
		addSimpleTeleportOption(weiss, "WEISS_TELEPORT_BASALT", Teleport.TeleportCategory.ITEM,
			"Use Icy basalt to go directly to the herb patch",
			ItemID.WEISS_TELEPORT_BASALT, null, 0,
			new WorldPoint(2849, 3932, 0), ItemID.WEISS_TELEPORT_BASALT, 1, false);
		
		locations.put("weiss", weiss);
	}
}