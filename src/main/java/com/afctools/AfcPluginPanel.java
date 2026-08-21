package com.afctools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.LinkBrowser;

public class AfcPluginPanel extends PluginPanel
{
    private final AfcToolsConfig config;
    private final JPanel pvpContainer;
    private final JPanel gearContainer;
    private final Map<String, JPanel> fightBoxes = new HashMap<>();

    private JLabel ticketsLabel;
    private JLabel lootLabel;

    public AfcPluginPanel(AfcToolsConfig config)
    {
        super();
        this.config = config;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- DISCORD INFO BANNER ---
        JPanel discordBox = new JPanel(new BorderLayout());
        discordBox.setBackground(new Color(88, 101, 242));
        discordBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorScheme.DARK_GRAY_COLOR, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel infoText = new JLabel("<html><div style='text-align: center;'>For more info on gear, strategy and rules for group runs visit:<br><br><font color='#FFFF00'><b><u>discord.gg/agilityfc</u></b></font></div></html>");
        infoText.setForeground(Color.WHITE);
        infoText.setFont(FontManager.getRunescapeSmallFont());
        infoText.setHorizontalAlignment(JLabel.CENTER);
        infoText.setCursor(new Cursor(Cursor.HAND_CURSOR));
        infoText.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                LinkBrowser.browse("https://discord.gg/agilityfc");
            }
        });

        discordBox.add(infoText, BorderLayout.CENTER);
        add(discordBox);
        add(createSpacer());

        // --- GEAR CHECKLIST SECTION ---
        JPanel gearBox = createBaseBox("Pre-Run Checklist");
        gearContainer = new JPanel(new GridLayout(0, 1, 0, 4));
        gearContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        gearBox.add(gearContainer, BorderLayout.CENTER);
        rebuildGearList();
        add(gearBox);
        add(createSpacer());

        // --- AFC RUNNER SETTINGS SECTION ---
        JPanel settingsBox = createBaseBox("AFC Runner Settings");
        JPanel settingsItems = new JPanel(new GridLayout(0, 1, 0, 4));
        settingsItems.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        addItem(settingsItems, "Entity Hider: ON");
        addItem(settingsItems, "Auto-Retaliate: OFF");
        addItem(settingsItems, "Player Attack: Right-Click / Hidden");
        addItem(settingsItems, "NPC Attack: Hidden");
        addItem(settingsItems, "Skull Prevention: OFF");
        settingsBox.add(settingsItems, BorderLayout.CENTER);
        add(settingsBox);
        add(createSpacer());

        // --- RUN STATUS SECTION ---
        JPanel statusBox = createBaseBox("Run Status");
        JPanel statusItems = new JPanel(new GridLayout(0, 1, 0, 4));
        statusItems.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        ticketsLabel = new JLabel("Dispenser Tickets: 0");
        ticketsLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        ticketsLabel.setFont(FontManager.getRunescapeSmallFont());
        statusItems.add(ticketsLabel);

        lootLabel = new JLabel("Looting Bag: 0 gp");
        lootLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        lootLabel.setFont(FontManager.getRunescapeSmallFont());
        statusItems.add(lootLabel);

        statusBox.add(statusItems, BorderLayout.CENTER);
        add(statusBox);
        add(createSpacer());

        // --- PVP TRACKER SECTION ---
        JPanel pvpBox = createBaseBox("PvP Encounters");
        pvpContainer = new JPanel();
        pvpContainer.setLayout(new BoxLayout(pvpContainer, BoxLayout.Y_AXIS));
        pvpContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        pvpBox.add(pvpContainer, BorderLayout.CENTER);
        add(pvpBox);

        // --- CREATOR FOOTER ---
        add(createSpacer());
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        JLabel creditLabel = new JLabel("Plug-in Created by: DawnKeedic");
        creditLabel.setForeground(ColorScheme.MEDIUM_GRAY_COLOR);
        creditLabel.setFont(FontManager.getRunescapeSmallFont());
        footerPanel.add(creditLabel);
        add(footerPanel);
    }

    public void rebuildGearList()
    {
        gearContainer.removeAll();
        String rawList = config.customGearList();
        if (rawList != null && !rawList.trim().isEmpty())
        {
            String[] items = rawList.split("[\n,]");
            for (String item : items)
            {
                String trimmed = item.trim();
                if (!trimmed.isEmpty())
                {
                    addItem(gearContainer, trimmed);
                }
            }
        }
        gearContainer.revalidate();
        gearContainer.repaint();
    }

    private JPanel createBaseBox(String titleText)
    {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorScheme.DARK_GRAY_COLOR, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel title = new JLabel(titleText);
        title.setForeground(Color.WHITE);
        title.setFont(FontManager.getRunescapeSmallFont());
        title.setBorder(new EmptyBorder(0, 0, 4, 0));
        box.add(title, BorderLayout.NORTH);

        return box;
    }

    private void addItem(JPanel panel, String text)
    {
        JLabel label = new JLabel("- " + text);
        label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        label.setFont(FontManager.getRunescapeSmallFont());
        panel.add(label);
    }

    private JPanel createSpacer()
    {
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(0, 8));
        return spacer;
    }

    public void updateTickets(int count)
    {
        ticketsLabel.setText("Dispenser Tickets: " + count);
    }

    public void updateLootValue(long value)
    {
        lootLabel.setText("Looting Bag: " + String.format("%,d gp", value));
    }

    public void updatePvPStats(String opponentName, int damageDealt, int damageTaken, int world)
    {
        if (fightBoxes.containsKey(opponentName))
        {
            pvpContainer.remove(fightBoxes.get(opponentName));
        }

        JPanel fightBox = new JPanel(new BorderLayout());
        fightBox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        fightBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorScheme.DARK_GRAY_COLOR, 1),
                new EmptyBorder(5, 5, 5, 5)
        ));

        JLabel nameLabel = new JLabel(opponentName + " (W" + world + ")");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(FontManager.getRunescapeBoldFont());
        fightBox.add(nameLabel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 2));
        statsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JLabel dealtLabel = new JLabel("Dealt: " + damageDealt);
        dealtLabel.setForeground(Color.GREEN);

        JLabel takenLabel = new JLabel("Taken: " + damageTaken);
        takenLabel.setForeground(Color.RED);

        statsPanel.add(dealtLabel);
        statsPanel.add(takenLabel);

        fightBox.add(statsPanel, BorderLayout.SOUTH);

        fightBoxes.put(opponentName, fightBox);
        pvpContainer.add(fightBox, 0);

        pvpContainer.revalidate();
        pvpContainer.repaint();
    }

    public void resetPvP()
    {
        pvpContainer.removeAll();
        fightBoxes.clear();
        pvpContainer.revalidate();
        pvpContainer.repaint();
    }
}