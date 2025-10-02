package com.easyfarming.core;

import net.runelite.api.Client;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.coords.WorldPoint;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

/**
 * Base class for different types of farming runs
 * This class provides common functionality for all farming run types
 */
public abstract class FarmingRun
{
    protected final Client client;
    protected final FarmingRunState runState;
    protected final List<Location> locations;
    protected final Map<Integer, Integer> requiredItems;
    protected final Map<Integer, Integer> inventoryCounts;
    
    public FarmingRun(Client client, FarmingRunState runState)
    {
        this.client = Objects.requireNonNull(client, "client must not be null");
        this.runState = Objects.requireNonNull(runState, "runState must not be null");
        this.locations = new ArrayList<>();
        this.requiredItems = new HashMap<>();
        this.inventoryCounts = new HashMap<>();
    }
    
    /**
     * Initialize the farming run with locations and requirements
     */
    public abstract void initialize();
    
    /**
     * Update the current state based on player location and inventory
     */
    public abstract void updateState();
    
    /**
     * Get all required items for this farming run
     */
    public abstract Map<Integer, Integer> getRequiredItems();
    
    /**
     * Get the next location to visit
     */
    public abstract Location getNextLocation();
    
    /**
     * Advance to the next location
     */
    public abstract void advanceToNextLocation();
    
    /**
     * Check if the player is at the current location's patch
     */
    protected boolean isPlayerAtPatch(Location location)
    {
        if (client.getLocalPlayer() == null)
        {
            return false;
        }
        
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        WorldPoint patchLocation = location.getPatchPoint();
        
        // Check if player is within 5 tiles of the patch
        int dx = Math.abs(playerLocation.getX() - patchLocation.getX());
        int dy = Math.abs(playerLocation.getY() - patchLocation.getY());
        int distance = Math.max(dx, dy);
        
        return distance <= 5 && playerLocation.getPlane() == patchLocation.getPlane();
    }
    
    /**
     * Check if the player is at the teleport destination
     */
    protected boolean isPlayerAtTeleportDestination(Location location)
    {
        if (client.getLocalPlayer() == null)
        {
            return false;
        }
        
        Teleport selectedTeleport = location.getSelectedTeleport();
        if (selectedTeleport == null)
        {
            return false;
        }
        
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        WorldPoint teleportDestination = selectedTeleport.getDestination();
        
        // Check if player is within 10 tiles of the teleport destination
        int dx = Math.abs(playerLocation.getX() - teleportDestination.getX());
        int dy = Math.abs(playerLocation.getY() - teleportDestination.getY());
        int distance = Math.max(dx, dy);
        
        return distance <= 10 && playerLocation.getPlane() == teleportDestination.getPlane();
    }
    
    /**
     * Update inventory counts from client
     */
    protected void updateInventoryCounts()
    {
        inventoryCounts.clear();
        
        if (client.getItemContainer(InventoryID.INV) != null)
        {
            for (Item item : client.getItemContainer(InventoryID.INV).getItems())
            {
                if (item != null && item.getId() != -1)
                {
                    inventoryCounts.merge(item.getId(), item.getQuantity(), Integer::sum);
                }
            }
        }
    }
    
    // Getters
    public FarmingState getCurrentState() { return runState.getCurrentState(); }
    public Location getCurrentLocation() { return runState.getCurrentLocation(); }
    public List<Location> getLocations() { return new ArrayList<>(locations); }
    public Map<Integer, Integer> getInventoryCounts() { return new HashMap<>(inventoryCounts); }
    public FarmingRunState getRunState() { return runState; }
    
    @Override
    public String toString()
    {
        return "FarmingRun{state=" + getCurrentState() + ", location=" + 
               (getCurrentLocation() != null ? getCurrentLocation().getName() : "none") + 
               ", locations=" + locations.size() + "}";
    }
}
