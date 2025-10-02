package com.easyfarming;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.ui.overlay.OverlayManager;

import java.awt.image.BufferedImage;
import lombok.extern.slf4j.Slf4j;

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

	private NavigationButton navButton;

	@Provides
	EasyFarmingConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EasyFarmingConfig.class);
	}

	@Override
	protected void startUp()
	{
		log.info("Easy Farming started!");
		
		// TODO: Initialize new farming system
		// - Create farming runs
		// - Set up overlays
		// - Initialize UI
	}

	@Override
	protected void shutDown()
	{
		log.info("Easy Farming stopped!");
		
		// TODO: Clean up resources
		// - Remove overlays
		// - Clean up UI
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			log.info("Player logged in");
		}
	}

	// Getters for other components
	public Client getClient() { return client; }
	public EasyFarmingConfig getConfig() { return config; }
}