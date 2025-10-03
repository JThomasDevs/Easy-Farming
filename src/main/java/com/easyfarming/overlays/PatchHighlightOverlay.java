package com.easyfarming.overlays;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.HighlightUtils;
import com.easyfarming.core.*;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import net.runelite.api.Point;

/**
 * Highlights farming patches when the player is near them.
 * This overlay helps guide players to the correct patch location.
 */
public class PatchHighlightOverlay extends Overlay
{
    private final Client client;
    private final EasyFarmingConfig config;
    private final FarmingRunState runState;
    
    private static final int HIGHLIGHT_RADIUS = 10; // Tiles
    private static final Color PATCH_HIGHLIGHT_COLOR = new Color(0, 255, 0, 50);
    private static final Color PATCH_BORDER_COLOR = Color.GREEN;
    
    @Inject
    public PatchHighlightOverlay(Client client, EasyFarmingConfig config, FarmingRunState runState)
    {
        this.client = client;
        this.config = config;
        this.runState = runState;
        
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }
    
    @Override
    public Dimension render(Graphics2D graphics)
    {
        // Only show highlights if farming run is active and highlighting is enabled
        if (!runState.isRunActive() || !config.highlightNextAction())
        {
            return null;
        }
        
        FarmingState currentState = runState.getCurrentState();
        
        // Highlight patch when navigating, at patch, or performing patch actions
        if (currentState == FarmingState.NAVIGATING || 
            currentState == FarmingState.AT_PATCH ||
            currentState.isAtPatch())
        {
            highlightCurrentPatch(graphics);
        }
        
        return null;
    }
    
    /**
     * Highlight the current farming patch
     */
    private void highlightCurrentPatch(Graphics2D graphics)
    {
        Location currentLocation = runState.getCurrentLocation();
        if (currentLocation == null)
        {
            return;
        }
        
        WorldPoint patchPoint = currentLocation.getPatchPoint();
        if (patchPoint == null)
        {
            return;
        }
        
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return;
        }
        
        // Check if player is within highlighting range
        WorldPoint playerLocation = player.getWorldLocation();
        int distance = Math.max(
            Math.abs(playerLocation.getX() - patchPoint.getX()),
            Math.abs(playerLocation.getY() - patchPoint.getY())
        );
        
        if (distance > HIGHLIGHT_RADIUS)
        {
            return; // Too far away
        }
        
        // Convert world point to local point
        LocalPoint patchLocalPoint = LocalPoint.fromWorld(client, patchPoint);
        if (patchLocalPoint == null)
        {
            return;
        }
        
        // Render tile highlight
        renderTileHighlight(graphics, patchLocalPoint, patchPoint);
    }
    
    /**
     * Render a tile highlight at the specified location
     */
    private void renderTileHighlight(Graphics2D graphics, LocalPoint localPoint, WorldPoint worldPoint)
    {
        if (localPoint == null)
        {
            return;
        }
        
        // Get the polygon for the tile
        Polygon tilePoly = Perspective.getCanvasTilePoly(client, localPoint);
        if (tilePoly == null)
        {
            return;
        }
        
        // Draw filled polygon
        graphics.setColor(PATCH_HIGHLIGHT_COLOR);
        graphics.fillPolygon(tilePoly);
        
        // Draw border
        graphics.setColor(PATCH_BORDER_COLOR);
        graphics.setStroke(new BasicStroke(2));
        graphics.drawPolygon(tilePoly);
        
        // Draw patch name above the tile
        Location currentLoc = getCurrentLocation();
        if (currentLoc != null)
        {
            Point textLocation = Perspective.getCanvasTextLocation(client, graphics, localPoint, 
                currentLoc.getName(), 0);
            if (textLocation != null)
            {
                renderTextLabel(graphics, textLocation, currentLoc.getName());
            }
        }
    }
    
    /**
     * Render a text label at the specified location
     */
    private void renderTextLabel(Graphics2D graphics, Point location, String text)
    {
        FontMetrics fm = graphics.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        
        // Draw background
        graphics.setColor(new Color(0, 0, 0, 128));
        graphics.fillRect(
            location.getX() - textWidth / 2 - 2,
            location.getY() - textHeight - 2,
            textWidth + 4,
            textHeight + 4
        );
        
        // Draw text
        graphics.setColor(Color.WHITE);
        graphics.drawString(text, location.getX() - textWidth / 2, location.getY() - 2);
    }
    
    /**
     * Get the current location from the run state
     */
    private Location getCurrentLocation()
    {
        return runState.getCurrentLocation();
    }
}
