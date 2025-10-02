package com.easyfarming.core;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the state machine for farming runs.
 * This class handles state transitions, patch state detection, and run logic.
 */
public class FarmingRunState
{
    private static final Logger logger = Logger.getLogger(FarmingRunState.class.getName());
    
    private final Client client;
    private final RequirementManager requirementManager;
    private final LocationManager locationManager;
    
    // Current state
    private FarmingState currentState;
    private Location currentLocation;
    private PatchState currentPatchState;
    private int currentLocationIndex;
    
    // Run configuration
    private List<Location> enabledLocations;
    private boolean isHerbRunActive;
    private boolean isTreeRunActive;
    private boolean isFruitTreeRunActive;
    private boolean isAllotmentRunActive;
    
    // State tracking
    private Map<Location, PatchState> patchStates;
    private Set<Location> completedLocations;
    private WorldPoint lastPlayerPosition;
    private long lastStateUpdate;
    
    // Constants
    private static final int PATCH_DETECTION_RADIUS = 5;
    private static final long STATE_UPDATE_INTERVAL = 1000; // 1 second

    public FarmingRunState(Client client, RequirementManager requirementManager, LocationManager locationManager)
    {
        this.client = client;
        this.requirementManager = requirementManager;
        this.locationManager = locationManager;
        
        this.currentState = FarmingState.IDLE;
        this.currentLocation = null;
        this.currentPatchState = PatchState.UNKNOWN;
        this.currentLocationIndex = 0;
        
        this.enabledLocations = new ArrayList<>();
        this.isHerbRunActive = false;
        this.isTreeRunActive = false;
        this.isFruitTreeRunActive = false;
        this.isAllotmentRunActive = false;
        
        this.patchStates = new HashMap<>();
        this.completedLocations = new HashSet<>();
        this.lastPlayerPosition = null;
        this.lastStateUpdate = 0;
    }

    /**
     * Start a farming run with the specified run types
     */
    public void startRun(boolean herbRun, boolean treeRun, boolean fruitTreeRun, boolean allotmentRun)
    {
        if (currentState != FarmingState.IDLE)
        {
            return; // Already running
        }
        
        this.isHerbRunActive = herbRun;
        this.isTreeRunActive = treeRun;
        this.isFruitTreeRunActive = fruitTreeRun;
        this.isAllotmentRunActive = allotmentRun;
        
        // Build list of enabled locations
        enabledLocations.clear();
        if (herbRun)
        {
            enabledLocations.addAll(locationManager.getHerbLocations());
        }
        if (treeRun)
        {
            enabledLocations.addAll(locationManager.getTreeLocations());
        }
        if (fruitTreeRun)
        {
            enabledLocations.addAll(locationManager.getFruitTreeLocations());
        }
        if (allotmentRun)
        {
            enabledLocations.addAll(locationManager.getAllotmentLocations());
        }
        
        if (enabledLocations.isEmpty())
        {
            currentState = FarmingState.ERROR;
            return;
        }
        
        // Initialize run
        currentLocationIndex = 0;
        currentLocation = enabledLocations.get(0);
        completedLocations.clear();
        patchStates.clear();
        
        // Check if we have all required items
        if (hasAllRequiredItems())
        {
            currentState = FarmingState.READY_TO_TELEPORT;
        }
        else
        {
            currentState = FarmingState.GATHERING_ITEMS;
        }
        
        lastStateUpdate = System.currentTimeMillis();
    }

    /**
     * Stop the current farming run
     */
    public void stopRun()
    {
        currentState = FarmingState.IDLE;
        currentLocation = null;
        currentPatchState = PatchState.UNKNOWN;
        currentLocationIndex = 0;
        enabledLocations.clear();
        patchStates.clear();
        completedLocations.clear();
        lastPlayerPosition = null;
    }

    /**
     * Pause the current farming run
     */
    public void pauseRun()
    {
        if (currentState.isActiveRun())
        {
            currentState = FarmingState.PAUSED;
        }
    }

    /**
     * Resume a paused farming run
     */
    public void resumeRun()
    {
        if (currentState == FarmingState.PAUSED)
        {
            // Determine the appropriate state to resume to
            if (hasAllRequiredItems())
            {
                currentState = FarmingState.READY_TO_TELEPORT;
            }
            else
            {
                currentState = FarmingState.GATHERING_ITEMS;
            }
        }
    }

