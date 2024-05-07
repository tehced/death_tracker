package com.cedolad;

import net.runelite.api.Client;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import net.runelite.client.account.SessionManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;

public class DeathTrackerPanel extends PluginPanel {

    @Inject
    private Client client;

    @Inject
    private EventBus eventBus;

    @Inject
    private SessionManager sessionManager;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    @Named("runelite.version")
    private String runeliteVersion;


    void init()
    {
//        JPanel versionPanel = new JPanel();
//        versionPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
//        versionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
//        versionPanel.setLayout(new GridLayout(0, 1));

        final Font smallFont = FontManager.getRunescapeSmallFont();

        eventBus.register(this);
    }

    void deinit()
    {
        eventBus.unregister(this);
    }

    private static String htmlLabel(String key, String value)
    {
        return "<html><body style = 'color:#a5a5a5'>" + key + "<span style = 'color:white'>" + value + "</span></body></html>";
    }
}
