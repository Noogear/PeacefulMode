package cn.noTarget;

import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.UUID;

import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY;
import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY;

public class Listener implements org.bukkit.event.Listener {

    @EventHandler(ignoreCancelled = true)
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player player) {
            UUID uuid = player.getUniqueId();
            if (Cache.getTarget(uuid)) return;
            EntityTargetEvent.TargetReason reason = event.getReason();
            if (reason != TARGET_ATTACKED_ENTITY && reason != TARGET_ATTACKED_NEARBY_ENTITY) {
                event.setCancelled(true);
            } else {
                Cache.addTarget(uuid);
            }
        }
    }

}
