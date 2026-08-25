package com.afctools.pklog;

import com.afctools.AfcPluginPanel;
import com.afctools.AfcToolsConfig;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Hitsplat;
import net.runelite.api.HitsplatID;
import net.runelite.api.Player;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
import net.runelite.client.eventbus.Subscribe;

@Singleton
public class PkLogManager
{
	@Inject
	private Client client;

	@Inject
	private AfcToolsConfig config;

	private AfcPluginPanel pluginPanel;
	private Player lastAttacker;
	private final Map<String, int[]> fightStats = new HashMap<>();
	private long fallImmunityTime = 0;

	public void setPluginPanel(AfcPluginPanel pluginPanel)
	{
		this.pluginPanel = pluginPanel;
	}

	public void triggerFallImmunity()
	{
		fallImmunityTime = System.currentTimeMillis();
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged event)
	{
		if (event.getTarget() == client.getLocalPlayer())
		{
			if (event.getSource() instanceof Player)
			{
				lastAttacker = (Player) event.getSource();
			}
			else
			{
				lastAttacker = null;
			}
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		if (!config.pkLogEnabled() || pluginPanel == null) return;

		Actor actor = event.getActor();
		Hitsplat hitsplat = event.getHitsplat();

		if (hitsplat.getAmount() <= 0) return;

		// BORROWED LOGIC: Strictly whitelist standard combat hitsplats.
		// This ignores minigame damage (Barbarian Assault), environmental damage, disease, etc.
		int hitType = hitsplat.getHitsplatType();
		if (!(hitType == HitsplatID.DAMAGE_ME
				|| hitType == HitsplatID.DAMAGE_ME_ORANGE
				|| hitType == HitsplatID.DAMAGE_OTHER_ORANGE
				|| hitType == HitsplatID.DAMAGE_OTHER
				|| hitType == HitsplatID.DAMAGE_MAX_ME
				|| hitType == HitsplatID.DAMAGE_MAX_ME_ORANGE))
		{
			return;
		}

		// 1. We TOOK damage
		if (actor == client.getLocalPlayer())
		{
			if (System.currentTimeMillis() - fallImmunityTime < 3000)
			{
				return;
			}

			if (lastAttacker != null)
			{
				addDamage(lastAttacker.getName(), 0, hitsplat.getAmount());
			}
		}
		// 2. We DEALT damage TO another player
		else if (actor instanceof Player && actor != client.getLocalPlayer())
		{
			if (hitsplat.isMine())
			{
				addDamage(((Player) actor).getName(), hitsplat.getAmount(), 0);
			}
		}
	}

	private void addDamage(String opponentName, int dealt, int taken)
	{
		int[] stats = fightStats.computeIfAbsent(opponentName, k -> new int[]{0, 0});
		stats[0] += dealt;
		stats[1] += taken;

		int currentWorld = client.getWorld();

		SwingUtilities.invokeLater(() ->
				pluginPanel.updatePvPStats(opponentName, stats[0], stats[1], currentWorld)
		);
	}

	public void reset()
	{
		lastAttacker = null;
		fightStats.clear();
		if (pluginPanel != null)
		{
			SwingUtilities.invokeLater(() -> pluginPanel.resetPvP());
		}
	}
}