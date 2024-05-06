package com.cedolad;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;

@PluginDescriptor(
	name = "Death Tracker",
	description = "Tracks deaths, and logs gravestone costs",
	tags = {"death, tracker"}
)
@Slf4j
public class DeathTrackerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private DeathTrackerConfig config;

	private DeathTrackerPanel panel;
	private NavigationButton navButton;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");

		panel = injector.getInstance(DeathTrackerPanel.class);
		panel.init();

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "grave_icon.png");

		navButton = NavigationButton.builder()
				.tooltip("Death Tracker")
				.icon(icon)
				.priority(1)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
		panel.deinit();
		clientToolbar.removeNavigation(navButton);
		panel = null;
		navButton = null;
	}

	@Provides
	DeathTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DeathTrackerConfig.class);
	}
}
