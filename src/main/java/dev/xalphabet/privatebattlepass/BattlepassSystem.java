package dev.xalphabet.privatebattlepass;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BattlepassSystem {
    private final String baseDirectory;
    private final int maxMenus;
    private final Logger logger;
    private final Map<Integer, Map<String, Object>> pages;
    private final BattlepassGUI battlepassGUI;

    public BattlepassSystem(String baseDirectory, int maxMenus, Logger logger) {
        this.baseDirectory = baseDirectory;
        this.maxMenus = maxMenus;
        this.logger = logger;
        this.pages = new HashMap<>();
        this.battlepassGUI = new BattlepassGUI(this);
        loadPages();
    }

    private void loadPages() {
        for (int i = 1; i <= maxMenus; i++) {
            File pageFile = new File(baseDirectory + "/pages/page_" + i + ".yml");
            if (pageFile.exists()) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(pageFile);
                pages.put(i, config.getConfigurationSection("Rewards").getValues(false));
            }
        }
    }

    public void addReward(int page, String rewardName, Material material, int slot, String displayName, String lore,
                          List<String> leftClickCommands, List<String> rightClickCommands) {
        if (page < 1 || page > maxMenus) return;

        Map<String, Object> rewardData = new HashMap<>();
        rewardData.put("material", material.toString());
        rewardData.put("slot", slot);
        rewardData.put("itemDisplayName", displayName);
        rewardData.put("itemLore", lore);
        rewardData.put("leftClickCommands", leftClickCommands);
        rewardData.put("rightClickCommands", rightClickCommands);

        pages.computeIfAbsent(page, k -> new HashMap<>()).put(rewardName, rewardData);
        savePage(page);
    }

    public void reloadPages() {
        pages.clear();
        loadPages();
    }

    private void savePage(int page) {
        File pageFile = new File(baseDirectory + "/pages/page_" + page + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        config.createSection("Rewards", pages.get(page));
        try {
            config.save(pageFile);
        } catch (IOException e) {
            logger.severe("Failed to save page " + page + ": " + e.getMessage());
        }
    }

    public Map<Integer, Map<String, Object>> getPages() {
        return pages;
    }

    public BattlepassGUI getBattlepassGUI() {
        return battlepassGUI;
    }

    public int getMaxMenus() {
        return maxMenus;
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }
}
