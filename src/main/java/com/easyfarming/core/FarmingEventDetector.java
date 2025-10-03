package com.easyfarming.core;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Detects farming-related events and state changes.
 * This class provides methods to detect various farming actions and conditions.
 */
@Slf4j
public class FarmingEventDetector
{
    
    private final Client client;
    private final RequirementManager requirementManager;
    
    // Constants for detection
    private static final int PATCH_DETECTION_RADIUS = 5;
    private static final int TELEPORT_DETECTION_RADIUS = 10;
    
    // Farming region IDs for areas with herb patches
    private static final Set<Integer> FARMING_REGIONS = Set.of(
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
    
    
    public FarmingEventDetector(Client client, RequirementManager requirementManager)
    {
        if (client == null)
        {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (requirementManager == null)
        {
            throw new IllegalArgumentException("RequirementManager cannot be null");
        }
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
        
        // Plane changes indicate teleportation
        if (previousLocation.getPlane() != currentLocation.getPlane())
        {
            return true;
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
        if (requirements == null)
        {
            return false;
        }
        
        requirementManager.updateInventoryCounts();
        
        for (ItemRequirement requirement : requirements)
        {
            if (requirement == null)
            {
                return false;
            }
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
        
        return FARMING_REGIONS.contains(regionId);
    }
    
    /**
     * Detect if the player is near any farming patch
     */
    public Location detectNearbyPatch(List<Location> locations)
    {
        if (locations == null || locations.isEmpty())
        {
            return null;
        }
        
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return null;
        }
        
        WorldPoint playerPos = player.getWorldLocation();
        
        for (Location location : locations)
        {
            if (location == null)
            {
                continue;
            }
            
            WorldPoint patchPos = location.getPatchPoint();
            if (patchPos == null)
            {
                continue;
            }
            
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
            log.warn("Location is null for patch state detection");
            return PatchState.UNKNOWN;
        }
        
        try
        {
            // Get the varbit ID for this location
            int varbitId = VarbitMapper.getHerbPatchVarbitId(location);
            if (varbitId == -1)
            {
                log.warn("No varbit ID found for location: {}", location.getName());
                return PatchState.UNKNOWN;
            }
            
            // Get the varbit value from the client
            int varbitValue = client.getVarbitValue(varbitId);
            
            // Map the varbit value to a PatchState based on patch type
            PatchType patchType = VarbitMapper.getPatchTypeForLocation(location);
            PatchState currentState = VarbitMapper.mapVarbitValueToPatchState(varbitValue, patchType);
            
            log.debug("Patch state for {}: {} (varbit: {})", location.getName(), currentState, varbitValue);
            return currentState;
        }
        catch (Exception e)
        {
            log.warn("Exception while detecting patch state: {}", e.getMessage());
            return PatchState.UNKNOWN;
        }
    }
    
    
}
