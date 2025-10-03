package com.easyfarming.overlays;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.HighlightUtils;
import com.easyfarming.core.*;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

/**
 * Highlights teleport items, spellbook icons, and spells based on the current farming state.
 * This overlay helps guide the player to the next action they need to take.
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
        // Only show highlights if farming run is active and highlighting is enabled
        if (!runState.isRunActive() || !config.highlightNextAction())
        {
            return null;
        }
        
        FarmingState currentState = runState.getCurrentState();
        
        // Highlight based on current state
        switch (currentState)
        {
            case READY_TO_TELEPORT:
                highlightTeleportMethod(graphics);
                break;
                
            case GATHERING_ITEMS:
                // Could highlight missing items in bank/inventory
                break;
                
            case AT_PATCH:
            case HARVESTING:
            case PLANTING:
            case TREATING_DISEASE:
            case REMOVING_DEAD:
            case COMPOSTING:
            case WATERING:
                // Could highlight farming patch
                break;
                
            default:
                break;
        }
        
        return null;
    }
    
    /**
     * Highlight the teleport method the player should use
     */
    private void highlightTeleportMethod(Graphics2D graphics)
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
                highlightSpellbookIcon(graphics);
                highlightTeleportSpell(graphics, selectedTeleport.getSpellId());
                break;
                
            case ITEM:
                highlightTeleportItem(graphics, selectedTeleport.getItemId());
                break;
                
            case PORTAL_NEXUS:
            case JEWELLERY_BOX:
                highlightPOHTeleport(graphics, selectedTeleport);
                break;
                
            case SPIRIT_TREE:
                highlightSpiritTree(graphics);
                break;
                
            case MOUNTED_XERICS:
                highlightMountedXerics(graphics);
                break;
        }
    }
    
    /**
     * Highlight the spellbook icon
     */
    private void highlightSpellbookIcon(Graphics2D graphics)
    {
        Widget spellbookWidget = getSpellbookWidget();
        if (spellbookWidget != null && !spellbookWidget.isHidden())
        {
            Rectangle bounds = spellbookWidget.getBounds();
            HighlightUtils.highlightWidget(graphics, bounds, Color.CYAN);
        }
    }
    
    /**
     * Get the spellbook widget based on current viewport mode
     * The spellbook icon (STONE6) appears in different top-level interfaces depending on viewport mode
     */
    private Widget getSpellbookWidget()
    {
        // Try fixed viewport
        Widget spellBookWidget = client.getWidget(InterfaceID.Toplevel.STONE6);
        if (spellBookWidget != null && !spellBookWidget.isHidden())
        {
            return spellBookWidget;
        }
        return null;
    }
    
    /**
     * Highlight a specific teleport spell
     */
    private void highlightTeleportSpell(Graphics2D graphics, int spellId)
    {
        if (spellId == 0)
        {
            return;
        }
        
        // Get the spell widget from the spellbook
        Widget spellWidget = getSpellWidget(spellId);
        if (spellWidget != null && !spellWidget.isHidden())
        {
            Rectangle bounds = spellWidget.getBounds();
            HighlightUtils.highlightWidget(graphics, bounds, Color.GREEN);
        }
    }
    
    /**
     * Get the spell widget for a specific spell ID
     * Maps spell IDs to their corresponding widget child indices in the spellbook
     */
    private Widget getSpellWidget(int spellId)
    {
        if (spellId == 0)
        {
            return null;
        }
        
        // Get the child widget index for this spell ID
        Integer childIndex = getSpellWidgetChildIndex(spellId);
        if (childIndex == null)
        {
            // Log missing mapping for debugging
            System.out.println("HighlightOverlay: No widget mapping found for spell ID: " + spellId);
            return null;
        }
        
        // Try to get the spellbook widget container
        Widget spellbookContainer = client.getWidget(InterfaceID.MagicSpellbook, 0);
        if (spellbookContainer == null)
        {
            return null;
        }
        
        // Get the specific spell child widget
        Widget spellWidget = client.getWidget(InterfaceID.MagicSpellbook, childIndex);
        if (spellWidget == null || spellWidget.isHidden())
        {
            // Fallback: scan children for matching spell ID
            return scanSpellbookForSpell(spellId);
        }
        
        return spellWidget;
    }
    
    /**
     * Maps spell IDs to their spellbook widget child indices
     * Based on standard spellbook layout (widget group 218)
     * Uses InterfaceID.MagicSpellbook constants for spell widget child IDs
     */
    private Integer getSpellWidgetChildIndex(int spellId)
    {
        // Spell widget child IDs are defined in InterfaceID.MagicSpellbook
        // These constants represent the child widget index in the spellbook interface
        
        switch (spellId)
        {
            // Standard teleport spells (widget group 218 - Standard Spellbook)
            case InterfaceID.MagicSpellbook.FALADOR_TELEPORT:
                return InterfaceID.MagicSpellbook.FALADOR_TELEPORT;
            case InterfaceID.MagicSpellbook.CAMELOT_TELEPORT:
                return InterfaceID.MagicSpellbook.CAMELOT_TELEPORT;
            case InterfaceID.MagicSpellbook.ARDOUGNE_TELEPORT:
                return InterfaceID.MagicSpellbook.ARDOUGNE_TELEPORT;
            case InterfaceID.MagicSpellbook.TROLLHEIM_TELEPORT:
                return InterfaceID.MagicSpellbook.TROLLHEIM_TELEPORT;
            
            // Arceuus spellbook spells (widget group 218 - Arceuus Spellbook)
            case InterfaceID.MagicSpellbook.TELEPORT_DRAYNOR_MANOR:
                return InterfaceID.MagicSpellbook.TELEPORT_DRAYNOR_MANOR;
            
            default:
                // For unmapped spells, use the spell ID directly as child index
                // This assumes the spell ID is already the correct child widget index
                return spellId;
        }
    }
    
    /**
     * Fallback method: scan spellbook children for a spell matching the given ID
     * This handles cases where the spell layout might be dynamic or different from expected
     */
    private Widget scanSpellbookForSpell(int spellId)
    {
        Widget spellbookContainer = client.getWidget(InterfaceID.SPELLBOOK, 0);
        if (spellbookContainer == null)
        {
            return null;
        }
        
        Widget[] children = spellbookContainer.getChildren();
        if (children == null)
        {
            return null;
        }
        
        // Scan through children looking for matching spell ID
        for (Widget child : children)
        {
            if (child == null || child.isHidden())
            {
                continue;
            }
            
            // Check if this child represents the spell we're looking for
            // The spell ID might be stored in the child's itemId or other properties
            if (child.getId() == spellId || child.getItemId() == spellId)
            {
                return child;
            }
        }
        
        return null;
    }
    
    /**
     * Highlight a teleport item in the inventory
     */
    private void highlightTeleportItem(Graphics2D graphics, int itemId)
    {
        if (itemId == 0)
        {
            return;
        }
        
        // Get inventory widget
        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
        if (inventoryWidget == null || inventoryWidget.isHidden())
        {
            return;
        }
        
        // Search for the item in inventory
        Widget[] inventoryItems = inventoryWidget.getDynamicChildren();
        if (inventoryItems == null)
        {
            return;
        }
        
        for (Widget item : inventoryItems)
        {
            if (item != null && item.getItemId() == itemId)
            {
                Rectangle bounds = item.getBounds();
                HighlightUtils.highlightWidget(graphics, bounds, Color.YELLOW);
                break; // Only highlight first instance
            }
        }
    }
    
    /**
     * Highlight POH teleport options (portal nexus or jewellery box)
     */
    private void highlightPOHTeleport(Graphics2D graphics, Teleport teleport)
    {
        // First, highlight the POH teleport spell/item to enter the house
        // This would typically be the "Home Teleport" spell or a teleport to house tablet
        
        // For now, we can highlight a generic POH teleport item
        // TODO: Implement POH portal nexus and jewellery box highlighting
    }
    
    /**
     * Highlight spirit tree
     */
    private void highlightSpiritTree(Graphics2D graphics)
    {
        // Highlight the spirit tree in the world
        // This would require NPC highlighting logic
        // TODO: Implement spirit tree highlighting
    }
    
    /**
     * Highlight mounted Xeric's talisman in POH
     */
    private void highlightMountedXerics(Graphics2D graphics)
    {
        // Highlight the mounted Xeric's talisman in POH
        // This would require object highlighting logic
        // TODO: Implement mounted Xeric's highlighting
    }
}
