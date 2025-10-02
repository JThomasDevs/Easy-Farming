package com.easyfarming.core;

import net.runelite.api.coords.WorldPoint;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.Collections;

/**
 * Represents a farming location with its patch coordinates and teleport options
 */
public class Location
{
    private final String name;
    private final WorldPoint patchPoint;
    private final boolean farmLimps;
    private final List<Teleport> teleportOptions;
    private final Function<Object, String> selectedTeleportFunction; // Will be replaced with proper config function
    
    public Location(String name, WorldPoint patchPoint, boolean farmLimps)
    {
        this.name = name;
        this.patchPoint = patchPoint;
        this.farmLimps = farmLimps;
        this.teleportOptions = new ArrayList<>();
        this.selectedTeleportFunction = null; // Will be set by config system
    }
    
    /**
     * Add a teleport option to this location
     */
    public void addTeleportOption(Teleport teleport)
    {
        teleportOptions.add(teleport);
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
    
    @Override
    public String toString()
    {
        return "Location{name='" + name + "', patchPoint=" + patchPoint + 
               ", teleportOptions=" + teleportOptions.size() + "}";
    }
}