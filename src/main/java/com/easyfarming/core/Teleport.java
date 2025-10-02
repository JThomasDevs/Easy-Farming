package com.easyfarming.core;

import net.runelite.api.coords.WorldPoint;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a teleport option for a farming location
 */
public class Teleport
{
    public enum TeleportCategory
    {
        SPELLBOOK,      // Standard spellbook teleports
        ITEM,           // Teleport items (tabs, jewelry, etc.)
        PORTAL_NEXUS,   // Player-owned house portal nexus
        JEWELLERY_BOX,  // Player-owned house jewellery box
        SPIRIT_TREE,    // Spirit tree network
        MOUNTED_XERICS  // Mounted Xeric's talisman in PoH
    }
    
    private final String name;
    private final TeleportCategory category;
    private final String description;
    private final int itemId;                    // Item ID for item-based teleports (0 for spells)
    private final String rightClickOption;       // Right-click option for items
    private final int spellId;                   // Spell ID for spellbook teleports (0 for items)
    private final WorldPoint destination;        // Where player lands after teleporting
    private final List<ItemRequirement> requirements;
    private final int regionId;                  // Region ID for teleport destination
    
    public Teleport(String name, TeleportCategory category, String description, 
                   int itemId, String rightClickOption, int spellId, 
                   WorldPoint destination, List<ItemRequirement> requirements)
    {
        // Validate parameters
        this.name = Objects.requireNonNull(name, "name must not be null");
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("name must not be empty");
        }
        
        this.category = Objects.requireNonNull(category, "category must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        
        if (itemId < 0) {
            throw new IllegalArgumentException("itemId must not be negative");
        }
        this.itemId = itemId;
        
        this.rightClickOption = rightClickOption == null ? "" : rightClickOption;
        
        if (spellId < 0) {
            throw new IllegalArgumentException("spellId must not be negative");
        }
        this.spellId = spellId;
        
        this.destination = Objects.requireNonNull(destination, "destination must not be null");
        
        Objects.requireNonNull(requirements, "requirements must not be null");
        // Check for null entries in requirements
        for (ItemRequirement req : requirements) {
            if (req == null) {
                throw new IllegalArgumentException("requirements list must not contain null entries");
            }
        }
        this.requirements = new ArrayList<>(requirements);
        this.regionId = destination.getRegionID();
    }
    
    /**
     * Create a teleport with a single item requirement
     */
    public Teleport(String name, TeleportCategory category, String description,
                   int itemId, String rightClickOption, int spellId,
                   WorldPoint destination, int requiredItemId, int quantity)
    {
        this(name, category, description, itemId, rightClickOption, spellId, destination,
             List.of(new ItemRequirement(requiredItemId, quantity)));
    }
    
    /**
     * Create a spellbook teleport
     */
    public Teleport(String name, String description, int spellId, 
                   WorldPoint destination, List<ItemRequirement> requirements)
    {
        this(name, TeleportCategory.SPELLBOOK, description, 0, null, spellId, 
             destination, requirements);
    }
    
    /**
     * Create an item teleport
     */
    public Teleport(String name, String description, int itemId, String rightClickOption,
                   WorldPoint destination, List<ItemRequirement> requirements)
    {
        this(name, TeleportCategory.ITEM, description, itemId, rightClickOption, 0,
             destination, requirements);
    }
    
    // Getters
    public String getName() { return name; }
    public TeleportCategory getCategory() { return category; }
    public String getDescription() { return description; }
    public int getItemId() { return itemId; }
    public String getRightClickOption() { return rightClickOption; }
    public int getSpellId() { return spellId; }
    public WorldPoint getDestination() { return destination; }
    public List<ItemRequirement> getRequirements() { return new ArrayList<>(requirements); }
    public int getRegionId() { return regionId; }
    
    /**
     * Check if this is a house-based teleport
     */
    public boolean isHouseTeleport()
    {
        return category == TeleportCategory.PORTAL_NEXUS || 
               category == TeleportCategory.JEWELLERY_BOX || 
               category == TeleportCategory.MOUNTED_XERICS;
    }
    
    /**
     * Check if this is a spellbook teleport
     */
    public boolean isSpellTeleport()
    {
        return category == TeleportCategory.SPELLBOOK;
    }
    
    /**
     * Check if this is an item teleport
     */
    public boolean isItemTeleport()
    {
        return category == TeleportCategory.ITEM;
    }
    
    @Override
    public String toString()
    {
        return "Teleport{name='" + name + "', category=" + category + 
               ", description='" + description + "'}";
    }
}
