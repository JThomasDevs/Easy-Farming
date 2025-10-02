package com.easyfarming.core;

import net.runelite.api.gameval.ItemID;
import java.util.*;

/**
 * Represents a requirement for herb seeds (any type, seed-agnostic)
 * This allows the plugin to work with any herb seed type
 */
public class HerbSeedRequirement
{
    private final int quantity;
    private final List<Integer> allHerbSeedIds;
    
    public HerbSeedRequirement(int quantity)
    {
        this.quantity = quantity;
        this.allHerbSeedIds = getAllHerbSeedIds();
    }
    
    /**
     * Get all herb seed item IDs from Guam to Torstol
     */
    private List<Integer> getAllHerbSeedIds()
    {
        return Arrays.asList(
            ItemID.GUAM_SEED,           // 5291 - Guam seed
            ItemID.MARRENTILL_SEED,     // 5292 - Marrentill seed
            ItemID.TARROMIN_SEED,       // 5293 - Tarromin seed
            ItemID.HARRALANDER_SEED,    // 5294 - Harralander seed
            ItemID.RANARR_SEED,         // 5295 - Ranarr seed
            ItemID.TOADFLAX_SEED,       // 5296 - Toadflax seed
            ItemID.IRIT_SEED,           // 5297 - Irit seed
            ItemID.AVANTOE_SEED,        // 5298 - Avantoe seed
            ItemID.KWUARM_SEED,         // 5299 - Kwuarm seed
            ItemID.SNAPDRAGON_SEED,     // 5300 - Snapdragon seed
            ItemID.CADANTINE_SEED,      // 5301 - Cadantine seed
            ItemID.LANTADYME_SEED,      // 5302 - Lantadyme seed
            ItemID.DWARF_WEED_SEED,     // 5303 - Dwarf weed seed
            ItemID.TORSTOL_SEED,        // 5304 - Torstol seed
            ItemID.HUASCA_SEED          // 30088 - Huascas seed
        );
    }
    
    /**
     * Get the quantity of herb seeds needed
     */
    public int getQuantity()
    {
        return quantity;
    }
    
    /**
     * Get all herb seed item IDs
     */
    public List<Integer> getHerbSeedIds()
    {
        return new ArrayList<>(allHerbSeedIds);
    }
    
    /**
     * Get a representative seed ID for display purposes (uses Ranarr as example)
     */
    public int getDisplaySeedId()
    {
        return ItemID.RANARR_SEED;
    }
    
    /**
     * Get the display name for this requirement
     */
    public String getDisplayName()
    {
        return quantity + "x Herb seeds (any type)";
    }
    
    /**
     * Check if a given item ID is a herb seed
     */
    public boolean isHerbSeed(int itemId)
    {
        return allHerbSeedIds.contains(itemId);
    }
    
    /**
     * Get the total count of herb seeds in a given inventory
     */
    public int getTotalHerbSeedCount(Map<Integer, Integer> inventoryCounts)
    {
        return allHerbSeedIds.stream()
            .mapToInt(seedId -> inventoryCounts.getOrDefault(seedId, 0))
            .sum();
    }
    
    /**
     * Check if the requirement is satisfied with the given inventory
     */
    public boolean isSatisfied(Map<Integer, Integer> inventoryCounts)
    {
        return getTotalHerbSeedCount(inventoryCounts) >= quantity;
    }
    
    /**
     * Get how many more herb seeds are needed
     */
    public int getRemainingCount(Map<Integer, Integer> inventoryCounts)
    {
        return Math.max(0, quantity - getTotalHerbSeedCount(inventoryCounts));
    }
    
    @Override
    public String toString()
    {
        return "HerbSeedRequirement{quantity=" + quantity + ", seeds=" + allHerbSeedIds.size() + " types}";
    }
}
