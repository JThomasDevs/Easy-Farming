package com.easyfarming.core;

/**
 * Represents the state of a farming patch.
 * This enum defines all possible states a patch can be in.
 */
public enum PatchState
{
    /**
     * Patch state is unknown or not yet checked
     */
    UNKNOWN("Unknown", "Patch state not yet determined"),
    
    /**
     * Patch is empty and ready for planting
     */
    EMPTY("Empty", "Patch is empty and ready for planting"),
    
    /**
     * Patch has crops that are ready to harvest
     */
    READY("Ready", "Crops are ready to harvest"),
    
    /**
     * Patch has crops that are diseased and need treatment
     */
    DISEASED("Diseased", "Crops are diseased and need plant cure"),
    
    /**
     * Patch has dead crops that need to be removed
     */
    DEAD("Dead", "Crops are dead and need to be removed"),
    
    /**
     * Patch has crops that are growing (not ready yet)
     */
    GROWING("Growing", "Crops are still growing"),
    
    /**
     * Patch has crops that are watered
     */
    WATERED("Watered", "Crops have been watered"),
    
    /**
     * Patch has been composted
     */
    COMPOSTED("Composted", "Patch has been composted"),
    
    /**
     * Patch is protected from disease
     */
    PROTECTED("Protected", "Patch is protected from disease");

    private final String displayName;
    private final String description;

    PatchState(String displayName, String description)
    {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Get the display name for this patch state
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * Get the description for this patch state
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Check if this patch state requires immediate action
     */
    public boolean requiresAction()
    {
        return this == READY || this == DISEASED || this == DEAD;
    }

    /**
     * Check if this patch state allows planting
     */
    public boolean canPlant()
    {
        return this == EMPTY || this == DEAD;
    }

    /**
     * Check if this patch state allows harvesting
     */
    public boolean canHarvest()
    {
        return this == READY;
    }

    /**
     * Check if this patch state requires treatment
     */
    public boolean needsTreatment()
    {
        return this == DISEASED;
    }

    /**
     * Check if this patch state requires removal
     */
    public boolean needsRemoval()
    {
        return this == DEAD;
    }
}
