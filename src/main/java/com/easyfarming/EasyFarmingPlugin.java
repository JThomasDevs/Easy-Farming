package com.easyfarming;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.game.ItemManager;

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
	private ItemManager itemManager;

	private NavigationButton navButton;
	private EasyFarmingPanel panel;
	private HerbRunManager herbRunManager;
	private RequiredItemsOverlay requiredItemsOverlay;
	private DirectionalArrowOverlay directionalArrowOverlay;
	private InventoryHighlightOverlay inventoryHighlightOverlay;
	private PatchHighlightOverlay patchHighlightOverlay;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Easy Farming started!");
		
		// Create the herb run manager
		herbRunManager = new HerbRunManager(config);
		
		// Create the panel
		panel = new EasyFarmingPanel(this, herbRunManager);
		
		// Create the overlays
		requiredItemsOverlay = new RequiredItemsOverlay(herbRunManager, itemManager);
		directionalArrowOverlay = new DirectionalArrowOverlay(this, herbRunManager);
		inventoryHighlightOverlay = new InventoryHighlightOverlay(this, herbRunManager);
		patchHighlightOverlay = new PatchHighlightOverlay(this, herbRunManager);
		overlayManager.add(requiredItemsOverlay);
		overlayManager.add(directionalArrowOverlay);
		overlayManager.add(inventoryHighlightOverlay);
		overlayManager.add(patchHighlightOverlay);
		
		// Create navigation button with the herb icon
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/icon.png");
		navButton = NavigationButton.builder()
			.tooltip("Easy Farming")
			.icon(icon)
			.priority(5)
			.panel(panel)
			.build();
		
		clientToolbar.addNavigation(navButton);
		log.info("Easy Farming panel added to sidebar!");
		
		// Initial inventory scan
		updateInventoryCounts();
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Easy Farming stopped!");
		
		// Remove overlays
		if (requiredItemsOverlay != null)
		{
			overlayManager.remove(requiredItemsOverlay);
		}
		if (directionalArrowOverlay != null)
		{
			overlayManager.remove(directionalArrowOverlay);
		}
		if (inventoryHighlightOverlay != null)
		{
			overlayManager.remove(inventoryHighlightOverlay);
		}
		if (patchHighlightOverlay != null)
		{
			overlayManager.remove(patchHighlightOverlay);
		}
		
		// Remove navigation button
		if (navButton != null)
		{
			clientToolbar.removeNavigation(navButton);
		}
	}
	
	public Client getClient()
	{
		return client;
	}
	
	public net.runelite.client.game.ItemManager getItemManager()
	{
		return itemManager;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Easy Farming plugin is now active!", null);
			// Scan inventory when logging in
			updateInventoryCounts();
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		// Update inventory counts when items are added/removed
		updateInventoryCounts();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		// Update inventory counts periodically to catch any missed changes
		updateInventoryCounts();
	}

	private void updateInventoryCounts()
	{
		if (herbRunManager != null && herbRunManager.isHerbRunActive())
		{
			// Scan current inventory
			scanInventory();
			// Update the panel to reflect current inventory
			if (panel != null)
			{
				panel.updatePanel();
			}
		}
	}

	public void forceInventoryUpdate()
	{
		if (herbRunManager != null && herbRunManager.isHerbRunActive())
		{
			scanInventory();
			if (panel != null)
			{
				panel.updatePanel();
			}
		}
	}
	
	private void scanInventory()
	{
		if (client == null || client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		
		try
		{
			// Get the player's inventory container
			var inventory = client.getItemContainer(93); // 93 is the inventory container ID
			if (inventory == null)
			{
				log.debug("Inventory container is null");
				return;
			}
			
			// Clear previous inventory counts
			herbRunManager.clearInventoryCounts();
			
			int itemsFound = 0;
			// Scan each item in inventory
			for (var item : inventory.getItems())
			{
				if (item.getId() != -1) // -1 means empty slot
				{
					int itemId = item.getId();
					int quantity = item.getQuantity();
					herbRunManager.updateInventoryCount(itemId, quantity);
					itemsFound++;
					log.debug("Found item: ID={}, Quantity={}", itemId, quantity);
				}
			}
			
			log.debug("Inventory scanned: {} items found", itemsFound);
		}
		catch (Exception e)
		{
			log.error("Error scanning inventory", e);
		}
	}

	@Provides
	EasyFarmingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EasyFarmingConfig.class);
	}
}