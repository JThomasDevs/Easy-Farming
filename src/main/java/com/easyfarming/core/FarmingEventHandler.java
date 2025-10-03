package com.easyfarming.core;

import net.runelite.api.Client;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Handles farming-related events to detect state changes and user actions.
 * This class listens for chat messages, animations, and other game events
 * to help determine the current farming state.
 */
@Slf4j
public class FarmingEventHandler
{
    private final Client client;
    private final FarmingRunState runState;
    
    // Chat message patterns for farming actions
    private static final Pattern TELEPORT_PATTERN = Pattern.compile(
        "You teleport to (.*?)\\."
    );
    
    private static final Pattern HARVEST_PATTERN = Pattern.compile(
        "You harvest (\\d+) (.*?)\\."
    );
    
    private static final Pattern PLANT_PATTERN = Pattern.compile(
        "You plant (\\d+) (.*?) seed in the (.*?)\\."
    );
    
    private static final Pattern DISEASE_PATTERN = Pattern.compile(
        "Your (.*?) has become diseased\\."
    );
    
    private static final Pattern DEAD_PATTERN = Pattern.compile(
        "Your (.*?) has died\\."
    );
    
    private static final Pattern CURE_PATTERN = Pattern.compile(
        "You cure the diseased (.*?)\\."
    );
    
    private static final Pattern COMPOST_PATTERN = Pattern.compile(
        "You treat the (.*?) with (.*?)\\."
    );
    
    private static final Pattern WATER_PATTERN = Pattern.compile(
        "You water the (.*?)\\."
    );
    
    private static final Pattern REMOVE_PATTERN = Pattern.compile(
        "You remove the dead (.*?)\\."
    );
    
    // Animation IDs for farming actions
    private static final int TELEPORT_ANIMATION_ID = 714;
    private static final int HARVEST_ANIMATION_ID = 830;
    private static final int PLANT_ANIMATION_ID = 2291;
    private static final int CURE_ANIMATION_ID = 2288;
    private static final int COMPOST_ANIMATION_ID = 2283;
    private static final int WATER_ANIMATION_ID = 2293;
    private static final int REMOVE_ANIMATION_ID = 2294;
    
    // State tracking
    private boolean isTeleporting = false;
    private boolean isHarvesting = false;
    private boolean isPlanting = false;
    private boolean isCuring = false;
    private boolean isComposting = false;
    private boolean isWatering = false;
    private boolean isRemoving = false;
    
    public FarmingEventHandler(Client client, FarmingRunState runState)
    {
        this.client = client;
        this.runState = runState;
    }
    
    /**
     * Handle chat messages to detect farming actions
     */
    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (!runState.isRunActive())
        {
            return;
        }
        
        String message = event.getMessage();
        
        // Check for teleport messages
        Matcher teleportMatcher = TELEPORT_PATTERN.matcher(message);
        if (teleportMatcher.find())
        {
            handleTeleportMessage(teleportMatcher.group(1));
            return;
        }
        
        // Check for harvest messages
        Matcher harvestMatcher = HARVEST_PATTERN.matcher(message);
        if (harvestMatcher.find())
        {
            handleHarvestMessage(harvestMatcher.group(1), harvestMatcher.group(2));
            return;
        }
        
        // Check for plant messages
        Matcher plantMatcher = PLANT_PATTERN.matcher(message);
        if (plantMatcher.find())
        {
            handlePlantMessage(plantMatcher.group(1), plantMatcher.group(2), plantMatcher.group(3));
            return;
        }
        
        // Check for disease messages
        Matcher diseaseMatcher = DISEASE_PATTERN.matcher(message);
        if (diseaseMatcher.find())
        {
            handleDiseaseMessage(diseaseMatcher.group(1));
            return;
        }
        
        // Check for dead crop messages
        Matcher deadMatcher = DEAD_PATTERN.matcher(message);
        if (deadMatcher.find())
        {
            handleDeadMessage(deadMatcher.group(1));
            return;
        }
        
        // Check for cure messages
        Matcher cureMatcher = CURE_PATTERN.matcher(message);
        if (cureMatcher.find())
        {
            handleCureMessage(cureMatcher.group(1));
            return;
        }
        
        // Check for compost messages
        Matcher compostMatcher = COMPOST_PATTERN.matcher(message);
        if (compostMatcher.find())
        {
            handleCompostMessage(compostMatcher.group(1), compostMatcher.group(2));
            return;
        }
        
        // Check for water messages
        Matcher waterMatcher = WATER_PATTERN.matcher(message);
        if (waterMatcher.find())
        {
            handleWaterMessage(waterMatcher.group(1));
            return;
        }
        
