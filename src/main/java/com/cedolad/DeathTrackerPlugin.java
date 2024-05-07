package com.cedolad;

import com.google.inject.Provides;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.Client;
import net.runelite.api.events.ActorDeath;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
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
	private int deathCount;

	@Inject
	private Client client;

	@Inject
	private DeathTrackerConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private DeathTrackerOverlay overlay;

	@Inject
	private ClientToolbar clientToolbar;

	private DeathTrackerPanel panel;
	private NavigationButton navButton;

	@Provides
	DeathTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DeathTrackerConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		log.info("Death Tracker started!");

		overlayManager.add(overlay);

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
		log.info("Death Tracker stopped!");
		overlayManager.remove(overlay);
		panel.deinit();
		clientToolbar.removeNavigation(navButton);
		panel = null;
		navButton = null;
	}

	@Subscribe
	public void onActorDeath(ActorDeath actor)
	{
		if (config.trackerOption()) {
			String name = actor.getActor().getName();
			deathCount++;
		}
	}

	int getDeathCount()
	{
		return deathCount;
	}
}
