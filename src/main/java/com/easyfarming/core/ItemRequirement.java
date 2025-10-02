package com.easyfarming.core;

import java.util.List;

import net.runelite.api.gameval.ItemID;

import java.util.ArrayList;

/**
 * Represents an item requirement for farming runs
 */
public class ItemRequirement
{
    private final int itemId;
    private final int quantity;
    private final boolean tierAgnostic;
    private final List<Integer> alternativeItems;
    private final boolean isPohRequirement;
    private final int constructionLevel;
    private final String pohFurnitureName;
    private final boolean allowDiaryCompletion;
    
    /**
     * Create a simple item requirement
     */
    public ItemRequirement(int itemId, int quantity)
    {
        this.itemId = itemId;
        this.quantity = quantity;
        this.tierAgnostic = false;
        this.alternativeItems = new ArrayList<>();
        this.isPohRequirement = false;
        this.constructionLevel = 0;
        this.pohFurnitureName = null;
        this.allowDiaryCompletion = false;
    }
    
    /**
     * Create a tier-agnostic item requirement (for diary items)
     */
    public ItemRequirement(int baseItemId, int quantity, boolean tierAgnostic)
    {
        this.itemId = baseItemId;
        this.quantity = quantity;
        this.tierAgnostic = tierAgnostic;
        this.alternativeItems = new ArrayList<>();
        this.isPohRequirement = false;
        this.constructionLevel = 0;
        this.pohFurnitureName = null;
        this.allowDiaryCompletion = false;
        
        if (tierAgnostic)
        {
            this.alternativeItems.addAll(getTierVariants(baseItemId));
        }
    }
    
    /**
     * Create an item requirement with alternative items (for seeds)
     */
    public ItemRequirement(int primaryItemId, int quantity, List<Integer> alternativeItems)
    {
        this.itemId = primaryItemId;
        this.quantity = quantity;
        this.tierAgnostic = false;
        this.alternativeItems = alternativeItems == null ? new ArrayList<>() : new ArrayList<>(alternativeItems);
        this.isPohRequirement = false;
        this.constructionLevel = 0;
        this.pohFurnitureName = null;
        this.allowDiaryCompletion = false;
    }
    
    /**
     * Create a POH furniture requirement
     */
    public ItemRequirement(int constructionLevel, String pohFurnitureName)
    {
        this.itemId = 0; // No item required for POH
        this.quantity = 0;
        this.tierAgnostic = false;
        this.alternativeItems = new ArrayList<>();
        this.isPohRequirement = true;
        this.constructionLevel = constructionLevel;
        this.pohFurnitureName = pohFurnitureName;
        this.allowDiaryCompletion = false;
    }
    
    /**
     * Create a multi-item requirement with diary completion option
     */
    public ItemRequirement(List<Integer> itemIds, int quantity, boolean allowDiaryCompletion)
    {
        if (itemIds == null) {
            throw new IllegalArgumentException("itemIds cannot be null");
        }
        
        this.itemId = itemIds.isEmpty() ? 0 : itemIds.get(0);
        this.quantity = quantity;
        this.tierAgnostic = false;
        this.alternativeItems = itemIds.size() > 1 ? new ArrayList<>(itemIds.subList(1, itemIds.size())) : new ArrayList<>();
        this.isPohRequirement = false;
        this.constructionLevel = 0;
        this.pohFurnitureName = null;
        this.allowDiaryCompletion = allowDiaryCompletion;
        // Note: Diary completion logic would need to be implemented in the requirement checking system
    }
    
    /**
     * Get tier variants for diary items
     */
    private List<Integer> getTierVariants(int baseItemId)
    {
        List<Integer> variants = new ArrayList<>();
        
        // Ardougne Cloak variants (2, 3, 4)
        if (baseItemId == ItemID.ARDY_CAPE_MEDIUM) // Ardougne Cloak 2
        {
            variants.add(ItemID.ARDY_CAPE_MEDIUM);
            variants.add(ItemID.ARDY_CAPE_HARD); // Cloak 3
            variants.add(ItemID.ARDY_CAPE_ELITE); // Cloak 4
        }
        // Explorer's Ring variants (2, 3, 4)
        else if (baseItemId == ItemID.LUMBRIDGE_RING_MEDIUM) // Explorer's Ring 2
        {
            variants.add(ItemID.LUMBRIDGE_RING_MEDIUM); // Ring 2
            variants.add(ItemID.LUMBRIDGE_RING_HARD); // Ring 3
            variants.add(ItemID.LUMBRIDGE_RING_ELITE); // Ring 4
        }
        // Skills Necklace variants (1-6)
        else if (baseItemId == ItemID.JEWL_NECKLACE_OF_SKILLS_1) // Skills Necklace 1
        {
            variants.add(ItemID.JEWL_NECKLACE_OF_SKILLS_1); // Necklace 1
            variants.add(ItemID.JEWL_NECKLACE_OF_SKILLS_2); // Necklace 2
            variants.add(ItemID.JEWL_NECKLACE_OF_SKILLS_3); // Necklace 3
            variants.add(ItemID.JEWL_NECKLACE_OF_SKILLS_4); // Necklace 4
            variants.add(ItemID.JEWL_NECKLACE_OF_SKILLS_5); // Necklace 5
            variants.add(ItemID.JEWL_NECKLACE_OF_SKILLS_6); // Necklace 6
        }
        // Combat Bracelet variants (1-6)
        else if (baseItemId == ItemID.JEWL_BRACELET_OF_COMBAT_1) // Combat Bracelet 1
        {
            variants.add(ItemID.JEWL_BRACELET_OF_COMBAT_1); // Bracelet 1
            variants.add(ItemID.JEWL_BRACELET_OF_COMBAT_2); // Bracelet 2
            variants.add(ItemID.JEWL_BRACELET_OF_COMBAT_3); // Bracelet 3
            variants.add(ItemID.JEWL_BRACELET_OF_COMBAT_4); // Bracelet 4
            variants.add(ItemID.JEWL_BRACELET_OF_COMBAT_5); // Bracelet 5
            variants.add(ItemID.JEWL_BRACELET_OF_COMBAT_6); // Bracelet 6
        }        else
        {
            // Default: just the base item
            variants.add(baseItemId);
        }
        
        return variants;
    }
    
    // Getters
    public int getItemId() { return itemId; }
    public int getQuantity() { return quantity; }
    public boolean isTierAgnostic() { return tierAgnostic; }
    public List<Integer> getAlternativeItems() { return new ArrayList<>(alternativeItems); }
    public boolean isPohRequirement() { return isPohRequirement; }
    public int getConstructionLevel() { return constructionLevel; }
    public String getPohFurnitureName() { return pohFurnitureName; }
    public boolean isAllowDiaryCompletion() { return allowDiaryCompletion; }
    
    /**
     * Get all possible item IDs for this requirement
     */
    public List<Integer> getAllItemIds()
    {
        List<Integer> allIds = new ArrayList<>();
        allIds.add(itemId);
        allIds.addAll(alternativeItems);
        return allIds;
    }
    
    @Override
    public String toString()
    {
        return "ItemRequirement{itemId=" + itemId + ", quantity=" + quantity + 
               ", tierAgnostic=" + tierAgnostic + ", alternatives=" + alternativeItems.size() + "}";
    }
}
