package dev.xalphabet.privatebattlepass;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BattlepassCommands implements CommandExecutor {
    private final BattlepassGUI battlepassGUI;
    private final RewardTracker rewardTracker;
    private final BattlepassSystem battlepassSystem;

    public BattlepassCommands(BattlepassGUI battlepassGUI, RewardTracker rewardTracker, BattlepassSystem battlepassSystem) {
        this.battlepassGUI = battlepassGUI;
        this.rewardTracker = rewardTracker;
        this.battlepassSystem = battlepassSystem;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("battlepass")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                int page = 1; // Default to page 1 or parse from args
                if (args.length > 0) {
                    try {
                        page = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        player.sendMessage("Invalid page number. Showing page 1.");
                    }
                }
                player.openInventory(battlepassGUI.getInventory(player, page));
                return true;
            }
        } else if (label.equalsIgnoreCase("addreward")) {
            if (args.length < 8) {
                sender.sendMessage("Usage: /addreward <page> <rewardName> <material> <slot> <displayName> <lore> <leftClickCommand> <rightClickCommand>");
                return false;
            }

            try {
                int page = Integer.parseInt(args[0]);
                String rewardName = args[1];
                String materialString = args[2].toUpperCase();
                Material material;
                try {
                    material = Material.valueOf(materialString);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("Invalid material: " + materialString);
                    return false;
                }
                int slot = Integer.parseInt(args[3]);

                // Join the remaining args correctly handling quoted strings
                String[] displayNameAndLore = parseQuotedArgs(Arrays.copyOfRange(args, 4, args.length - 2));
                if (displayNameAndLore == null || displayNameAndLore.length < 2) {
                    sender.sendMessage("Error parsing displayName and lore. Ensure they are properly quoted.");
                    return false;
                }
                String displayName = displayNameAndLore[0];
                String lore = displayNameAndLore[1];

                String leftClickCommand = args[args.length - 2];
                String rightClickCommand = args[args.length - 1];

                List<String> leftClickCommands = new ArrayList<>();
                leftClickCommands.add(leftClickCommand);

                List<String> rightClickCommands = new ArrayList<>();
                rightClickCommands.add(rightClickCommand);

                battlepassSystem.addReward(page, rewardName, material, slot, displayName, lore,
                        leftClickCommands, rightClickCommands);
                sender.sendMessage("Reward '" + rewardName + "' added to page " + page + " successfully!");

                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number format in arguments.");
                return false;
            }
        }
        return false;
    }

    private String[] parseQuotedArgs(String[] args) {
        StringBuilder builder = new StringBuilder();
        boolean inQuotes = false;
        List<String> result = new ArrayList<>();
        for (String arg : args) {
            if (arg.startsWith("\"")) {
                inQuotes = true;
                builder.append(arg.substring(1)).append(" ");
            } else if (arg.endsWith("\"")) {
                inQuotes = false;
                builder.append(arg, 0, arg.length() - 1);
                result.add(builder.toString());
                builder.setLength(0); // Reset the builder
            } else if (inQuotes) {
                builder.append(arg).append(" ");
            } else {
                result.add(arg);
            }
        }
        return result.toArray(new String[0]);
    }
}
