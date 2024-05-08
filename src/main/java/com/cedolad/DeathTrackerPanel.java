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
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;

public class DeathTrackerPanel extends PluginPanel
{
    private PluginErrorPanel errorPanel = new PluginErrorPanel();

    private final DeathTrackerPlugin plugin;
    private final DeathTrackerConfig config;

    DeathTrackerPanel(DeathTrackerPlugin plugin, DeathTrackerConfig config)
    {
        this.plugin = plugin;
        this.config = config;

        setBorder(new EmptyBorder(6, 6, 6, 6));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setLayout(new BorderLayout());

        JPanel layoutPanel = new JPanel();
        layoutPanel.setLayout(new BoxLayout(layoutPanel, BoxLayout.Y_AXIS));
        add(layoutPanel, BorderLayout.NORTH);

        errorPanel.setContent("Death tracker", "You have not died... yet.");
        add(errorPanel);
    }

    private JPanel buildActionsPanel()
    {
        JPanel actionsContainer = new JPanel();
        actionsContainer.setLayout(new BorderLayout());
        actionsContainer.setBackground(ColorScheme.DARK_GRAY_COLOR);
        actionsContainer.setPreferredSize(new Dimension(0, 30));
        actionsContainer.setBorder(new EmptyBorder(5, 5, 5, 10));
        actionsContainer.setVisible(false);

        return actionsContainer;
    }

    private static String htmlLabel(String key, String value)
    {
        return "<html><body style = 'color:#a5a5a5'>" + key + "<span style = 'color:white'>" + value + "</span></body></html>";
    }
}
