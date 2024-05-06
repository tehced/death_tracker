package com.cedolad;

import net.runelite.api.Client;

import javax.annotation.Nullable;
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

    private static final ImageIcon ARROW_RIGHT_ICON;

    @Inject
    @Nullable
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

    static
    {
        ARROW_RIGHT_ICON = new ImageIcon(ImageUtil.loadImageResource(DeathTrackerPanel.class, "/util/arrow_right.png"));
    }

    void init()
    {

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel versionPanel = new JPanel();
        versionPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        versionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        versionPanel.setLayout(new GridLayout(0, 1));

        final Font smallFont = FontManager.getRunescapeSmallFont();

        JLabel version = new JLabel(htmlLabel("Runelite Version: ", runeliteVersion));
        version.setFont(smallFont);

        versionPanel.add(version);

        add(versionPanel, BorderLayout.NORTH);

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
