package com.cedolad;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.Objects;

@PluginDescriptor(
        name = "Death Tracker",
        description = "Tracks deaths, and logs gravestone costs",
        tags = {"death, tracker"}
)
@Slf4j
public class DeathTrackerPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private DeathTrackerConfig config;

    @Inject
    private ConfigManager configManager;

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

    private boolean previouslyLoggedIn;
    private String playersLastInteractionWithNPC;
    private String NPCsLastInteractionWithPlayer;

    @Setter
    @Getter
    private String currentlyLoggedInAccount;

    @Setter
    @Getter
    private int previousHitsplitTypeID;

    @Provides
    DeathTrackerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DeathTrackerConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        log.info("Death Tracker started!");

        if (config.infoBoxesOption()) {
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
    protected void shutDown() throws Exception {
        log.info("Death Tracker stopped!");
        overlayManager.remove(overlay);
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN) {
            onLoggedInGameState();
        } else if (event.getGameState() == GameState.LOGIN_SCREEN && previouslyLoggedIn) {
            if (currentlyLoggedInAccount != null && client.getGameState() != GameState.LOGGED_IN) {
                handleLogout();
            }
        }
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged interaction) {
        Actor source = interaction.getSource();
        Actor target = interaction.getTarget();
        if (target != null && Objects.equals(source.getName(), currentlyLoggedInAccount)) {
            log.info("{} interacting with {}", source.getName(), target.getName());
            playersLastInteractionWithNPC = target.getName();
        }
        if (target != null) {
            log.info("{} interacting with {}", target.getName(), source.getName());
            NPCsLastInteractionWithPlayer = source.getName();
        }
    }

    @Subscribe
    public void onActorDeath(ActorDeath death) {
        log.info(String.valueOf(previousHitsplitTypeID));
        log.info(String.valueOf(HitsplatID.POISON));
        Actor actor = death.getActor();
        String actorName = getActorName(actor);

        Actor npc = actor.getInteracting();
        if (Objects.equals(actorName, currentlyLoggedInAccount)) {
            log.debug(currentlyLoggedInAccount);
            if (npc != null) {
                String npcName = getActorName(npc);
                setNPCName(npcName);
                log.info("{} killed {}", npcName, actorName);
            } else if (previousHitsplitTypeID == HitsplatID.POISON){
                // probably means you died to poison damage
                log.info("Died to poison probably");
            }
            log.info("{} last targeted by player, {} last targeted player", playersLastInteractionWithNPC, NPCsLastInteractionWithPlayer);
            deathCount++;
            panel.updateOverall();
        }
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied appliedHitsplat) {
        Hitsplat hitsplat = appliedHitsplat.getHitsplat();
        int hitsplatType = hitsplat.getHitsplatType();
        int amount = hitsplat.getAmount();

        Actor actor = appliedHitsplat.getActor();

        if (hitsplat.isMine()) {
            if (Objects.equals(actor.getName(), currentlyLoggedInAccount)) {
                log.info("{} taking {} damage", actor.getName(), amount);
                setPreviousHitsplitTypeID(hitsplatType);
            }

        }
    }

    private void onLoggedInGameState() {
        clientThread.invokeLater(() ->
        {
            if (client.getGameState() != GameState.LOGGED_IN) {
                return true;
            }

            final Player player = client.getLocalPlayer();

            if (player == null) {
                return false;
            }

            final String name = player.getName();

            if (name == null) {
                return false;
            }

            if (name.isEmpty()) {
                return false;
            }

            previouslyLoggedIn = true;

            if (currentlyLoggedInAccount == null) {
                handleLogin(name);
            }

            return true;
        });
    }

    public void handleLogin(String displayName) {
        log.info("{} has logged in.", displayName);
        currentlyLoggedInAccount = displayName;

    }

    public void handleLogout() {
        log.info("{} has logged out", currentlyLoggedInAccount);
        currentlyLoggedInAccount = null;
    }

    private String getActorName(Actor actor) {
        return actor.getName();
    }

}
