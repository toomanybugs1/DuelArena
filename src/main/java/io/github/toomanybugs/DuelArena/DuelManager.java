package io.github.toomanybugs.DuelArena;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DuelManager {
	
	public static DuelArena plugin;
	
	public static Player challenger1, challenger2;
	public static boolean isDueling;
	public static boolean isHoldingBeforeDuel;
	
	public static Location pos1, pos2;
	public static Location prevPos1, prevPos2;
	
	public static ItemStack[] items1, items2;
	public static ItemStack[] armor1, armor2;
	public static int xp1, xp2;
	
	//how long do challenge requests last? (in seconds)
	private static int challengeLifeTime = 10;
	
	public static void StartDuel() {
		isDueling = true;
		
		prevPos1 = challenger1.getLocation();
		prevPos2 = challenger2.getLocation();
		
		challenger1.teleport(pos1);
		challenger2.teleport(pos2);
		
		GetInventories();
		ClearInventory(challenger1);
		ClearInventory(challenger2);
		GiveItemsFromConfig();
		
		StartDuelCountDown();
	}
	
	public static void StartChallengeTimer() {
		Runnable runnable = new Runnable() {
		    @Override
		    public void run() {
		        try {
		        	Thread.sleep(challengeLifeTime * 1000);
		        	
		            if (!isDueling) {
	            		challenger1.sendMessage("Your challenge was not accepted.");
	            		challenger2.sendMessage("You did not accept the challenge.");
	            		
	            		challenger1 = challenger2 = null;
	            		
	            		isDueling = false;
	            	}		        
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
		    }
		};
		Thread t = new Thread(runnable);
		t.start();
	}
	
	public static void StartDuelCountDown() {
		isHoldingBeforeDuel = true;
		
		CountdownTimer timer = new CountdownTimer(plugin,
		        5,
		        () -> {
		        	challenger1.sendTitle(ChatColor.RED + "Get Ready", "", 5, 10, 5);
		        	challenger2.sendTitle(ChatColor.RED + "Get Ready", "", 5, 10, 5);
		        },
		        () -> {
		        	isHoldingBeforeDuel = false;
	        		
	        		challenger1.sendTitle(ChatColor.RED + "Fight!", "", 10, 50, 10);
	        		challenger2.sendTitle(ChatColor.RED + "Fight!", "", 10, 50, 10);
		        },
		        (t) -> {
		        	challenger1.sendTitle(ChatColor.RED + "" + t.getSecondsLeft(), "", 5, 10, 5);
		        	challenger2.sendTitle(ChatColor.RED + "" + t.getSecondsLeft(), "", 5, 10, 5);
		        }

		);

		timer.scheduleTimer();
	}
	
	public static void AnnounceWinner(Player loser) {
		if (loser == challenger1) {
			Bukkit.broadcastMessage(challenger2.getDisplayName() + " has defeated " + challenger1.getDisplayName() + " in a duel!");
			challenger2.teleport(prevPos2);
			RestoreInventory(challenger2);
		}
		else {
			Bukkit.broadcastMessage(challenger1.getDisplayName() + " has defeated " + challenger2.getDisplayName() + " in a duel!");
			challenger1.teleport(prevPos1);
			RestoreInventory(challenger1);
		}
	}
	
	public static void FinishDuel(Player loser) {
		if (loser == challenger1) {
			RestoreInventory(challenger1);
		}
		else {
			RestoreInventory(challenger2);
		}
		
		challenger1 = challenger2 = null;
		isDueling = false;
	}
	
	public static void SetDuelPosition(int posNum, Location loc) {
		if (posNum == 1) {
			pos1 = loc;
			plugin.getConfig().set("pos1.x", loc.getX());
			plugin.getConfig().set("pos1.y", loc.getY());
			plugin.getConfig().set("pos1.z", loc.getZ());
			plugin.getConfig().set("pos1.pitch", loc.getPitch());
			plugin.getConfig().set("pos1.yaw", loc.getYaw());
		}
		else if (posNum == 2) {
			pos2 = loc;
			plugin.getConfig().set("pos2.x", loc.getX());
			plugin.getConfig().set("pos2.y", loc.getY());
			plugin.getConfig().set("pos2.z", loc.getZ());
			plugin.getConfig().set("pos2.pitch", loc.getPitch());
			plugin.getConfig().set("pos2.yaw", loc.getYaw());
		}
		
		plugin.saveConfig();
	}
	
	public static void LoadPositionsFromConfig() {
		pos1 = new Location(
				Bukkit.getWorld("world"),
				plugin.getConfig().getDouble("pos1.x"),
				plugin.getConfig().getDouble("pos1.y"),
				plugin.getConfig().getDouble("pos1.z"),
				(float) plugin.getConfig().getDouble("pos1.yaw"),
				(float) plugin.getConfig().getDouble("pos1.pitch"));
		
		pos2 = new Location(
				Bukkit.getWorld("world"),
				plugin.getConfig().getDouble("pos2.x"),
				plugin.getConfig().getDouble("pos2.y"),
				plugin.getConfig().getDouble("pos2.z"),
				(float) plugin.getConfig().getDouble("pos2.yaw"),
				(float) plugin.getConfig().getDouble("pos2.pitch"));
	}
	
	private static void ClearInventory(Player challenger) {
		challenger.getInventory().clear();
		challenger.getInventory().setHelmet(null);
		challenger.getInventory().setChestplate(null);
		challenger.getInventory().setLeggings(null);
		challenger.getInventory().setBoots(null);
	}
	
	// we must specify because one will be after respawn
	private static void RestoreInventory(Player challenger) {
		if (challenger == challenger1) {
			ClearInventory(challenger1);
			challenger1.getInventory().setContents(items1);
			challenger1.getInventory().setArmorContents(armor1);
			challenger1.setTotalExperience(xp1);
		}
		
		else if (challenger == challenger2) {
			ClearInventory(challenger2);
			challenger2.getInventory().setContents(items2);
			challenger2.getInventory().setArmorContents(armor2);
			challenger2.setTotalExperience(xp2);
		}
	}
	
	private static void GetInventories() {
		items1 = challenger1.getInventory().getContents();
		armor1 = challenger1.getInventory().getArmorContents();
		xp1 = challenger1.getTotalExperience();
		
		items2 = challenger2.getInventory().getContents();
		armor2 = challenger2.getInventory().getArmorContents();
		xp2 = challenger2.getTotalExperience();
	}
	
	private static void GiveItemsFromConfig() {
		List<String> itemStrings = plugin.getConfig().getStringList("items");
		String helmetString = plugin.getConfig().getString("helmet");
		String chestPlateString = plugin.getConfig().getString("chestplate");
		String leggingsString = plugin.getConfig().getString("leggings");
		String bootsString = plugin.getConfig().getString("boots");
		
		ItemStack[] items = new ItemStack[itemStrings.size()];
		ItemStack[] armor = {
				new ItemStack(Material.matchMaterial(bootsString), 1),
				new ItemStack(Material.matchMaterial(leggingsString), 1),
				new ItemStack(Material.matchMaterial(chestPlateString), 1),
				new ItemStack(Material.matchMaterial(helmetString), 1)
		};
		
		for (int i = 0; i < items.length; i++) {
			String[] lineParts = itemStrings.get(i).split(" ");
			if (lineParts.length != 2)
				continue;
			
			Material m = Material.matchMaterial(lineParts[0]);
			items[i] = new ItemStack(m, Integer.parseInt(lineParts[1]));
		}
		
		challenger1.getInventory().setContents(items);
		challenger1.getInventory().setArmorContents(armor);
		
		challenger2.getInventory().setContents(items);
		challenger2.getInventory().setArmorContents(armor);
	}
}
