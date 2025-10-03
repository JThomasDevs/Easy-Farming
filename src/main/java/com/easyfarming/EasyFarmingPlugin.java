package com.easyfarming;

import com.easyfarming.core.*;
import com.easyfarming.runs.HerbRun;
import com.easyfarming.overlays.*;
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
	
	// Overlays
	private FarmingOverlay farmingOverlay;
	private InstructionOverlay instructionOverlay;
	private ItemCountOverlay itemCountOverlay;
	private HighlightOverlay highlightOverlay;
	private InventoryHighlightOverlay inventoryHighlightOverlay;
	private PatchHighlightOverlay patchHighlightOverlay;

	@Provides
	EasyFarmingConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EasyFarmingConfig.class);
	}
	
	@Provides
	RequirementManager getRequirementManager(Client client)
	{
		return new RequirementManager(client);
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
		
		// Initialize overlays
		farmingOverlay = new FarmingOverlay(client, config, runState, herbRun, requirementManager);
		instructionOverlay = new InstructionOverlay(client, config, runState);
		itemCountOverlay = new ItemCountOverlay(client, config, runState, herbRun, requirementManager);
		highlightOverlay = new HighlightOverlay(client, config, runState);
		inventoryHighlightOverlay = new InventoryHighlightOverlay(client, config, runState, herbRun, requirementManager);
		patchHighlightOverlay = new PatchHighlightOverlay(client, config, runState);
		
		// Register overlays
		overlayManager.add(farmingOverlay);
		overlayManager.add(instructionOverlay);
		overlayManager.add(itemCountOverlay);
		overlayManager.add(highlightOverlay);
		overlayManager.add(inventoryHighlightOverlay);
		overlayManager.add(patchHighlightOverlay);
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
		
		try
		{
			// Remove overlays
			if (farmingOverlay != null)
			{
				overlayManager.remove(farmingOverlay);
				log.debug("Removed farming overlay");
			}
			if (instructionOverlay != null)
			{
				overlayManager.remove(instructionOverlay);
				log.debug("Removed instruction overlay");
			}
			if (itemCountOverlay != null)
			{
				overlayManager.remove(itemCountOverlay);
				log.debug("Removed item count overlay");
			}
			if (highlightOverlay != null)
			{
				overlayManager.remove(highlightOverlay);
				log.debug("Removed highlight overlay");
			}
			if (inventoryHighlightOverlay != null)
			{
				overlayManager.remove(inventoryHighlightOverlay);
				log.debug("Removed inventory highlight overlay");
			}
			if (patchHighlightOverlay != null)
			{
				overlayManager.remove(patchHighlightOverlay);
				log.debug("Removed patch highlight overlay");
			}
		}
		catch (Exception e)
		{
			log.warn("Failed to remove overlays: {}", e.getMessage());
		}
		
		// Null out references to help garbage collection
		runState = null;
		eventHandler = null;
		locationManager = null;
		requirementManager = null;
		herbRun = null;
		navButton = null;
		farmingOverlay = null;
		instructionOverlay = null;
		itemCountOverlay = null;
		highlightOverlay = null;
		inventoryHighlightOverlay = null;
		patchHighlightOverlay = null;
		
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