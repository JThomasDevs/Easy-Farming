package com.easyfarming.core;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Centralized utility class for mapping varbit IDs and values to patch states.
 * This class consolidates the varbit-to-patch logic that was previously duplicated
 * across FarmingEventDetector and FarmingRunState.
 */
public class VarbitMapper
{
    private static final Logger logger = Logger.getLogger(VarbitMapper.class.getName());
    
    /**
     * Get the varbit ID for a specific farming location
     * @param location The farming location
     * @return The varbit ID for the location, or -1 if unknown
     */
    public static int getVarbitIdForLocation(Location location)
    {
        if (location == null)
        {
            logger.warning("Location is null for varbit ID mapping");
            return -1;
        }
        
        String locationName = location.getName().toLowerCase();
        
        // Map location names to their corresponding varbit IDs
        // These are the main herb patch varbits based on OSRS farming system
        switch (locationName)
        {
            case "ardougne":
                return 4771;
            case "catherby":
                return 4772;
            case "falador":
                return 4773;
            case "farming guild":
                return 4774;
            case "harmony":
                return 4775;
            case "kourend":
                return 7904;
            case "morytania":
                return 7905;
            case "troll stronghold":
                return 7906;
            case "weiss":
                return 7907;
            default:
                logger.warning("Unknown location for varbit mapping: " + locationName);
                return -1;
        }
    }
    
    /**
     * Get the patch type for a specific location based on its metadata
     * @param location The farming location
     * @return The primary patch type for the location, or HERB as default
     */
    public static PatchType getPatchTypeForLocation(Location location)
    {
        if (location == null)
        {
            logger.warning("Location is null for patch type mapping, returning HERB as default");
            return PatchType.HERB;
        }
        
        Set<PatchType> patchTypes = location.getPatchTypes();
        if (patchTypes.isEmpty())
        {
            logger.fine("No patch types found for location: " + location.getName() + ", returning HERB as default");
            return PatchType.HERB;
        }
        
        // Return the first patch type found
        // In most cases, locations will have only one patch type
        PatchType primaryType = patchTypes.iterator().next();
        logger.fine("Primary patch type for " + location.getName() + ": " + primaryType);
        return primaryType;
    }
    
    /**
     * Map varbit values to PatchState enum values based on patch type
     * @param varbitValue The varbit value from the client
     * @param patchType The type of patch
     * @return The corresponding PatchState
     */
    public static PatchState mapVarbitValueToPatchState(int varbitValue, PatchType patchType)
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
            case FLOWER:
                return mapFlowerVarbitToPatchState(varbitValue);
            default:
                logger.warning("Unknown patch type: " + patchType + ", returning UNKNOWN");
                return PatchState.UNKNOWN;
        }
    }
    
    /**
     * Map herb patch varbit values to PatchState
     * Based on OSRS herb patch varbit ranges (4771-4775, 7904-7914)
     */
    private static PatchState mapHerbVarbitToPatchState(int varbitValue)
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
    private static PatchState mapTreeVarbitToPatchState(int varbitValue)
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
    private static PatchState mapFruitTreeVarbitToPatchState(int varbitValue)
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
    private static PatchState mapAllotmentVarbitToPatchState(int varbitValue)
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
    private static PatchState mapHopVarbitToPatchState(int varbitValue)
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
    private static PatchState mapBushVarbitToPatchState(int varbitValue)
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
    private static PatchState mapSpiritTreeVarbitToPatchState(int varbitValue)
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
    private static PatchState mapSpecialVarbitToPatchState(int varbitValue)
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
     * Map flower patch varbit values to PatchState
     * Based on OSRS flower patch varbit ranges
     */
    private static PatchState mapFlowerVarbitToPatchState(int varbitValue)
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
                logger.fine("Unknown flower varbit value: " + varbitValue + ", returning UNKNOWN");
                return PatchState.UNKNOWN;
        }
    }
}
