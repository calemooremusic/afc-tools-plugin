package com.afctools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
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
        setPosition(OverlayPosition.BOTTOM_RIGHT);
    }

    @Override
    public Dimension render(Graphics2D g)
    {
        if (!config.enableHud())
        {
            return null;
        }

        double scale = config.hudScale() / 100.0;
        AffineTransform oldTransform = g.getTransform();
        g.scale(scale, scale);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = 140;
        int height = 85;

        g.setColor(GLASS_BG);
        g.fillRoundRect(0, 0, width, height, 12, 12);
        g.setColor(BORDER);
        g.drawRoundRect(0, 0, width, height, 12, 12);

        g.setFont(CRISP_FONT);

        int tickets = plugin.getSessionTickets();
        int bagValue = plugin.getLootingBagValue();
        int falls = plugin.getSessionFalls();

        BufferedImage ticketSprite = itemManager.getImage(ItemID.AGILITY_ARENA_TICKET);
        if (ticketSprite != null) g.drawImage(ticketSprite, 10, 8, null);
        g.setColor(Color.WHITE);
        g.drawString("Tickets: " + tickets, 42, 23);

        BufferedImage bagSprite = itemManager.getImage(ItemID.LOOTING_BAG);
        if (bagSprite != null) g.drawImage(bagSprite, 10, 33, null);

        if (bagValue > 1500000) g.setColor(new Color(255, 80, 80));
        else if (bagValue > 500000) g.setColor(new Color(255, 200, 50));
        else g.setColor(Color.WHITE);

        g.drawString("Risk: " + formatGp(bagValue), 42, 48);

        BufferedImage bootSprite = itemManager.getImage(ItemID.GRACEFUL_BOOTS);
        if (bootSprite != null) g.drawImage(bootSprite, 10, 58, null);
        g.setColor(Color.WHITE);
        g.drawString("Falls: " + falls, 42, 73);

        g.setTransform(oldTransform);

        // Scale the bounding box so shift-dragging recognizes the new size
        return new Dimension((int)(width * scale), (int)(height * scale));
    }

    private String formatGp(int amount)
    {
        if (amount >= 1000000) return String.format("%.1fM", amount / 1000000.0);
        if (amount >= 1000) return (amount / 1000) + "K";
        return String.valueOf(amount);
    }
}