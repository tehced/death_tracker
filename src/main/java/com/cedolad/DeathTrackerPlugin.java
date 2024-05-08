package com.cedolad;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Hitsplat;
import net.runelite.api.annotations.HitsplatType;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
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

	@Setter
	@Getter
	private String NPCName;

	@Getter
	private int deathCount;

	@Provides
	DeathTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DeathTrackerConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		log.info("Death Tracker started!");

		if (config.infoBoxesOption())
		{
			overlayManager.add(overlay);
		}

		panel = new DeathTrackerPanel(this, config);

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "grave_icon.png");

		navButton = NavigationButton.builder()
				.tooltip("Death Tracker")
				.icon(icon)
				.priority(2)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Death Tracker stopped!");
		overlayManager.remove(overlay);
		clientToolbar.removeNavigation(navButton);
	}

    @Subscribe
    public void onInteractingChanged(InteractingChanged interaction)
    {
        Actor source = interaction.getSource();
        Actor target = interaction.getTarget();
        if (source != null && target != null)
        {
            log.info("{} {}", source.getName(), target.getName());
        }
    }

	@Subscribe
	public void onActorDeath(ActorDeath death)
	{
		Actor actor = death.getActor();
		String actorName = getActorName(actor);

		Actor npc = actor.getInteracting();
		if (npc != null)
		{
			String npcName = getActorName(npc);
			setNPCName(npcName);
			log.info("{} killed {}", npcName, actorName);
		}
		else
		{
			// probably means you died to poison damage
			log.info("Died to poison probably");
		}

		deathCount++;
		panel.updateOverall();
	}

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied appliedHitsplat)
    {
        Hitsplat hitsplat = appliedHitsplat.getHitsplat();
		int hitsplatType = hitsplat.getHitsplatType();
		String hitsplatTypeString;
		int amount = hitsplat.getAmount();

        Actor actor = appliedHitsplat.getActor();

		switch (hitsplatType){
			case 16:
				hitsplatTypeString = "normal";
				break;
			case 65:
				hitsplatTypeString = "poison";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + hitsplatType);
        }

		log.info("{} taking {} damage: {}", actor.getName(), hitsplatTypeString, amount);
    }

	private String getActorName(Actor actor)
	{
        return actor.getName();
	}

}
