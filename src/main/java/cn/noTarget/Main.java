package cn.noTarget;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class Main extends JavaPlugin {
    public double price;
    public int fileSaveInterval;
    private File playerFile;
    private YamlConfiguration playerConfig;
    private Economy econ;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        price = this.getConfig().getDouble("price", 500);
        fileSaveInterval = this.getConfig().getInt("file-save-interval", 3);

        playerFile = new File(getDataFolder(), "player.yml");
        if (!playerFile.exists()) {
            saveResource("player.yml", false);
        }
        playerConfig = YamlConfiguration.loadConfiguration(playerFile);
        Set<UUID> uuidSet = playerConfig.getStringList("uuid").stream()
                .map(UUID::fromString)
                .collect(Collectors.toSet());

        new Cache(uuidSet);
        Bukkit.getPluginManager().registerEvents(new Listener(), this);
        getCommand("notarget").setExecutor(new Command(this));
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::savePlayerUuid, 1, fileSaveInterval * 60 * 20L);
        econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }

    public Economy getEconomy() {
        return econ;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        savePlayerUuid();
    }

    private void savePlayerUuid() {
        List<String> uuidStrings = Cache.getTargetPlayer().stream()
                .map(UUID::toString)
                .collect(Collectors.toList());
        playerConfig.set("uuid", uuidStrings);
        try {
            playerConfig.save(playerFile);
        } catch (IOException e) {
            getLogger().severe("无法保存 player.yml 文件: " + e.getMessage());
        }
    }

}
