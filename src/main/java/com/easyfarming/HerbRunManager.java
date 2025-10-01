package com.easyfarming;

import java.util.*;

public class HerbRunManager
{
	private final EasyFarmingConfig config;
	private boolean herbRunActive = false;
	private final Map<Integer, Integer> requiredItems = new HashMap<>();
	private final Map<Integer, Integer> inventoryCounts = new HashMap<>();
	
	// Navigation tracking
	private int currentPatchIndex = 0;
	private final String[] patchOrder = {"ardougne", "catherby", "falador", "morytania", "trollStronghold", "kourend", "farmingGuild", "harmony", "weiss"};
	private final Map<String, int[]> patchCoordinates = new HashMap<>();

	// Location-specific teleport requirements
	private static final Map<String, Map<Integer, Integer>> LOCATION_TELEPORTS = new HashMap<>();
	
	static
	{
		// Ardougne - Ardougne teleport spell (requires 2 law runes, 2 water runes)
		Map<Integer, Integer> ardougneTeleports = new HashMap<>();
		ardougneTeleports.put(563, 2); // Law rune
		ardougneTeleports.put(555, 2); // Water rune
		LOCATION_TELEPORTS.put("ardougne", ardougneTeleports);
		
		// Catherby - Camelot teleport spell (requires 1 law rune, 5 air runes)
		Map<Integer, Integer> catherbyTeleports = new HashMap<>();
		catherbyTeleports.put(563, 1); // Law rune
		catherbyTeleports.put(556, 5); // Air rune
		LOCATION_TELEPORTS.put("catherby", catherbyTeleports);
		
		// Falador - Falador teleport spell (requires 1 law rune, 3 air runes, 1 water rune)
		Map<Integer, Integer> faladorTeleports = new HashMap<>();
		faladorTeleports.put(563, 1); // Law rune
		faladorTeleports.put(556, 3); // Air rune
		faladorTeleports.put(555, 1); // Water rune
		LOCATION_TELEPORTS.put("falador", faladorTeleports);
		
		// Morytania - Ectophial (quest item, no runes needed)
		Map<Integer, Integer> morytaniaTeleports = new HashMap<>();
		morytaniaTeleports.put(4251, 1); // Ectophial
		LOCATION_TELEPORTS.put("morytania", morytaniaTeleports);
		
		// Troll Stronghold - Trollheim teleport spell (requires 2 law runes, 2 fire runes)
		Map<Integer, Integer> trollStrongholdTeleports = new HashMap<>();
		trollStrongholdTeleports.put(563, 2); // Law rune
		trollStrongholdTeleports.put(554, 2); // Fire rune
		LOCATION_TELEPORTS.put("trollStronghold", trollStrongholdTeleports);
		
		// Kourend - Xeric's talisman or Xeric's heart (no runes needed)
		Map<Integer, Integer> kourendTeleports = new HashMap<>();
		kourendTeleports.put(13393, 1); // Xeric's talisman
		LOCATION_TELEPORTS.put("kourend", kourendTeleports);
		
		// Farming Guild - Skills necklace (no runes needed)
		Map<Integer, Integer> farmingGuildTeleports = new HashMap<>();
		farmingGuildTeleports.put(11118, 1); // Skills necklace
		LOCATION_TELEPORTS.put("farmingGuild", farmingGuildTeleports);
		
		// Harmony Island - Harmony teleport tab (no runes needed)
		Map<Integer, Integer> harmonyTeleports = new HashMap<>();
		harmonyTeleports.put(15420, 1); // Harmony teleport tab
		LOCATION_TELEPORTS.put("harmony", harmonyTeleports);
		
		// Weiss - Icy basalt (no runes needed)
		Map<Integer, Integer> weissTeleports = new HashMap<>();
		weissTeleports.put(23975, 1); // Icy basalt
		LOCATION_TELEPORTS.put("weiss", weissTeleports);
	}

