package cn.peacefulMode;

import cn.peacefulMode.Cache.PlayerCache;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Command implements CommandExecutor, TabCompleter {
    private final List<String> subcommands;
    private final Main plugin;

    public Command(Main main) {
        this.plugin = main;
        subcommands = new ArrayList<>(List.of("reload", "price"));
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (args.length == 0 || !subcommands.contains(args[0].toLowerCase())) {
            return false;
        }

        if (!sender.hasPermission("notarget." + args[0])) {
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                reloadConfig();
                sender.sendMessage("§a配置文件已重新加载!");
                break;
            case "price":
                if (sender instanceof Player p) {
                    Economy economy = plugin.getEconomy();
                    double cost = plugin.price;
                    if (economy.getBalance(p) < cost) {
                        p.sendMessage(ChatColor.RED + "所需金币不足");
                        p.sendMessage(ChatColor.RED + "你需要: " + cost + " 金币才能开启和平模式!");
                    } else {
                        EconomyResponse economyResponse = economy.withdrawPlayer(p, cost);
                        if (economyResponse.transactionSuccess()) {
                            PlayerCache.removeTarget(p.getUniqueId());
                            if (cost != 0) {
                                p.sendMessage(ChatColor.GREEN + "您已消耗 " + cost + " 金币");
                                p.sendMessage(ChatColor.GREEN + "您开启了和平模式!");
                            }
                        } else {
                            p.sendMessage("未知错误：" + economyResponse.errorMessage);
                        }
                    }
                }
                break;
        }
        return true;
    }

    private void reloadConfig() {
        plugin.reloadConfig();
        plugin.price = plugin.getConfig().getDouble("price", 500);
        plugin.fileSaveInterval = plugin.getConfig().getInt("file-save-interval", 3);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String alias, String[] args) {
        List<String> ret = new ArrayList<>();
        if (args.length == 1) {
            for (String subcmd : subcommands) {
                if (!sender.hasPermission("notarget." + subcmd)) continue;
                ret.add(subcmd);
            }
            return StringUtil.copyPartialMatches(args[0].toLowerCase(), ret, new ArrayList<>());
        }

        return Collections.emptyList();
    }
}