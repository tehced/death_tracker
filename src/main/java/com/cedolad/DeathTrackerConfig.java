package com.cedolad;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(DeathTrackerConfig.GROUP)
public interface DeathTrackerConfig extends Config
{
	String GROUP = "deathtracker";

	@ConfigItem(
			position = 1,
			keyName = "infoBoxesOption",
			name = "Info Boxes",
			description = "Toggles info boxes overlay"
	)
	default boolean infoBoxesOption() { return false; }

}
