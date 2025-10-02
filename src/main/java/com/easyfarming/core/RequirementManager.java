package com.easyfarming.core;

import net.runelite.api.Client;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.gameval.ItemID;
import java.util.*;

/**
 * Manages and checks all types of farming requirements
 * Handles tier-agnostic diary items, seed-agnostic herb seeds, and regular items
 */
public class RequirementManager
{
    private final Client client;
    private final Map<Integer, Integer> inventoryCounts;
    
    public RequirementManager(Client client)
    {
        this.client = client;
        this.inventoryCounts = new HashMap<>();
        updateInventoryCounts();
    }
    
    /**
     * Update inventory counts from the client
     */
    public void updateInventoryCounts()
    {
        inventoryCounts.clear();
        
        if (client.getItemContainer(InventoryID.INV) != null)
        {
            for (Item item : client.getItemContainer(InventoryID.INV).getItems())
            {
                if (item.getId() != -1) // Valid item
                {
                    inventoryCounts.merge(item.getId(), item.getQuantity(), Integer::sum);
                }
            }
        }
    }
    
    /**
     * Check if a single item requirement is satisfied
     */
    public boolean isItemRequirementSatisfied(ItemRequirement requirement)
    {
        if (requirement.isPohRequirement())
        {
            // POH requirements need special handling (construction level, built furniture)
            // For now, assume satisfied if player has the required construction level
            return client.getRealSkillLevel(net.runelite.api.Skill.CONSTRUCTION) >= requirement.getConstructionLevel();
        }
        
        if (requirement.isTierAgnostic())
        {
            // Check if player has any of the tier variants
            List<Integer> allIds = requirement.getAllItemIds();
            int totalCount = allIds.stream()
                .mapToInt(itemId -> inventoryCounts.getOrDefault(itemId, 0))
                .sum();
            return totalCount >= requirement.getQuantity();
        }
        else
        {
            // Regular item requirement
            int currentCount = inventoryCounts.getOrDefault(requirement.getItemId(), 0);
            return currentCount >= requirement.getQuantity();
        }
    }
    
    /**
     * Check if a herb seed requirement is satisfied
     */
    public boolean isHerbSeedRequirementSatisfied(HerbSeedRequirement requirement)
    {
        return requirement.isSatisfied(inventoryCounts);
    }
    
    /**
     * Get remaining count for a single item requirement
     */
    public int getRemainingItemCount(ItemRequirement requirement)
    {
        if (requirement.isPohRequirement())
        {
            int currentLevel = client.getRealSkillLevel(net.runelite.api.Skill.CONSTRUCTION);
            return Math.max(0, requirement.getConstructionLevel() - currentLevel);
        }
        
        if (requirement.isTierAgnostic())
        {
            List<Integer> allIds = requirement.getAllItemIds();
            int totalCount = allIds.stream()
                .mapToInt(itemId -> inventoryCounts.getOrDefault(itemId, 0))
                .sum();
            return Math.max(0, requirement.getQuantity() - totalCount);
        }
        else
        {
            int currentCount = inventoryCounts.getOrDefault(requirement.getItemId(), 0);
            return Math.max(0, requirement.getQuantity() - currentCount);
        }
    }
    
    /**
     * Get remaining count for herb seed requirement
     */
    public int getRemainingHerbSeedCount(HerbSeedRequirement requirement)
    {
        return requirement.getRemainingCount(inventoryCounts);
    }
    
    /**
     * Get a display item ID for tier-agnostic requirements (returns the first tier)
     */
    public int getDisplayItemId(ItemRequirement requirement)
    {
        if (requirement.isTierAgnostic())
        {
            return requirement.getItemId(); // Returns the base tier (e.g., tier 2)
        }
        return requirement.getItemId();
    }
    
    /**
     * Get all unsatisfied requirements from a list
     */
    public List<ItemRequirement> getUnsatisfiedItemRequirements(List<ItemRequirement> requirements)
    {
        return requirements.stream()
            .filter(req -> !isItemRequirementSatisfied(req))
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get all unsatisfied herb seed requirements from a list
     */
    public List<HerbSeedRequirement> getUnsatisfiedHerbSeedRequirements(List<HerbSeedRequirement> requirements)
    {
        return requirements.stream()
            .filter(req -> !isHerbSeedRequirementSatisfied(req))
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Check if all requirements for a teleport option are satisfied
     */
    public boolean areTeleportRequirementsSatisfied(Teleport teleport)
    {
        List<ItemRequirement> itemRequirements = teleport.getRequirements();
        
        // Check all item requirements
        for (ItemRequirement requirement : itemRequirements)
        {
            if (!isItemRequirementSatisfied(requirement))
            {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Get a summary of all unsatisfied requirements for a teleport
     */
    public Map<Integer, Integer> getUnsatisfiedTeleportRequirements(Teleport teleport)
    {
        Map<Integer, Integer> unsatisfied = new HashMap<>();
        List<ItemRequirement> requirements = teleport.getRequirements();
        
        for (ItemRequirement requirement : requirements)
        {
            if (!isItemRequirementSatisfied(requirement))
            {
                int displayId = getDisplayItemId(requirement);
                int remaining = getRemainingItemCount(requirement);
                unsatisfied.put(displayId, remaining);
            }
        }
        
        return unsatisfied;
    }
    
    /**
     * Get current inventory counts (for external use)
     */
    public Map<Integer, Integer> getInventoryCounts()
    {
        return new HashMap<>(inventoryCounts);
    }
    
    /**
     * Check if player has any herb seeds
     */
    public boolean hasAnyHerbSeeds()
    {
        HerbSeedRequirement herbSeeds = new HerbSeedRequirement(1);
        return herbSeeds.getTotalHerbSeedCount(inventoryCounts) > 0;
    }
    
    /**
     * Get total count of all herb seeds in inventory
     */
    public int getTotalHerbSeedCount()
    {
        HerbSeedRequirement herbSeeds = new HerbSeedRequirement(1);
        return herbSeeds.getTotalHerbSeedCount(inventoryCounts);
    }
}
