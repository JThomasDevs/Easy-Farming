package com.easyfarming;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PatchHighlightOverlay extends Overlay
{
	private final EasyFarmingPlugin plugin;
	private final HerbRunManager herbRunManager;
	
	// Highlight colors and dimensions
	private static final Color PATCH_HIGHLIGHT_COLOR = new Color(255, 255, 0, 150); // Semi-transparent yellow
	private static final Color PATCH_BORDER_COLOR = new Color(255, 255, 0, 255); // Solid yellow border
	
	// Herb patch object IDs (these are the actual patch objects in the game)
	private static final int[] HERB_PATCH_IDS = {
		8151, 8152, 8153, 8154, 8155, 8156, 8157, 8158, 8159, 8160, 8161, 8162, 8163, 8164, 8165, 8166, 8167, 8168, 8169, 8170,
		8171, 8172, 8173, 8174, 8175, 8176, 8177, 8178, 8179, 8180, 8181, 8182, 8183, 8184, 8185, 8186, 8187, 8188, 8189, 8190,
		8191, 8192, 8193, 8194, 8195, 8196, 8197, 8198, 8199, 8200, 8201, 8202, 8203, 8204, 8205, 8206, 8207, 8208, 8209, 8210,
		8211, 8212, 8213, 8214, 8215, 8216, 8217, 8218, 8219, 8220, 8221, 8222, 8223, 8224, 8225, 8226, 8227, 8228, 8229, 8230,
		8231, 8232, 8233, 8234, 8235, 8236, 8237, 8238, 8239, 8240, 8241, 8242, 8243, 8244, 8245, 8246, 8247, 8248, 8249, 8250
	};
	
	public PatchHighlightOverlay(EasyFarmingPlugin plugin, HerbRunManager herbRunManager)
	{
		this.plugin = plugin;
		this.herbRunManager = herbRunManager;
		
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(150.0F); // Higher priority than directional arrows
		setLayer(OverlayLayer.ABOVE_SCENE); // Render above the game world
	}
	
	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!herbRunManager.isHerbRunActive())
		{
			return null;
		}
		
		Client client = plugin.getClient();
		if (client == null || client.getGameState() != GameState.LOGGED_IN)
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
		
		// Only show highlight if we're very close to the patch (within 3 tiles)
		if (distance > 3)
		{
			return null;
		}
		
		// Find and highlight nearby herb patches
		highlightNearbyHerbPatches(graphics, client, targetLocation);
		
		return null;
	}
	
	private void highlightNearbyHerbPatches(Graphics2D graphics, Client client, WorldPoint targetLocation)
	{
		// Find all herb patch GameObjects within range
		List<GameObject> nearbyPatches = findNearbyHerbPatches(client, targetLocation, 5);
		
		// Highlight each found patch
		for (GameObject patch : nearbyPatches)
		{
			drawGameObjectHighlight(graphics, patch);
		}
	}
	
	private List<GameObject> findNearbyHerbPatches(Client client, WorldPoint targetLocation, int range)
	{
		List<GameObject> foundPatches = new ArrayList<>();
		
		// Search in a square area around the target location
		for (int x = targetLocation.getX() - range; x <= targetLocation.getX() + range; x++)
		{
			for (int y = targetLocation.getY() - range; y <= targetLocation.getY() + range; y++)
			{
				// Check if this tile is within the scene
				if (x < 0 || y < 0 || x >= Constants.SCENE_SIZE || y >= Constants.SCENE_SIZE)
				{
					continue;
				}
				
				// Get the tile at this location
				Tile tile = client.getScene().getTiles()[client.getPlane()][x][y];
				if (tile == null)
				{
					continue;
				}
				
				// Check all GameObjects on this tile
				for (GameObject gameObject : tile.getGameObjects())
				{
					if (gameObject != null && isHerbPatch(gameObject.getId()))
					{
						foundPatches.add(gameObject);
					}
				}
			}
		}
		
		return foundPatches;
	}
	
	private boolean isHerbPatch(int objectId)
	{
		// Check if the object ID is in our herb patch list
		for (int patchId : HERB_PATCH_IDS)
		{
			if (objectId == patchId)
			{
				return true;
			}
		}
		return false;
	}
	
	private void drawGameObjectHighlight(Graphics2D graphics, GameObject gameObject)
	{
		Shape clickbox = gameObject.getClickbox();
		if (clickbox != null)
		{
			// Draw the highlight border
			graphics.setColor(PATCH_BORDER_COLOR);
			graphics.setStroke(new BasicStroke(2.0f));
			graphics.draw(clickbox);
			
			// Fill the clickbox with semi-transparent color
			graphics.setColor(PATCH_HIGHLIGHT_COLOR);
			graphics.fill(clickbox);
		}
	}
}
