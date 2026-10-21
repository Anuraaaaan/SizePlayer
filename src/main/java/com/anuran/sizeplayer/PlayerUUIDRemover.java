package com.anuran.sizeplayer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;

public class PlayerUUIDRemover implements Listener {

    private final Map<UUID, BukkitTask> changingPlayers;

    public PlayerUUIDRemover(Map<UUID, BukkitTask> changingPlayers) {
        this.changingPlayers = changingPlayers;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        BukkitTask task = changingPlayers.remove(uuid);

        if (task != null) {
            task.cancel();
        }
    }
}
