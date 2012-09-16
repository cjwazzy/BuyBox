package com.noheroes.buybox;

import java.util.logging.Level;

import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class BuyBoxPlayerListener implements Listener {
	private BuyBox bbx;
    
    public BuyBoxPlayerListener(BuyBox bbx) {
        this.bbx = bbx;
    }     
    
	
	
    @SuppressWarnings("deprecation") // player.updateinventory()...you can depreciate this when you replace it you stupid bukkit
	@EventHandler (ignoreCancelled=true, priority = EventPriority.NORMAL)
    public void onPlayerClick(PlayerInteractEvent event) {
        //admin edit mode
    	if (bbx.bbxEditMode.contains(event.getPlayer())) {
    		Player player = event.getPlayer();
    		String playername = player.getName().toLowerCase();
        	if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) && event.getClickedBlock() != null) {
        		if (event.getClickedBlock().getType() == Material.CHEST) {
	        		//get and add coords, remove player from edit
	        		bbx.getConfig().set("X", event.getClickedBlock().getLocation().getBlockX());
	        		bbx.getConfig().set("Y", event.getClickedBlock().getLocation().getBlockY());
	        		bbx.getConfig().set("Z", event.getClickedBlock().getLocation().getBlockZ());
	        		bbx.saveConfig();
	        		player.sendMessage(ChatColor.RED + "Buybox coordinates now set to chest at " + ChatColor.BLUE + bbx.getConfig().getInt("X") + ", " + bbx.getConfig().getInt("Y") + ", " + bbx.getConfig().getInt("Z"));
	        		player.sendMessage(ChatColor.RED + "Please set price, amount, and material");
	        		bbx.bbxEditMode.remove(player);
	        		bbx.log(Level.INFO, "Admin " + playername + " created BuyBox at " + bbx.getConfig().getInt("X") + ", " + bbx.getConfig().getInt("Y") + ", " + bbx.getConfig().getInt("Z"));
        		} else {
        			player.sendMessage(ChatColor.RED + "That is not a chest, please hit a chest this time :D");
        		}
        	}
        	if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock() != null) {
        		bbx.bbxEditMode.remove(player);
        		player.sendMessage(ChatColor.RED + "Selection mode canceled");
        		event.setCancelled(true);
        	}
        } else { 
        //regular player interaction
        	// BuyBox info on Lt click
        	if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) && event.getClickedBlock() != null) {
        		if (event.getClickedBlock().getLocation().getBlockX() == bbx.getConfig().getInt("X") && event.getClickedBlock().getLocation().getBlockY() == bbx.getConfig().getInt("Y") && event.getClickedBlock().getLocation().getBlockZ() == bbx.getConfig().getInt("Z")) {
        			Player player = event.getPlayer();
        			String playername = player.getName().toLowerCase();
        			event.setCancelled(true);
        			player.sendMessage("This is " + bbx.getConfig().getString("CityName") + "'s BuyBox.  To see current purchase orders, use any of the following: " + ChatColor.YELLOW + "/buybox /bbox /bbx");
                	player.sendMessage("Use '/bbx info' for more information");
                	player.sendMessage(ChatColor.BLUE + bbx.getConfig().getString("CityName") + " is currently buying " + ChatColor.WHITE + bbx.getConfig().getString("ItemsPerPlayer") + " " + bbx.getConfig().getString("ItemInNeed") + ChatColor.BLUE + " at " + ChatColor.WHITE + bbx.getConfig().getInt("PricePerItem") + " " + BuyBox.econ.currencyNamePlural() + ChatColor.BLUE + " each.");
                	if (bbx.itemsleftHash.get(playername) == null) {
	            		player.sendMessage(ChatColor.BLUE + "You may sell " + ChatColor.WHITE + bbx.getConfig().getInt("ItemsPerPlayer") + ChatColor.BLUE + " more on this purchase order");
	            	}
	            	else {
	            		player.sendMessage(ChatColor.BLUE + "You may sell " + ChatColor.WHITE + bbx.itemsleftHash.get(playername) + ChatColor.BLUE + " more on this purchase order");
	            	}
                	player.sendMessage(ChatColor.RED + "Right click to sell");
        		}
        	}
        	// sell to BuyBox on Rt click
	    	if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock() != null) {
	        	if (event.getClickedBlock().getLocation().getBlockX() == bbx.getConfig().getInt("X") && event.getClickedBlock().getLocation().getBlockY() == bbx.getConfig().getInt("Y") && event.getClickedBlock().getLocation().getBlockZ() == bbx.getConfig().getInt("Z")) {
	        		Player player = event.getPlayer();
	        		String playername = player.getName().toLowerCase();
	        		PlayerInventory inventory = player.getInventory();
	        		Integer itemsleft = 0;
	        		Material block = event.getClickedBlock().getType();
	        		if (block == Material.CHEST) {
	        			event.setCancelled(true);
	        			if(!bbx.itemsleftHash.containsKey(playername)){
	        				// no player found, create player with max itemsleft
	        				bbx.itemsleftHash.put(playername, bbx.getConfig().getInt("ItemsPerPlayer"));
	        				// write to mini
	        				bbx.getUtils().saveAll(bbx.itemsleftHash);
	        		    }
	        		    itemsleft = bbx.itemsleftHash.get(playername);
	        		    if (itemsleft > 0) {
		        		    if (inventory.contains(Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")))) {
		        		    	ItemStack neededstack = new ItemStack(Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")), itemsleft);
		        		    	Integer removeamt = 0; //
		        		    	Integer sellcount = 0; // count sold items for final player message
	        					// loop through each stack in inv, looking for sales items, sell if purchase order allows it
		        		    	for(ItemStack invstack : player.getInventory().getContents()){
	        						if (itemsleft > 0) {
		    	        	    	    if(invstack.getType() == neededstack.getType()){
		    	        	    	    	neededstack.setAmount (itemsleft);
		    	        	    	    	// set remove amount to smaller value (items left vs. amount in stack)
		    	        	    	    	if(invstack.getAmount() >= neededstack.getAmount()){
		    	        	    	    		removeamt = neededstack.getAmount();
		    	        	    	    	}
		    	        	    	    	
		    	        	    	    	if(invstack.getAmount() < neededstack.getAmount()){
		    	        	    	    	    removeamt = invstack.getAmount();
		    	        	    	    	}
		    	        	    	    	
		    	        	    	    	EconomyResponse er = BuyBox.econ.withdrawPlayer(player.getName(), -(removeamt * bbx.getConfig().getInt("PricePerItem")));
		    	        	    	    	if (er.type == ResponseType.SUCCESS) {
					        					//deduct from city account if it exists, just for fun
					        					String city = bbx.getConfig().getString("CityName").toLowerCase();
					        					if (BuyBox.econ.getBalance(city) > (removeamt * bbx.getConfig().getInt("PricePerItem"))) {
					        						BuyBox.econ.withdrawPlayer(city, (removeamt * bbx.getConfig().getInt("PricePerItem")));
					        					}
					        					ItemStack removestack = new ItemStack(Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")), removeamt);
					        					inventory.removeItem(removestack);
					        					sellcount += removeamt;
					        					itemsleft -= removeamt;
					        					bbx.itemsleftHash.put(playername, itemsleft);
					        					// write to mini
						        				bbx.getUtils().saveAll(bbx.itemsleftHash);
					        					bbx.log(Level.INFO, playername + " sold " + removeamt + " " + Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")) + " to Atlantis for " + (removeamt * bbx.getConfig().getInt("PricePerItem")) + " " + BuyBox.econ.currencyNamePlural() + " and may sell " + itemsleft + " more.");
				        					}
		    	        	    	    } else {
		    	        	    	    	continue; // skip stack if it's not correct material
		    	        	    	    }
	        						} else {
	        							break; // stop looping if purchase order is exhausted
	        						}
	    	        	    	}
		        		    	player.updateInventory();
	        					player.sendMessage("You sold " + sellcount + " " + Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")) + " to Atlantis for " + ChatColor.GREEN + (sellcount * bbx.getConfig().getInt("PricePerItem")) + " " + BuyBox.econ.currencyNamePlural() + ChatColor.WHITE + ". We need " + itemsleft + " more.");	
	        				} else {
	        					player.sendMessage(ChatColor.RED + "You do not have the requested material (" + Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")) + ")");
	        				}
	        			} else {
	        				player.sendMessage("Atlantis does not need any more materials from you at this time.  Thank you for you contributions.");
	        			}
	        		}
	        	}
	        }
	    }
    }
    
    // Exits edit mode if a player logs off
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
    	bbx.removePlayerFromEditMode(event.getPlayer());
    }
}
