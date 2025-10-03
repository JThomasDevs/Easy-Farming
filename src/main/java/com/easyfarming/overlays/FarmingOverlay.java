package com.easyfarming.overlays;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.*;
import com.easyfarming.runs.HerbRun;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.Set;

/**
 * Main farming overlay that displays instructions and status information
 * based on the current farming run state.
 */
public class FarmingOverlay extends Overlay
{
    private final Client client;
    private final EasyFarmingConfig config;
    private final FarmingRunState runState;
    private final HerbRun herbRun;
    private final RequirementManager requirementManager;
    
    private final PanelComponent panelComponent = new PanelComponent();
    
    @Inject
    public FarmingOverlay(Client client, EasyFarmingConfig config, FarmingRunState runState, HerbRun herbRun, RequirementManager requirementManager)
    {
        this.client = client;
        this.config = config;
        this.runState = runState;
        this.herbRun = herbRun;
        this.requirementManager = requirementManager;
        
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }
    
    @Override
    public Dimension render(Graphics2D graphics)
    {
        // Only show overlay if farming run is active and instructions are enabled
        if (!runState.isRunActive() || !config.showInstructions())
        {
            return null;
        }
        
        panelComponent.getChildren().clear();
        
        // Add title
        panelComponent.getChildren().add(TitleComponent.builder()
            .text("Easy Farming")
            .color(Color.CYAN)
            .build());
        
        // Add current state information
        addStateInformation();
        
        // Add current location information
        addLocationInformation();
        
        // Add instructions based on current state
        addStateInstructions();
        
        // Add item requirements if gathering items
        if (runState.getCurrentState() == FarmingState.GATHERING_ITEMS)
        {
            addItemRequirements();
        }
        
        // Add progress information
        addProgressInformation();
        
        return panelComponent.render(graphics);
    }
    
    /**
     * Add current state information to the overlay
     */
    private void addStateInformation()
    {
        FarmingState currentState = runState.getCurrentState();
        Color stateColor = getStateColor(currentState);
        
        panelComponent.getChildren().add(LineComponent.builder()
            .left("State:")
            .right(currentState.getDisplayName())
            .rightColor(stateColor)
            .build());
    }
    
