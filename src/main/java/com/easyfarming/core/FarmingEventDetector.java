package com.easyfarming.core;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.Item;
import net.runelite.api.gameval.InventoryID;

import java.util.*;
import java.util.logging.Logger;

/**
 * Detects farming-related events and state changes.
 * This class provides methods to detect various farming actions and conditions.
 */
public class FarmingEventDetector
{
    private static final Logger logger = Logger.getLogger(FarmingEventDetector.class.getName());
    
    private final Client client;
    private final RequirementManager requirementManager;
    
    // Constants for detection
    private static final int PATCH_DETECTION_RADIUS = 5;
    private static final int TELEPORT_DETECTION_RADIUS = 10;
    
    
    public FarmingEventDetector(Client client, RequirementManager requirementManager)
    {
        this.client = client;
        this.requirementManager = requirementManager;
    }
    
    /**
     * Detect if the player has teleported to a new location
     */
    public boolean hasPlayerTeleported(WorldPoint previousLocation, WorldPoint currentLocation)
    {
        if (previousLocation == null || currentLocation == null)
        {
            return false;
        }
        
        // Check if player moved more than 20 tiles (likely a teleport)
        int distance = Math.max(
            Math.abs(currentLocation.getX() - previousLocation.getX()),
            Math.abs(currentLocation.getY() - previousLocation.getY())
        );
        
        return distance > 20;
    }
    
    /**
     * Detect if the player is at a specific farming patch
     */
    public boolean isPlayerAtPatch(Location location)
    {
        if (location == null)
        {
            return false;
        }
        
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return false;
        }
        
        WorldPoint playerPos = player.getWorldLocation();
        WorldPoint patchPos = location.getPatchPoint();
        
        // Check if player is within detection radius of the patch
        int distance = Math.max(
            Math.abs(playerPos.getX() - patchPos.getX()),
            Math.abs(playerPos.getY() - patchPos.getY())
        );
        
        return distance <= PATCH_DETECTION_RADIUS && 
               playerPos.getPlane() == patchPos.getPlane();
    }
    
    /**
     * Detect if the player is at a teleport destination
     */
    public boolean isPlayerAtTeleportDestination(Location location)
    {
        if (location == null)
        {
            return false;
        }
        
        Teleport selectedTeleport = location.getSelectedTeleport();
        if (selectedTeleport == null)
        {
            return false;
        }
        
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return false;
        }
        
        WorldPoint playerPos = player.getWorldLocation();
        WorldPoint teleportDest = selectedTeleport.getDestination();
        
        // Check if player is within detection radius of the teleport destination
        int distance = Math.max(
            Math.abs(playerPos.getX() - teleportDest.getX()),
            Math.abs(playerPos.getY() - teleportDest.getY())
        );
        
        return distance <= TELEPORT_DETECTION_RADIUS && 
               playerPos.getPlane() == teleportDest.getPlane();
    }
    
    /**
     * Detect if the player has all required items for the current run
     */
    public boolean hasAllRequiredItems(List<ItemRequirement> requirements)
    {
        requirementManager.updateInventoryCounts();
        
        for (ItemRequirement requirement : requirements)
        {
            if (!requirementManager.isItemRequirementSatisfied(requirement))
            {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Detect if the player has the required herb seeds
     */
    public boolean hasRequiredHerbSeeds(HerbSeedRequirement herbSeedRequirement)
    {
        requirementManager.updateInventoryCounts();
        return requirementManager.isHerbSeedRequirementSatisfied(herbSeedRequirement);
    }
    
    /**
     * Detect if the player has farming supplies
     */
    public boolean hasFarmingSupplies()
    {
        requirementManager.updateInventoryCounts();
        
        // Check for supercompost
        if (requirementManager.getItemCount(ItemID.BUCKET_SUPERCOMPOST) == 0)
        {
            return false;
        }
        
        // Check for watering can (any level)
        boolean hasWateringCan = false;
        for (int i = 0; i <= 8; i++)
        {
            int wateringCanId = ItemID.WATERING_CAN_0 + i;
            if (requirementManager.getItemCount(wateringCanId) > 0)
            {
                hasWateringCan = true;
                break;
            }
        }
        
        if (!hasWateringCan)
        {
            return false;
        }
        
        // Check for secateurs
        return requirementManager.getItemCount(ItemID.FAIRY_ENCHANTED_SECATEURS) > 0;
    }
    
    /**
     * Detect if the player is currently performing a farming action
     */
    public boolean isPlayerPerformingAction()
    {
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return false;
        }
        
        int animationId = player.getAnimation();
        
        // Check for farming-related animations
        return animationId == 714 ||  // Teleport
               animationId == 830 ||  // Harvest
               animationId == 2291 || // Plant
               animationId == 2288 || // Cure
               animationId == 2283 || // Compost
               animationId == 2293 || // Water
               animationId == 2294;   // Remove
    }
    
    
    /**
     * Detect if the player is in a farming-related area
     */
    public boolean isPlayerInFarmingArea()
    {
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return false;
        }
        
        WorldPoint playerPos = player.getWorldLocation();
        int regionId = playerPos.getRegionID();
        
        // Check if player is in a region with farming patches
        // These are the region IDs for areas with herb patches
        Set<Integer> farmingRegions = Set.of(
            10033, // Ardougne
            10029, // Catherby
            11828, // Falador
            14388, // Morytania
            11573, // Troll Stronghold
            12850, // Kourend
            14948, // Farming Guild
            15148, // Harmony Island
            11325  // Weiss
        );
        
        return farmingRegions.contains(regionId);
    }
    
    /**
     * Detect if the player is near any farming patch
     */
    public Location detectNearbyPatch(List<Location> locations)
    {
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return null;
        }
        
        WorldPoint playerPos = player.getWorldLocation();
        
        for (Location location : locations)
        {
            WorldPoint patchPos = location.getPatchPoint();
            
            int distance = Math.max(
                Math.abs(playerPos.getX() - patchPos.getX()),
                Math.abs(playerPos.getY() - patchPos.getY())
            );
            
            if (distance <= PATCH_DETECTION_RADIUS && 
                playerPos.getPlane() == patchPos.getPlane())
            {
                return location;
            }
        }
        
        return null;
    }
    
    /**
     * Get the current patch state for a location
     * This is the main method for dynamic instruction/highlight updates
     */
    public PatchState getCurrentPatchState(Location location)
    {
        if (location == null)
        {
            logger.warning("Location is null for patch state detection");
            return PatchState.UNKNOWN;
        }
        
        try
        {
            // Get the varbit ID for this location
            int varbitId = VarbitMapper.getVarbitIdForLocation(location);
            if (varbitId == -1)
            {
                logger.warning("No varbit ID found for location: " + location.getName());
                return PatchState.UNKNOWN;
            }
            
            // Get the varbit value from the client
            int varbitValue = client.getVarbitValue(varbitId);
            
            // Map the varbit value to a PatchState based on patch type
            PatchType patchType = VarbitMapper.getPatchTypeForLocation(location);
            PatchState currentState = VarbitMapper.mapVarbitValueToPatchState(varbitValue, patchType);
            
            logger.fine("Patch state for " + location.getName() + ": " + currentState + " (varbit: " + varbitValue + ")");
            return currentState;
        }
        catch (Exception e)
        {
            logger.warning("Exception while detecting patch state: " + e.getMessage());
            return PatchState.UNKNOWN;
        }
    }
    
    
}
