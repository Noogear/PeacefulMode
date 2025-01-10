package cn.noTarget;

import java.util.Set;
import java.util.UUID;

public class Cache {

    private final Set<UUID> targetPlayer;
    public static Cache instance;

    public Cache(Set<UUID> targetPlayer) {
        instance = this;
        this.targetPlayer = targetPlayer;
    }

    public static void addTarget(UUID uuid) {
        instance.targetPlayer.add(uuid);
    }

    public static void removeTarget(UUID uuid) {
        instance.targetPlayer.remove(uuid);
    }

    public static boolean getTarget(UUID uuid) {
        return instance.targetPlayer.contains(uuid);
    }

    public static Set<UUID> getTargetPlayer() {
        return instance.targetPlayer;
    }


}
