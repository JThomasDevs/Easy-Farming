package com.easyfarming.core;

/**
 * Represents the current state of a farming run.
 * This enum defines all possible states the player can be in during a farming run.
 */
public enum FarmingState
{
    /**
     * No farming run is active
     */
    IDLE("Idle", "No farming run active"),
    
    /**
     * Gathering required items before starting the run
     */
    GATHERING_ITEMS("Gathering Items", "Collecting required items for the farming run"),
    
    /**
     * Ready to start teleporting - all items gathered
     */
    READY_TO_TELEPORT("Ready to Teleport", "All items gathered, ready to teleport"),
    
    /**
     * Player is teleporting (animation in progress)
     */
    TELEPORTING("Teleporting", "Teleporting to farming location"),
    
    /**
     * Player has teleported and needs to navigate to the patch
     */
    NAVIGATING("Navigating", "Running to the farming patch"),
    
    /**
     * Player is at the patch and needs to interact with it
     */
    AT_PATCH("At Patch", "At the farming patch, ready to interact"),
    
    /**
     * Player is harvesting crops from the patch
     */
    HARVESTING("Harvesting", "Harvesting crops from the patch"),
    
    /**
     * Player is planting new seeds in the patch
     */
    PLANTING("Planting", "Planting new seeds in the patch"),
    
    /**
     * Player is treating diseased crops
     */
    TREATING_DISEASE("Treating Disease", "Using plant cure on diseased crops"),
    
    /**
     * Player is removing dead crops
     */
    REMOVING_DEAD("Removing Dead", "Removing dead crops from the patch"),
    
    /**
     * Player is applying compost to the patch
     */
    COMPOSTING("Composting", "Applying compost to the patch"),
    
    /**
     * Player is watering the patch
     */
    WATERING("Watering", "Watering the patch"),
    
    /**
     * Current location is complete, moving to next location
     */
    MOVING_TO_NEXT("Moving to Next", "Moving to the next farming location"),
    
    /**
     * All farming locations are complete
     */
    RUN_COMPLETE("Run Complete", "All farming locations have been completed"),
    
    /**
     * Farming run has been paused or stopped
     */
    PAUSED("Paused", "Farming run has been paused"),
    
    /**
     * An error occurred during the farming run
     */
    ERROR("Error", "An error occurred during the farming run");

    private final String displayName;
    private final String description;

    FarmingState(String displayName, String description)
    {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Get the display name for this state
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * Get the description for this state
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Check if this state represents an active farming run
     */
    public boolean isActiveRun()
    {
        return this != IDLE && this != RUN_COMPLETE && this != PAUSED && this != ERROR;
    }

    /**
     * Check if this state represents the player being at a patch
     */
    public boolean isAtPatch()
    {
        return this == AT_PATCH || this == HARVESTING || this == PLANTING || 
               this == TREATING_DISEASE || this == REMOVING_DEAD || 
               this == COMPOSTING || this == WATERING;
    }

    /**
     * Check if this state represents the player traveling
     */
    public boolean isTraveling()
    {
        return this == TELEPORTING || this == NAVIGATING || this == MOVING_TO_NEXT;
    }

    /**
     * Check if this state represents the player preparing for the run
     */
    public boolean isPreparing()
    {
        return this == GATHERING_ITEMS || this == READY_TO_TELEPORT;
    }
}
