package com.easyfarming;

import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;

public class InventoryHighlightOverlay extends Overlay
{
	private final EasyFarmingPlugin plugin;
	private final HerbRunManager herbRunManager;
	
	public InventoryHighlightOverlay(EasyFarmingPlugin plugin, HerbRunManager herbRunManager)
	{
		this.plugin = plugin;
		this.herbRunManager = herbRunManager;
		
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(150.0F); // Very high priority to show above other overlays
		setLayer(OverlayLayer.ABOVE_SCENE);
	}
	
	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!herbRunManager.isHerbRunActive())
		{
			return null;
		}
		
		Client client = plugin.getClient();
		if (client == null || client.getGameState() != net.runelite.api.GameState.LOGGED_IN)
		{
			return null;
		}
		
		// Only show highlight when all items are gathered
		if (!herbRunManager.getRemainingItems().isEmpty())
		{
			return null;
		}
		
		// Get the next teleport item to highlight
		Integer nextTeleportItemId = herbRunManager.getNextTeleportItemId();
		if (nextTeleportItemId == null)
		{
			return null;
		}
		
		// Get the inventory container
		ItemContainer inventory = client.getItemContainer(93); // 93 is inventory
		if (inventory == null)
		{
			return null;
		}
		
		// Find the teleport item in inventory and highlight it
		Item[] items = inventory.getItems();
		for (int i = 0; i < items.length; i++)
		{
			Item item = items[i];
			if (item.getId() == nextTeleportItemId)
			{
				highlightInventorySlot(graphics, client, i);
				break;
			}
		}
		
		return null;
	}
	
	private void highlightInventorySlot(Graphics2D graphics, Client client, int slotIndex)
	{
		// Calculate inventory slot position
		int row = slotIndex / 4; // 4 items per row
		int col = slotIndex % 4;
		
		// Inventory starts at (563, 206) with 42x36 slots
		int slotSize = 42;
		int startX = 563;
		int startY = 206;
		
		int slotX = startX + (col * slotSize);
		int slotY = startY + (row * slotSize);
		
		// Use screen coordinates directly - no world conversion needed
		// Draw highlight rectangle around the slot
		graphics.setColor(new Color(255, 255, 0, 150)); // Semi-transparent yellow
		graphics.setStroke(new BasicStroke(3.0f));
		graphics.drawRect(slotX, slotY, slotSize, slotSize);
		
		// Add a pulsing effect with multiple rectangles
		graphics.setColor(new Color(255, 255, 0, 80));
		graphics.setStroke(new BasicStroke(2.0f));
		graphics.drawRect(slotX - 2, slotY - 2, slotSize + 4, slotSize + 4);
		
		graphics.setColor(new Color(255, 255, 0, 40));
		graphics.setStroke(new BasicStroke(1.0f));
		graphics.drawRect(slotX - 4, slotY - 4, slotSize + 8, slotSize + 8);
	}
}
