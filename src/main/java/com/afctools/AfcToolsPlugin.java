package com.afctools;

import com.google.inject.Provides;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import com.afctools.pklog.PkLogManager;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
		name = "AFC Tools",
		description = "Wilderness Agility tools and trackers",
		tags = {"agility", "wilderness", "tracker"}
)
public class AfcToolsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private AfcToolsConfig config;

	@Inject
	private EventBus eventBus;

	@Inject
	private TicketLootManager ticketLootManager;

	@Inject
	private PkLogManager pkLogManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private TileMarkerOverlay tileMarkerOverlay;

	private NavigationButton navButton;
	private AfcPluginPanel panel;

	private int sessionFalls;

	private static final List<String> FALL_MESSAGES = Arrays.asList(
			"you lose your footing and fall into the wolf pit",
			"you slip and fall to the pit below",
			"you lose your footing and fall into the lava",
			"you slip and fall onto the spikes below",
			"you fall into the pit below. the spikes hurt"
	);

	@Provides
	AfcToolsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AfcToolsConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		log.info("AFC Tools started!");
		sessionFalls = 0;

		panel = new AfcPluginPanel(config);

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/icon.png");

		ticketLootManager.setPluginPanel(panel);
		pkLogManager.setPluginPanel(panel);

		navButton = NavigationButton.builder()
				.tooltip("AFC Tools")
				.icon(icon)
				.priority(5)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);
		eventBus.register(ticketLootManager);
		eventBus.register(pkLogManager);
		overlayManager.add(tileMarkerOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("AFC Tools stopped!");
		sessionFalls = 0;

		clientToolbar.removeNavigation(navButton);
		eventBus.unregister(ticketLootManager);
		eventBus.unregister(pkLogManager);
		overlayManager.remove(tileMarkerOverlay);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("afctools") && panel != null)
		{
			if (event.getKey().equals("customGearList"))
			{
				panel.rebuildGearList();
			}
			else if (event.getKey().equals("customSettingsList"))
			{
				panel.rebuildSettingsList();
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (!config.fallTrackerEnabled())
		{
			return;
		}

		if (event.getType() == ChatMessageType.GAMEMESSAGE || event.getType() == ChatMessageType.SPAM)
		{
			String message = Text.removeTags(event.getMessage()).toLowerCase();
			boolean isFall = FALL_MESSAGES.stream().anyMatch(message::contains);

			if (isFall)
			{
				sessionFalls++;
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Ouch! Session Falls: " + sessionFalls, null);

				if (panel != null)
				{
					panel.updateFallCount(sessionFalls);
				}
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN ||
				gameStateChanged.getGameState() == GameState.HOPPING)
		{
			sessionFalls = 0;

			if (panel != null)
			{
				panel.updateFallCount(sessionFalls);
			}
		}
	}
}