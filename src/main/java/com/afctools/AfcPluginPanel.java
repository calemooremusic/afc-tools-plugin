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
    private final JPanel settingsContainer;
    private final Map<String, JPanel> fightBoxes = new HashMap<>();

    private JLabel ticketsLabel;
    private JLabel lootLabel;
    private JLabel fallsLabel;

    private JLabel autoRetaliateLabel;
    private JLabel playerAttackLabel;
    private JLabel npcAttackLabel;
    private JLabel skullPreventionLabel;

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

        // --- REQUIRED SETTINGS CHECK SECTION ---
        JPanel liveStatusBox = createBaseBox("Required Settings Check");
        JPanel liveStatusContainer = new JPanel(new GridLayout(0, 1, 0, 4));
        liveStatusContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        autoRetaliateLabel = new JLabel("- Auto-Retaliate: Unknown");
        autoRetaliateLabel.setFont(FontManager.getRunescapeSmallFont());
        autoRetaliateLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        liveStatusContainer.add(autoRetaliateLabel);

        playerAttackLabel = new JLabel("- Player Attack: Unknown");
        playerAttackLabel.setFont(FontManager.getRunescapeSmallFont());
        playerAttackLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        liveStatusContainer.add(playerAttackLabel);

        npcAttackLabel = new JLabel("- NPC Attack: Unknown");
        npcAttackLabel.setFont(FontManager.getRunescapeSmallFont());
        npcAttackLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        liveStatusContainer.add(npcAttackLabel);

        skullPreventionLabel = new JLabel("- Skull Prevention: Unknown");
        skullPreventionLabel.setFont(FontManager.getRunescapeSmallFont());
        skullPreventionLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        liveStatusContainer.add(skullPreventionLabel);

        liveStatusBox.add(liveStatusContainer, BorderLayout.CENTER);
        add(liveStatusBox);
        add(createSpacer());

        // --- CUSTOM SETTINGS SECTION ---
        JPanel settingsBox = createBaseBox("Custom Settings");
        settingsContainer = new JPanel(new GridLayout(0, 1, 0, 4));
        settingsContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        settingsBox.add(settingsContainer, BorderLayout.CENTER);
        rebuildSettingsList();
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
        lootLabel.setForeground(Color.RED);
        lootLabel.setFont(FontManager.getRunescapeSmallFont());
        statusItems.add(lootLabel);

        fallsLabel = new JLabel("Session Falls: 0");
        fallsLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        fallsLabel.setFont(FontManager.getRunescapeSmallFont());
        statusItems.add(fallsLabel);

        statusBox.add(statusItems, BorderLayout.CENTER);
        add(statusBox);
        add(createSpacer());

        // --- SAFE BANKING GUIDE (DROPDOWN) ---
        JPanel bankingBox = new JPanel(new BorderLayout());
        bankingBox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        bankingBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorScheme.DARK_GRAY_COLOR, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JPanel bankingHeader = new JPanel(new BorderLayout());
        bankingHeader.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        bankingHeader.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel bankingTitle = new JLabel("Safe Solo Banking Guide \u25BE");
        bankingTitle.setForeground(Color.WHITE);
        bankingTitle.setFont(FontManager.getRunescapeSmallFont());
        bankingHeader.add(bankingTitle, BorderLayout.CENTER);
        bankingBox.add(bankingHeader, BorderLayout.NORTH);

        JPanel bankingContent = new JPanel(new BorderLayout());
        bankingContent.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        bankingContent.setBorder(new EmptyBorder(8, 0, 0, 0));

        JLabel bankingText = new JLabel("<html>" +
                "1. Hop to a random world<br>" +
                "2. Leave the Friends Chat (FC)<br>" +
                "3. Hop worlds again<br>" +
                "4. Travel to the bank safely" +
                "</html>");
        bankingText.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        bankingText.setFont(FontManager.getRunescapeSmallFont());
        bankingContent.add(bankingText, BorderLayout.CENTER);
        bankingContent.setVisible(false);

        bankingBox.add(bankingContent, BorderLayout.CENTER);

        bankingHeader.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                boolean isVisible = bankingContent.isVisible();
                bankingContent.setVisible(!isVisible);
                bankingTitle.setText(isVisible ? "Safe Solo Banking Guide \u25BE" : "Safe Solo Banking Guide \u25B4");
                bankingBox.revalidate();
                bankingBox.repaint();
            }
        });

        add(bankingBox);
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

    public void rebuildSettingsList()
    {
        settingsContainer.removeAll();
        String rawList = config.customSettingsList();
        if (rawList != null && !rawList.trim().isEmpty())
        {
            String[] items = rawList.split("[\n,]");
            for (String item : items)
            {
                String trimmed = item.trim();
                if (!trimmed.isEmpty())
                {
                    addItem(settingsContainer, trimmed);
                }
            }
        }
        settingsContainer.revalidate();
        settingsContainer.repaint();
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
        JLabel label = new JLabel("<html>- " + text + "</html>");
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

    public void updateLiveSettings(int autoRetalState, int playerAttackState, int npcAttackState, int skullPreventionState)
    {
        // Auto-Retaliate: 0 = ON, 1 = OFF
        if (autoRetalState == 1) {
            autoRetaliateLabel.setText("- Auto-Retaliate: OFF");
            autoRetaliateLabel.setForeground(Color.GREEN);
        } else {
            autoRetaliateLabel.setText("- Auto-Retaliate: ON");
            autoRetaliateLabel.setForeground(Color.RED);
        }

        // Player Attack: 1 = Right-Click (Required)
        if (playerAttackState == 1) {
            playerAttackLabel.setText("- Player Attack: Right-Click");
            playerAttackLabel.setForeground(Color.GREEN);
        } else if (playerAttackState == 3) {
            playerAttackLabel.setText("- Player Attack: Hidden");
            playerAttackLabel.setForeground(Color.RED);
        } else {
            playerAttackLabel.setText("- Player Attack: Left-Click");
            playerAttackLabel.setForeground(Color.RED);
        }

        // NPC Attack: 1 = Right-Click (Required)
        if (npcAttackState == 1) {
            npcAttackLabel.setText("- NPC Attack: Right-Click");
            npcAttackLabel.setForeground(Color.GREEN);
        } else if (npcAttackState == 3) {
            npcAttackLabel.setText("- NPC Attack: Hidden");
            npcAttackLabel.setForeground(Color.RED);
        } else {
            npcAttackLabel.setText("- NPC Attack: Left-Click");
            npcAttackLabel.setForeground(Color.RED);
        }

        // Skull Prevention: 0 = OFF (Required), 1 = ON
        if (skullPreventionState == 0) {
            skullPreventionLabel.setText("- Skull Prevention: OFF");
            skullPreventionLabel.setForeground(Color.GREEN);
        } else {
            skullPreventionLabel.setText("- Skull Prevention: ON");
            skullPreventionLabel.setForeground(Color.RED);
        }
    }

    public void updateTickets(int count)
    {
        if (count > 0)
        {
            int bankedXp = calculateTicketXp(count);
            ticketsLabel.setText("Dispenser Tickets: " + count + " (" + String.format("%,d XP", bankedXp) + ")");

            if (count >= 101)
            {
                ticketsLabel.setForeground(Color.GREEN);
            }
            else
            {
                ticketsLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
            }
        }
        else
        {
            ticketsLabel.setText("Dispenser Tickets: 0");
            ticketsLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        }
    }

    private int calculateTicketXp(int tickets)
    {
        if (tickets <= 0) return 0;
        if (tickets <= 10) return tickets * 200;
        if (tickets <= 50) return tickets * 210;
        if (tickets <= 100) return tickets * 220;
        return tickets * 230;
    }

    public void updateFallCount(int count)
    {
        fallsLabel.setText("Session Falls: " + count);
    }

    public void updateLootValue(long value)
    {
        if (config.streamerModeLoot())
        {
            lootLabel.setText("Looting Bag: Hidden");
            lootLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
            return;
        }

        // 5m+ override for GO BANK
        if (value >= 5000000)
        {
            lootLabel.setText("Looting Bag: GO BANK");
            lootLabel.setForeground(Color.RED);
        }
        else
        {
            lootLabel.setText("Looting Bag: " + String.format("%,d gp", value));

            if (value < 150000)
            {
                lootLabel.setForeground(Color.RED);
            }
            else if (value >= 1500000)
            {
                lootLabel.setForeground(Color.ORANGE);
            }
            else
            {
                lootLabel.setForeground(Color.WHITE);
            }
        }
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