        // Check for remove messages
        Matcher removeMatcher = REMOVE_PATTERN.matcher(message);
        if (removeMatcher.find())
        {
            handleRemoveMessage(removeMatcher.group(1));
            return;
        }
    }
    
    /**
     * Handle animation changes to detect farming actions
     */
    @Subscribe
    public void onAnimationChanged(AnimationChanged event)
    {
        if (!runState.isRunActive() || event.getActor() != client.getLocalPlayer())
        {
            return;
        }
        
        int animationId = event.getActor().getAnimation();
        
        switch (animationId)
        {
            case TELEPORT_ANIMATION_ID:
                handleTeleportAnimation();
                break;
            case HARVEST_ANIMATION_ID:
                handleHarvestAnimation();
                break;
            case PLANT_ANIMATION_ID:
                handlePlantAnimation();
                break;
            case CURE_ANIMATION_ID:
                handleCureAnimation();
                break;
            case COMPOST_ANIMATION_ID:
                handleCompostAnimation();
                break;
            case WATER_ANIMATION_ID:
                handleWaterAnimation();
                break;
            case REMOVE_ANIMATION_ID:
                handleRemoveAnimation();
                break;
        }
    }
    
    /**
     * Handle game state changes
     */
    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == net.runelite.api.GameState.LOGGED_IN)
        {
            // Reset all action flags when logging in
            resetActionFlags();
        }
    }
    
    // Chat message handlers
    private void handleTeleportMessage(String destination)
    {
        log.debug("Teleport detected to: {}", destination);
        isTeleporting = false; // Teleport completed
        // The FarmingRunState will handle location detection
    }
    
    private void handleHarvestMessage(String quantity, String item)
    {
        log.debug("Harvest detected: {} {}", quantity, item);
        isHarvesting = false; // Harvest completed
        // Update patch state to empty
    }
    
    private void handlePlantMessage(String quantity, String seed, String patch)
    {
        log.debug("Plant detected: {} {} in {}", quantity, seed, patch);
        isPlanting = false; // Planting completed
        // Update patch state to growing
    }
    
    private void handleDiseaseMessage(String crop)
    {
        log.debug("Disease detected: {}", crop);
        // Update patch state to diseased
    }
    
    private void handleDeadMessage(String crop)
    {
        log.debug("Dead crop detected: {}", crop);
        // Update patch state to dead
    }
    
    private void handleCureMessage(String crop)
    {
        log.debug("Cure detected: {}", crop);
        isCuring = false; // Curing completed
        // Update patch state to growing
    }
    
    private void handleCompostMessage(String patch, String compost)
    {
        log.debug("Compost detected: {} with {}", patch, compost);
        isComposting = false; // Composting completed
        // Update patch state to composted
    }
    
    private void handleWaterMessage(String patch)
    {
        log.debug("Water detected: {}", patch);
        isWatering = false; // Watering completed
        // Update patch state to watered
    }
    
    private void handleRemoveMessage(String crop)
    {
        log.debug("Remove detected: {}", crop);
        isRemoving = false; // Removing completed
        // Update patch state to empty
    }
    
    // Animation handlers
    private void handleTeleportAnimation()
    {
        log.debug("Teleport animation started");
        isTeleporting = true;
    }
    
    private void handleHarvestAnimation()
    {
        log.debug("Harvest animation started");
        isHarvesting = true;
    }
    
    private void handlePlantAnimation()
    {
        log.debug("Plant animation started");
        isPlanting = true;
    }
    
    private void handleCureAnimation()
    {
        log.debug("Cure animation started");
        isCuring = true;
    }
    
    private void handleCompostAnimation()
    {
        log.debug("Compost animation started");
        isComposting = true;
    }
    
    private void handleWaterAnimation()
    {
        log.debug("Water animation started");
        isWatering = true;
    }
    
    private void handleRemoveAnimation()
    {
        log.debug("Remove animation started");
        isRemoving = true;
    }
    
    /**
     * Reset all action flags
     */
    private void resetActionFlags()
    {
        isTeleporting = false;
        isHarvesting = false;
        isPlanting = false;
        isCuring = false;
        isComposting = false;
        isWatering = false;
        isRemoving = false;
    }
    
    // Getters for action states
    public boolean isTeleporting() { return isTeleporting; }
    public boolean isHarvesting() { return isHarvesting; }
    public boolean isPlanting() { return isPlanting; }
    public boolean isCuring() { return isCuring; }
    public boolean isComposting() { return isComposting; }
    public boolean isWatering() { return isWatering; }
    public boolean isRemoving() { return isRemoving; }
    
    /**
     * Check if any farming action is in progress
     */
    public boolean isAnyActionInProgress()
    {
        return isTeleporting || isHarvesting || isPlanting || isCuring || 
               isComposting || isWatering || isRemoving;
    }
}
