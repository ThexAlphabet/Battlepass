package dev.xalphabet.privatebattlepass;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BattlepassGUI {
    private final BattlepassSystem battlepassSystem;

    public BattlepassGUI(BattlepassSystem battlepassSystem) {
        this.battlepassSystem = battlepassSystem;
    }

    public Inventory getInventory(Player player, int page) {
        Map<Integer, Map<String, Object>> pages = battlepassSystem.getPages();
        Map<String, Object> rewards = pages.get(page);

        Inventory inventory = Bukkit.createInventory(null, 54, "Battlepass Page " + page);

        if (rewards != null) {
            for (Map.Entry<String, Object> entry : rewards.entrySet()) {
                String rewardName = entry.getKey();
                Map<String, Object> reward = (Map<String, Object>) entry.getValue();
                Material material = Material.valueOf((String) reward.get("material"));
                int slot = (int) reward.get("slot");
                String displayName = ChatColor.translateAlternateColorCodes('&', (String) reward.get("itemDisplayName"));

                // Handling lore
                Object loreObj = reward.get("itemLore");
                List<String> lore;
                if (loreObj instanceof List) {
                    // If it's already a list, just cast it
                    lore = (List<String>) loreObj;
                } else if (loreObj instanceof String) {
                    // Convert '\n' characters to actual newlines
                    String loreString = ((String) loreObj).replace("\\n", "\n");
                    // Translate '&' color codes
                    loreString = ChatColor.translateAlternateColorCodes('&', loreString);
                    // Split the string by newline characters to create a list
                    lore = Arrays.asList(loreString.split("\n"));
                } else {
                    // Handle other cases, like MemorySection
                    lore = Collections.emptyList(); // Or any other appropriate default
                }




                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(displayName);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }

                inventory.setItem(slot, item);
            }
        }

        // Add navigation, control, and decorative items
        addControlItems(inventory, page);

        return inventory;
    }

    private void addControlItems(Inventory inventory, int page) {
        // Exit barrier
        ItemStack exitBarrier = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exitBarrier.getItemMeta();
        if (exitMeta != null) {
            exitMeta.setDisplayName(ChatColor.RED + "Exit");
            exitBarrier.setItemMeta(exitMeta);
        }
        inventory.setItem(53, exitBarrier);

        // Unnamed barrier
        ItemStack unnamedBarrier = new ItemStack(Material.BARRIER);
        ItemMeta unnamedBarrierMeta = unnamedBarrier.getItemMeta();
        if (unnamedBarrierMeta != null) {
            unnamedBarrierMeta.setDisplayName(""); // Set the display name to an empty string
            unnamedBarrier.setItemMeta(unnamedBarrierMeta);
        }
        inventory.setItem(49, unnamedBarrier);

        // Previous page paper
        ItemStack previousPage = new ItemStack(Material.PAPER);
        ItemMeta previousMeta = previousPage.getItemMeta();
        if (previousMeta != null) {
            previousMeta.setDisplayName(ChatColor.YELLOW + "Previous Page");
            previousPage.setItemMeta(previousMeta);
        }
        inventory.setItem(48, previousPage);

        // Next page paper
        ItemStack nextPage = new ItemStack(Material.PAPER);
        ItemMeta nextMeta = nextPage.getItemMeta();
        if (nextMeta != null) {
            nextMeta.setDisplayName(ChatColor.YELLOW + "Next Page");
            nextPage.setItemMeta(nextMeta);
        }
        inventory.setItem(50, nextPage);

        // Gray stained glass pane
        ItemStack grayGlassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta grayMeta = grayGlassPane.getItemMeta();
        if (grayMeta != null) {
            grayMeta.setDisplayName(ChatColor.GRAY + "");
            grayGlassPane.setItemMeta(grayMeta);
        }

        // Set gray stained glass pane in slots 45, 46, 47, 48, 51, 52, 53
        int[] glassPaneSlots = {45, 46, 47, 51, 52, 36, 37, 38, 39, 40, 41, 42, 43, 44};
        for (int glassPaneSlot : glassPaneSlots) {
            inventory.setItem(glassPaneSlot, grayGlassPane);
        }
    }
}
