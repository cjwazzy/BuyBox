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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class BuyBoxPlayerListener implements Listener {
	private BuyBox bbx;
    
    public BuyBoxPlayerListener(BuyBox bbx) {
        this.bbx = bbx;
    }     
    
	
	
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
	        		BuyBox.log(Level.INFO, "Admin " + playername + " created BuyBox at " + bbx.getConfig().getInt("X") + ", " + bbx.getConfig().getInt("Y") + ", " + bbx.getConfig().getInt("Z"));
        		} else {
        			player.sendMessage(ChatColor.RED + "That is not a chest, please hit a chest this time :D");
        		}
        	}
        	if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock() != null) {
        		bbx.bbxEditMode.remove(player);
        		player.sendMessage(ChatColor.RED + "Selection mode canceled");
        		event.setCancelled(true);
        	}
        } else { //regular player interaction
        	// BuyBox info
        	if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) && event.getClickedBlock() != null) {
        		if (event.getClickedBlock().getLocation().getBlockX() == bbx.getConfig().getInt("X") && event.getClickedBlock().getLocation().getBlockY() == bbx.getConfig().getInt("Y") && event.getClickedBlock().getLocation().getBlockZ() == bbx.getConfig().getInt("Z")) {
        			Player player = event.getPlayer();
        			String playername = player.getName().toLowerCase();
        			event.setCancelled(true);
        			player.sendMessage("This is " + bbx.getConfig().getString("CityName") + "'s BuyBox.  To see current purchase orders, use any of the following: " + ChatColor.YELLOW + "/buybox /bbox /bbx");
                	player.sendMessage("Use '/bbx info' for more information");
                	player.sendMessage(ChatColor.BLUE + bbx.getConfig().getString("CityName") + " is currently buying " + ChatColor.WHITE + bbx.getConfig().getString("ItemsPerPlayer") + " " + bbx.getConfig().getString("ItemInNeed") + ChatColor.BLUE + " at " + ChatColor.WHITE + bbx.getConfig().getInt("PricePerItem") + " " + bbx.econ.currencyNamePlural() + ChatColor.BLUE + " each.");
                	if (bbx.Itemsleft.get(playername) == null) {
	            		player.sendMessage(ChatColor.BLUE + "You may sell " + ChatColor.WHITE + bbx.getConfig().getInt("ItemsPerPlayer") + ChatColor.BLUE + " more on this purchase order");
	            	}
	            	else {
	            		player.sendMessage(ChatColor.BLUE + "You may sell " + ChatColor.WHITE + bbx.Itemsleft.get(playername) + ChatColor.BLUE + " more on this purchase order");
	            	}
                	player.sendMessage(ChatColor.RED + "Right click to sell");
        		}
        	}
        	// sell to BuyBox
	    	if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock() != null) {
	        	if (event.getClickedBlock().getLocation().getBlockX() == bbx.getConfig().getInt("X") && event.getClickedBlock().getLocation().getBlockY() == bbx.getConfig().getInt("Y") && event.getClickedBlock().getLocation().getBlockZ() == bbx.getConfig().getInt("Z")) {
	        		Player player = event.getPlayer();
	        		String playername = player.getName().toLowerCase();
	        		PlayerInventory inventory = player.getInventory();
	        		Material mat = Material.getMaterial(bbx.getConfig().getString("ItemInNeed"));
	        		ItemStack itemstack = new ItemStack(Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")), 1);
	        		Material block = event.getClickedBlock().getType();
	        		Integer itemsleft = 0;
	        		if (block == Material.CHEST) {
	        			event.setCancelled(true);
	        			if(!bbx.Itemsleft.containsKey(playername)){
	        				// no player found, create player with max itemsleft
	        				bbx.Itemsleft.put(playername, bbx.getConfig().getInt("ItemsPerPlayer"));
	        				// write to file
	        				try{
	        					SLAPI.save(bbx.Itemsleft,"itemsleft.bin");
	        			    }catch(Exception e){
	        			        e.printStackTrace();
	        			    }
	        		    }
	        		    itemsleft = bbx.Itemsleft.get(playername);
	        			if (itemsleft > 0) {
	        				if (inventory.contains(Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")))) {
	        					EconomyResponse er = bbx.econ.withdrawPlayer(player.getName(), -bbx.getConfig().getInt("PricePerItem"));
	        					if (er.type == ResponseType.SUCCESS) {
		        					//deduct from city account if it exists, just for fun
		        					String city = bbx.getConfig().getString("CityName").toLowerCase();
		        					if (bbx.econ.getBalance(city) > bbx.getConfig().getInt("PricePerItem")) {
		        						bbx.econ.withdrawPlayer(city, bbx.getConfig().getInt("PricePerItem"));
		        					}
		        					
		        					inventory.removeItem(itemstack);
		        					itemsleft -= 1;
		        					bbx.Itemsleft.put(playername, itemsleft);
		        					// write to file
		            				try{
		            					SLAPI.save(bbx.Itemsleft,"itemsleft.bin");
		            			    }catch(Exception e){
		            			        e.printStackTrace();
		            			    }
		        					player.sendMessage("You sold 1 " + Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")) + " to Atlantis for " + ChatColor.GREEN + bbx.getConfig().getInt("PricePerItem") + " " + bbx.econ.currencyNamePlural() + ChatColor.WHITE + ". We need " + itemsleft + " more.");
		        					BuyBox.log(Level.INFO, playername + " sold 1 " + Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")) + " to Atlantis for " + bbx.getConfig().getInt("PricePerItem") + " " + bbx.econ.currencyNamePlural() + " and may sell " + itemsleft + " more.");
	        					}
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
	        		
	        		if (block == Material.DISPENSER) {
	        			Double balance = bbx.econ.getBalance(player.getName());
	        			player.sendMessage(ChatColor.GREEN + "Current balance is " + balance + " " + bbx.econ.currencyNamePlural());
	        		}
	        		// TODO Remove: END Testing Methods 
	        	}
	        }
	    }
    }
 
	
}
