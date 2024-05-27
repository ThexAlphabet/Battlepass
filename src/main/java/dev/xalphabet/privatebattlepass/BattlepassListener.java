package dev.xalphabet.privatebattlepass;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.Map;
import java.util.List;

public class BattlepassListener implements Listener {
    private final RewardTracker rewardTracker;
    private final BattlepassSystem battlepassSystem;

    public BattlepassListener(RewardTracker rewardTracker, BattlepassSystem battlepassSystem) {
        this.rewardTracker = rewardTracker;
        this.battlepassSystem = battlepassSystem;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        // Check if the clicked inventory is a battle pass page
        if (event.getView().getTitle().startsWith("Battlepass Page ")) {
            event.setCancelled(true); // Prevent item moving

            // Retrieve page number from the inventory title
            int page = Integer.parseInt(event.getView().getTitle().split(" ")[2]);

            // Retrieve the clicked slot
            int slot = event.getRawSlot();

            // Handle control items
            switch (slot) {
                case 53: // Exit
                    player.closeInventory();
                    return;
                case 48: // Previous page
                    if (page > 1) {
                        player.openInventory(battlepassSystem.getBattlepassGUI().getInventory(player, page - 1));
                    }
                    return;
                case 50: // Next page
                    if (page < battlepassSystem.getMaxMenus()) {
                        player.openInventory(battlepassSystem.getBattlepassGUI().getInventory(player, page + 1));
                    }
                    return;
                case 49: // Unnamed barrier
                    // No action
                    return;
                default:
                    break;
            }

            // Retrieve rewards for the clicked page
            Map<String, Object> rewards = battlepassSystem.getPages().get(page);

            // Check if rewards exist for the clicked page
            if (rewards != null) {
                // Loop through rewards to find the clicked item
                for (Map.Entry<String, Object> entry : rewards.entrySet()) {
                    String rewardName = entry.getKey(); // Get the key of the map entry (reward name)
                    Map<String, Object> reward = (Map<String, Object>) entry.getValue(); // Get the value of the map entry (reward data)
                    int rewardSlot = (int) reward.get("slot");

                    // Check if the clicked slot matches the reward slot
                    if (slot == rewardSlot) {
                        // Retrieve left and right click commands
                        List<String> leftClickCommands = (List<String>) reward.get("leftClickCommands");
                        List<String> rightClickCommands = (List<String>) reward.get("rightClickCommands");

                        // Execute left click commands
                        for (String command : leftClickCommands) {
                            player.performCommand(command.replace("%player%", player.getName()));
                        }

                        // Execute right click commands if right clicked
                        if (event.isRightClick()) {
                            for (String command : rightClickCommands) {
                                player.performCommand(command.replace("%player%", player.getName()));
                            }
                        }

                        // Mark reward as claimed
                        rewardTracker.markRewardClaimed(player.getName(), rewardName);
                        return; // Exit loop after finding the clicked reward
                    }
                }
            }
        }
    }
}
