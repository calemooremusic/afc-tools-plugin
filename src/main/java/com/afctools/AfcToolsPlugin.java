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
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
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
		name = "Wilderness Agility Tool",
		description = "A customizable toolkit for Wilderness Agility survival and tracking.",
		tags = {"wilderness", "agility", "pvp", "tracker", "overlay"}
)
public class AfcToolsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ConfigManager configManager;

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

	@Inject
	private AfcTrackerOverlay trackerOverlay;

	@Inject
	private HiscoresManager hiscoresManager;

	private NavigationButton navButton;
	private AfcPluginPanel panel;

	private int sessionFalls = 0;
	private int sessionTickets = 0;
	private long lootingBagValue = 0;

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
		sessionFalls = 0;
		sessionTickets = 0;
		lootingBagValue = 0;

		try
		{
			panel = new AfcPluginPanel(config);
			final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/icon.png");

			panel.setResetPvPCallback(() -> pkLogManager.reset());
			panel.setRefreshHiscoresCallback(this::fetchHiscores);
			panel.setForceSyncCallback(this::forceSyncLaps);

			ticketLootManager.setPlugin(this);
			pkLogManager.setPluginPanel(panel);

			navButton = NavigationButton.builder()
					.tooltip("Wilderness Agility Tool")
					.icon(icon)
					.priority(5)
					.panel(panel)
					.build();

			clientToolbar.addNavigation(navButton);
			eventBus.register(ticketLootManager);
			eventBus.register(pkLogManager);
			overlayManager.add(tileMarkerOverlay);
			if (trackerOverlay != null) overlayManager.add(trackerOverlay);

			if (client.getGameState() == GameState.LOGGED_IN) updateSafetySettings();

			fetchHiscores();
		}
		catch (Exception e)
		{
			log.error("Failed to cleanly start plugin panel.", e);
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
		eventBus.unregister(ticketLootManager);
		eventBus.unregister(pkLogManager);
		overlayManager.remove(tileMarkerOverlay);
		if (trackerOverlay != null) overlayManager.remove(trackerOverlay);
	}

	private void fetchHiscores()
	{
		hiscoresManager.fetchHiscores(hiscores -> {
			if (panel != null) panel.updateHiscores(hiscores);
		});
	}

	private void forceSyncLaps()
	{
		clientThread.invokeLater(() ->
		{
			if (client.getLocalPlayer() == null)
			{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>Wilderness Agility Tool:</col> Please log in to sync your laps.", null);
				return;
			}

			try
			{
				String[] possibleKeys = {"wilderness agility", "wilderness agility course", "wilderness agility ticket"};
				Integer laps = null;

				for (String key : possibleKeys)
				{
					// Check the modern RSProfile specific config first
					laps = configManager.getRSProfileConfiguration("killcount", key, Integer.class);
					if (laps != null && laps > 0) break;

					// Fallback to legacy global config
					laps = configManager.getConfiguration("killcount", key, Integer.class);
					if (laps != null && laps > 0) break;
				}

				if (laps != null && laps > 0)
				{
					hiscoresManager.submitLapCount(client.getLocalPlayer().getName(), laps);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=00ff00>Wilderness Agility Tool:</col> Found " + laps + " saved laps in RuneLite. Syncing to Hiscores...", null);
					return;
				}
			}
			catch (Exception e)
			{
				log.debug("Failed to parse laps from config", e);
			}

			// Adjusted error message since chat commands are no longer supported for syncing
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>Wilderness Agility Tool:</col> Direct memory scan failed. Please complete one physical lap so RuneLite can track your score!", null);
		});
	}

	private void updateSafetySettings()
	{
		if (client.getGameState() != GameState.LOGGED_IN || panel == null) return;
		try {
			panel.updateLiveSettings(client.getVarpValue(172), client.getVarpValue(1107), client.getVarpValue(1306), client.getVarbitValue(13131));
		} catch (Exception ignored) {}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event) { updateSafetySettings(); }

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("afctools") && panel != null)
		{
			if (event.getKey().equals("customGearList")) panel.rebuildGearList();
			else if (event.getKey().equals("customSettingsList")) panel.rebuildSettingsList();
			else if (event.getKey().equals("streamerModeLoot") && client.getGameState() == GameState.LOGGED_IN) panel.updateLootValue(lootingBagValue);
			else if (event.getKey().equals("showPanelStats")) panel.togglePanelStats(config.showPanelStats());
			else if (event.getKey().equals("showHiscores")) panel.toggleHiscores(config.showHiscores());
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		String rawMsg = Text.removeTags(event.getMessage()).replace('\u00A0', ' ').toLowerCase();

		// Track Course Falls (Only triggers on raw game messages)
		if (config.fallTrackerEnabled() && (event.getType() == ChatMessageType.GAMEMESSAGE || event.getType() == ChatMessageType.SPAM))
		{
			if (FALL_MESSAGES.stream().anyMatch(rawMsg::contains))
			{
				sessionFalls++;
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Ouch! Session Falls: " + sessionFalls, null);
				pkLogManager.triggerFallImmunity();
				if (panel != null) panel.updateFallCount(sessionFalls);
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN || gameStateChanged.getGameState() == GameState.HOPPING)
		{
			sessionFalls = 0;
			if (panel != null) panel.updateFallCount(sessionFalls);
		}
		else if (gameStateChanged.getGameState() == GameState.LOGGED_IN) updateSafetySettings();
	}

	public void setSessionTickets(int tickets)
	{
		this.sessionTickets = tickets;
		if (panel != null) panel.updateTickets(tickets);
	}

	public void setLootingBagValue(long value)
	{
		this.lootingBagValue = value;
		if (panel != null) panel.updateLootValue(value);
	}

	public int getSessionTickets() { return sessionTickets; }
	public int getLootingBagValue() { return (int) lootingBagValue; }
	public int getSessionFalls() { return sessionFalls; }
}