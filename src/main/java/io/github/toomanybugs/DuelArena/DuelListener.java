package io.github.toomanybugs.DuelArena;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class DuelListener implements Listener {
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
        if (e.getEntity() instanceof Player){
        	// if someone dies during a duel, we'll check who it is; if its a challenger, end the duel
        	if (DuelManager.isDueling) {
        		Player player = e.getEntity();
        		
        		if (player == DuelManager.challenger1 || player == DuelManager.challenger2) {
        			e.setKeepLevel(true);
        			DuelManager.AnnounceWinner(player);
        		}
        		
        	}
        }
    }
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (DuelManager.isHoldingBeforeDuel) {
			Player player = e.getPlayer();
			if (player == DuelManager.challenger1)
				player.teleport(DuelManager.pos1);
			else if (player == DuelManager.pos2)
				player.teleport(DuelManager.pos2);
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		if (DuelManager.isDueling) {
			Player player = e.getPlayer();
			if (player == DuelManager.challenger1 || player == DuelManager.challenger2) {
				if (player == DuelManager.challenger1) {
					e.setRespawnLocation(DuelManager.prevPos1);
					DuelManager.FinishDuel(player);	
				}
				else if (player == DuelManager.challenger2) {
					e.setRespawnLocation(DuelManager.prevPos2);
					DuelManager.FinishDuel(player);	
				}	
			}
		}
    }
}
