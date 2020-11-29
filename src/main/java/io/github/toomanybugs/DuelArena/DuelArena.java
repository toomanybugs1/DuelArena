package io.github.toomanybugs.DuelArena;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public final class DuelArena extends JavaPlugin {
	
	DuelListener listener;
	
	@Override
    public void onEnable() {
		listener = new DuelListener();
		getServer().getPluginManager().registerEvents(listener, this);
		
        DuelManager.plugin = this;
        this.saveDefaultConfig();
        DuelManager.LoadPositionsFromConfig();
    }
    
    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (cmd.getName().equalsIgnoreCase("challenge")) {
    		if (!(sender instanceof Player)) {
    			sender.sendMessage(ChatColor.DARK_GRAY + "This command can only be run by a player.");
    			return true;
    		}
    		if (DuelManager.challenger1 != null) {
    			sender.sendMessage(ChatColor.DARK_GRAY + "A duel is ongoing or pending. You must wait until it is finished to challenge someone.");
    			return true;
    		}
    		if (args.length != 1) {
    			sender.sendMessage(ChatColor.DARK_GRAY + "Usage: /challenge [player]");
    			return true;
    		}    		
    		Player challengedPlayer = Bukkit.getPlayer(args[0]);
    		if (challengedPlayer == null) {
    			sender.sendMessage(ChatColor.DARK_GRAY + "Couldn't find player with name " + args[0]);
    			return true;
    		}
    		
    		if (challengedPlayer == (Player) sender) {
    			sender.sendMessage(ChatColor.DARK_GRAY + "You cannot challenge yourself.");
    			return true;
    		}
    		
    		DuelManager.challenger1.sendMessage(ChatColor.GOLD + "You have challenged " + args[0] + " to a duel!");
    		DuelManager.challenger2.sendMessage(ChatColor.GOLD + DuelManager.challenger1.getDisplayName() + " has challenged you to a duel. Type /accept or /decline to respond!");
    		
    		DuelManager.challenger1 = (Player) sender;
    		DuelManager.challenger2 = challengedPlayer;
    		
    		DuelManager.StartChallengeTimer();
    		
    		return true;
    	}
    	
    	else if (cmd.getName().equalsIgnoreCase("accept")) {
    		if (!(sender instanceof Player)) {
    			sender.sendMessage(ChatColor.DARK_GRAY + "This command can only be run by a player.");
    		}
    		if (args.length != 0) {
    			sender.sendMessage(ChatColor.DARK_GRAY + "Usage: /accept");
    			return true;
    		}    		
    		
    		Player player = (Player) sender;
    		if (DuelManager.challenger2 == null || DuelManager.challenger2.getDisplayName() != player.getDisplayName()) {
    			player.sendMessage(ChatColor.DARK_GRAY + "You do not have a challenge to accept.");
    			return true;
    		}
    		
    		player.sendMessage(ChatColor.GREEN + "You have accepted the challenge!");
    		DuelManager.challenger1.sendMessage(ChatColor.GREEN + player.getDisplayName() + " has accepted your challenge!");
    		
    		return true;
		}
	
    	else if (cmd.getName().equalsIgnoreCase("decline")) {
    		if (!(sender instanceof Player)) {
    			sender.sendMessage(ChatColor.DARK_GRAY + "This command can only be run by a player.");
    		}
    		if (args.length != 0) {
    			sender.sendMessage(ChatColor.DARK_GRAY + "Usage: /decline");
    			return true;
    		}    		
    		
    		Player player = (Player) sender;
    		if (DuelManager.challenger2 == null || DuelManager.challenger2.getDisplayName() != player.getDisplayName()) {
    			player.sendMessage(ChatColor.DARK_GRAY + "You do not have a challenge to decline.");
    			return true;
    		}
    		
    		player.sendMessage(ChatColor.GOLD + "You have declined the challenge!");
    		DuelManager.challenger1.sendMessage(ChatColor.GOLD + player.getDisplayName() + " has declined your challenge.");
    		
    		DuelManager.challenger1 = null;
    		DuelManager.challenger2 = null;
    		
    		return true;
    	}
    	
    	else if (cmd.getName().equalsIgnoreCase("duelpos")) {
    		if (!(sender instanceof Player)) {
    			sender.sendMessage(ChatColor.DARK_GRAY + "This command can only be run by a player.");
    		}
    		if (args.length != 1) {
    			sender.sendMessage(ChatColor.DARK_GRAY + "Usage: /duelpos <1 or 2>");
    			return true;
    		}    		
    		
    		Player player = (Player) sender;
    		
    		if (args[0].equals("1")) {
    			DuelManager.SetDuelPosition(1, player.getLocation());
    			player.sendMessage(ChatColor.GREEN + "Set duel position for Challenger 1.");
    		}
    		else if (args[0].equals("2")) {
    			DuelManager.SetDuelPosition(2, player.getLocation());
    			player.sendMessage(ChatColor.GREEN + "Set duel position for Challenger 2.");
    		}
    		else {
    			sender.sendMessage("Second argument must be 1 or 2.");
    			return true;
    		}
    		
    		return true;
    	}
    	
    	return false; 
    }
}
