package com.cedolad;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

import static net.runelite.client.externalplugins.ExternalPluginManager.*;

public class DeathTrackerTest {
    public static void main(String[] args) throws Exception {
        loadBuiltin(DeathTrackerPlugin.class);
        RuneLite.main(args);
    }
}