package com.easyfarming;

import com.easyfarming.core.*;
import com.easyfarming.runs.HerbRun;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.ui.overlay.OverlayManager;

import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
	name = "Easy Farming",
	description = "Helps with farm runs with icons and tab highlighting."
)
public class EasyFarmingPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private EasyFarmingConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private EventBus eventBus;

	private NavigationButton navButton;
	
	// Core farming system components
	private LocationManager locationManager;
	private RequirementManager requirementManager;
	private FarmingRunState runState;
	private FarmingEventHandler eventHandler;
	private HerbRun herbRun;

	@Provides
	EasyFarmingConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EasyFarmingConfig.class);
	}

	@Override
	protected void startUp()
	{
		log.info("Easy Farming started!");
		
		// Initialize core farming system components
		locationManager = new LocationManager();
		requirementManager = new RequirementManager(client);
		runState = new FarmingRunState(client, requirementManager, locationManager);
		eventHandler = new FarmingEventHandler(client, runState);
		
		// Register event handler with event bus
		eventBus.register(eventHandler);
		
		// Initialize herb run
		herbRun = new HerbRun(client, runState, locationManager);
		herbRun.initialize();
		
		// TODO: Set up overlays and UI
	}

	@Override
	protected void shutDown()
	{
		log.info("Easy Farming stopped!");
		
		try
		{
			// Unregister event handler from event bus
			if (eventHandler != null)
			{
				eventBus.unregister(eventHandler);
				log.debug("Unregistered FarmingEventHandler from event bus");
			}
		}
		catch (Exception e)
		{
			log.warn("Failed to unregister event handler: {}", e.getMessage());
		}
		
		try
		{
			// Stop farming run state
			if (runState != null)
			{
				runState.stopRun();
				log.debug("Stopped farming run state");
			}
		}
		catch (Exception e)
		{
			log.warn("Failed to stop farming run state: {}", e.getMessage());
		}
		
		try
		{
			// Remove navigation button if it exists
			if (navButton != null)
			{
				clientToolbar.removeNavigation(navButton);
				log.debug("Removed navigation button");
			}
		}
		catch (Exception e)
		{
			log.warn("Failed to remove navigation button: {}", e.getMessage());
		}
		
		// Note: No overlays or UI elements to clean up currently
		// No scheduled tasks or executors to shutdown currently
		
		// Null out references to help garbage collection
		runState = null;
		eventHandler = null;
		locationManager = null;
		requirementManager = null;
		herbRun = null;
		navButton = null;
		
		log.debug("Cleaned up all component references");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			log.info("Player logged in");
			
			// Start farming runs based on config
			startFarmingRuns();
		}
	}
	
	/**
	 * Start farming runs based on configuration
	 */
	private void startFarmingRuns()
	{
		if (runState == null)
		{
			return;
		}
		
		// Start runs based on config settings
		runState.startRun(
			config.enableHerbRuns(),
			config.enableTreeRuns(),
			config.enableFruitTreeRuns(),
			config.enableAllotmentRuns()
		);
		
		log.info("Farming runs started - Herb: {}, Tree: {}, Fruit Tree: {}, Allotment: {}", 
			config.enableHerbRuns(), config.enableTreeRuns(), 
			config.enableFruitTreeRuns(), config.enableAllotmentRuns());
	}

	// Getters for other components
	public Client getClient() { return client; }
	public EasyFarmingConfig getConfig() { return config; }
}