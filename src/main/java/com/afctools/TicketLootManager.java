package com.afctools;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.Text;

@Singleton
public class TicketLootManager
{
    @Inject
    private Client client;

    @Inject
    private ItemManager itemManager;

    private AfcPluginPanel pluginPanel;

    private static final int LOOTING_BAG_CONTAINER_ID = 516;

    private long currentLootValue = 0;

    public void setPluginPanel(AfcPluginPanel pluginPanel)
    {
        this.pluginPanel = pluginPanel;
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (pluginPanel == null) return;

        String rawMsg = Text.removeTags(event.getMessage());

        // 1. DISPENSER LOOT PARSER (Reads the exact chat message from the image)
        if (rawMsg.startsWith("You have been awarded ") && rawMsg.contains("from the Agility dispenser"))
        {
            long addedValue = 0;

            // Strip away the extra fluff so we are left with just "4 x Blighted manta ray and 1 x Adamant platebody"
            String itemsStr = rawMsg.replace("You have been awarded ", "")
                    .replace(" from the Agility dispenser.", "")
                    .replace(" from the Agility dispenser", "");

            // Split by " and " or ", " so we can process each item individually
            String[] parts = itemsStr.split(", | and ");

            for (String part : parts)
            {
                String[] qtyAndItem = part.split(" x ", 2);
                if (qtyAndItem.length == 2)
                {
                    try
                    {
                        int qty = Integer.parseInt(qtyAndItem[0].replace(",", "").trim());
                        String itemName = qtyAndItem[1].trim();

                        // Search RuneLite's cache for the live GE price
                        java.util.List<net.runelite.http.api.item.ItemPrice> results = itemManager.search(itemName);
                        if (results != null && !results.isEmpty())
                        {
                            addedValue += (long) results.get(0).getPrice() * qty;
                        }
                    }
                    catch (Exception ignored)
                    {
                    }
                }
            }

            // Push the newly calculated math to the sidebar
            if (addedValue > 0)
            {
                currentLootValue += addedValue;
                final long finalVal = currentLootValue;
                SwingUtilities.invokeLater(() -> pluginPanel.updateLootValue(finalVal));
            }
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        if (pluginPanel == null) return;

        // 2. TICKET TRACKER (Scans physical inventory for tickets)
        if (event.getContainerId() == 93) // Inventory ID
        {
            int ticketCount = 0;
            for (Item item : event.getItemContainer().getItems())
            {
                if (item.getId() > 0)
                {
                    ItemComposition comp = itemManager.getItemComposition(item.getId());
                    if (comp != null && comp.getName() != null && comp.getName().toLowerCase().contains("ticket"))
                    {
                        ticketCount += item.getQuantity();
                    }
                }
            }
            final int finalCount = ticketCount;
            SwingUtilities.invokeLater(() -> pluginPanel.updateTickets(finalCount));
        }

        // 3. LOOT BAG HARD-SYNC (Resets math when you manually 'Check' the bag)
        if (event.getContainerId() == LOOTING_BAG_CONTAINER_ID)
        {
            long totalValue = 0;
            for (Item item : event.getItemContainer().getItems())
            {
                if (item.getId() > 0 && item.getQuantity() > 0)
                {
                    totalValue += (long) itemManager.getItemPrice(item.getId()) * item.getQuantity();
                }
            }
            currentLootValue = totalValue;
            final long finalVal = currentLootValue;
            SwingUtilities.invokeLater(() -> pluginPanel.updateLootValue(finalVal));
        }
    }
}