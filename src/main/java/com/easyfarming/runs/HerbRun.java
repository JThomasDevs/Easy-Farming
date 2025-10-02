package com.easyfarming.runs;

import com.easyfarming.core.*;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;
import java.util.*;

/**
 * Handles herb farming runs specifically.
 * This class manages herb-specific logic and requirements.
 */
public class HerbRun extends FarmingRun
{
    private HerbSeedRequirement herbSeedRequirement;
    private final List<ItemRequirement> teleportRequirements;
    
    public HerbRun(Client client, FarmingRunState runState, LocationManager locationManager)
    {
        super(client, runState);
        
        // Initialize herb-specific requirements
        this.herbSeedRequirement = new HerbSeedRequirement(1); // 1 seed per patch
        this.teleportRequirements = new ArrayList<>();
        
        // Add locations for herb run
        locations.addAll(locationManager.getHerbLocations());
        
        // Initialize teleport requirements based on selected options
        initializeTeleportRequirements(locationManager);
    }
    
    @Override
    public void initialize()
    {
        // Calculate total seeds needed (1 per enabled patch)
        int totalSeedsNeeded = locations.size();
        herbSeedRequirement = new HerbSeedRequirement(totalSeedsNeeded);
        
        // Update required items map
        updateRequiredItems();
    }
    
    @Override
    public void updateState()
    {
        // Update inventory counts
        updateInventoryCounts();
        
        // Update the run state
        runState.updateState();
        
        // Update required items based on current state
        updateRequiredItems();
    }
    
    @Override
    public Map<Integer, Integer> getRequiredItems()
    {
        return new HashMap<>(requiredItems);
    }
    
    @Override
    public Location getNextLocation()
    {
        return runState.getCurrentLocation();
    }
    
    @Override
    public void advanceToNextLocation()
    {
        // The FarmingRunState handles location advancement
        // This method is kept for compatibility but delegates to the state manager
    }
    
    /**
     * Initialize teleport requirements based on selected teleport options
     */
    private void initializeTeleportRequirements(LocationManager locationManager)
    {
        teleportRequirements.clear();
        
        for (Location location : locations)
        {
            Teleport selectedTeleport = location.getSelectedTeleport();
            if (selectedTeleport != null)
            {
                teleportRequirements.addAll(selectedTeleport.getRequirements());
            }
        }
    }
    
    /**
     * Update the required items map based on current state and requirements
     */
    private void updateRequiredItems()
    {
        requiredItems.clear();
        
        // Add herb seeds requirement
        for (int seedId : herbSeedRequirement.getHerbSeedIds())
        {
            requiredItems.put(seedId, herbSeedRequirement.getQuantity());
        }
        
        // Add teleport requirements
        for (ItemRequirement requirement : teleportRequirements)
        {
            if (requirement.isTierAgnostic())
            {
                // For tier-agnostic items, add all variants
                for (int itemId : requirement.getAllItemIds())
                {
                    requiredItems.put(itemId, requirement.getQuantity());
                }
            }
            else
            {
                requiredItems.put(requirement.getItemId(), requirement.getQuantity());
            }
        }
        
        // Add farming supplies if enabled
        addFarmingSupplies();
    }
    
    /**
     * Add farming supplies to required items
     */
    private void addFarmingSupplies()
    {
        // Add supercompost (1 per patch)
        requiredItems.put(ItemID.BUCKET_SUPERCOMPOST, locations.size());
        
        // Add watering can (1 total) - using watering can 8 as example
        requiredItems.put(ItemID.WATERING_CAN_8, 1);
        
        // Add secateurs (1 total)
        requiredItems.put(ItemID.FAIRY_ENCHANTED_SECATEURS, 1);
    }
    
    /**
     * Get the herb seed requirement
     */
    public HerbSeedRequirement getHerbSeedRequirement()
    {
        return herbSeedRequirement;
    }
    
    /**
     * Get all teleport requirements
     */
    public List<ItemRequirement> getTeleportRequirements()
    {
        return new ArrayList<>(teleportRequirements);
    }
    
    /**
     * Check if the player has all required items for the herb run
     */
    public boolean hasAllRequiredItems()
    {
        // Check herb seeds
        if (!hasRequiredHerbSeeds())
        {
            return false;
        }
        
        // Check teleport requirements
        for (ItemRequirement requirement : teleportRequirements)
        {
            if (!hasItemRequirement(requirement))
            {
                return false;
            }
        }
        
        // Check farming supplies
        return hasFarmingSupplies();
    }
    
    /**
     * Check if the player has the required herb seeds
     */
    private boolean hasRequiredHerbSeeds()
    {
        int totalSeeds = 0;
        for (int seedId : herbSeedRequirement.getHerbSeedIds())
        {
            totalSeeds += inventoryCounts.getOrDefault(seedId, 0);
        }
        return totalSeeds >= herbSeedRequirement.getQuantity();
    }
    
    /**
     * Check if the player has a specific item requirement
     */
    private boolean hasItemRequirement(ItemRequirement requirement)
    {
        if (requirement.isTierAgnostic())
        {
            // For tier-agnostic items, check if any variant is present
            for (int itemId : requirement.getAllItemIds())
            {
                if (inventoryCounts.getOrDefault(itemId, 0) >= requirement.getQuantity())
                {
                    return true;
                }
            }
            return false;
        }
        else
        {
            return inventoryCounts.getOrDefault(requirement.getItemId(), 0) >= requirement.getQuantity();
        }
    }
    
    /**
     * Check if the player has farming supplies
     */
    private boolean hasFarmingSupplies()
    {
        return inventoryCounts.getOrDefault(ItemID.BUCKET_SUPERCOMPOST, 0) >= locations.size() &&
               inventoryCounts.getOrDefault(ItemID.WATERING_CAN_8, 0) >= 1 &&
               inventoryCounts.getOrDefault(ItemID.FAIRY_ENCHANTED_SECATEURS, 0) >= 1;
    }
    
    /**
     * Get the remaining herb seeds needed
     */
    public int getRemainingHerbSeeds()
    {
        int totalSeeds = 0;
        for (int seedId : herbSeedRequirement.getHerbSeedIds())
        {
            totalSeeds += inventoryCounts.getOrDefault(seedId, 0);
        }
        return Math.max(0, herbSeedRequirement.getQuantity() - totalSeeds);
    }
    
    /**
     * Get a representative herb seed ID for display purposes
     */
    public int getDisplayHerbSeedId()
    {
        return herbSeedRequirement.getDisplaySeedId();
    }
}
