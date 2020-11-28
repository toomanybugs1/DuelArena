package io.github.toomanybugs.DuelArena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DuelManager {
	
	public static DuelArena plugin;
	
	public static Player challenger1, challenger2;
	public static boolean isDueling;
	public static boolean isHoldingBeforeDuel;
	
	public static Location pos1, pos2;
	public static Location prevPos1, prevPos2;
	
	//how long do challenge requests last? (in seconds)
	private static int challengeLifeTime = 10;
	
	public static void StartDuel() {
		isDueling = true;
		
		challenger1.teleport(pos1);
		challenger2.teleport(pos2);
		
		StartDuelCountDown();
	}
	
	public static void StartChallengeTimer() {
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
            	if (!isDueling) {
            		challenger1.sendMessage("Your challenge was not accepted.");
            		challenger2.sendMessage("You did not accept the challenge.");
            		
            		challenger1 = challenger2 = null;
            	}
            }
        }, 0, 20 * challengeLifeTime);
	}
	
	public static void StartDuelCountDown() {
		isHoldingBeforeDuel = true;
		// TODO: count down 5 seconds while holding the combatants in place
		isHoldingBeforeDuel = false;
		
		challenger1.sendTitle("Fight!", "", 10, 50, 10);
		challenger2.sendTitle("Fight!", "", 10, 50, 10);
	}
	
	public static void FinishDuel(Player loser) {
		if (loser == challenger1)
			Bukkit.broadcastMessage(challenger2.getDisplayName() + " has defeated " + challenger1.getDisplayName() + " in a duel!");
		else 
			Bukkit.broadcastMessage(challenger1.getDisplayName() + " has defeated " + challenger2.getDisplayName() + " in a duel!");
		
		challenger1 = challenger2 = null;
		isDueling = false;
	}
}
