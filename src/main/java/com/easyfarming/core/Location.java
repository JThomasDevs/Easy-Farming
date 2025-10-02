package com.easyfarming.core;

import net.runelite.api.coords.WorldPoint;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Function;

/**
 * Represents a farming location with its patch coordinates and teleport options
 */
public class Location
{
    private final String name;
    private final WorldPoint patchPoint;
    private final boolean farmLimps;
    private final boolean enabled;
    private final List<Teleport> teleportOptions;
    private final Function<Object, String> selectedTeleportFunction; // Will be replaced with proper config function
    private final Set<PatchType> patchTypes;
    
    public Location(String name, WorldPoint patchPoint, boolean farmLimps)
    {
        this.name = name;
        this.patchPoint = patchPoint;
        this.farmLimps = farmLimps;
        this.enabled = true; // Default to enabled
        this.teleportOptions = new ArrayList<>();
        this.selectedTeleportFunction = null; // Will be set by config system
        this.patchTypes = new HashSet<>();
    }
    
    public Location(String name, WorldPoint patchPoint, boolean farmLimps, Set<PatchType> patchTypes)
    {
        this.name = name;
        this.patchPoint = patchPoint;
        this.farmLimps = farmLimps;
        this.enabled = true; // Default to enabled
        this.teleportOptions = new ArrayList<>();
        this.selectedTeleportFunction = null; // Will be set by config system
        this.patchTypes = new HashSet<>(patchTypes);
    }
    
    public Location(String name, WorldPoint patchPoint, boolean farmLimps, boolean enabled)
    {
        this.name = name;
        this.patchPoint = patchPoint;
        this.farmLimps = farmLimps;
        this.enabled = enabled;
        this.teleportOptions = new ArrayList<>();
        this.selectedTeleportFunction = null; // Will be set by config system
        this.patchTypes = new HashSet<>();
    }
    
    public Location(String name, WorldPoint patchPoint, boolean farmLimps, boolean enabled, Set<PatchType> patchTypes)
    {
        this.name = name;
        this.patchPoint = patchPoint;
        this.farmLimps = farmLimps;
        this.enabled = enabled;
        this.teleportOptions = new ArrayList<>();
        this.selectedTeleportFunction = null; // Will be set by config system
        this.patchTypes = new HashSet<>(patchTypes);
    }
    
    /**
     * Add a teleport option to this location
     */
    public void addTeleportOption(Teleport teleport)
    {
        teleportOptions.add(teleport);
    }
    
    /**
     * Add a patch type to this location
     */
    public void addPatchType(PatchType patchType)
    {
        patchTypes.add(patchType);
    }
    
    /**
     * Check if this location has a specific patch type
     */
    public boolean hasPatchType(PatchType patchType)
    {
        return patchTypes.contains(patchType);
    }
    
    /**
     * Get the selected teleport option (will be implemented with config system)
     */
    public Teleport getSelectedTeleport()
    {
        // For now, return the first teleport option
        // This will be replaced with proper config integration
        return teleportOptions.isEmpty() ? null : teleportOptions.get(0);
    }
    
    /**
     * Get a teleport option by name
     */
    public Teleport getTeleportOption(String name)
    {
        return teleportOptions.stream()
            .filter(teleport -> teleport.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Get all teleport options for this location
     */
    public List<Teleport> getTeleportOptions()
    {
        return new ArrayList<>(teleportOptions);
    }
    
    // Getters
    public String getName() { return name; }
    public WorldPoint getPatchPoint() { return patchPoint; }
    public boolean getFarmLimps() { return farmLimps; }
    public boolean isEnabled() { return enabled; }
    public Set<PatchType> getPatchTypes() { return new HashSet<>(patchTypes); }
    
    @Override
    public String toString()
    {
        return "Location{name='" + name + "', patchPoint=" + patchPoint + 
               ", teleportOptions=" + teleportOptions.size() + "}";
    }
}