package io.github.toomanybugs.DuelArena;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class DuelListener implements Listener {
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
        if (e.getEntity() instanceof Player){
        	// if someone dies during a duel, we'll check who it is; if its a challenger, end the duel
        	if (DuelManager.isDueling) {
        		Player player = e.getEntity();
        		
        		if (player == DuelManager.challenger1 || player == DuelManager.challenger2) 
        			DuelManager.FinishDuel(player);
        	}
        }
    }
}
