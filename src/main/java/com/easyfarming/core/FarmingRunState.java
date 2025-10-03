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
    private final FarmingEventDetector eventDetector;
    
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
        this.eventDetector = new FarmingEventDetector(client, requirementManager);
        
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
                // Check if teleport animation is complete using event detector
                if (eventDetector.hasPlayerTeleported(lastPlayerPosition, playerPosition))
                {
                    currentState = FarmingState.NAVIGATING;
                }
                break;
                
            case NAVIGATING:
                if (eventDetector.isPlayerAtPatch(currentLocation))
                {
                    currentState = FarmingState.AT_PATCH;
                    currentPatchState = eventDetector.getCurrentPatchState(currentLocation);
                }
                break;
                
            case AT_PATCH:
                currentPatchState = eventDetector.getCurrentPatchState(currentLocation);
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
                // TODO: Implement proper action completion detection or timing-based progression
                // For now, these states would need to be managed by the UI/overlay system
                // based on patch state detection rather than action completion detection
                logger.warning("Action state " + currentState + " needs proper completion handling");
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
