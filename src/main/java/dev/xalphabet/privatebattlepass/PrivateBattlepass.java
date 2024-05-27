package dev.xalphabet.privatebattlepass;

import org.bukkit.Material;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class PrivateBattlepass extends JavaPlugin {
    private BattlepassSystem battlepassSystem;
    private BattlepassGUI battlepassGUI;
    private RewardTracker rewardTracker;
    private BattlepassCommands battlepassCommands;
    private BattlepassListener battlepassListener;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        battlepassSystem = new BattlepassSystem(this.getConfig().getString("baseDirectory"), this.getConfig().getInt("maxMenus"), getLogger());
        battlepassGUI = new BattlepassGUI(battlepassSystem);
        rewardTracker = new RewardTracker(battlepassSystem.getBaseDirectory());
        battlepassCommands = new BattlepassCommands(battlepassGUI, rewardTracker, battlepassSystem);
        battlepassListener = new BattlepassListener(rewardTracker, battlepassSystem);

        // Add an example reward to slot 1 on page 1
        addExampleReward();

        this.getCommand("battlepass").setExecutor(battlepassCommands);
        this.getCommand("addreward").setExecutor(battlepassCommands);

        getServer().getPluginManager().registerEvents(battlepassListener, this);

        // Check if PlaceholderAPI is installed
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            /*
             * We register the EventListener here, when PlaceholderAPI is installed.
             * Since all events are in the main class (this class), we simply use "this"
             */
            getServer().getPluginManager().registerEvents(battlepassListener, this);
        } else {
            /*
             * We inform about the fact that PlaceholderAPI isn't installed and then
             * disable this plugin to prevent issues.
             */
            getLogger().warning("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
    }

    private void addExampleReward() {
        // Example reward details
        int page = 1;
        String rewardName = "ExampleReward";
        Material material = Material.DIAMOND;
        int slot = 0;
        String displayName = "&6Test Reward: &lpage_1.yml";
        String lore = "&bThis is a test reward\n&bIt demonstrates the features of the plugin";
        String leftClickCommand = "say Left clicked the test reward!";
        String rightClickCommand = "say Right clicked the test reward!";

        // Create lists for commands
        ArrayList<String> leftClickCommands = new ArrayList<>();
        leftClickCommands.add(leftClickCommand);

        ArrayList<String> rightClickCommands = new ArrayList<>();
        rightClickCommands.add(rightClickCommand);

        // Add the example reward
        battlepassSystem.addReward(page, rewardName, material, slot, displayName, lore, leftClickCommands, rightClickCommands);
    }
}
