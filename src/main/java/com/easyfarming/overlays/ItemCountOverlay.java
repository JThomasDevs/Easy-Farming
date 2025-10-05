package com.easyfarming.overlays;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.*;
import com.easyfarming.runs.HerbRun;
import net.runelite.api.*;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simplified item count overlay using ImageComponent with item icons.
 * Shows missing items with their icons and counts.
 */
public class ItemCountOverlay extends Overlay
{
    private final Client client;
    private final EasyFarmingConfig config;
    private final FarmingRunState runState;
    private final HerbRun herbRun;
    private final RequirementManager requirementManager;
    private final ItemManager itemManager;
    private final PanelComponent panelComponent = new PanelComponent();
    
    // Basic herb seed IDs
    private static final List<Integer> HERB_SEED_IDS = Arrays.asList(
        ItemID.GUAM_SEED, ItemID.MARRENTILL_SEED, ItemID.TARROMIN_SEED, ItemID.HARRALANDER_SEED,
        ItemID.RANARR_SEED, ItemID.TOADFLAX_SEED, ItemID.IRIT_SEED, ItemID.AVANTOE_SEED,
        ItemID.KWUARM_SEED, ItemID.SNAPDRAGON_SEED, ItemID.CADANTINE_SEED, ItemID.LANTADYME_SEED,
        ItemID.DWARF_WEED_SEED, ItemID.TORSTOL_SEED
    );
    private static final int BASE_SEED_ID = ItemID.GUAM_SEED;
    
    @Inject
    public ItemCountOverlay(Client client, EasyFarmingConfig config, FarmingRunState runState,
                           HerbRun herbRun, RequirementManager requirementManager, ItemManager itemManager)
    {
        this.client = client;
        this.config = config;
        this.runState = runState;
        this.herbRun = herbRun;
        this.requirementManager = requirementManager;
        this.itemManager = itemManager;
        
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }
    
    @Override
    public Dimension render(Graphics2D graphics)
    {
        // Only show if farming run is active and item counts are enabled
        if (!runState.isRunActive() || !config.showItemCounts())
        {
            return null;
        }
        
        // Only show during item gathering phase
        if (runState.getCurrentState() != FarmingState.GATHERING_ITEMS)
        {
            return null;
        }
        
        // Get required items from herb run
        Map<Integer, Integer> itemsToCheck = getRequiredItems();
        if (itemsToCheck == null || itemsToCheck.isEmpty())
        {
            return null;
        }
        
        // Get inventory
        ItemContainer inventory = client.getItemContainer(InventoryID.INV);
        Item[] items;
        if (inventory == null || inventory.getItems() == null)
        {
            items = new Item[0];
        }
        else
        {
            items = inventory.getItems();
        }
        
        // Count herb seeds
        int totalSeeds = countItems(items, HERB_SEED_IDS);
        
        panelComponent.getChildren().clear();
        List<AbstractMap.SimpleEntry<Integer, Integer>> missingItemsWithCounts = new ArrayList<>();
        
        // Check each required item
        for (Map.Entry<Integer, Integer> entry : itemsToCheck.entrySet())
        {
            int itemId = entry.getKey();
            int requiredCount = entry.getValue();
            
            int inventoryCount = getItemCount(itemId, items, totalSeeds);
            
            // If we don't have enough, add to missing items
            if (inventoryCount < requiredCount)
            {
                int missingCount = requiredCount - inventoryCount;
                BufferedImage itemImage = itemManager.getImage(itemId);
                if (itemImage != null)
                {
                    ImageComponent imageComponent = new ImageComponent(itemImage);
                    panelComponent.getChildren().add(imageComponent);
                    missingItemsWithCounts.add(new AbstractMap.SimpleEntry<>(itemId, missingCount));
                }
            }
        }
        
        Dimension panelSize = panelComponent.render(graphics);
        
        // Draw item counts on top of the overlay
        int yOffset = 0;
        for (AbstractMap.SimpleEntry<Integer, Integer> pair : missingItemsWithCounts)
        {
            int itemId = pair.getKey();
            int missingCount = pair.getValue();
            
            BufferedImage itemImage = itemManager.getImage(itemId);
            if (itemImage != null)
            {
                // Draw item count
                if (missingCount > 1)
                {
                    String countText = Integer.toString(missingCount);
                    int textX = 2;
                    int textY = yOffset + 15;
                    graphics.setColor(Color.WHITE);
                    graphics.drawString(countText, textX, textY);
                }
                
                yOffset += itemImage.getHeight() + 2;
            }
        }
        
        return panelSize;
    }
    
    /**
     * Get required items from the herb run
     */
    private Map<Integer, Integer> getRequiredItems()
    {
        Map<Integer, Integer> items = new HashMap<>();
        
        // Add herb seeds (9 patches)
        items.put(BASE_SEED_ID, 9);
        
        // Add teleport items based on enabled locations
        if (config.ardougneHerb())
        {
            items.put(ItemID.ARDY_CAPE_MEDIUM, 1); // Ardougne Cloak 2
        }
        if (config.catherbyHerb())
        {
            items.put(ItemID.LUNAR_TABLET_CATHERBY_TELEPORT, 1);
        }
        if (config.faladorHerb())
        {
            items.put(ItemID.LUMBRIDGE_RING_MEDIUM, 1); // Explorer's Ring 2
        }
        if (config.morytaniaHerb())
        {
            items.put(ItemID.ECTOPHIAL, 1);
        }
        if (config.trollStrongholdHerb())
        {
            items.put(ItemID.STRONGHOLD_TELEPORT_BASALT, 1);
        }
        if (config.kourendHerb())
        {
            items.put(ItemID.XERIC_TALISMAN, 1);
        }
        if (config.farmingGuildHerb())
        {
            items.put(ItemID.JEWL_NECKLACE_OF_SKILLS_1, 1);
        }
        if (config.harmonyHerb())
        {
            items.put(ItemID.TELETAB_HARMONY, 1);
        }
        if (config.weissHerb())
        {
            items.put(ItemID.WEISS_TELEPORT_BASALT, 1);
        }
        
        // Add farming supplies
        items.put(ItemID.BUCKET_ULTRACOMPOST, 9);
        items.put(ItemID.WATERING_CAN_8, 1);
        items.put(ItemID.FAIRY_ENCHANTED_SECATEURS, 1);
        
        return items;
    }
    
    /**
     * Count items in inventory matching any of the given IDs
     */
    private int countItems(Item[] items, List<Integer> itemIds)
    {
        int count = 0;
        for (Item item : items)
        {
            if (item != null && itemIds.contains(item.getId()))
            {
                count += item.getQuantity();
            }
        }
        return count;
    }
    
    /**
     * Get count of a specific item, handling special cases
     */
    private int getItemCount(int itemId, Item[] items, int totalSeeds)
    {
        // Handle herb seeds
        if (itemId == BASE_SEED_ID)
        {
            return totalSeeds;
        }
        
        // Regular item count
        int count = 0;
        for (Item item : items)
        {
            if (item != null && item.getId() == itemId)
            {
                count += item.getQuantity();
            }
        }
        
        return count;
    }
}