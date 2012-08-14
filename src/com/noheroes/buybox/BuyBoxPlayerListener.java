package com.noheroes.buybox;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class BuyBoxPlayerListener implements Listener {
	public static BuyBox bbx;
    
    public BuyBoxPlayerListener(BuyBox bbx) {
        this.bbx = bbx;
    }
	
    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerClick(PlayerInteractEvent event) {
        // Left clicked block
    	Player player = event.getPlayer();
    	PlayerInventory inventory = player.getInventory();
        ItemStack itemstack = new ItemStack(Material.COBBLESTONE, 64);
    	Material mat = event.getClickedBlock().getType();
		ChatColor RED = ChatColor.RED;
		ChatColor WHITE = ChatColor.WHITE;
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
        	player.sendMessage("lt click");
        	if (mat == Material.DISPENSER) {
        		player.sendMessage("dispenser");
        		if (inventory.contains(itemstack)) {
        			player.sendMessage("item detected");
        			EconomyResponse er = bbx.econ.withdrawPlayer(player.getName(), -1);
        	        /*return er.transactionSuccess();*/
        			inventory.remove(itemstack); 
        			player.sendMessage("You sold 64 cobble to Atlantis for " + ChatColor.GREEN + "$1");
        	    } else {
        	    	player.sendMessage(ChatColor.RED + "You do not have the requested materials");
        	    }
        		
                
            }
        	if (mat == Material.FURNACE) {
                player.sendMessage(ChatColor.GREEN + "Bummer, You burned your money. -$5");
                EconomyResponse er = bbx.econ.withdrawPlayer(player.getName(), 5);
            }
        	if (mat == Material.CHEST) {
        		Double balance = bbx.econ.getBalance(player.getName());
                player.sendMessage(ChatColor.GREEN + "Current balance is " + balance);
            }
        	
        }
        
    }
 
	
}
