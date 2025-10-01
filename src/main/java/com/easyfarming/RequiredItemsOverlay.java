package com.easyfarming;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.game.ItemManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class RequiredItemsOverlay extends Overlay
{
    private final HerbRunManager herbRunManager;
	private final PanelComponent panelComponent;
	private final ItemManager itemManager;

	public RequiredItemsOverlay(HerbRunManager herbRunManager, ItemManager itemManager)
	{
        this.herbRunManager = herbRunManager;
		this.itemManager = itemManager;
		
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(50.0F); // Low priority using floating point
		
		panelComponent = new PanelComponent();
		panelComponent.setPreferredSize(new Dimension(200, 0));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!herbRunManager.isHerbRunActive())
		{
			return null;
		}

		panelComponent.getChildren().clear();

		// Get remaining items
		Map<Integer, Integer> remainingItems = herbRunManager.getRemainingItems();
		
		if (remainingItems.isEmpty())
		{
			// Show navigation instructions when all items collected
			String nextLocation = herbRunManager.getNextPatchLocation();
			if (nextLocation != null)
			{
				String navigationText = getNavigationInstructions(nextLocation);
				panelComponent.getChildren().add(LineComponent.builder()
					.left(navigationText)
					.leftColor(Color.YELLOW)
					.build());
			}
		}
		else
		{
			for (Map.Entry<Integer, Integer> entry : remainingItems.entrySet())
			{
				int itemId = entry.getKey();
				int remaining = entry.getValue();
				
				// Create item sprite with count overlay
				BufferedImage itemSprite = createItemSpriteWithCount(itemId, remaining);
				if (itemSprite != null)
				{
					panelComponent.getChildren().add(new ImageComponent(itemSprite));
				}
			}
		}

		return panelComponent.render(graphics);
	}

	private BufferedImage createItemSpriteWithCount(int itemId, int count)
	{
		try
		{
			// Based on the working runelite-time-tracking-reminder plugin
			// Use ItemManager.getImage() which is the correct RuneLite API method
			if (itemManager != null)
			{
				// Get the item sprite using the correct method
				java.awt.Image itemSprite = itemManager.getImage(itemId);
				if (itemSprite != null)
				{
					// Scale to 24x24 for overlay
					java.awt.Image scaledSprite = itemSprite.getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH);
					
					// Convert to BufferedImage
					BufferedImage bufferedImage = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2d = bufferedImage.createGraphics();
					
					// Draw the item sprite
					g2d.drawImage(scaledSprite, 0, 0, null);
					
					// Draw count overlay in bottom-right corner
					if (count > 0)
					{
						// Set up text rendering
						g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
						
						// Draw background circle for count - positioned in bottom-right
						// Circle size: 10x10, positioned to be visible in bottom-right of 24x24 sprite
						g2d.setColor(new Color(0, 0, 0, 180));
						g2d.fillOval(12, 12, 12, 12);
						
						// Draw count text
						g2d.setColor(Color.WHITE);
						g2d.setFont(new Font("Arial", Font.BOLD, 9));
						String countText = String.valueOf(count);
						
						// Center the text in the circle
						FontMetrics fm = g2d.getFontMetrics();
						int textWidth = fm.stringWidth(countText);
						int textHeight = fm.getAscent();
						
						// Circle center is at (18, 18), so center text there
						int x = 18 - (textWidth / 2);
						int y = 18 + (textHeight / 2);
						
						g2d.drawString(countText, x, y);
					}
					
					g2d.dispose();
					return bufferedImage;
				}
			}
		}
		catch (Exception e)
		{
			// Fallback to null if sprite can't be loaded
		}
		
		return null;
	}
	
	/**
	 * Get navigation instructions for a specific patch location
	 */
	private String getNavigationInstructions(String location)
	{
		switch (location)
		{
			case "portPhasmatys":
				return "Click Ectophial then run west";
			case "ardougne":
				return "Cast Ardougne teleport then run north";
			case "catherby":
				return "Cast Camelot teleport then run east";
			case "falador":
				return "Cast Falador teleport then run north";
			case "trollheim":
				return "Cast Trollheim teleport then run south";
			case "hosidius":
				return "Use Xeric's talisman then run north";
			default:
				return "Navigate to " + location;
		}
	}


}
