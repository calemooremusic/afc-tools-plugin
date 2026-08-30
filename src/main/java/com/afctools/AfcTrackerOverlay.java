package com.afctools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

public class AfcTrackerOverlay extends Overlay
{
    private final Client client;
    private final AfcToolsPlugin plugin;
    private final AfcToolsConfig config;
    private final ItemManager itemManager;

    // The custom visual style for the glass UI
    private static final Color GLASS_BG = new Color(20, 20, 20, 180);
    private static final Color BORDER = new Color(60, 60, 60, 255);
    private static final Font CRISP_FONT = new Font("SansSerif", Font.BOLD, 12);

    @Inject
    public AfcTrackerOverlay(Client client, AfcToolsPlugin plugin, AfcToolsConfig config, ItemManager itemManager)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;

        // Defaults to the bottom right, but users can hold ALT to drag it anywhere
        setPosition(OverlayPosition.BOTTOM_RIGHT);
    }

    @Override
    public Dimension render(Graphics2D g)
    {
        // Master toggle check
        if (!config.enableHud())
        {
            return null;
        }

        // Enable anti-aliasing for smooth, crisp text and rounded borders
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = 140;
        int height = 85;

        // Draw the translucent glass background
        g.setColor(GLASS_BG);
        g.fillRoundRect(0, 0, width, height, 12, 12);
        g.setColor(BORDER);
        g.drawRoundRect(0, 0, width, height, 12, 12);

        g.setFont(CRISP_FONT);

        // Fetch live data from your main plugin class
        int tickets = plugin.getSessionTickets();
        int bagValue = plugin.getLootingBagValue();
        int falls = plugin.getSessionFalls();

        // Row 1: Agility Tickets
        BufferedImage ticketSprite = itemManager.getImage(ItemID.AGILITY_ARENA_TICKET);
        if (ticketSprite != null) g.drawImage(ticketSprite, 10, 8, null);
        g.setColor(Color.WHITE);
        g.drawString("Tickets: " + tickets, 42, 23);

        // Row 2: Looting Bag Risk
        BufferedImage bagSprite = itemManager.getImage(ItemID.LOOTING_BAG);
        if (bagSprite != null) g.drawImage(bagSprite, 10, 33, null);

        // Reactive typography for risk
        if (bagValue > 1500000) g.setColor(new Color(255, 80, 80)); // Red warning
        else if (bagValue > 500000) g.setColor(new Color(255, 200, 50)); // Yellow warning
        else g.setColor(Color.WHITE);

        g.drawString("Risk: " + formatGp(bagValue), 42, 48);

        // Row 3: Falls Tracker (Using Graceful Boots icon)
        BufferedImage bootSprite = itemManager.getImage(ItemID.GRACEFUL_BOOTS);
        if (bootSprite != null) g.drawImage(bootSprite, 10, 58, null);
        g.setColor(Color.WHITE);
        g.drawString("Falls: " + falls, 42, 73);

        return new Dimension(width, height);
    }

    // Helper to make huge GP numbers look clean (e.g. 1.5M instead of 1500000)
    private String formatGp(int amount)
    {
        if (amount >= 1000000) return String.format("%.1fM", amount / 1000000.0);
        if (amount >= 1000) return (amount / 1000) + "K";
        return String.valueOf(amount);
    }
}
