package com.afctools;

import com.afctools.pklog.PkLogManager;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

@Slf4j
@PluginDescriptor(
		name = "AFC Tools",
		description = "All-in-one toolkit for Wilderness Agility FC runs",
		tags = {"wilderness", "agility", "pvp", "afc"}
)
public class AfcToolsPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private AfcToolsConfig config;

	@Inject
	private PkLogManager pkLogManager;

	@Inject
	private TicketLootManager ticketLootManager;

	@Inject
	private TileMarkerOverlay tileMarkerOverlay;

	@Inject
	private EventBus eventBus;

	@Inject
	private ClientToolbar clientToolbar;

	private AfcPluginPanel pluginPanel;
	private NavigationButton navButton;

	@Override
	protected void startUp() throws Exception
	{
		log.debug("AFC Tools started");

		pluginPanel = new AfcPluginPanel(config);

		pkLogManager.setPluginPanel(pluginPanel);
		ticketLootManager.setPluginPanel(pluginPanel);

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/icon.png");

		navButton = NavigationButton.builder()
				.tooltip("AFC Tools")
				.icon(icon)
				.priority(5)
				.panel(pluginPanel)
				.build();

		clientToolbar.addNavigation(navButton);

		eventBus.register(this);
		eventBus.register(pkLogManager);
		eventBus.register(ticketLootManager);
		overlayManager.add(tileMarkerOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.debug("AFC Tools stopped");

		clientToolbar.removeNavigation(navButton);

		eventBus.unregister(this);
		eventBus.unregister(pkLogManager);
		eventBus.unregister(ticketLootManager);

		pkLogManager.reset();
		overlayManager.remove(tileMarkerOverlay);
	}

	@net.runelite.client.eventbus.Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if ("afctools".equals(event.getGroup()) && "customGearList".equals(event.getKey()))
		{
			if (pluginPanel != null)
			{
				pluginPanel.rebuildGearList();
			}
		}
	}

	@Provides
	AfcToolsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AfcToolsConfig.class);
	}
}