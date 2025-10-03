package com.easyfarming.core;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Centralized utility class for mapping varbit IDs and values to patch states.
 * This class consolidates the varbit-to-patch logic that was previously duplicated
 * across FarmingEventDetector and FarmingRunState.
 */
@Slf4j
public class VarbitMapper
{
    
    /**
     * Get the varbit ID for herb patches at a specific farming location.
     * Returns the varbit ID used to track herb patch states in Old School RuneScape.
     * 
     * @param location The farming location containing herb patches
     * @return The varbit ID for the location's herb patches, or -1 if unknown
     * @see <a href="https://oldschool.runescape.wiki/w/Varbit">OSRS Wiki - Varbit</a>
     */
    public static int getHerbPatchVarbitId(Location location)
    {
        if (location == null)
        {
            log.warn("Location is null for varbit ID mapping");
            return -1;
        }
        
        String locationName = location.getName().toLowerCase();
        
        // These varbit IDs correspond specifically to main herb patches per the OSRS Wiki
        // Source: https://oldschool.runescape.wiki/w/Varbit
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
                log.warn("Unknown location for varbit mapping: {}", locationName);
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
            log.warn("Location is null for patch type mapping, returning HERB as default");
            return PatchType.HERB;
        }
        
        Set<PatchType> patchTypes = location.getPatchTypes();
        if (patchTypes.isEmpty())
        {
            log.debug("No patch types found for location: {}, returning HERB as default", location.getName());
            return PatchType.HERB;
        }
        
        // Return the first patch type found
        // In most cases, locations will have only one patch type
        PatchType primaryType = patchTypes.iterator().next();
        log.debug("Primary patch type for {}: {}", location.getName(), primaryType);
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
            log.warn("Patch type is null, returning UNKNOWN");
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
                log.warn("Unknown patch type: {}, returning UNKNOWN", patchType);
                return PatchState.UNKNOWN;
        }
    }
    
    /**
     * Map herb patch varbit values to PatchState.
     * 
     * Varbit IDs for herb patches:
     * - Ardougne: 4771
     * - Catherby: 4772
     * - Falador: 4773
     * - Farming Guild: 4774
     * - Harmony: 4775
     * - Kourend: 7904
     * - Morytania: 7905
     * - Troll Stronghold: 7906
     * - Weiss: 7907
     * 
     * @param varbitValue The varbit value from the client (0-9)
     * @return The corresponding PatchState
     * @see <a href="https://oldschool.runescape.wiki/w/Farming#Herb_patches">OSRS Wiki - Farming Herb Patches</a>
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
                log.debug("Unknown herb varbit value: {}, returning UNKNOWN", varbitValue);
                return PatchState.UNKNOWN;
        }
    }
    
    /**
     * Map tree patch varbit values to PatchState.
     * 
     * Varbit IDs for tree patches vary by location and are typically in the 7900+ range.
     * Common tree patch locations include Lumbridge, Varrock, Falador, Taverley, and Gnome Stronghold.
     * 
     * @param varbitValue The varbit value from the client (0-12)
     * @return The corresponding PatchState
     * @see <a href="https://oldschool.runescape.wiki/w/Farming#Tree_patches">OSRS Wiki - Farming Tree Patches</a>
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
                log.debug("Unknown tree varbit value: {}, returning UNKNOWN", varbitValue);
                return PatchState.UNKNOWN;
        }
    }
    
    /**
     * Map fruit tree patch varbit values to PatchState.
     * 
     * Varbit IDs for fruit tree patches vary by location and are typically in the 7900+ range.
     * Common fruit tree patch locations include Gnome Stronghold, Tree Gnome Village, Brimhaven, and Catherby.
     * 
     * @param varbitValue The varbit value from the client (0-12)
     * @return The corresponding PatchState
     * @see <a href="https://oldschool.runescape.wiki/w/Farming#Fruit_tree_patches">OSRS Wiki - Farming Fruit Tree Patches</a>
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
                log.debug("Unknown fruit tree varbit value: {}, returning UNKNOWN", varbitValue);
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
                log.debug("Unknown allotment varbit value: {}, returning UNKNOWN", varbitValue);
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
                log.debug("Unknown hop varbit value: {}, returning UNKNOWN", varbitValue);
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
                log.debug("Unknown bush varbit value: {}, returning UNKNOWN", varbitValue);
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
                log.debug("Unknown spirit tree varbit value: {}, returning UNKNOWN", varbitValue);
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
                log.debug("Unknown special patch varbit value: {}, returning UNKNOWN", varbitValue);
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
                log.debug("Unknown flower varbit value: {}, returning UNKNOWN", varbitValue);
                return PatchState.UNKNOWN;
        }
    }
}