	public HerbRunManager(EasyFarmingConfig config)
	{
		this.config = config;
		
		// Initialize patch coordinates from Farming Helper plugin (exact coordinates)
		patchCoordinates.put("ardougne", new int[]{2670, 3374, 0});
		patchCoordinates.put("catherby", new int[]{2813, 3463, 0});
		patchCoordinates.put("falador", new int[]{3058, 3307, 0});
		patchCoordinates.put("morytania", new int[]{3601, 3525, 0});
		patchCoordinates.put("trollStronghold", new int[]{2824, 3696, 0});
		patchCoordinates.put("kourend", new int[]{1738, 3550, 0});
		patchCoordinates.put("farmingGuild", new int[]{1238, 3726, 0});
		patchCoordinates.put("harmony", new int[]{3789, 2837, 0});
		patchCoordinates.put("weiss", new int[]{2847, 3931, 0});
	}

	public boolean isHerbRunActive()
	{
		return herbRunActive;
	}

    public Map<Integer, Integer> getRequiredItems()
    {
       return new HashMap<>(requiredItems);
    }	public Map<Integer, Integer> getInventoryCounts()
	{
		return new HashMap<>(inventoryCounts);
	}
	
	/**
	 * Get the next patch location to visit
	 */
	public String getNextPatchLocation()
	{
		if (!herbRunActive || patchOrder.length == 0)
		{
			return null;
		}
		
		// Start from currentPatchIndex and scan forward
		for (int i = 0; i < patchOrder.length; i++)
		{
			int index = (currentPatchIndex + i) % patchOrder.length;
			String location = patchOrder[index];
			
			if (isLocationEnabled(location))
			{
				return location;
			}
		}
		
		// No enabled patches found
		return null;
	}
	
	/**
	 * Get coordinates for a specific patch location
	 */
public int[] getPatchCoordinates(String location)
{
    int[] coords = patchCoordinates.get(location);
    return coords != null ? coords.clone() : null;
}	
	/**
	 * Check if a location is enabled in config
	 */
	private boolean isLocationEnabled(String location)
	{
		switch (location)
		{
			case "ardougne": return config.ardougne();
			case "catherby": return config.catherby();
			case "falador": return config.falador();
			case "morytania": return config.morytania();
			case "trollStronghold": return config.trollStronghold();
			case "kourend": return config.kourend();
			case "farmingGuild": return config.farmingGuild();
			case "harmony": return config.harmony();
			case "weiss": return config.weiss();
			default: return false;
		}
	}
	
	/**
	 * Get the teleport item ID for the next location
	 */
	public Integer getNextTeleportItemId()
	{
		String nextLocation = getNextPatchLocation();
		if (nextLocation == null)
		{
			return null;
		}
		
		Map<Integer, Integer> teleports = LOCATION_TELEPORTS.get(nextLocation);
		if (teleports != null && !teleports.isEmpty())
		{
			return teleports.keySet().iterator().next();
		}
		return null;
	}
	
	public void completeCurrentPatch()
	{
		if (!herbRunActive)
		{
			return;
		}
 	
		// Get the current patch location
		String currentLocation = getNextPatchLocation();
		if (currentLocation != null)
		{
			// Remove the teleport requirements for this location
			Map<Integer, Integer> teleports = LOCATION_TELEPORTS.get(currentLocation);
			if (teleports != null)
			{
				for (Integer itemId : teleports.keySet())
				{
					requiredItems.remove(itemId);
				}
			}
			
			// Increment to next patch, wrapping around if necessary
			currentPatchIndex = (currentPatchIndex + 1) % patchOrder.length;
		}
	}

