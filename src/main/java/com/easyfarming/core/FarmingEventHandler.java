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
     * Handle game state changes
     */
    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == net.runelite.api.GameState.LOGGED_IN)
        {
            // Reset state when logging in
            log.debug("Player logged in, resetting farming state");
        }
    }
    
    // Chat message handlers
    private void handleTeleportMessage(String destination)
    {
        log.debug("Teleport detected to: {}", destination);
        // The FarmingRunState will handle location detection
    }
    
    private void handleHarvestMessage(String quantity, String item)
    {
        log.debug("Harvest detected: {} {}", quantity, item);
        // Patch state will be updated via varbit detection
    }
    
    private void handlePlantMessage(String quantity, String seed, String patch)
    {
        log.debug("Plant detected: {} {} in {}", quantity, seed, patch);
        // Patch state will be updated via varbit detection
    }
    
    private void handleDiseaseMessage(String crop)
    {
        log.debug("Disease detected: {}", crop);
        // Patch state will be updated via varbit detection
    }
    
    private void handleDeadMessage(String crop)
    {
        log.debug("Dead crop detected: {}", crop);
        // Patch state will be updated via varbit detection
    }
    
    private void handleCureMessage(String crop)
    {
        log.debug("Cure detected: {}", crop);
        // Patch state will be updated via varbit detection
    }
    
    private void handleCompostMessage(String patch, String compost)
    {
        log.debug("Compost detected: {} with {}", patch, compost);
        // Patch state will be updated via varbit detection
    }
    
    private void handleWaterMessage(String patch)
    {
        log.debug("Water detected: {}", patch);
        // Patch state will be updated via varbit detection
    }
    
    private void handleRemoveMessage(String crop)
    {
        log.debug("Remove detected: {}", crop);
        // Patch state will be updated via varbit detection
    }
}
