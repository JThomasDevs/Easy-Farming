package com.easyfarming.overlays;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.*;
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

/**
 * Displays step-by-step instructions for the current farming action.
 * This overlay shows contextual instructions based on the current state and location.
 */
public class InstructionOverlay extends Overlay
{
    private final Client client;
    private final EasyFarmingConfig config;
    private final FarmingRunState runState;
    
    private final PanelComponent panelComponent = new PanelComponent();
    
    @Inject
    public InstructionOverlay(Client client, EasyFarmingConfig config, FarmingRunState runState)
    {
        this.client = client;
        this.config = config;
        this.runState = runState;
        
        setPosition(OverlayPosition.TOP_RIGHT);
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
            .text("Farming Instructions")
            .color(Color.CYAN)
            .build());
        
        // Add current step
        addCurrentStep();
        
        // Add detailed instructions
        addDetailedInstructions();
        
        // Add location-specific tips
        addLocationTips();
        
        return panelComponent.render(graphics);
    }
    
    /**
     * Add current step information
     */
    private void addCurrentStep()
    {
        FarmingState currentState = runState.getCurrentState();
        Location currentLocation = runState.getCurrentLocation();
        
        String stepText = "Step: " + getStepNumber() + " of " + getTotalSteps();
        panelComponent.getChildren().add(LineComponent.builder()
            .left(stepText)
            .right(currentState.getDisplayName())
            .rightColor(getStateColor(currentState))
            .build());
        
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
     * Add detailed instructions for the current state
     */
    private void addDetailedInstructions()
    {
        FarmingState currentState = runState.getCurrentState();
        List<String> instructions = getDetailedInstructions(currentState);
        
        if (!instructions.isEmpty())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                .left("Instructions:")
                .build());
            
            for (int i = 0; i < instructions.size(); i++)
            {
                String instruction = instructions.get(i);
                Color color = i == 0 ? Color.WHITE : Color.LIGHT_GRAY;
                
                panelComponent.getChildren().add(LineComponent.builder()
                    .left((i + 1) + ". " + instruction)
                    .leftColor(color)
                    .build());
            }
        }
    }
    
    /**
     * Add location-specific tips
     */
    private void addLocationTips()
    {
        Location currentLocation = runState.getCurrentLocation();
        if (currentLocation == null)
        {
            return;
        }
        
        List<String> tips = getLocationTips(currentLocation);
        if (!tips.isEmpty())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                .left("Tips:")
                .build());
            
            for (String tip : tips)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("• " + tip)
                    .leftColor(Color.CYAN)
                    .build());
            }
        }
    }
    
    /**
     * Get detailed instructions for the current state
     */
    private List<String> getDetailedInstructions(FarmingState state)
    {
        switch (state)
        {
            case GATHERING_ITEMS:
                return List.of(
                    "Open your bank",
                    "Withdraw required herb seeds",
                    "Withdraw teleport items (cloak, ring, etc.)",
                    "Withdraw farming supplies (compost, watering can, secateurs)",
                    "Check that all items are in your inventory"
                );
                
            case READY_TO_TELEPORT:
                return List.of(
                    "Open your spellbook or inventory",
                    "Find your selected teleport method",
                    "Click on the teleport item or spell",
                    "Wait for teleport animation to complete"
                );
                
            case TELEPORTING:
                return List.of(
                    "Teleport animation in progress",
                    "Wait for teleport to complete",
                    "You will be taken to the teleport destination"
                );
                
            case NAVIGATING:
                return List.of(
                    "Look for the farming patch",
                    "Run towards the highlighted patch",
                    "Use the minimap to navigate",
                    "Stop when you reach the patch"
                );
                
            case AT_PATCH:
                return getPatchDetailedInstructions();
                
            case HARVESTING:
                return List.of(
                    "Click on the farming patch",
                    "Select 'Harvest' from the menu",
                    "Wait for harvesting animation",
                    "Collect all herbs that appear"
                );
                
            case PLANTING:
                return List.of(
                    "Click on the empty farming patch",
                    "Select 'Use' from the menu",
                    "Click on your herb seeds",
                    "Wait for planting animation"
                );
                
            case TREATING_DISEASE:
                return List.of(
                    "Click on the diseased farming patch",
                    "Select 'Use' from the menu",
                    "Click on your plant cure",
                    "Wait for curing animation"
                );
                
            case REMOVING_DEAD:
                return List.of(
                    "Click on the dead farming patch",
                    "Select 'Use' from the menu",
                    "Click on your spade",
                    "Wait for removal animation"
                );
                
            case COMPOSTING:
                return List.of(
                    "Click on the farming patch",
                    "Select 'Use' from the menu",
                    "Click on your supercompost",
                    "Wait for composting animation"
                );
                
            case WATERING:
                return List.of(
                    "Click on the farming patch",
                    "Select 'Use' from the menu",
                    "Click on your watering can",
                    "Wait for watering animation"
                );
                
            case MOVING_TO_NEXT:
                return List.of(
                    "This location is complete!",
                    "Prepare for the next location",
                    "Check your inventory for remaining items",
                    "Get ready to teleport to the next patch"
                );
                
            case RUN_COMPLETE:
                return List.of(
                    "Congratulations!",
                    "All farming locations are complete",
                    "You can bank your herbs",
                    "Start a new run when ready"
                );
                
            default:
                return List.of("No specific instructions available");
        }
    }
    
    /**
     * Get detailed patch instructions
     */
    private List<String> getPatchDetailedInstructions()
    {
        PatchState patchState = runState.getCurrentPatchState();
        
        switch (patchState)
        {
            case READY:
                return List.of(
                    "Your crops are ready to harvest!",
                    "Click on the farming patch",
                    "Select 'Harvest' from the menu",
                    "Collect all herbs that appear"
                );
                
            case DISEASED:
                return List.of(
                    "Your crops are diseased!",
                    "Click on the farming patch",
                    "Select 'Use' from the menu",
                    "Click on your plant cure"
                );
                
            case DEAD:
                return List.of(
                    "Your crops are dead!",
                    "Click on the farming patch",
                    "Select 'Use' from the menu",
                    "Click on your spade to remove them"
                );
                
            case EMPTY:
                return List.of(
                    "The patch is empty and ready for planting",
                    "Click on the farming patch",
                    "Select 'Use' from the menu",
                    "Click on your herb seeds"
                );
                
            case GROWING:
                return List.of(
                    "Your crops are still growing",
                    "You can wait for them to mature",
                    "Or move on to the next location",
                    "They will be ready later"
                );
                
            default:
                return List.of(
                    "Check the patch visually",
                    "Look for crop state indicators",
                    "Determine the appropriate action"
                );
        }
    }
    
    /**
     * Get location-specific tips
     */
    private List<String> getLocationTips(Location location)
    {
        String locationName = location.getName().toLowerCase();
        
        switch (locationName)
        {
            case "ardougne":
                return List.of(
                    "Use Ardougne cloak for direct farm teleport",
                    "Run north from Ardougne city center",
                    "Watch out for aggressive guards"
                );
                
            case "catherby":
                return List.of(
                    "Use Catherby teleport tab for convenience",
                    "Patch is just north of the bank",
                    "Great for banking between runs"
                );
                
            case "falador":
                return List.of(
                    "Use Explorer's ring for direct farm teleport",
                    "Run north from Falador city center",
                    "Close to the Falador bank"
                );
                
            case "morytania":
                return List.of(
                    "Use Ectophial for quick access",
                    "Run north from the Ectofuntus",
                    "Watch out for aggressive monsters"
                );
                
            case "trollstronghold":
                return List.of(
                    "Use Trollheim teleport spell",
                    "Run south to the patch",
                    "Requires completion of Eadgar's Ruse quest"
                );
                
            case "kourend":
                return List.of(
                    "Use Xeric's talisman for teleport",
                    "Run north from the teleport spot",
                    "Close to the Kourend Castle"
                );
                
            case "farmingguild":
                return List.of(
                    "Use Skills necklace to Farming Guild",
                    "Patch is inside the guild",
                    "Access to farming tools and supplies"
                );
                
            case "harmonyisland":
                return List.of(
                    "Use Harmony teleport tab",
                    "Patch is on the island",
                    "Requires completion of The Great Brain Robbery quest"
                );
                
            case "weiss":
                return List.of(
                    "Use Icy basalt for teleport",
                    "Patch is in the Weiss area",
                    "Requires completion of Making Friends with My Arm quest"
                );
                
            default:
                return List.of();
        }
    }
    
    /**
     * Get current step number
     */
    private int getStepNumber()
    {
        FarmingState currentState = runState.getCurrentState();
        
        switch (currentState)
        {
            case GATHERING_ITEMS:
                return 1;
            case READY_TO_TELEPORT:
            case TELEPORTING:
                return 2;
            case NAVIGATING:
                return 3;
            case AT_PATCH:
            case HARVESTING:
            case PLANTING:
            case TREATING_DISEASE:
            case REMOVING_DEAD:
            case COMPOSTING:
            case WATERING:
                return 4;
            case MOVING_TO_NEXT:
                return 5;
            case RUN_COMPLETE:
                return 6;
            default:
                return 1;
        }
    }
    
    /**
     * Get total number of steps
     */
    private int getTotalSteps()
    {
        return 6; // Gathering items, teleporting, navigating, patch action, moving to next, complete
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
}