	public Map<Integer, Integer> getRemainingItems()
	{
		Map<Integer, Integer> remainingItems = new HashMap<>();
		
		for (Map.Entry<Integer, Integer> entry : requiredItems.entrySet())
		{
			int itemId = entry.getKey();
			int required = entry.getValue();
			
			// Special handling for herb seeds - check all herb seed types
			if (itemId == 5295) // Generic herb seed placeholder ID
			{
				int totalHerbSeeds = getTotalHerbSeeds();
				int remaining = Math.max(0, required - totalHerbSeeds);
				
				if (remaining > 0)
				{
					remainingItems.put(itemId, remaining);
				}
			}
			else
			{
				// Normal item handling
				int inventory = inventoryCounts.getOrDefault(itemId, 0);
				int remaining = Math.max(0, required - inventory);
				
				if (remaining > 0)
				{
					remainingItems.put(itemId, remaining);
				}
			}
		}
		
		return remainingItems;
	}

	public void updateInventoryCount(int itemId, int count)
	{
		inventoryCounts.put(itemId, count);
	}
	
	public void clearInventoryCounts()
	{
		inventoryCounts.clear();
	}
	
	/**
	 * Get the total count of all herb seeds in inventory
	 * This allows users to use any herb seed type for their runs
	 */
	private int getTotalHerbSeeds()
	{
		int total = 0;
		
		// Common herb seed IDs - add more as needed
		int[] herbSeedIds = {
			//5291-5303
			5291, // Guam seed
			5292, // Marrentill seed
			5293, // Tarromin seed
			5294, // Harralander seed
			5295, // Ranarr seed 
			5296, // Toadflax seed
			5297, // Irit seed
			5298, // Avantoe seed
			5299, // Kwuarm seed
			5300, // Snapdragon seed
			5301, // Cadantine seed
			5302, // Lantadyme seed
			5303, // Dwarf weed seed
			5304, // Torstol seed
			30088 // Huasca seed
		};
		
		for (int seedId : herbSeedIds)
		{
			total += inventoryCounts.getOrDefault(seedId, 0);
		}
		
		return total;
	}

	public void startHerbRun()
	{
		herbRunActive = true;
		calculateRequiredItems();
	}

	public void stopHerbRun()
	{
		herbRunActive = false;
		currentPatchIndex = 0;
		requiredItems.clear();
		inventoryCounts.clear();
	}
	
	private void calculateRequiredItems()
	{
		// Count enabled herb patches and add location-specific teleports
		int patchCount = 0;
		if (config.ardougne())
		{
			patchCount++;
			addTeleportItems("ardougne");
		}
		if (config.catherby())
		{
			patchCount++;
			addTeleportItems("catherby");
		}
		if (config.falador())
		{
			patchCount++;
			addTeleportItems("falador");
		}
		if (config.morytania())
		{
			patchCount++;
			addTeleportItems("morytania");
		}
		if (config.trollStronghold())
		{
			patchCount++;
			addTeleportItems("trollStronghold");
		}
		if (config.kourend())
		{
			patchCount++;
			addTeleportItems("kourend");
		}
		if (config.farmingGuild())
		{
			patchCount++;
			addTeleportItems("farmingGuild");
		}
		if (config.harmony())
		{
			patchCount++;
			addTeleportItems("harmony");
		}
		if (config.weiss())
		{
			patchCount++;
			addTeleportItems("weiss");
		}

		// Add herb seeds - any herb seed type can be used (1 per patch)
		requiredItems.put(5295, patchCount); // Generic herb seed placeholder (ID 5295)

		// Add compost if enabled
		if (config.useCompost())
		{
			requiredItems.put(6032, patchCount); // Supercompost
		}

		// Add watering can if enabled
		if (config.useWateringCan())
		{
			requiredItems.put(5331, 1); // Watering can (8)
		}
	}
	
	private void addTeleportItems(String location)
	{
		Map<Integer, Integer> teleports = LOCATION_TELEPORTS.get(location);
		if (teleports != null)
		{
			for (Map.Entry<Integer, Integer> entry : teleports.entrySet())
			{
				int itemId = entry.getKey();
				int quantity = entry.getValue();
				requiredItems.merge(itemId, quantity, Integer::sum);
			}
		}
	}
}
