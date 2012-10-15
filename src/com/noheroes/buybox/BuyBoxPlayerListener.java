package com.noheroes.buybox;

import java.util.logging.Level;

import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
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
    	if (event.getClickedBlock() == null) {
    		return;
		}
    	
        //admin edit mode
    	if (bbx.bbxEditMode.containsKey(event.getPlayer())) {
    		Player player = event.getPlayer();
    		String playername = player.getName().toLowerCase();
        	if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
        		if (event.getClickedBlock().getType() != Material.CHEST) {
        			player.sendMessage(ChatColor.RED + "That is not a chest, try to hit a chest this time :D");
        			return;
        		}
        		
    			String bbxname = bbx.bbxEditMode.get(player);
        		//get and add coords, remove player from edit
    			Location loc = event.getClickedBlock().getLocation();
                bbx.getConfig().set("Boxes." + bbxname + ".X", loc.getBlockX());
                bbx.getConfig().set("Boxes." + bbxname + ".Y", loc.getBlockY());
                bbx.getConfig().set("Boxes." + bbxname + ".Z", loc.getBlockZ());
                bbx.getConfig().set("Boxes." + bbxname + ".World", loc.getWorld().getName());
                bbx.saveConfig();
        		player.sendMessage(ChatColor.RED + "Buybox " + bbxname + " created on chest at " + ChatColor.BLUE + bbx.getConfig().getInt("Boxes." + bbxname + ".X") + ", " + bbx.getConfig().getInt("Boxes." + bbxname + ".Y") + ", " + bbx.getConfig().getInt("Boxes." + bbxname + ".Z") + " in " + bbx.getConfig().getInt("Boxes." + bbxname + ".World"));
        		player.sendMessage(ChatColor.RED + "Please set price, amount, and material");
        		bbx.bbxEditMode.remove(player);
        		bbx.log(Level.INFO, "Admin " + playername + " created BuyBox " + bbxname + " at " + bbx.getConfig().getInt("Boxes." + bbxname + ".X") + ", " + bbx.getConfig().getInt("Boxes." + bbxname + ".Y") + ", " + bbx.getConfig().getInt("Boxes." + bbxname + ".Z") + " in " + bbx.getConfig().getInt("Boxes." + bbxname + ".World"));
        		bbx.buyBoxLocs = bbx.getUtils().loadLocs();
        		return;
        	}
        	
        	if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
        		bbx.bbxEditMode.remove(player);
        		player.sendMessage(ChatColor.RED + "Selection mode canceled");
        		event.setCancelled(true);
        		return;
        	}
        } else { 
        //regular player interaction
        	Location loc = event.getClickedBlock().getLocation();
        	// BuyBox info on Lt click
        	if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
        		if (!bbx.getUtils().isBuyBox(loc)) {
        			return;
        		}
        		
    			Player player = event.getPlayer();
    			String playername = player.getName().toLowerCase();
    			event.setCancelled(true);
    			player.sendMessage("This is " + bbx.getConfig().getString("CityName") + "'s BuyBox.  To see current purchase orders, use any of the following: " + ChatColor.YELLOW + "/buybox /bbox /bbx");
            	player.sendMessage("Use '/bbx info' for more information");
            	player.sendMessage(ChatColor.BLUE + bbx.getConfig().getString("CityName") + " is currently buying " + ChatColor.WHITE + bbx.getConfig().getString("ItemsPerPlayer") + " " + bbx.getConfig().getString("ItemInNeed") + ChatColor.BLUE + " at " + ChatColor.WHITE + bbx.getConfig().getDouble("PricePerItem") + " " + BuyBox.econ.currencyNamePlural() + ChatColor.BLUE + " each.");
            	if (bbx.itemsleftHash.get(playername) == null) {
            		player.sendMessage(ChatColor.BLUE + "You may sell " + ChatColor.WHITE + bbx.getConfig().getInt("ItemsPerPlayer") + ChatColor.BLUE + " more on this purchase order");
            	} else {
            		player.sendMessage(ChatColor.BLUE + "You may sell " + ChatColor.WHITE + bbx.itemsleftHash.get(playername) + ChatColor.BLUE + " more on this purchase order");
            	}
            	
            	player.sendMessage(ChatColor.RED + "Right click to sell");
        	}
        	
        	// sell to BuyBox on Rt click
	    	if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
	    		if (!bbx.getUtils().isBuyBox(loc)) {
	    			return;
	    		}
	    		
        		Player player = event.getPlayer();
        		String playername = player.getName().toLowerCase();
        		PlayerInventory inventory = player.getInventory();
        		Integer itemsleft = 0;
        		Material block = event.getClickedBlock().getType();
        		if (block != Material.CHEST) {
        			return;
        		}
        		
    			event.setCancelled(true);
    			if(!bbx.itemsleftHash.containsKey(playername)){
    				// no player found, create player with max itemsleft
    				bbx.itemsleftHash.put(playername, bbx.getConfig().getInt("ItemsPerPlayer"));
					if (bbx.getConfig().getString("Debug") == "on") { //DEBUG
		    			bbx.log(Level.INFO, "DEBUG: " + playername + " not found in mini; creating player with " + bbx.getConfig().getInt("ItemsPerPlayer") + " items in hash."); //DEBUG
		    		} //DEBUG
					
    				// write to mini
    				bbx.getUtils().saveAll(bbx.itemsleftHash);
    		    }
    			
    		    itemsleft = bbx.itemsleftHash.get(playername);
    		    if (!(itemsleft > 0)) { // player has no itemsleft on Purchase Order
    		    	player.sendMessage("Atlantis does not need any more materials from you at this time.  Thank you for you contributions.");
    		    	return;
    		    }
    		    
    		    if (!inventory.contains(Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")))) {
    		    	player.sendMessage(ChatColor.RED + "You do not have the requested material (" + Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")) + ")");
    		    	return;
    		    }
    		    
		    	ItemStack neededstack = new ItemStack(Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")), itemsleft);
		    	Integer removeamt = 0; //
		    	Integer sellcount = 0; // count sold items for final player message
				// loop through each stack in inv, looking for sales items, sell if purchase order allows it
		    	if (bbx.getConfig().getString("Debug") == "on") { //DEBUG
	    			bbx.log(Level.INFO, "DEBUG:Correct item found, Searching each slot"); //DEBUG
	    		} //DEBUG
		    	
		    	for(ItemStack invstack : player.getInventory().getContents()){
		    		if (bbx.getConfig().getString("Debug") == "on") { //DEBUG
		    			bbx.log(Level.INFO, "DEBUG:Searching individual slot"); //DEBUG
		    		} //DEBUG
		    		
					if (!(itemsleft > 0)) {
						if (bbx.getConfig().getString("Debug") == "on") { //DEBUG
    		    			bbx.log(Level.INFO, "DEBUG:Purchase Order exhausted, stop loop"); //DEBUG
    		    		} //DEBUG
						break;
					}
					
					if (invstack == null) {
						continue;
					}
					
    	    	    if(invstack.getType() != neededstack.getType()){
    	    	    	continue;
    	    	    }
    	    	    
	    	    	if (bbx.getConfig().getString("Debug") == "on") { //DEBUG
		    			bbx.log(Level.INFO, "DEBUG:Correct item found in this slot, initiating sale"); //DEBUG
		    		} //DEBUG
	    	    	
	    	    	neededstack.setAmount (itemsleft);
	    	    	// set remove amount to smaller value (items left vs. amount in stack)
	    	    	if(invstack.getAmount() >= neededstack.getAmount()){
	    	    		removeamt = neededstack.getAmount();
	    	    		if (bbx.getConfig().getString("Debug") == "on") { //DEBUG
    		    			bbx.log(Level.INFO, "DEBUG:PO smaller than stack, exhausting PO"); //DEBUG
    		    		} //DEBUG
	    	    	}
	    	    	
	    	    	if(invstack.getAmount() < neededstack.getAmount()){
	    	    	    removeamt = invstack.getAmount();
	    	    	    if (bbx.getConfig().getString("Debug") == "on") { //DEBUG
    		    			bbx.log(Level.INFO, "DEBUG:Stack smaller than PO, selling whole stack"); //DEBUG
    		    		} //DEBUG
	    	    	}
	    	    	
	    	    	EconomyResponse er = BuyBox.econ.depositPlayer(player.getName(), (removeamt * bbx.getConfig().getDouble("PricePerItem")));
	    	    	if (er.type != ResponseType.SUCCESS) {
	    	    		if (bbx.getConfig().getString("Debug") == "on") { //DEBUG
							player.sendMessage(ChatColor.RED + "Economy exchange failure, canceling transaction");
    		    		} //DEBUG
	    	    		return;
	    	    	}
	    	    		
					//deduct from city account if it exists, just for fun
					String city = bbx.getConfig().getString("CityName").toLowerCase();
					if (BuyBox.econ.getBalance(city) > (removeamt * bbx.getConfig().getDouble("PricePerItem"))) {
						BuyBox.econ.withdrawPlayer(city, (removeamt * bbx.getConfig().getDouble("PricePerItem")));
					}
					
					ItemStack removestack = new ItemStack(Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")), removeamt);
					inventory.removeItem(removestack);
					sellcount += removeamt;
					itemsleft -= removeamt;
					bbx.itemsleftHash.put(playername, itemsleft);
					// write to mini
    				bbx.getUtils().saveAll(bbx.itemsleftHash);
					bbx.log(Level.INFO, playername + " sold " + removeamt + " " + Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")) + " to Atlantis for " + (removeamt * bbx.getConfig().getDouble("PricePerItem")) + " " + BuyBox.econ.currencyNamePlural() + " and may sell " + itemsleft + " more.");
					if (bbx.getConfig().getString("Debug") == "on") { //DEBUG
		    			bbx.log(Level.INFO, "DEBUG:stack sale of " + removeamt + " " + Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")) + " for " + (removeamt * bbx.getConfig().getDouble("PricePerItem")) + " " + BuyBox.econ.currencyNamePlural() + "; may sell " + itemsleft + " more."); //DEBUG
		    		} //DEBUG
    	    	}
		    	
		    	if (bbx.getConfig().getString("Debug") == "on") { //DEBUG
	    			bbx.log(Level.INFO, "DEBUG:Transaction Complete, update player inv and report to player"); //DEBUG
	    		} //DEBUG
		    	player.updateInventory();
		    	if (sellcount > 0) {
		    		player.sendMessage("You sold " + sellcount + " " + Utils.getMaterialFromString(bbx.getConfig().getString("ItemInNeed")) + " to Atlantis for " + ChatColor.GREEN + (sellcount * bbx.getConfig().getDouble("PricePerItem")) + " " + BuyBox.econ.currencyNamePlural() + ChatColor.WHITE + ". We need " + itemsleft + " more.");
		    	} else {
		    		player.sendMessage("No sale: please report this event to an admin for debugging");
		    	}
				
    			
        		
        	
        		
	        }
	    }
    }
    
    // Monitors block breaking to remove broken BuyBox
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getBlock().getType().equals((Material.CHEST))) {
        	return;
        }
        
        Location loc = event.getBlock().getLocation();
        if (bbx.getUtils().isBuyBox(loc)) {
        	Player player = event.getPlayer();
        	String playername;
        	if (player.getName() == null) {
        		playername = "an environmental event";
        	} else {
        		playername = player.getName();
        	}
        		
            String bbxname = bbx.getUtils().getBuyBoxName(loc);
            bbx.getConfig().set("Boxes." + bbxname, null);
    		bbx.saveConfig();
    		bbx.log(Level.INFO, "Admin " + playername + " destroyed Buybox " + bbxname);
        }
        
    }
    
    // Exits edit mode if a player logs off
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
    	bbx.removePlayerFromEditMode(event.getPlayer());
    }
}
