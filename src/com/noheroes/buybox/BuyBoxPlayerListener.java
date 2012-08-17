package com.noheroes.buybox;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class BuyBoxPlayerListener implements Listener {
	public static BuyBox bbx;
    
    public BuyBoxPlayerListener(BuyBox bbx) {
        this.bbx = bbx;
    }
       
	
    @EventHandler (ignoreCancelled=true, priority = EventPriority.NORMAL)
    public void onPlayerClick(PlayerInteractEvent event) { // Left clicked block
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
        	// Insert air/block check, location check
        	Player player = event.getPlayer();
        	PlayerInventory inventory = player.getInventory();
        	Material mat = Material.COBBLESTONE;
        	ItemStack itemstack = new ItemStack(mat, 1);
        	Material block = event.getClickedBlock().getType();
        	if (block == Material.DISPENSER) {
        		player.sendMessage("dispenser");
        		if (inventory.contains(mat)) {
        			player.sendMessage("item detected");
        			EconomyResponse er = bbx.econ.withdrawPlayer(player.getName(), -bbx.getConfig().getInt("PricePerItem"));
        	        /*return er.transactionSuccess();*/
        			inventory.removeItem(itemstack); 
        			player.sendMessage("You sold 1 " + mat + " to Atlantis for " + ChatColor.GREEN + "$" + bbx.getConfig().getInt("PricePerItem"));
        	    } else {
        	    	player.sendMessage(ChatColor.RED + "You do not have the requested material (" + mat + ")");
        	    }
        		
                
            }
        	// 	Testing Methods
        	if (block == Material.FURNACE) {
                player.sendMessage(ChatColor.GREEN + "Bummer, You burned your money. -$5");
                EconomyResponse er = bbx.econ.withdrawPlayer(player.getName(), 5);
            }
        	if (block == Material.CHEST) {
        		Double balance = bbx.econ.getBalance(player.getName());
                player.sendMessage(ChatColor.GREEN + "Current balance is " + balance);
            }
        	// END Testing Methods 
        }
        
    }
 
	
}
