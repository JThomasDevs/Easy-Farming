package com.easyfarming;

import java.awt.*;

public class HighlightUtils
{
	// RuneLite's typical blue color
	public static final Color HIGHLIGHT_COLOR = new Color(0, 150, 255, 200);
	public static final float STROKE_WIDTH_THICK = 3.0f;
	public static final float STROKE_WIDTH_NORMAL = 2.0f;
	
	public static void drawHighlight(Graphics2D graphics, Rectangle bounds)
	{
		if (bounds == null)
		{
			return;
		}
		
		// Draw simple highlight
		graphics.setColor(HIGHLIGHT_COLOR);
		graphics.setStroke(new BasicStroke(STROKE_WIDTH_NORMAL));
		graphics.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	public static void drawHighlight(Graphics2D graphics, Polygon polygon)
	{
		if (polygon == null)
		{
			return;
		}
		
		// Draw simple highlight
		graphics.setColor(HIGHLIGHT_COLOR);
		graphics.setStroke(new BasicStroke(STROKE_WIDTH_NORMAL));
		graphics.draw(polygon);
	}
	
	public static void drawHighlight(Graphics2D graphics, Shape shape)
	{
		if (shape == null)
		{
			return;
		}
		
		// Draw simple highlight
		graphics.setColor(HIGHLIGHT_COLOR);
		graphics.setStroke(new BasicStroke(STROKE_WIDTH_NORMAL));
		graphics.draw(shape);
	}
	
	public static void drawHighlightWithAlpha(Graphics2D graphics, Rectangle bounds, Color color, float alpha)
	{
		if (bounds == null || color == null)
		{
			return;
		}
		
		// Save the original composite
		Composite originalComposite = graphics.getComposite();
		
		// Set the color and alpha composite
		graphics.setColor(color);
		AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		graphics.setComposite(alphaComposite);
		
		// Fill the rectangle
		graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		
		// Restore the original composite
		graphics.setComposite(originalComposite);
	}
	
	/**
	 * Draw a highlight with a custom color (no alpha blending)
	 */
	public static void drawHighlightWithColor(Graphics2D graphics, Shape shape, Color color)
	{
		if (shape == null || color == null)
		{
			return;
		}
		
		// Set the color
		graphics.setColor(color);
		
		// Fill the shape
		graphics.fill(shape);
	}
}