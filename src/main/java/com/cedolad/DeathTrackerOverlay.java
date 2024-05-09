package com.cedolad;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

class DeathTrackerOverlay extends OverlayPanel {
    private final DeathTrackerPlugin plugin;
    private final DeathTrackerConfig config;

    @Inject
    private DeathTrackerOverlay(DeathTrackerPlugin plugin, DeathTrackerConfig config) {
        super(plugin);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.infoBoxesOption()) {
            final String deathCountString = "Deaths " + plugin.getDeathCount();

            panelComponent.getChildren().add(TitleComponent.builder()
                    .text(deathCountString)
                    .color(Color.RED)
                    .build());
            panelComponent.setPreferredSize(new Dimension(
                    graphics.getFontMetrics().stringWidth(deathCountString) + 10,
                    0));

            return super.render(graphics);
        }

        return null;
    }
}
