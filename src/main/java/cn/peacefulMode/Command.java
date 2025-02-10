package cn.peacefulMode;

import cn.peacefulMode.Cache.PlayerCache;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class Command implements CommandExecutor, TabCompleter {
    private final List<String> subcommands;
    private final Main plugin;

    public Command(Main main) {
        this.plugin = main;
        subcommands = new ArrayList<>(List.of("reload", "toggle"));
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (args.length == 0 || !subcommands.contains(args[0].toLowerCase())) {
            return false;
        }

        if (!sender.hasPermission("peacefulmode." + args[0])) {
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                reloadConfig();
                sender.sendMessage("§a配置文件已重新加载!");
                break;
            case "toggle":
                handleToggleCommand(sender, args);
                break;
        }
        return true;
    }

    private void handleToggleCommand(CommandSender sender, String[] args) {
        int argCount = args.length - 1; // 减去子命令本身

        switch (argCount) {
            case 0:
                if(sender instanceof Player p) {
                    PlayerCache.toggle(p.getUniqueId());
                }
                break;
            case 1:
                handleSingleArg(sender, args[1]);
                break;
            default:
                handleMultiArgs(sender, args[1], args[2]);
        }
    }

    private void handleSingleArg(CommandSender sender, String param) {
        // 检查玩家是否存在
        Player target = plugin.getServer().getPlayer(param);

        if (target != null) {
            // 权限检查：操作其他玩家需要额外权限
            if (!sender.equals(target) && !sender.hasPermission("peacefulmode.toggle.other")) {
                sender.sendMessage("§c你没有权限修改其他玩家的状态");
                return;
            }
            PlayerCache.toggle(target.getUniqueId());
        } else {
            sender.sendMessage("§c玩家 " + param + " 不存在或不在线");
        }
    }

    private void handleMultiArgs(CommandSender sender, String playerName, String state) {
        // 状态验证
        if (!state.equalsIgnoreCase("on") && !state.equalsIgnoreCase("off")) {
            sender.sendMessage("§c无效状态，请输入 on 或 off");
            return;
        }

        // 获取目标玩家
        Player target = plugin.getServer().getPlayer(playerName);

        // 玩家验证
        if (target == null) {
            sender.sendMessage("§c玩家 " + playerName + " 不存在或不在线");
            return;
        }

        // 权限验证
        if (!sender.equals(target) && !sender.hasPermission("peacefulmode.toggle.other")) {
            sender.sendMessage("§c你没有权限修改其他玩家的状态");
            return;
        }

        // 实际业务逻辑（示例）
        boolean newState = state.equalsIgnoreCase("on");
        PlayerCache.toggle(target.getUniqueId(), newState);
        sender.sendMessage("§a已成功设置 " + target.getName() + " 的状态为 " + state);
    }

    private void reloadConfig() {
        plugin.reloadConfig();
        plugin.price = plugin.getConfig().getDouble("price", 500);
        plugin.fileSaveInterval = plugin.getConfig().getInt("file-save-interval", 3);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // 子命令补全（原有逻辑）
            subcommands.stream()
                    .filter(sc -> sender.hasPermission("peacefulmode." + sc))
                    .forEach(completions::add);
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("toggle")) {
            // Toggle 子命令的补全
            switch (args.length) {
                case 2:
                    // 玩家名补全（带权限过滤）
                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        if (sender.equals(p) || sender.hasPermission("peacefulmode.toggle.other")) {
                            completions.add(p.getName());
                        }
                    }
                    // 添加控制台专用的 [all] 参数
                    if (sender instanceof ConsoleCommandSender) {
                        completions.add("all");
                    }
                    break;
                case 3:
                    // 状态补全
                    completions.add("on");
                    completions.add("off");
                    break;
            }
        }

        // 使用bukkit的智能补全算法
        return StringUtil.copyPartialMatches(
                args[args.length - 1],
                completions,
                new ArrayList<>(completions.size())
        );
    }
}