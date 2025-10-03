package com.easyfarming.overlays;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.HighlightUtils;
import com.easyfarming.core.*;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

/**
 * Action-driven highlighting overlay.
 * Only highlights what the player needs to click for the CURRENT step.
 */
public class HighlightOverlay extends Overlay
{
    private final Client client;
    private final EasyFarmingConfig config;
    private final FarmingRunState runState;
    
    @Inject
    public HighlightOverlay(Client client, EasyFarmingConfig config, FarmingRunState runState)
    {
        this.client = client;
        this.config = config;
        this.runState = runState;
        
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }
    
    @Override
    public Dimension render(Graphics2D graphics)
    {
        // Only highlight if run is active and highlighting is enabled
        if (!runState.isRunActive() || !config.highlightNextAction())
        {
            return null;
        }
        
        FarmingState currentState = runState.getCurrentState();
        
        // Highlight based on what action needs to be taken
        switch (currentState)
        {
            case READY_TO_TELEPORT:
                // Highlight teleport method (spell/item/etc)
                highlightTeleportAction(graphics);
                break;
                
            case PLANTING:
                // Highlight seed in inventory (player should use on patch)
                highlightSeedForPlanting(graphics);
                break;
                
            case COMPOSTING:
                // Highlight compost in inventory
                highlightCompostInInventory(graphics);
                break;
                
            case TREATING_DISEASE:
                // Highlight plant cure in inventory
                highlightPlantCureInInventory(graphics);
                break;
                
            // For other states, patches/NPCs are highlighted by PatchHighlightOverlay
            default:
                break;
        }
        
        return null;
    }
    
    /**
     * Highlight the teleport action (spell, item, etc)
     */
    private void highlightTeleportAction(Graphics2D graphics)
    {
        Location currentLocation = runState.getCurrentLocation();
        if (currentLocation == null)
        {
            return;
        }
        
        Teleport selectedTeleport = currentLocation.getSelectedTeleport();
        if (selectedTeleport == null)
        {
            return;
        }
        
        // Highlight based on teleport category
        switch (selectedTeleport.getCategory())
        {
            case SPELLBOOK:
                // First highlight spellbook icon, then spell
                highlightSpellbookIcon(graphics);
                highlightTeleportSpell(graphics, selectedTeleport.getSpellId());
                break;
                
            case ITEM:
                // Highlight teleport item in inventory
                highlightItemInInventory(graphics, selectedTeleport.getItemId(), Color.YELLOW);
                break;
                
            // TODO: Add POH, Spirit Tree, etc when implemented
            default:
                break;
        }
    }
    
    /**
     * Highlight seed in inventory for planting
     */
    private void highlightSeedForPlanting(Graphics2D graphics)
    {
        // Highlight any herb seed (seed-agnostic)
        // Get first available herb seed
        ItemContainer inventory = client.getItemContainer(InventoryID.INV);
        if (inventory == null)
        {
            return;
        }
        
        for (Item item : inventory.getItems())
        {
            if (item != null && isHerbSeed(item.getId()))
            {
                highlightItemInInventory(graphics, item.getId(), Color.GREEN);
                break; // Only highlight first one
            }
        }
    }
    
    /**
     * Highlight compost in inventory
     */
    private void highlightCompostInInventory(Graphics2D graphics)
    {
        // Highlight ultracompost/supercompost
        highlightItemInInventory(graphics, net.runelite.api.gameval.ItemID.BUCKET_ULTRACOMPOST, Color.ORANGE);
    }
    
    /**
     * Highlight plant cure in inventory
     */
    private void highlightPlantCureInInventory(Graphics2D graphics)
    {
        highlightItemInInventory(graphics, net.runelite.api.gameval.ItemID.PLANT_CURE, Color.MAGENTA);
    }
    
    /**
     * Check if an item ID is a herb seed
     */
    private boolean isHerbSeed(int itemId)
    {
        // Add all herb seed IDs
        return itemId == net.runelite.api.gameval.ItemID.GUAM_SEED ||
               itemId == net.runelite.api.gameval.ItemID.MARRENTILL_SEED ||
               itemId == net.runelite.api.gameval.ItemID.TARROMIN_SEED ||
               itemId == net.runelite.api.gameval.ItemID.HARRALANDER_SEED ||
               itemId == net.runelite.api.gameval.ItemID.RANARR_SEED ||
               itemId == net.runelite.api.gameval.ItemID.TOADFLAX_SEED ||
               itemId == net.runelite.api.gameval.ItemID.IRIT_SEED ||
               itemId == net.runelite.api.gameval.ItemID.AVANTOE_SEED ||
               itemId == net.runelite.api.gameval.ItemID.KWUARM_SEED ||
               itemId == net.runelite.api.gameval.ItemID.SNAPDRAGON_SEED ||
               itemId == net.runelite.api.gameval.ItemID.CADANTINE_SEED ||
               itemId == net.runelite.api.gameval.ItemID.LANTADYME_SEED ||
               itemId == net.runelite.api.gameval.ItemID.DWARF_WEED_SEED ||
               itemId == net.runelite.api.gameval.ItemID.TORSTOL_SEED;
    }
    
    /**
     * Highlight the spellbook icon
     */
    private void highlightSpellbookIcon(Graphics2D graphics)
    {
        Widget spellbookWidget = client.getWidget(InterfaceID.Toplevel.STONE6);
        if (spellbookWidget != null && !spellbookWidget.isHidden())
        {
            Rectangle bounds = spellbookWidget.getBounds();
            HighlightUtils.highlightWidget(graphics, bounds, Color.CYAN);
        }
    }
    
    /**
     * Highlight a specific teleport spell
     */
    private void highlightTeleportSpell(Graphics2D graphics, int spellWidgetId)
    {
        if (spellWidgetId == 0)
        {
            return;
        }
        
        Widget spellWidget = client.getWidget(spellWidgetId);
        if (spellWidget != null && !spellWidget.isHidden())
        {
            Rectangle bounds = spellWidget.getBounds();
            HighlightUtils.highlightWidget(graphics, bounds, Color.GREEN);
        }
    }
    
    /**
     * Highlight an item in the inventory
     */
    private void highlightItemInInventory(Graphics2D graphics, int itemId, Color color)
    {
        if (itemId == 0)
        {
            return;
        }
        
        // Get inventory container
        ItemContainer inventoryContainer = client.getItemContainer(InventoryID.INV);
        if (inventoryContainer == null)
        {
            return;
        }
        
        // Get inventory items
        Item[] items = inventoryContainer.getItems();
        if (items == null)
        {
            return;
        }
        
        // Get inventory widget for bounds
        Widget inventoryWidget = client.getWidget(InterfaceID.INVENTORY, 0);
        if (inventoryWidget == null)
        {
            return;
        }
        
        Widget[] children = inventoryWidget.getChildren();
        if (children == null)
        {
            return;
        }
        
        // Find and highlight the item
        for (int i = 0; i < items.length && i < children.length; i++)
        {
            if (items[i] != null && items[i].getId() == itemId)
            {
                Widget itemWidget = children[i];
                if (itemWidget != null && !itemWidget.isHidden())
                {
                    Rectangle bounds = itemWidget.getBounds();
                    HighlightUtils.highlightWidget(graphics, bounds, color);
                    break; // Only highlight first instance
                }
            }
        }
    }
}
