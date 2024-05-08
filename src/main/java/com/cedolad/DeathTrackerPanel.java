package com.cedolad;

import net.runelite.api.Client;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import net.runelite.client.account.SessionManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.QuantityFormatter;

import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;

public class DeathTrackerPanel extends PluginPanel
{
    private static final String HTML_LABEL_TEMPLATE =
            "<html><body style='color:%s'>%s<span style='color:white'>%s</span></body></html>";

    private final PluginErrorPanel errorPanel = new PluginErrorPanel();

    public final JPanel logsContainer = new JPanel();
    public final JPanel overallPanel = new JPanel();
    private final JLabel overallDeathsLabel = new JLabel();
    private final JLabel overallCostLabel = new JLabel();
    private final JLabel overallIcon = new JLabel();
    private int overallDeaths;
    private int overallCost;

    private final DeathTrackerPlugin plugin;

    DeathTrackerPanel(DeathTrackerPlugin plugin, DeathTrackerConfig config)
    {
        this.plugin = plugin;
        setBorder(new EmptyBorder(6, 6, 6, 6));
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        final JPanel layoutPanel = new JPanel();
        layoutPanel.setLayout(new BoxLayout(layoutPanel, BoxLayout.Y_AXIS));
        add(layoutPanel, BorderLayout.NORTH);

        overallPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        overallPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        overallPanel.setLayout(new BorderLayout());
        overallPanel.setVisible(true);

        final JPanel overallInfo = new JPanel();
        overallInfo.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        overallInfo.setLayout(new GridLayout(2, 1));
        overallInfo.setBorder(new EmptyBorder(0, 10, 0, 0));
        overallDeathsLabel.setFont(FontManager.getRunescapeSmallFont());
        overallCostLabel.setFont(FontManager.getRunescapeSmallFont());
        overallInfo.add(overallDeathsLabel);
        overallInfo.add(overallCostLabel);
        overallPanel.add(overallIcon, BorderLayout.WEST);
        overallPanel.add(overallInfo, BorderLayout.CENTER);

        logsContainer.setLayout(new BoxLayout(logsContainer, BoxLayout.Y_AXIS));
        layoutPanel.add(overallPanel);
        layoutPanel.add(logsContainer);

        overallPanel.setVisible(false);
        logsContainer.setVisible(true);

        errorPanel.setContent("Death Tracker", "You have not died... yet.");
        add(errorPanel);

    }

    public void updateOverall()
    {
        overallDeaths = plugin.getDeathCount();

        overallDeathsLabel.setText(htmlLabel("Total Deaths: ", overallDeaths));
        overallCostLabel.setText(htmlLabel("Total Cost: ", overallCost));

        if (overallDeaths <= 0)
        {
            add(errorPanel);
            overallPanel.setVisible(false);
        }
        else
        {
            remove(errorPanel);
            overallPanel.setVisible(true);
        }

    }

    private static String htmlLabel(String key, long value)
    {
        return String.format(HTML_LABEL_TEMPLATE, ColorUtil.toHexColor(ColorScheme.LIGHT_GRAY_COLOR), key, value);
    }
}

