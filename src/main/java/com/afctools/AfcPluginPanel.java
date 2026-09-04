package com.afctools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

public class AfcPluginPanel extends PluginPanel
{
    private final AfcToolsConfig config;
    private final JPanel pvpContainer;
    private final JPanel gearContainer;
    private final JPanel settingsContainer;
    private final JPanel statusBox;
    private final JPanel hiscoresBox;
    private final JPanel hiscoresContainer;
    private final Map<String, JPanel> fightBoxes = new HashMap<>();

    private JLabel ticketsLabel;
    private JLabel lootLabel;
    private JLabel fallsLabel;
    private JLabel autoRetaliateLabel;
    private JLabel playerAttackLabel;
    private JLabel npcAttackLabel;
    private JLabel skullPreventionLabel;

    private Runnable resetPvPCallback;
    private Runnable refreshHiscoresCallback;
    private Runnable forceSyncCallback;

    public AfcPluginPanel(AfcToolsConfig config)
    {
        super();
        this.config = config;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerBox = new JPanel(new BorderLayout());
        headerBox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        headerBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorScheme.DARK_GRAY_COLOR, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
        JLabel titleText = new JLabel("<html><div style='text-align: center;'><font color='#FFFFFF'><b>Wilderness Agility Tool</b></font><br><font color='#A0A0A0'>By: DawnKeedic</font></div></html>");
        titleText.setFont(FontManager.getRunescapeSmallFont());
        titleText.setHorizontalAlignment(JLabel.CENTER);
        headerBox.add(titleText, BorderLayout.CENTER);
        add(headerBox);
        add(createSpacer());

        JPanel gearBox = createBaseBox("Pre-Run Checklist");
        gearContainer = new JPanel(new GridLayout(0, 1, 0, 4));
        gearContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        gearBox.add(gearContainer, BorderLayout.CENTER);
        rebuildGearList();
        add(gearBox);
        add(createSpacer());

        JPanel liveStatusBox = createBaseBox("Safety Settings Check");
        JPanel liveStatusContainer = new JPanel(new GridLayout(0, 1, 0, 4));
        liveStatusContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        autoRetaliateLabel = new JLabel("- Auto-Retaliate: Unknown");
        autoRetaliateLabel.setFont(FontManager.getRunescapeSmallFont());
        liveStatusContainer.add(autoRetaliateLabel);

        playerAttackLabel = new JLabel("- Player Attack: Unknown");
        playerAttackLabel.setFont(FontManager.getRunescapeSmallFont());
        liveStatusContainer.add(playerAttackLabel);

        npcAttackLabel = new JLabel("- NPC Attack: Unknown");
        npcAttackLabel.setFont(FontManager.getRunescapeSmallFont());
        liveStatusContainer.add(npcAttackLabel);

        skullPreventionLabel = new JLabel("- Skull Prevention: Unknown");
        skullPreventionLabel.setFont(FontManager.getRunescapeSmallFont());
        liveStatusContainer.add(skullPreventionLabel);

        liveStatusBox.add(liveStatusContainer, BorderLayout.CENTER);
        add(liveStatusBox);
        add(createSpacer());

        JPanel settingsBox = createBaseBox("Custom Settings");
        settingsContainer = new JPanel(new GridLayout(0, 1, 0, 4));
        settingsContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        settingsBox.add(settingsContainer, BorderLayout.CENTER);
        rebuildSettingsList();
        add(settingsBox);
        add(createSpacer());

        statusBox = createBaseBox("Run Status");
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
        statusBox.setVisible(config.showPanelStats());

        hiscoresBox = createBaseBox("Global Lap Hiscores");
        hiscoresContainer = new JPanel();
        hiscoresContainer.setLayout(new BoxLayout(hiscoresContainer, BoxLayout.Y_AXIS));
        hiscoresContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JLabel loadingLabel = new JLabel("Loading hiscores...");
        loadingLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        loadingLabel.setFont(FontManager.getRunescapeSmallFont());
        loadingLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        hiscoresContainer.add(loadingLabel);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 4, 0));
        btnPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        btnPanel.setBorder(new EmptyBorder(4, 0, 0, 0));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(FontManager.getRunescapeSmallFont());
        refreshBtn.setFocusable(false);
        refreshBtn.addActionListener(e -> {
            hiscoresContainer.removeAll();
            hiscoresContainer.add(loadingLabel);
            hiscoresContainer.revalidate();
            hiscoresContainer.repaint();
            if (refreshHiscoresCallback != null) refreshHiscoresCallback.run();
        });

        JButton syncBtn = new JButton("Force Sync");
        syncBtn.setFont(FontManager.getRunescapeSmallFont());
        syncBtn.setFocusable(false);
        syncBtn.addActionListener(e -> {
            if (forceSyncCallback != null) forceSyncCallback.run();
        });

        btnPanel.add(refreshBtn);
        btnPanel.add(syncBtn);

        hiscoresBox.add(hiscoresContainer, BorderLayout.CENTER);
        hiscoresBox.add(btnPanel, BorderLayout.SOUTH);
        add(hiscoresBox);
        add(createSpacer());
        hiscoresBox.setVisible(config.showHiscores());

        JPanel bankingBox = new JPanel(new BorderLayout());
        bankingBox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        bankingBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ColorScheme.DARK_GRAY_COLOR, 1), new EmptyBorder(8, 8, 8, 8)));

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

        JLabel bankingText = new JLabel("<html>1. Hop to a random world<br>2. Hover teleport options<br>3. Avoid multi-combat zones<br>4. Travel to the bank safely</html>");
        bankingText.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        bankingText.setFont(FontManager.getRunescapeSmallFont());
        bankingContent.add(bankingText, BorderLayout.CENTER);
        bankingContent.setVisible(false);

        bankingBox.add(bankingContent, BorderLayout.CENTER);
        bankingHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean isVisible = bankingContent.isVisible();
                bankingContent.setVisible(!isVisible);
                bankingTitle.setText(isVisible ? "Safe Solo Banking Guide \u25BE" : "Safe Solo Banking Guide \u25B4");
            }
        });
        add(bankingBox);
        add(createSpacer());

        JPanel pvpBox = createBaseBox("PvP Encounters");
        pvpContainer = new JPanel();
        pvpContainer.setLayout(new BoxLayout(pvpContainer, BoxLayout.Y_AXIS));
        pvpContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JButton pvpResetBtn = new JButton("Reset PvP Log");
        pvpResetBtn.setFont(FontManager.getRunescapeSmallFont());
        pvpResetBtn.setFocusable(false);
        pvpResetBtn.addActionListener(e -> {
            if (resetPvPCallback != null) resetPvPCallback.run();
        });

        pvpBox.add(pvpContainer, BorderLayout.CENTER);
        pvpBox.add(pvpResetBtn, BorderLayout.SOUTH);
        add(pvpBox);
    }

    public void setResetPvPCallback(Runnable callback) { this.resetPvPCallback = callback; }
    public void setRefreshHiscoresCallback(Runnable callback) { this.refreshHiscoresCallback = callback; }
    public void setForceSyncCallback(Runnable callback) { this.forceSyncCallback = callback; }

    public void togglePanelStats(boolean visible) { statusBox.setVisible(visible); }
    public void toggleHiscores(boolean visible) { hiscoresBox.setVisible(visible); }

    public void rebuildGearList()
    {
        SwingUtilities.invokeLater(() -> {
            try {
                gearContainer.removeAll();
                String rawList = config.customGearList();
                if (rawList != null && !rawList.trim().isEmpty()) {
                    for (String item : rawList.split("[\n,]")) {
                        if (!item.trim().isEmpty()) addItem(gearContainer, item.trim());
                    }
                }
                gearContainer.revalidate();
                gearContainer.repaint();
            } catch (Exception ignored) {}
        });
    }

    public void rebuildSettingsList()
    {
        SwingUtilities.invokeLater(() -> {
            try {
                settingsContainer.removeAll();
                String rawList = config.customSettingsList();
                if (rawList != null && !rawList.trim().isEmpty()) {
                    for (String item : rawList.split("[\n,]")) {
                        if (!item.trim().isEmpty()) addItem(settingsContainer, item.trim());
                    }
                }
                settingsContainer.revalidate();
                settingsContainer.repaint();
            } catch (Exception ignored) {}
        });
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
        SwingUtilities.invokeLater(() -> {
            try {
                if (config.prefAutoRetaliate() == AfcToolsConfig.RetaliateOption.IGNORE) {
                    autoRetaliateLabel.setText("- Auto-Retaliate: Ignored");
                    autoRetaliateLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
                } else {
                    boolean isOff = (autoRetalState == 1);
                    boolean wantsOff = (config.prefAutoRetaliate() == AfcToolsConfig.RetaliateOption.OFF);
                    autoRetaliateLabel.setText("- Auto-Retaliate: " + (isOff ? "OFF" : "ON"));
                    autoRetaliateLabel.setForeground((isOff == wantsOff) ? Color.GREEN : Color.RED);
                }

                processAttackOption(playerAttackLabel, "- Player Attack: ", playerAttackState, config.prefPlayerAttack());
                processAttackOption(npcAttackLabel, "- NPC Attack: ", npcAttackState, config.prefNpcAttack());

                if (config.prefSkullPrevention() == AfcToolsConfig.SkullPreventionOption.IGNORE) {
                    skullPreventionLabel.setText("- Skull Prevention: Ignored");
                    skullPreventionLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
                } else {
                    boolean isOn = (skullPreventionState == 1);
                    boolean wantsOn = (config.prefSkullPrevention() == AfcToolsConfig.SkullPreventionOption.ON);
                    skullPreventionLabel.setText("- Skull Prevention: " + (isOn ? "ON" : "OFF"));
                    skullPreventionLabel.setForeground((isOn == wantsOn) ? Color.GREEN : Color.RED);
                }
            } catch (Exception ignored) {}
        });
    }

    private void processAttackOption(JLabel label, String prefix, int state, AfcToolsConfig.AttackOption pref)
    {
        if (pref == AfcToolsConfig.AttackOption.IGNORE) {
            label.setText(prefix + "Ignored");
            label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
            return;
        }
        String currentStr = (state == 1) ? "Right-Click" : (state == 3) ? "Hidden" : "Left-Click";
        label.setText(prefix + currentStr);

        boolean matches = false;
        if (state == 1 && pref == AfcToolsConfig.AttackOption.RIGHT_CLICK) matches = true;
        if (state == 3 && pref == AfcToolsConfig.AttackOption.HIDDEN) matches = true;
        if ((state != 1 && state != 3) && pref == AfcToolsConfig.AttackOption.LEFT_CLICK) matches = true;

        label.setForeground(matches ? Color.GREEN : Color.RED);
    }

    public void updateTickets(int count)
    {
        SwingUtilities.invokeLater(() -> {
            if (count > 0) {
                ticketsLabel.setText("Dispenser Tickets: " + count + " (" + String.format("%,d XP", calculateTicketXp(count)) + ")");
                ticketsLabel.setForeground(count >= 101 ? Color.GREEN : Color.WHITE);
            } else {
                ticketsLabel.setText("Dispenser Tickets: 0");
                ticketsLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
            }
        });
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
        SwingUtilities.invokeLater(() -> fallsLabel.setText("Session Falls: " + count));
    }

    public void updateLootValue(long value)
    {
        SwingUtilities.invokeLater(() -> {
            if (config.streamerModeLoot()) {
                lootLabel.setText("Looting Bag: Hidden");
                lootLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
                return;
            }
            if (value >= 5000000) {
                lootLabel.setText("Looting Bag: GO BANK");
                lootLabel.setForeground(Color.RED);
            } else {
                lootLabel.setText("Looting Bag: " + String.format("%,d gp", value));
                if (value < 150000) lootLabel.setForeground(Color.RED);
                else if (value >= 1500000) lootLabel.setForeground(Color.ORANGE);
                else lootLabel.setForeground(Color.WHITE);
            }
        });
    }

    public void updateHiscores(List<HiscoresManager.HiscoreEntry> hiscores)
    {
        SwingUtilities.invokeLater(() -> {
            hiscoresContainer.removeAll();

            if (hiscores == null || hiscores.isEmpty())
            {
                JLabel errorLabel = new JLabel("No data available yet.");
                errorLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
                errorLabel.setFont(FontManager.getRunescapeSmallFont());
                errorLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
                hiscoresContainer.add(errorLabel);
            }
            else
            {
                int rank = 1;
                for (HiscoresManager.HiscoreEntry entry : hiscores)
                {
                    JPanel row = new JPanel(new BorderLayout());
                    row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                    row.setBorder(new EmptyBorder(3, 3, 3, 3));

                    JLabel nameLabel = new JLabel(rank + ". " + entry.getUsername());
                    nameLabel.setFont(FontManager.getRunescapeSmallFont());
                    nameLabel.setForeground(rank <= 3 ? Color.ORANGE : Color.WHITE);

                    JLabel lapLabel = new JLabel(entry.getLaps() + " Laps");
                    lapLabel.setFont(FontManager.getRunescapeSmallFont());
                    lapLabel.setForeground(Color.GREEN);

                    row.add(nameLabel, BorderLayout.WEST);
                    row.add(lapLabel, BorderLayout.EAST);
                    hiscoresContainer.add(row);
                    rank++;
                }
            }

            hiscoresContainer.revalidate();
            hiscoresContainer.repaint();
        });
    }

    public void updatePvPStats(String opponentName, int damageDealt, int damageTaken, int world)
    {
        SwingUtilities.invokeLater(() -> {
            if (fightBoxes.containsKey(opponentName)) pvpContainer.remove(fightBoxes.get(opponentName));

            JPanel fightBox = new JPanel(new BorderLayout());
            fightBox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            fightBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ColorScheme.DARK_GRAY_COLOR, 1), new EmptyBorder(5, 5, 5, 5)));

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
        });
    }

    public void resetPvP()
    {
        SwingUtilities.invokeLater(() -> {
            pvpContainer.removeAll();
            fightBoxes.clear();
            pvpContainer.revalidate();
            pvpContainer.repaint();
        });
    }
}