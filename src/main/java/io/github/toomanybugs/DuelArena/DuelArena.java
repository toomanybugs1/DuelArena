package io.github.toomanybugs.DuelArena;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class DuelArena extends JavaPlugin {
	
	@Override
    public void onEnable() {
        DuelManager.plugin = this;
    }
    
    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        if (e.getEntity() instanceof Player){
        	// if someone dies during a duel, we'll check who it is; if its a challenger, end the duel
        	if (DuelManager.isDueling) {
        		Player player = (Player) e;
        		
        		if (player == DuelManager.challenger1 || player == DuelManager.challenger2) 
        			DuelManager.FinishDuel(player);
        	}
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (cmd.getName().equalsIgnoreCase("challenge")) {
    		if (!(sender instanceof Player)) {
    			sender.sendMessage("This command can only be run by a player.");
    		}
    		if (DuelManager.challenger1 != null) {
    			sender.sendMessage("A duel is ongoing or pending. You must wait until it is finished to challenge someone.");
    			return false;
    		}
    		if (args.length != 2) {
    			sender.sendMessage("Usage: /challenge [player]");
    			return false;
    		}    		
    		Player challengedPlayer = Bukkit.getPlayer(args[1]);
    		if (challengedPlayer == null) {
    			sender.sendMessage("Couldn't find player with name " + args[1]);
    			return false;
    		}
    		
    		DuelManager.challenger1.sendMessage("You have challenged " + args[1] + " to a duel!");
    		DuelManager.challenger2.sendMessage(DuelManager.challenger1.getDisplayName() + " has challenged you to a duel. Type /accept or /decline to respond!");
    		
    		DuelManager.challenger1 = (Player) sender;
    		DuelManager.challenger2 = challengedPlayer;
    		
    		DuelManager.StartChallengeTimer();
    		
    		return true;
    	}
    	
    	else if (cmd.getName().equalsIgnoreCase("accept")) {
    		if (!(sender instanceof Player)) {
    			sender.sendMessage("This command can only be run by a player.");
    		}
    		if (args.length != 1) {
    			sender.sendMessage("Usage: /accept");
    			return false;
    		}    		
    		
    		Player player = (Player) sender;
    		if (DuelManager.challenger2.getDisplayName() != player.getDisplayName()) {
    			player.sendMessage("You do not have a challenge to accept.");
    			return false;
    		}
    		
    		player.sendMessage("You have accepted the challenge!");
    		DuelManager.challenger1.sendMessage(player.getDisplayName() + " has accepted your challenge!");
    		
    		return true;
		}
	
    	else if (cmd.getName().equalsIgnoreCase("decline")) {
    		if (!(sender instanceof Player)) {
    			sender.sendMessage("This command can only be run by a player.");
    		}
    		if (args.length != 1) {
    			sender.sendMessage("Usage: /decline");
    			return false;
    		}    		
    		
    		Player player = (Player) sender;
    		if (DuelManager.challenger2.getDisplayName() != player.getDisplayName()) {
    			player.sendMessage("You do not have a challenge to decline.");
    			return false;
    		}
    		
    		player.sendMessage("You have declined the challenge!");
    		DuelManager.challenger1.sendMessage(player.getDisplayName() + " has declined your challenge.");
    		
    		DuelManager.challenger1 = null;
    		DuelManager.challenger2 = null;
    		
    		return true;
    	}
    	
    	return false; 
    }
}
