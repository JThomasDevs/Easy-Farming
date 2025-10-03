package com.easyfarming.overlays;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.*;
import com.easyfarming.runs.HerbRun;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

/**
 * Displays remaining item counts for the farming run.
 * Shows how many items are still needed for the current run.
 */
public class ItemCountOverlay extends Overlay
{
    private final Client client;
    private final EasyFarmingConfig config;
    private final FarmingRunState runState;
    private final HerbRun herbRun;
    private final RequirementManager requirementManager;
    
    private final PanelComponent panelComponent = new PanelComponent();
    
    @Inject
    public ItemCountOverlay(Client client, EasyFarmingConfig config, FarmingRunState runState, 
                           HerbRun herbRun, RequirementManager requirementManager)
    {
        this.client = client;
        this.config = config;
        this.runState = runState;
        this.herbRun = herbRun;
        this.requirementManager = requirementManager;
        
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }
    
    @Override
    public Dimension render(Graphics2D graphics)
    {
        // Only show overlay if farming run is active and item counts are enabled
        if (!runState.isRunActive() || !config.showItemCounts())
        {
            return null;
        }
        
        panelComponent.getChildren().clear();
        
        // Add title
        panelComponent.getChildren().add(TitleComponent.builder()
            .text("Item Counts")
            .color(Color.CYAN)
            .build());
        
        // Add herb seed counts
        addHerbSeedCounts();
        
        // Add teleport item counts
        addTeleportItemCounts();
        
        // Add farming supply counts
        addFarmingSupplyCounts();
        
        return panelComponent.render(graphics);
    }
    
    /**
     * Add herb seed count information
     */
    private void addHerbSeedCounts()
    {
        HerbSeedRequirement herbSeedReq = herbRun.getHerbSeedRequirement();
        int remainingSeeds = herbRun.getRemainingHerbSeeds();
        int totalSeedsNeeded = herbSeedReq.getQuantity();
        
        Color color = remainingSeeds > 0 ? Color.RED : Color.GREEN;
        
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Herb Seeds:")
            .right(remainingSeeds + "/" + totalSeedsNeeded)
            .rightColor(color)
            .build());
    }
    
    /**
     * Add teleport item count information
     */
    private void addTeleportItemCounts()
    {
        List<ItemRequirement> teleportReqs = herbRun.getTeleportRequirements();
        
        for (ItemRequirement req : teleportReqs)
        {
            String itemName = getItemDisplayName(req);
            int currentCount = getCurrentItemCount(req);
            int neededCount = req.getQuantity();
            
            Color color = currentCount >= neededCount ? Color.GREEN : Color.RED;
            
            panelComponent.getChildren().add(LineComponent.builder()
                .left(itemName + ":")
                .right(currentCount + "/" + neededCount)
                .rightColor(color)
                .build());
        }
    }
    
    /**
     * Add farming supply count information
     */
    private void addFarmingSupplyCounts()
    {
        // Supercompost
        int supercompostCount = requirementManager.getItemCount(ItemID.BUCKET_SUPERCOMPOST);
        int supercompostNeeded = runState.getEnabledLocations().size();
        Color supercompostColor = supercompostCount >= supercompostNeeded ? Color.GREEN : Color.RED;
        
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Supercompost:")
            .right(supercompostCount + "/" + supercompostNeeded)
            .rightColor(supercompostColor)
            .build());
        
        // Watering can
        int wateringCanCount = getWateringCanCount();
        Color wateringCanColor = wateringCanCount > 0 ? Color.GREEN : Color.RED;
        
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Watering Can:")
            .right(wateringCanCount > 0 ? "✓" : "✗")
            .rightColor(wateringCanColor)
            .build());
        
        // Secateurs
        int secateursCount = requirementManager.getItemCount(ItemID.FAIRY_ENCHANTED_SECATEURS);
        Color secateursColor = secateursCount > 0 ? Color.GREEN : Color.RED;
        
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Secateurs:")
            .right(secateursCount > 0 ? "✓" : "✗")
            .rightColor(secateursColor)
            .build());
    }
    
    /**
     * Get display name for an item requirement
     */
    private String getItemDisplayName(ItemRequirement requirement)
    {
        int itemId = requirement.getItemId();
        
        // Map item IDs to display names
        switch (itemId)
        {
            case ItemID.ARDY_CAPE_MEDIUM:
            case ItemID.ARDY_CAPE_HARD:
            case ItemID.ARDY_CAPE_ELITE:
                return "Ardougne Cloak";
                
            case ItemID.LUMBRIDGE_RING_MEDIUM:
            case ItemID.LUMBRIDGE_RING_HARD:
            case ItemID.LUMBRIDGE_RING_ELITE:
                return "Explorer's Ring";
                
            case ItemID.LUNAR_TABLET_CATHERBY_TELEPORT:
                return "Catherby Tele Tab";
                
            case ItemID.ECTOPHIAL:
                return "Ectophial";
                
            case InterfaceID.MagicSpellbook.TROLLHEIM_TELEPORT:
                return "Trollheim Teleport";
                
            case ItemID.XERIC_TALISMAN:
                return "Xeric's Talisman";
                
            case ItemID.JEWL_NECKLACE_OF_SKILLS_1:
                return "Skills Necklace";
                
            case ItemID.TELETAB_HARMONY:
                return "Harmony Tele Tab";
                
            case ItemID.WEISS_TELEPORT_BASALT:
                return "Icy Basalt";
                
            default:
                return "Item " + itemId;
        }
    }
    
    /**
     * Get current count for an item requirement
     */
    private int getCurrentItemCount(ItemRequirement requirement)
    {
        if (requirement.isTierAgnostic())
        {
            // For tier-agnostic items, sum all variants
            int totalCount = 0;
            for (int itemId : requirement.getAllItemIds())
            {
                totalCount += requirementManager.getItemCount(itemId);
            }
            return totalCount;
        }
        else
        {
            return requirementManager.getItemCount(requirement.getItemId());
        }
    }
    
    /**
     * Get watering can count (any level)
     */
    private int getWateringCanCount()
    {
        int totalCount = 0;
        for (int i = 0; i <= 8; i++)
        {
            int wateringCanId = ItemID.WATERING_CAN_0 + i;
            totalCount += requirementManager.getItemCount(wateringCanId);
        }
        return totalCount;
    }
}
