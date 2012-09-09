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
	private BuyBox bbx;
    
    public BuyBoxPlayerListener(BuyBox bbx) {
        this.bbx = bbx;
    }     
    
	
	
    @EventHandler (ignoreCancelled=true, priority = EventPriority.NORMAL)
    public void onPlayerClick(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) && event.getClickedBlock() != null) {
        	// if (location) {
        		Player player = event.getPlayer();
        		String playername = player.getName().toLowerCase();
        		PlayerInventory inventory = player.getInventory();
        		Material mat = Material.getMaterial(bbx.getConfig().getString("ItemInNeed"));
        		ItemStack itemstack = new ItemStack(Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")), 1);
        		Material block = event.getClickedBlock().getType();
        		Integer itemsleft = 0;
        		if (block == Material.DISPENSER) {      			
        			if(!bbx.Itemsleft.containsKey(playername)){ // no player found, create player with max itemsleft
        				bbx.Itemsleft.put(playername, bbx.getConfig().getInt("ItemsPerPlayer"));
        		    }
        		    itemsleft = bbx.Itemsleft.get(playername);
        		        // TODO write to minidb
        		    		
        			if (itemsleft > 0) {
        				if (inventory.contains(Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")))) {
        					EconomyResponse er = bbx.econ.withdrawPlayer(player.getName(), -bbx.getConfig().getInt("PricePerItem"));
        					/*return er.transactionSuccess();*/
        					inventory.removeItem(itemstack);
        					itemsleft -= 1;
        					bbx.Itemsleft.put(playername, itemsleft);
        					// TODO write to minidb
        					player.sendMessage("You sold 1 " + Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")) + " to Atlantis for " + ChatColor.GREEN + bbx.getConfig().getInt("PricePerItem") + " " + bbx.econ.currencyNamePlural() + ChatColor.WHITE + ". We need " + itemsleft + " more.");
        				} else {
        					player.sendMessage(ChatColor.RED + "You do not have the requested material (" + Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")) + ")");
        				}
        			} else {
        				player.sendMessage("Atlantis does not need any more materials from you at this time.  Thank you for you contributions.");
        			}
        			
        		}
        		// TODO Remove: Testing Methods
        		if (block == Material.FURNACE) {
        			Double balance = bbx.econ.getBalance(player.getName());
        			player.sendMessage(ChatColor.GREEN + "Bummer, You burned $5. Now you have " + balance + " " + bbx.econ.currencyNamePlural());
        			EconomyResponse er = bbx.econ.withdrawPlayer(player.getName(), 5);
        		}
        		if (block == Material.CHEST) {
        			Double balance = bbx.econ.getBalance(player.getName());
        			player.sendMessage(ChatColor.GREEN + "Current balance is " + balance + " " + bbx.econ.currencyNamePlural());
        		}
        		// TODO Remove: END Testing Methods 
        	//end if location }
        }
    }
 
	
}