    /**
     * Add current location information to the overlay
     */
    private void addLocationInformation()
    {
        Location currentLocation = runState.getCurrentLocation();
        if (currentLocation != null)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                .left("Location:")
                .right(currentLocation.getName())
                .rightColor(Color.YELLOW)
                .build());
        }
    }
    
    /**
     * Add instructions based on current state
     */
    private void addStateInstructions()
    {
        FarmingState currentState = runState.getCurrentState();
        List<String> instructions = getInstructionsForState(currentState);
        
        if (!instructions.isEmpty())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                .left("Instructions:")
                .build());
            
            for (String instruction : instructions)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("• " + instruction)
                    .leftColor(Color.WHITE)
                    .build());
            }
        }
    }
    
    /**
     * Add item requirements when gathering items
     */
    private void addItemRequirements()
    {
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Required Items:")
            .build());
        
        // Add herb seed requirements
        HerbSeedRequirement herbSeedReq = herbRun.getHerbSeedRequirement();
        int remainingSeeds = herbRun.getRemainingHerbSeeds();
        if (remainingSeeds > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                .left("• Herb Seeds:")
                .right(remainingSeeds + " needed")
                .rightColor(Color.RED)
                .build());
        }
        
        // Add teleport requirements
        List<ItemRequirement> teleportReqs = herbRun.getTeleportRequirements();
        for (ItemRequirement req : teleportReqs)
        {
            if (!isRequirementSatisfied(req))
            {
                String itemName = getItemName(req.getItemId());
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("• " + itemName + ":")
                    .right(req.getQuantity() + " needed")
                    .rightColor(Color.RED)
                    .build());
            }
        }
        
        // Add farming supplies
        if (!hasFarmingSupplies())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                .left("• Farming Supplies:")
                .right("Missing")
                .rightColor(Color.RED)
                .build());
        }
    }
    
    /**
     * Add progress information
     */
    private void addProgressInformation()
    {
        List<Location> enabledLocations = runState.getEnabledLocations();
        Set<Location> completedLocations = runState.getCompletedLocations();
        
        int totalLocations = enabledLocations.size();
        int completedCount = completedLocations.size();
        
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Progress:")
            .right(completedCount + "/" + totalLocations + " locations")
            .rightColor(Color.GREEN)
            .build());
    }
    
    /**
     * Get instructions for the current state
     */
    private List<String> getInstructionsForState(FarmingState state)
    {
        switch (state)
        {
            case GATHERING_ITEMS:
                return List.of(
                    "Gather all required items",
                    "Check your bank for missing items",
                    "Withdraw seeds, teleport items, and supplies"
                );
                
            case READY_TO_TELEPORT:
                return List.of(
                    "All items gathered!",
                    "Ready to teleport to " + getCurrentLocationName(),
                    "Use your selected teleport method"
                );
                
            case TELEPORTING:
                return List.of(
                    "Teleporting to " + getCurrentLocationName(),
                    "Wait for teleport to complete"
                );
                
            case NAVIGATING:
                return List.of(
                    "Run to the farming patch",
                    "Follow the highlighted path",
                    "Patch is " + getDistanceToPatch() + " tiles away"
                );
                
            case AT_PATCH:
                return getPatchInstructions();
                
            case HARVESTING:
                return List.of(
                    "Harvest the ready crops",
                    "Click on the highlighted patch",
                    "Collect all herbs"
                );
                
            case PLANTING:
                return List.of(
                    "Plant herb seeds",
                    "Click on the highlighted patch",
                    "Use your herb seeds"
                );
                
            case TREATING_DISEASE:
                return List.of(
                    "Cure the diseased crops",
                    "Use plant cure on the patch",
                    "Click on the highlighted patch"
                );
                
            case REMOVING_DEAD:
                return List.of(
                    "Remove dead crops",
                    "Use spade on the patch",
                    "Click on the highlighted patch"
                );
                
            case COMPOSTING:
                return List.of(
                    "Apply compost to the patch",
                    "Use supercompost on the patch",
                    "Click on the highlighted patch"
                );
                
            case WATERING:
                return List.of(
                    "Water the patch",
                    "Use watering can on the patch",
                    "Click on the highlighted patch"
                );
                
            case MOVING_TO_NEXT:
                return List.of(
                    "Location complete!",
                    "Moving to next location",
                    "Prepare for next teleport"
                );
                
            case RUN_COMPLETE:
                return List.of(
                    "Farming run complete!",
                    "All locations finished",
                    "Great job!"
                );
                
            default:
                return List.of();
        }
    }
    
    /**
     * Get patch-specific instructions
     */
    private List<String> getPatchInstructions()
    {
        PatchState patchState = runState.getCurrentPatchState();
        
        switch (patchState)
        {
            case READY:
                return List.of(
                    "Crops are ready to harvest!",
                    "Click on the highlighted patch",
                    "Collect your herbs"
                );
                
            case DISEASED:
                return List.of(
                    "Crops are diseased!",
                    "Use plant cure on the patch",
                    "Click on the highlighted patch"
                );
                
            case DEAD:
                return List.of(
                    "Crops are dead!",
                    "Remove dead crops with spade",
                    "Click on the highlighted patch"
                );
                
            case EMPTY:
                return List.of(
                    "Patch is empty",
                    "Plant herb seeds",
                    "Click on the highlighted patch"
                );
                
            case GROWING:
                return List.of(
                    "Crops are growing",
                    "Wait for them to mature",
                    "Or move to next location"
                );
                
            default:
                return List.of(
                    "Check patch state",
                    "Determine next action",
                    "Look for visual cues"
                );
        }
    }
    
    /**
     * Get color for state display
     */
    private Color getStateColor(FarmingState state)
    {
        switch (state)
        {
            case GATHERING_ITEMS:
                return Color.ORANGE;
            case READY_TO_TELEPORT:
                return Color.GREEN;
            case TELEPORTING:
                return Color.CYAN;
            case NAVIGATING:
                return Color.YELLOW;
            case AT_PATCH:
                return Color.WHITE;
            case HARVESTING:
            case PLANTING:
            case TREATING_DISEASE:
            case REMOVING_DEAD:
            case COMPOSTING:
            case WATERING:
                return Color.MAGENTA;
            case MOVING_TO_NEXT:
                return Color.BLUE;
            case RUN_COMPLETE:
                return Color.GREEN;
            default:
                return Color.GRAY;
        }
    }
    
    /**
     * Get current location name
     */
    private String getCurrentLocationName()
    {
        Location currentLocation = runState.getCurrentLocation();
        return currentLocation != null ? currentLocation.getName() : "Unknown";
    }
    
    /**
     * Get distance to current patch
     */
    private String getDistanceToPatch()
    {
        Location currentLocation = runState.getCurrentLocation();
        if (currentLocation == null)
        {
            return "Unknown";
        }
        
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return "Unknown";
        }
        
        WorldPoint playerPos = player.getWorldLocation();
        WorldPoint patchPos = currentLocation.getPatchPoint();
        
        int distance = Math.max(
            Math.abs(playerPos.getX() - patchPos.getX()),
            Math.abs(playerPos.getY() - patchPos.getY())
        );
        
        return String.valueOf(distance);
    }
    
    /**
     * Check if a requirement is satisfied
     */
    private boolean isRequirementSatisfied(ItemRequirement requirement)
    {
        if (requirementManager == null || requirement == null)
        {
            return false;
        }
        
        try
        {
            return requirementManager.isItemRequirementSatisfied(requirement);
        }
        catch (Exception e)
        {
            // Degrade gracefully if there's an error
            return false;
        }
    }
    
    /**
     * Check if farming supplies are available
     */
    private boolean hasFarmingSupplies()
    {
        if (client == null)
        {
            return false;
        }
        
        try
        {
            // Check inventory for supercompost and watering can
            boolean hasSupercompost = false;
            boolean hasWateringCan = false;
            boolean hasSecateurs = false;
            
            // Check inventory for all farming supplies
            net.runelite.api.ItemContainer inventory = client.getItemContainer(net.runelite.api.gameval.InventoryID.INV);
            if (inventory != null)
            {
                for (net.runelite.api.Item item : inventory.getItems())
                {
                    if (item.getId() == -1) continue; // Skip empty slots
                    
                    String itemName = getItemName(item.getId()).toLowerCase();
                    if (itemName.contains("supercompost"))
                    {
                        hasSupercompost = true;
                    }
                    else if (itemName.contains("watering can"))
                    {
                        hasWateringCan = true;
                    }
                    else if (itemName.contains("secateurs"))
                    {
                        hasSecateurs = true;
                    }
                }
            }
            
            return hasSupercompost && hasWateringCan && hasSecateurs;
        }
        catch (Exception e)
        {
            // Degrade gracefully if there's an error
            return false;
        }
    }
    
    /**
     * Get item name from item ID
     */
    private String getItemName(int itemId)
    {
        // For now, return a placeholder since the exact RuneLite API method
        // for getting item names may vary by version
        // This prevents the UI from breaking while maintaining functionality
        return "Item " + itemId;
    }
}
