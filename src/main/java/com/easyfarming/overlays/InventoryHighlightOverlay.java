package com.easyfarming.overlays;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.HighlightUtils;
import com.easyfarming.core.*;
import com.easyfarming.runs.HerbRun;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Highlights required items in the inventory and bank.
 * This overlay helps players identify which items they need for their farming run.
 */
public class InventoryHighlightOverlay extends Overlay
{
    private final Client client;
    private final EasyFarmingConfig config;
    private final FarmingRunState runState;
    private final HerbRun herbRun;
    private final RequirementManager requirementManager;
    
    @Inject
    public InventoryHighlightOverlay(Client client, EasyFarmingConfig config, FarmingRunState runState,
                                     HerbRun herbRun, RequirementManager requirementManager)
    {
        this.client = client;
        this.config = config;
        this.runState = runState;
        this.herbRun = herbRun;
        this.requirementManager = requirementManager;
        
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }
    
    @Override
    public Dimension render(Graphics2D graphics)
    {
        // Only show highlights if farming run is active and inventory highlighting is enabled
        if (!runState.isRunActive() || !config.highlightInventory())
        {
            return null;
        }
        
        FarmingState currentState = runState.getCurrentState();
        
        // Only highlight during item gathering phase
        if (currentState == FarmingState.GATHERING_ITEMS)
        {
            highlightRequiredItems(graphics);
        }
        
        return null;
    }
    
    /**
     * Highlight required items in the inventory
     */
    private void highlightRequiredItems(Graphics2D graphics)
    {
        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
        if (inventoryWidget == null || inventoryWidget.isHidden())
        {
            return;
        }
        
        Widget[] inventoryItems = inventoryWidget.getDynamicChildren();
        if (inventoryItems == null)
        {
            return;
        }
        
        // Get all required item IDs
        Set<Integer> requiredItemIds = getRequiredItemIds();
        
        // Highlight each required item in the inventory
        for (Widget item : inventoryItems)
        {
            if (item != null && requiredItemIds.contains(item.getItemId()))
            {
                Rectangle bounds = item.getBounds();
                
                // Use different colors based on whether we have enough
                Color highlightColor = hasEnoughOfItem(item.getItemId()) ? Color.GREEN : Color.ORANGE;
                HighlightUtils.highlightWidget(graphics, bounds, highlightColor);
            }
        }
    }
    
    /**
     * Get all required item IDs for the current farming run
     */
    private Set<Integer> getRequiredItemIds()
    {
        Set<Integer> itemIds = new HashSet<>();
        
        // Add herb seed IDs
        HerbSeedRequirement herbSeedReq = herbRun.getHerbSeedRequirement();
        itemIds.addAll(herbSeedReq.getHerbSeedIds());
        
        // Add teleport item IDs
        List<ItemRequirement> teleportReqs = herbRun.getTeleportRequirements();
        for (ItemRequirement req : teleportReqs)
        {
            if (req.isTierAgnostic())
            {
                // Add all tier variants
                itemIds.addAll(req.getAllItemIds());
            }
            else
            {
                itemIds.add(req.getItemId());
            }
        }
        
        // Add farming supply IDs
        itemIds.add(net.runelite.api.gameval.ItemID.BUCKET_SUPERCOMPOST);
        itemIds.add(net.runelite.api.gameval.ItemID.FAIRY_ENCHANTED_SECATEURS);
        
        // Add watering can IDs (all levels)
        for (int i = 0; i <= 8; i++)
        {
            itemIds.add(net.runelite.api.gameval.ItemID.WATERING_CAN_0 + i);
        }
        
        return itemIds;
    }
    
    /**
     * Check if the player has enough of a specific item
     */
    private boolean hasEnoughOfItem(int itemId)
    {
        requirementManager.updateInventoryCounts();
        
        // Check herb seeds
        HerbSeedRequirement herbSeedReq = herbRun.getHerbSeedRequirement();
        if (herbSeedReq.getHerbSeedIds().contains(itemId))
        {
            return requirementManager.isHerbSeedRequirementSatisfied(herbSeedReq);
        }
        
        // Check teleport items
        List<ItemRequirement> teleportReqs = herbRun.getTeleportRequirements();
        for (ItemRequirement req : teleportReqs)
        {
            if (req.getAllItemIds().contains(itemId))
            {
                return requirementManager.isItemRequirementSatisfied(req);
            }
        }
        
        // For other items, just check if we have at least 1
        return requirementManager.getItemCount(itemId) > 0;
    }
}