    /**
     * Update the farming run state based on current game state
     */
    public void updateState()
    {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastStateUpdate < STATE_UPDATE_INTERVAL)
        {
            return; // Throttle updates
        }
        
        lastStateUpdate = currentTime;
        
        if (currentState == FarmingState.IDLE || currentState == FarmingState.PAUSED)
        {
            return; // No active run
        }
        
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return;
        }
        
        WorldPoint playerPosition = player.getWorldLocation();
        
        // Update inventory counts
        requirementManager.updateInventoryCounts();
        
        // Handle state transitions based on current state
        switch (currentState)
        {
            case GATHERING_ITEMS:
                if (hasAllRequiredItems())
                {
                    currentState = FarmingState.READY_TO_TELEPORT;
                }
                break;
                
            case READY_TO_TELEPORT:
                // State will change when player teleports
                break;
                
            case TELEPORTING:
                // Check if teleport animation is complete
                if (playerPosition != null && !playerPosition.equals(lastPlayerPosition))
                {
                    currentState = FarmingState.NAVIGATING;
                }
                break;
                
            case NAVIGATING:
                if (isPlayerAtPatch())
                {
                    currentState = FarmingState.AT_PATCH;
                    currentPatchState = detectPatchState();
                }
                break;
                
            case AT_PATCH:
                currentPatchState = detectPatchState();
                if (currentPatchState.requiresAction())
                {
                    // Transition to appropriate action state
                    if (currentPatchState.canHarvest())
                    {
                        currentState = FarmingState.HARVESTING;
                    }
                    else if (currentPatchState.needsTreatment())
                    {
                        currentState = FarmingState.TREATING_DISEASE;
                    }
                    else if (currentPatchState.needsRemoval())
                    {
                        currentState = FarmingState.REMOVING_DEAD;
                    }
                }
                else if (currentPatchState.canPlant())
                {
                    currentState = FarmingState.PLANTING;
                }
                break;
                
            case HARVESTING:
            case PLANTING:
            case TREATING_DISEASE:
            case REMOVING_DEAD:
            case COMPOSTING:
            case WATERING:
                // Check if action is complete
                if (isActionComplete())
                {
                    // Move to next location or complete run
                    if (moveToNextLocation())
                    {
                        currentState = FarmingState.MOVING_TO_NEXT;
                    }
                    else
                    {
                        currentState = FarmingState.RUN_COMPLETE;
                    }
                }
                break;
                
            case MOVING_TO_NEXT:
                if (currentLocation != null)
                {
                    if (hasAllRequiredItems())
                    {
                        currentState = FarmingState.READY_TO_TELEPORT;
                    }
                    else
                    {
                        currentState = FarmingState.GATHERING_ITEMS;
                    }
                }
                break;
        }
        
        lastPlayerPosition = playerPosition;
    }

    /**
     * Check if the player has all required items for the current run
     */
    private boolean hasAllRequiredItems()
    {
        // This would check against the selected teleport options and enabled patches
        // For now, return true to allow progression
        return true;
    }

    /**
     * Check if the player is at the current patch
     */
    private boolean isPlayerAtPatch()
    {
        if (currentLocation == null)
        {
            return false;
        }
        
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return false;
        }
        
        WorldPoint playerPos = player.getWorldLocation();
        WorldPoint patchPos = currentLocation.getPatchPoint();
        
        // Check if player is within detection radius of the patch
        return playerPos.distanceTo(patchPos) <= PATCH_DETECTION_RADIUS;
    }

    /**
     * Detect the current state of the patch
     */
    private PatchState detectPatchState()
    {
        if (client == null || currentLocation == null)
        {
            logger.warning("Client or current location is null, cannot detect patch state");
            return PatchState.UNKNOWN;
        }
        
        try
        {
            // Get the varbit ID for this location
            int varbitId = getVarbitIdForLocation(currentLocation);
            if (varbitId == -1)
            {
                logger.log(Level.WARNING, "No varbit ID found for location: {0}", currentLocation.getName());
                return PatchState.UNKNOWN;
            }
            
            // Get the varbit value from the client
            int varbitValue = client.getVarbitValue(varbitId);
            logger.log(Level.FINE, "Retrieved varbit value {0} for location {1}", new Object[]{varbitValue, currentLocation.getName()});
            
            // Map the varbit value to a PatchState based on patch type
            PatchType patchType = getPatchTypeForLocation(currentLocation);
            return mapVarbitValueToPatchState(varbitValue, patchType);
        }
        catch (Exception e)
        {
            logger.log(Level.WARNING, "Exception while detecting patch state: {0}", e.getMessage());
            return PatchState.UNKNOWN;
        }
    }
    
    /**
     * Get the varbit ID for a specific farming location
     * 
     * TODO: Currently only supports herb patches. Tree, fruit tree, allotment, hop, bush, 
     * spirit tree, and special patch types are not yet implemented. 
     * 
     * For broader patch support, implement mappings for other PatchType categories by adding 
     * their corresponding varbit IDs in the switch statement or a separate mapping structure.
     * See issue #XXX for tracking broader patch support implementation.
     */
    private int getVarbitIdForLocation(Location location)
    {
        if (location == null)
        {
            return -1;
        }
        
        // Check if this location has non-herb patch types
        Set<PatchType> patchTypes = location.getPatchTypes();
        boolean hasNonHerbPatches = patchTypes.stream().anyMatch(type -> type != PatchType.HERB);
        
        if (hasNonHerbPatches)
        {
            logger.info("Location '" + location.getName() + "' contains non-herb patch types: " + patchTypes + 
                       ". Only herb patch varbits are supported in this release. " +
                       "See issue #XXX for broader patch support implementation.");
        }
        
        String locationName = location.getName().toLowerCase();
        
        // Map location names to their corresponding varbit IDs
        // These are the main herb patch varbits based on OSRS farming system
        // TODO: Add varbit mappings for other patch types (TREE, FRUIT_TREE, ALLOTMENT, HOP, BUSH, SPIRIT_TREE, SPECIAL)
        switch (locationName)
        {
            case "ardougne":
                return 4772; // Ardougne herb patch
            case "catherby":
                return 4773; // Catherby herb patch
            case "falador":
                return 4774; // Falador herb patch
            case "morytania":
                return 4775; // Morytania herb patch
            case "trollstronghold":
                return 4776; // Troll Stronghold herb patch
            case "kourend":
                return 4777; // Kourend herb patch
            case "farmingguild":
                return 4778; // Farming Guild herb patch
            case "harmonyisland":
                return 4779; // Harmony Island herb patch
            case "weiss":
                return 4780; // Weiss herb patch
            default:
                logger.warning("Unknown location for varbit mapping: " + locationName);
                return -1;
        }
    }
    
    /**
     * Get the primary patch type for a location
     * Returns the first patch type found, or HERB as default for herb patches
     */
    private PatchType getPatchTypeForLocation(Location location)
    {
        if (location == null)
        {
            return PatchType.HERB; // Default fallback
        }
        
        Set<PatchType> patchTypes = location.getPatchTypes();
        if (patchTypes.isEmpty())
        {
            return PatchType.HERB; // Default fallback
        }
        
        // Return the first patch type found
        // In most cases, locations will have only one patch type
        return patchTypes.iterator().next();
    }
    
    /**
     * Map varbit values to PatchState enum values based on patch type
     * Uses OSRS FARMING_PATCH_STATUS varbit definitions for accurate growth stage interpretation
     */
    private PatchState mapVarbitValueToPatchState(int varbitValue, PatchType patchType)
    {
        if (patchType == null)
        {
            logger.warning("Patch type is null, returning UNKNOWN");
            return PatchState.UNKNOWN;
        }
        
        switch (patchType)
        {
            case HERB:
                return mapHerbVarbitToPatchState(varbitValue);
            case TREE:
                return mapTreeVarbitToPatchState(varbitValue);
            case FRUIT_TREE:
                return mapFruitTreeVarbitToPatchState(varbitValue);
            case ALLOTMENT:
                return mapAllotmentVarbitToPatchState(varbitValue);
            case HOP:
                return mapHopVarbitToPatchState(varbitValue);
            case BUSH:
                return mapBushVarbitToPatchState(varbitValue);
            case SPIRIT_TREE:
                return mapSpiritTreeVarbitToPatchState(varbitValue);
            case SPECIAL:
                return mapSpecialVarbitToPatchState(varbitValue);
            default:
                logger.warning("Unknown patch type: " + patchType + ", returning UNKNOWN");
                return PatchState.UNKNOWN;
        }
    }
    
    /**
     * Map herb patch varbit values to PatchState
     * Based on OSRS herb patch varbit ranges (4771-4775, 7904-7914)
     */
    private PatchState mapHerbVarbitToPatchState(int varbitValue)
    {
        switch (varbitValue)
        {
            case 0:
                return PatchState.EMPTY;
            case 1:
            case 2:
            case 3:
                return PatchState.GROWING;
            case 4:
                return PatchState.READY;
            case 5:
                return PatchState.DISEASED;
            case 6:
                return PatchState.DEAD;
            case 7:
                return PatchState.WATERED;
            case 8:
                return PatchState.COMPOSTED;
            case 9:
                return PatchState.PROTECTED;
            default:
                logger.fine("Unknown herb varbit value: " + varbitValue + ", returning UNKNOWN");
                return PatchState.UNKNOWN;
        }
    }
    
    /**
     * Map tree patch varbit values to PatchState
     * Based on OSRS tree patch varbit ranges
     */
    private PatchState mapTreeVarbitToPatchState(int varbitValue)
    {
        switch (varbitValue)
        {
            case 0:
                return PatchState.EMPTY;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return PatchState.GROWING;
            case 7:
                return PatchState.READY;
            case 8:
                return PatchState.DISEASED;
            case 9:
                return PatchState.DEAD;
            case 10:
                return PatchState.WATERED;
            case 11:
                return PatchState.COMPOSTED;
            case 12:
                return PatchState.PROTECTED;
            default:
                logger.fine("Unknown tree varbit value: " + varbitValue + ", returning UNKNOWN");
                return PatchState.UNKNOWN;
        }
    }
    
    /**
     * Map fruit tree patch varbit values to PatchState
     * Based on OSRS fruit tree patch varbit ranges
     */
    private PatchState mapFruitTreeVarbitToPatchState(int varbitValue)
    {
        switch (varbitValue)
        {
            case 0:
                return PatchState.EMPTY;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return PatchState.GROWING;
            case 7:
                return PatchState.READY;
            case 8:
                return PatchState.DISEASED;
            case 9:
                return PatchState.DEAD;
            case 10:
                return PatchState.WATERED;
            case 11:
                return PatchState.COMPOSTED;
            case 12:
                return PatchState.PROTECTED;
            default:
                logger.fine("Unknown fruit tree varbit value: " + varbitValue + ", returning UNKNOWN");
                return PatchState.UNKNOWN;
        }
    }
    
    /**
     * Map allotment patch varbit values to PatchState
     * Based on OSRS allotment patch varbit ranges
     */
    private PatchState mapAllotmentVarbitToPatchState(int varbitValue)
    {
        switch (varbitValue)
        {
            case 0:
                return PatchState.EMPTY;
            case 1:
            case 2:
            case 3:
            case 4:
                return PatchState.GROWING;
            case 5:
                return PatchState.READY;
            case 6:
                return PatchState.DISEASED;
            case 7:
                return PatchState.DEAD;
            case 8:
                return PatchState.WATERED;
            case 9:
                return PatchState.COMPOSTED;
            case 10:
                return PatchState.PROTECTED;
            default:
                logger.fine("Unknown allotment varbit value: " + varbitValue + ", returning UNKNOWN");
                return PatchState.UNKNOWN;
        }
    }
    
    /**
     * Map hop patch varbit values to PatchState
     * Based on OSRS hop patch varbit ranges
     */
    private PatchState mapHopVarbitToPatchState(int varbitValue)
    {
        switch (varbitValue)
        {
            case 0:
                return PatchState.EMPTY;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return PatchState.GROWING;
            case 9:
                return PatchState.READY;
            case 10:
                return PatchState.DISEASED;
            case 11:
                return PatchState.DEAD;
            case 12:
                return PatchState.WATERED;
            case 13:
                return PatchState.COMPOSTED;
            case 14:
                return PatchState.PROTECTED;
            default:
                logger.fine("Unknown hop varbit value: " + varbitValue + ", returning UNKNOWN");
                return PatchState.UNKNOWN;
        }
    }
    
    /**
     * Map bush patch varbit values to PatchState
     * Based on OSRS bush patch varbit ranges
     */
    private PatchState mapBushVarbitToPatchState(int varbitValue)
    {
        switch (varbitValue)
        {
            case 0:
                return PatchState.EMPTY;
            case 1:
            case 2:
            case 3:
            case 4:
                return PatchState.GROWING;
            case 5:
                return PatchState.READY;
            case 6:
                return PatchState.DISEASED;
            case 7:
                return PatchState.DEAD;
            case 8:
                return PatchState.WATERED;
            case 9:
                return PatchState.COMPOSTED;
            case 10:
                return PatchState.PROTECTED;
            default:
                logger.fine("Unknown bush varbit value: " + varbitValue + ", returning UNKNOWN");
                return PatchState.UNKNOWN;
        }
    }
    
    /**
     * Map spirit tree patch varbit values to PatchState
     * Based on OSRS spirit tree patch varbit ranges
     */
    private PatchState mapSpiritTreeVarbitToPatchState(int varbitValue)
    {
        switch (varbitValue)
        {
            case 0:
                return PatchState.EMPTY;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return PatchState.GROWING;
            case 10:
                return PatchState.READY;
            case 11:
                return PatchState.DISEASED;
            case 12:
                return PatchState.DEAD;
            case 13:
                return PatchState.WATERED;
            case 14:
                return PatchState.COMPOSTED;
            case 15:
                return PatchState.PROTECTED;
            default:
                logger.fine("Unknown spirit tree varbit value: " + varbitValue + ", returning UNKNOWN");
                return PatchState.UNKNOWN;
        }
    }
    
    /**
     * Map special patch varbit values to PatchState
     * Based on OSRS special patch varbit ranges (varies by patch)
     */
    private PatchState mapSpecialVarbitToPatchState(int varbitValue)
    {
        // Special patches have varying varbit ranges depending on the specific patch
        // For now, use a generic mapping with fallback to UNKNOWN for unknown values
        switch (varbitValue)
        {
            case 0:
                return PatchState.EMPTY;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return PatchState.GROWING;
            case 6:
                return PatchState.READY;
            case 7:
                return PatchState.DISEASED;
            case 8:
                return PatchState.DEAD;
            case 9:
                return PatchState.WATERED;
            case 10:
                return PatchState.COMPOSTED;
            case 11:
                return PatchState.PROTECTED;
            default:
                logger.fine("Unknown special patch varbit value: " + varbitValue + ", returning UNKNOWN");
                return PatchState.UNKNOWN;
        }
    }

    /**
     * Check if the current action is complete
     */
    private boolean isActionComplete()
    {
        // This would check if the current action (harvesting, planting, etc.) is complete
        // For now, return false to prevent automatic progression
        return false;
    }

    /**
     * Move to the next location in the run
     */
    private boolean moveToNextLocation()
    {
        currentLocationIndex++;
        if (currentLocationIndex >= enabledLocations.size())
        {
            return false; // No more locations
        }
        
        currentLocation = enabledLocations.get(currentLocationIndex);
        currentPatchState = PatchState.UNKNOWN;
        return true;
    }

    // Getters
    public FarmingState getCurrentState()
    {
        return currentState;
    }

    public Location getCurrentLocation()
    {
        return currentLocation;
    }

    public PatchState getCurrentPatchState()
    {
        return currentPatchState;
    }

    public List<Location> getEnabledLocations()
    {
        return new ArrayList<>(enabledLocations);
    }

    public Set<Location> getCompletedLocations()
    {
        return new HashSet<>(completedLocations);
    }

    public boolean isRunActive()
    {
        return currentState.isActiveRun();
    }

    public boolean isHerbRunActive()
    {
        return isHerbRunActive;
    }

    public boolean isTreeRunActive()
    {
        return isTreeRunActive;
    }

    public boolean isFruitTreeRunActive()
    {
        return isFruitTreeRunActive;
    }

    public boolean isAllotmentRunActive()
    {
        return isAllotmentRunActive;
    }
}
