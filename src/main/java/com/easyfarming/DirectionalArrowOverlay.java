package com.easyfarming;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DirectionalArrowOverlay extends Overlay
{
	private final EasyFarmingPlugin plugin;
	private final HerbRunManager herbRunManager;
	
	// Arrow images for different directions
	private BufferedImage arrow0, arrow45, arrow90, arrow135, arrow180, arrow225, arrow270, arrow315;
	
	// Arrow dimensions and scaling
	private static final int ARROW_SIZE = 32;
	private static final int ARROW_OFFSET = 40; // Distance from player
	
	public DirectionalArrowOverlay(EasyFarmingPlugin plugin, HerbRunManager herbRunManager)
	{
		this.plugin = plugin;
		this.herbRunManager = herbRunManager;
		
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(100.0F); // High priority using floating point
		setLayer(OverlayLayer.ABOVE_WIDGETS); // Render on minimap
		
		// Load arrow images
		loadArrowImages();
	}
	
	private void loadArrowImages()
	{
		try
		{
			// Load and scale arrow images to consistent size
			arrow0 = scaleImage(ImageUtil.loadImageResource(getClass(), "/quest_step_arrow.png"));
			arrow45 = scaleImage(ImageUtil.loadImageResource(getClass(), "/quest_step_arrow_45.png"));
			arrow90 = scaleImage(ImageUtil.loadImageResource(getClass(), "/quest_step_arrow_90.png"));
			arrow135 = scaleImage(ImageUtil.loadImageResource(getClass(), "/quest_step_arrow_135.png"));
			arrow180 = scaleImage(ImageUtil.loadImageResource(getClass(), "/quest_step_arrow_180.png"));
			arrow225 = scaleImage(ImageUtil.loadImageResource(getClass(), "/quest_step_arrow_225.png"));
			arrow270 = scaleImage(ImageUtil.loadImageResource(getClass(), "/quest_step_arrow_270.png"));
			arrow315 = scaleImage(ImageUtil.loadImageResource(getClass(), "/quest_step_arrow_315.png"));
		}
		catch (Exception e)
		{
			// Fallback to programmatic arrows if images fail to load
		}
	}
	
	private BufferedImage scaleImage(BufferedImage original)
	{
		if (original == null) return null;
		
		// Scale image to consistent size while maintaining aspect ratio
		Image scaled = original.getScaledInstance(ARROW_SIZE, ARROW_SIZE, Image.SCALE_SMOOTH);
		BufferedImage scaledImage = new BufferedImage(ARROW_SIZE, ARROW_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = scaledImage.createGraphics();
		g2d.drawImage(scaled, 0, 0, null);
		g2d.dispose();
		return scaledImage;
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
		
		// Get the next patch location
		String nextLocation = herbRunManager.getNextPatchLocation();
		if (nextLocation == null)
		{
			return null;
		}
		
		// Get player's current location
		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
		if (playerLocation == null)
		{
			return null;
		}
		
		// Get the target patch coordinates
		int[] patchCoords = herbRunManager.getPatchCoordinates(nextLocation);
		if (patchCoords == null)
		{
			return null;
		}
		
		WorldPoint targetLocation = new WorldPoint(patchCoords[0], patchCoords[1], patchCoords[2]);
		
		// Calculate distance to target
		int distance = playerLocation.distanceTo(targetLocation);
		
		// Only show arrow if we're within reasonable distance but not too close to the patch
		// If too close, the patch highlight overlay will take over
		if (distance > 50 || distance < 3)
		{
			return null;
		}
		
		// Calculate direction vector
		int deltaX = targetLocation.getX() - playerLocation.getX();
		int deltaY = targetLocation.getY() - playerLocation.getY();
		
		// Calculate angle in degrees (Quest Helper style)
		double angleRadians = Math.atan2(-deltaY, deltaX); // Note: Y is inverted in RuneScape
		int angleDegrees = (int) Math.toDegrees(angleRadians);
		
		// Normalize angle to 0-359 range
		if (angleDegrees < 0)
		{
			angleDegrees += 360;
		}
		
		// Draw directional arrow
		drawDirectionalArrow(graphics, client, playerLocation, angleDegrees, nextLocation);
		
		return null;
	}
	
	private void drawDirectionalArrow(Graphics2D graphics, Client client, WorldPoint playerLocation, int angleDegrees, String locationName)
	{
		// Get minimap widget and bounds
		Widget minimapWidget = client.getWidget(164); // 164 is the main minimap widget ID
		if (minimapWidget == null)
		{
			return;
		}
		
		// Get minimap bounds
		Rectangle minimapBounds = minimapWidget.getBounds();
		if (minimapBounds == null)
		{
			return;
		}
		
		// Calculate minimap center (player position)
		int minimapCenterX = minimapBounds.x + (minimapBounds.width / 2);
		int minimapCenterY = minimapBounds.y + (minimapBounds.height / 2);
		
		// Select the appropriate arrow image based on angle
		BufferedImage arrowImage = getArrowImageForAngle(angleDegrees);
		
		if (arrowImage != null)
		{
			// Calculate arrow position offset from player (Quest Helper style)
			double angleRadians = Math.toRadians(angleDegrees);
			int offsetX = (int) (ARROW_OFFSET * Math.cos(angleRadians));
			int offsetY = (int) (ARROW_OFFSET * Math.sin(angleRadians));
			
					// Position arrow offset from minimap center
		int arrowX = minimapCenterX + offsetX - (ARROW_SIZE / 2);
		int arrowY = minimapCenterY + offsetY - (ARROW_SIZE / 2);
			
			// Draw the arrow image
			graphics.drawImage(arrowImage, arrowX, arrowY, null);
			
					// Draw location name above the arrow
		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("Arial", Font.BOLD, 10)); // Smaller font for minimap
		FontMetrics fm = graphics.getFontMetrics();
		int textWidth = fm.stringWidth(locationName);
		graphics.drawString(locationName, arrowX + (ARROW_SIZE / 2) - (textWidth / 2), arrowY - 3);
		}
		else
		{
					// Fallback to programmatic arrow if no image is available
		drawFallbackArrow(graphics, minimapCenterX, minimapCenterY, angleDegrees, locationName);
		}
	}
	
	private BufferedImage getArrowImageForAngle(int angleDegrees)
	{
		// Map angle ranges to arrow images (Quest Helper style)
		// Each arrow covers a 45-degree range, centered on its direction
		if (angleDegrees >= 337 || angleDegrees < 22) // 0° ± 22.5° (east)
		{
			return arrow0;
		}
		else if (angleDegrees >= 22 && angleDegrees < 67) // 45° ± 22.5° (northeast)
		{
			return arrow45;
		}
		else if (angleDegrees >= 67 && angleDegrees < 112) // 90° ± 22.5° (north)
		{
			return arrow90;
		}
		else if (angleDegrees >= 112 && angleDegrees < 157) // 135° ± 22.5° (northwest)
		{
			return arrow135;
		}
		else if (angleDegrees >= 157 && angleDegrees < 202) // 180° ± 22.5° (west)
		{
			return arrow180;
		}
		else if (angleDegrees >= 202 && angleDegrees < 247) // 225° ± 22.5° (southwest)
		{
			return arrow225;
		}
		else if (angleDegrees >= 247 && angleDegrees < 292) // 270° ± 22.5° (south)
		{
			return arrow270;
		}
		else if (angleDegrees >= 292 && angleDegrees < 337) // 315° ± 22.5° (southeast)
		{
			return arrow315;
		}
		else // Fallback (should never reach here)
		{
			return arrow0;
		}
	}
	
	private void drawFallbackArrow(Graphics2D graphics, int centerX, int centerY, int angleDegrees, String locationName)
	{
		// Fallback to simple programmatic arrow if images fail to load
		graphics.setColor(new Color(255, 255, 0, 200)); // Semi-transparent yellow
		graphics.setStroke(new BasicStroke(2.0f));
		
		// Draw a simple arrow pointing in the right direction
		int arrowLength = 25;
		int arrowWidth = 12;
		
		// Convert angle to radians for drawing
		double angleRadians = Math.toRadians(angleDegrees);
		
		// Calculate arrow end point
		int endX = centerX + (int) (arrowLength * Math.cos(angleRadians));
		int endY = centerY + (int) (arrowLength * Math.sin(angleRadians));
		
		// Draw arrow shaft
		graphics.drawLine(centerX, centerY, endX, endY);
		
		// Draw arrow head
		double headAngle1 = angleRadians + Math.PI * 0.75;
		double headAngle2 = angleRadians - Math.PI * 0.75;
		
		int head1X = endX + (int) (arrowWidth * Math.cos(headAngle1));
		int head1Y = endY + (int) (arrowWidth * Math.sin(headAngle1));
		int head2X = endX + (int) (arrowWidth * Math.cos(headAngle2));
		int head2Y = endY + (int) (arrowWidth * Math.sin(headAngle2));
		
		graphics.drawLine(endX, endY, head1X, head1Y);
		graphics.drawLine(endX, endY, head2X, head2Y);
		
		// Draw location name
		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("Arial", Font.BOLD, 10)); // Smaller font for minimap
		FontMetrics fm = graphics.getFontMetrics();
		int textWidth = fm.stringWidth(locationName);
		graphics.drawString(locationName, centerX - (textWidth / 2), centerY - 15);
	}
}
