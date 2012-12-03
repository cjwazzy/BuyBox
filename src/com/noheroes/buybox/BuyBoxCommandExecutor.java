package com.noheroes.buybox;

import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuyBoxCommandExecutor implements CommandExecutor {
	private BuyBox bbx;
    
    public BuyBoxCommandExecutor(BuyBox bbx) {
        this.bbx = bbx;

    }

	@Override
	public boolean onCommand(CommandSender cs, Command command, String label,
			String[] args) {
		String com;
		String playername = cs.getName().toLowerCase();
        if (args.length == 0) {
            com = "help";
        }
        
        else {
            com = args[0];
        }
        
        if (com.equalsIgnoreCase("amount")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            }
            // Permission Check
            if (!(cs.hasPermission("buybox.admin") && cs.isOp())) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                bbx.log(Level.INFO, "Non-admin " + playername + " attempted to use /buybox amount");
                return true;
            }
            else {
            	if (Utils.isInteger(args[1]) && (Integer.valueOf(args[1]) >= 0)) {
            		bbx.getConfig().set("ItemsPerPlayer", Integer.valueOf(args[1]));
                	bbx.saveConfig();
                	bbx.itemsleftHash.clear();
                	// write to mini
    				bbx.getUtils().saveAll(bbx.itemsleftHash);
    				cs.sendMessage(ChatColor.BLUE + bbx.getConfig().getString("CityName") + " is now buying " + ChatColor.WHITE + bbx.getConfig().getString("ItemsPerPlayer") + " " + bbx.getConfig().getString("ItemInNeed") + ChatColor.BLUE + " at " + ChatColor.WHITE + bbx.getConfig().getDouble("PricePerItem") + " " + BuyBox.econ.currencyNamePlural() + ChatColor.BLUE + " each.");
                	cs.sendMessage(ChatColor.BLUE + "Itemsleft on all player purchase orders: reset to " + bbx.getConfig().getString("ItemsPerPlayer"));
                	bbx.log(Level.INFO, "Admin " + playername + " set BuyBox amount at " + bbx.getConfig().getString("ItemsPerPlayer") + " and reset each player's itemsleft");
                	bbx.log(Level.INFO, "After Admin " + playername + "'s action, BuyBox status is number: " + bbx.getConfig().getString("ItemsPerPlayer") + ", material: " + bbx.getConfig().getString("ItemInNeed") + ", price: " + bbx.getConfig().getDouble("PricePerItem") + BuyBox.econ.currencyNamePlural());
                }
                else {
                	cs.sendMessage(ChatColor.RED + "That is not a valid amount.  Please use a nonnegative integer");
                }
            	return true;
            }
        }
        
        if (com.equalsIgnoreCase("price")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            }
            // Permission Check
            if (!(cs.hasPermission("buybox.admin") && cs.isOp())) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                bbx.log(Level.INFO, "Non-admin " + playername + " attempted to use /buybox price");
                return true;
            }
            else {
                if (Utils.isDouble(args[1]) && Double.valueOf(args[1]) >= 0) {
                    // write PricePerItem to config here;
                	bbx.getConfig().set("PricePerItem", Double.valueOf(args[1]));
                	bbx.saveConfig();
                	cs.sendMessage(ChatColor.BLUE + bbx.getConfig().getString("CityName") + " is now buying " + ChatColor.WHITE + bbx.getConfig().getString("ItemsPerPlayer") + " " + bbx.getConfig().getString("ItemInNeed") + ChatColor.BLUE + " at " + ChatColor.WHITE + bbx.getConfig().getDouble("PricePerItem") + " " + BuyBox.econ.currencyNamePlural() + ChatColor.BLUE + " each.");
                	bbx.log(Level.INFO, "Admin " + playername + " set BuyBox price at " + bbx.getConfig().getString("PricePerItem"));
                	bbx.log(Level.INFO, "After Admin " + playername + "'s action, BuyBox status is number: " + bbx.getConfig().getString("ItemsPerPlayer") + ", material: " + bbx.getConfig().getString("ItemInNeed") + ", price: " + bbx.getConfig().getDouble("PricePerItem") + BuyBox.econ.currencyNamePlural());
                }
                else {
                	cs.sendMessage(ChatColor.RED + "That is not a valid price.  Please use a non-negative number; decimals are allowed.");
                }
                return true;
            }
        }
        
        if (com.equalsIgnoreCase("material") || com.equalsIgnoreCase("mat")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            }
            // Permission Check
            if (!(cs.hasPermission("buybox.admin") && cs.isOp())) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                bbx.log(Level.INFO, "Non-admin " + playername + " attempted to use /buybox material");
                return true;
            }
            else {
                if (Utils.getMaterialFromString(args[1]) != null) {
                    // write ItemInNeed to config here;
                	bbx.getConfig().set("ItemInNeed", (args[1]));
                	bbx.saveConfig();
                	cs.sendMessage(ChatColor.BLUE + bbx.getConfig().getString("CityName") + " is now buying " + ChatColor.WHITE + bbx.getConfig().getString("ItemsPerPlayer") + " " + bbx.getConfig().getString("ItemInNeed") + ChatColor.BLUE + " at " + ChatColor.WHITE + bbx.getConfig().getDouble("PricePerItem") + " " + BuyBox.econ.currencyNamePlural() + ChatColor.BLUE + " each.");
                	bbx.log(Level.INFO, "Admin " + playername + " set BuyBox material to " + bbx.getConfig().getString("ItemInNeed"));
                	bbx.log(Level.INFO, "After Admin " + playername + "'s action, BuyBox status is number: " + bbx.getConfig().getString("ItemsPerPlayer") + ", material: " + bbx.getConfig().getString("ItemInNeed") + ", price: " + bbx.getConfig().getDouble("PricePerItem") + BuyBox.econ.currencyNamePlural());
                }
                else {
                	cs.sendMessage(ChatColor.RED + "That is not a valid material");
                }
                return true;
            }
        }
        
        if (com.equalsIgnoreCase("reset") || com.equalsIgnoreCase("resetcount")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            }
            // Permission Check
            if (!cs.hasPermission("buybox.admin") && !cs.isOp()) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                bbx.log(Level.INFO, "Non-admin " + playername + " attempted to use /buybox reset");
                return true;
            }
            else {
            	bbx.itemsleftHash.clear();
            	// write to mini
				bbx.getUtils().clearMini(bbx.itemsleftHash);
				cs.sendMessage(ChatColor.BLUE + bbx.getConfig().getString("CityName") + " is still buying " + ChatColor.WHITE + bbx.getConfig().getString("ItemsPerPlayer") + " " + bbx.getConfig().getString("ItemInNeed") + ChatColor.BLUE + " at " + ChatColor.WHITE + bbx.getConfig().getDouble("PricePerItem") + " " + BuyBox.econ.currencyNamePlural() + ChatColor.BLUE + " each.");
            	cs.sendMessage(ChatColor.RED + "itemsleft on all player purchase orders: reset to " + bbx.getConfig().getString("ItemsPerPlayer"));
            	bbx.log(Level.INFO, "Admin " + playername + " reset all players' itemsleft to " + bbx.getConfig().getString("ItemsPerPlayer"));
            	return true;
            }
        }
        
        if (com.equalsIgnoreCase("city") || com.equalsIgnoreCase("cityname")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            }
            // Permission Check
            if (!(cs.hasPermission("buybox.admin") && cs.isOp())) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                bbx.log(Level.INFO, "Non-admin " + playername + " attempted to use /buybox city");
                return true;
            }
            else {
            	bbx.getConfig().set("CityName", (args[1]));
            	bbx.saveConfig();
            	cs.sendMessage(ChatColor.BLUE + "City name now set to " + bbx.getConfig().getString("CityName"));
            	bbx.log(Level.INFO, "Admin " + playername + " set BuyBox city name to " + bbx.getConfig().getString("CityName"));
            	bbx.log(Level.INFO, "After Admin " + playername + "'s action, BuyBox status is number: " + bbx.getConfig().getString("ItemsPerPlayer") + ", material: " + bbx.getConfig().getString("ItemInNeed") + ", price: " + bbx.getConfig().getDouble("PricePerItem") + BuyBox.econ.currencyNamePlural());
            	return true;
            }
        }
        
        if (com.equalsIgnoreCase("info")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            } else {
            	cs.sendMessage(ChatColor.WHITE + "The BuyBox is a place for " + bbx.getConfig().getString("CityName") + " to purchase needed materials from its helpful residents.");
            	cs.sendMessage(ChatColor.WHITE + "Purchase Orders may change frequently and without notice according to our current needs");
            	cs.sendMessage(ChatColor.WHITE + "Use " + ChatColor.YELLOW + "/bbx list" + ChatColor.WHITE + " For a list of locations");
            return true;	
            }
        } 
        
        if (com.equalsIgnoreCase("list")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            }
            if (!(bbx.getConfig().contains("Boxes"))) {
            	cs.sendMessage(ChatColor.WHITE + "No BuyBoxes found on the server, sorry.");
    			return true;
    		}
            if (!(cs.hasPermission("buybox.admin") && cs.isOp())) {
            	Set<String> keySet = bbx.getConfig().getConfigurationSection("Boxes").getKeys(false);
        		if ((keySet == null) || (keySet.isEmpty())) {
        			cs.sendMessage(ChatColor.WHITE + "No BuyBoxes found on the server, sorry.");
                    return true;
                }
        		cs.sendMessage(ChatColor.WHITE + "BuyBox Locations:");
        		for (String key : keySet) {
        			String wName = bbx.getConfig().getString("Boxes." + key + ".World");
        	        World world = Bukkit.getWorld(wName);
        	        if (world == null) {
        	                bbx.log(Level.WARNING, "World " + wName + " does not exist for the BuyBox location");
        	                break;
        	        }
        	        cs.sendMessage(bbx.getConfig().getInt("Boxes." + key + ".X") + ", " + bbx.getConfig().getInt("Boxes." + key + ".Y") + ", " + bbx.getConfig().getInt("Boxes." + key + ".Z") + " in " + wName);
        		}
        		return true;
            }
            else {
            	Set<String> keySet = bbx.getConfig().getConfigurationSection("Boxes").getKeys(false);
        		if ((keySet == null) || (keySet.isEmpty())) {
        			cs.sendMessage(ChatColor.WHITE + "No BuyBoxes found on the server, sorry.");
                    return true;
                }
        		cs.sendMessage(ChatColor.WHITE + "BuyBox Locations:");
        		for (String key : keySet) {
        			String wName = bbx.getConfig().getString("Boxes." + key + ".World");
        	        World world = Bukkit.getWorld(wName);
        	        if (world == null) {
        	                bbx.log(Level.WARNING, "World " + wName + " does not exist for the BuyBox location");
        	                break;
        	        }
        	        cs.sendMessage(key + " at " + bbx.getConfig().getInt("Boxes." + key + ".X") + ", " + bbx.getConfig().getInt("Boxes." + key + ".Y") + ", " + bbx.getConfig().getInt("Boxes." + key + ".Z") + " in " + wName);
        		}
        		return true;	
            }
        } 
        
        if (com.equalsIgnoreCase("create") || com.equalsIgnoreCase("add")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            }
            if (!(cs.hasPermission("buybox.admin") && cs.isOp())) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return true;
            }
            if (args.length < 2){
            	cs.sendMessage(ChatColor.RED + "Please use " + ChatColor.YELLOW + "/buybox create [name]" + ChatColor.RED + " for this buybox");
                return true;
            }
            else {
            		String bbxname = args[1].toLowerCase();
                    bbx.addPlayerToEditMode((Player)cs, (String)bbxname);
                    bbx.log(Level.INFO, "Admin " + playername + " entered create mode, buybox name = " + bbxname);
                    return true;
            }
        }
        
        if (com.equalsIgnoreCase("remove") || com.equalsIgnoreCase("delete")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            }
            if (!(cs.hasPermission("buybox.admin") && cs.isOp())) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return true;
            }
            if (args.length < 2){
            	cs.sendMessage(ChatColor.RED + "Please use " + ChatColor.YELLOW + "/buybox remove [name]" + ChatColor.RED + " for this buybox");
                return true;
            }
            else {
            		String bbxname = args[1].toLowerCase();
            		bbx.getConfig().set("Boxes." + bbxname, null);
            		bbx.saveConfig();
            		bbx.buyBoxLocs = bbx.getUtils().loadLocs();
            		cs.sendMessage(ChatColor.RED + "You have deleted Buybox " + bbxname);
                    bbx.log(Level.INFO, "Admin " + playername + " deleted Buybox " + bbxname);
                    return true;
            }
        }
        
        if (com.equalsIgnoreCase("reload")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            }
            if (!(cs.hasPermission("buybox.admin") && cs.isOp())) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                bbx.log(Level.INFO, "Non-admin " + playername + " attempted to use /buybox reload");
                return true;
            }
            else {
    			bbx.reloadConfig();
				bbx.itemsleftHash = bbx.getUtils().loadMiniToHash();
				bbx.buyBoxLocs = bbx.getUtils().loadLocs();
                bbx.log(Level.INFO, "Admin " + playername + " reloaded the Config, BuyBox locations, and itemsleft Hashmap from disk (minidb)");
                cs.sendMessage(ChatColor.RED + "Config, BuyBox locations, and itemsleft Hashmap reloaded");
                return true;
            }
        }
        
        if (com.equalsIgnoreCase("debug")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            }
            // Permission Check
            if (!(cs.hasPermission("buybox.admin") && cs.isOp())) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                bbx.log(Level.INFO, "Non-admin " + playername + " attempted to use /buybox debug");
                return true;
            }
            else {
            	if (args.length < 2 || !(args[1].equalsIgnoreCase ("on") || args[1].equalsIgnoreCase ("true") || args[1].equalsIgnoreCase ("enable") || args[1].equalsIgnoreCase ("off") || args[1].equalsIgnoreCase ("false") || args[1].equalsIgnoreCase ("disable"))) {
            		cs.sendMessage(ChatColor.BLUE + "Invalid debug state, please use 'on' or 'off'");
                	bbx.log(Level.INFO, "Invalid debug state, please use 'on' or 'off'");
            	}
            	else if (args[1].equalsIgnoreCase ("on") || args[1].equalsIgnoreCase ("true") || args[1].equalsIgnoreCase ("enable")) {
            		bbx.getConfig().set("Debug", "on");
                	bbx.saveConfig();
                	cs.sendMessage(ChatColor.BLUE + "BuyBox debug mode is now " + bbx.getConfig().getString("Debug") + ", check console for debug messages.");
                	bbx.log(Level.INFO, "BuyBox debug mode is now " + bbx.getConfig().getString("Debug"));
            	}
            	else if (args[1].equalsIgnoreCase ("off") || args[1].equalsIgnoreCase ("false") || args[1].equalsIgnoreCase ("disable")) {
            		bbx.getConfig().set("Debug", "off");
                	bbx.saveConfig();
                	cs.sendMessage(ChatColor.BLUE + "BuyBox debug mode is now " + bbx.getConfig().getString("Debug") + ", check console for debug messages.");
                	bbx.log(Level.INFO, "BuyBox debug mode is now " + bbx.getConfig().getString("Debug"));
            	}
            	return true;
            }
        }
        
        if (com.equalsIgnoreCase("help")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            } else {
            	// Reverse Permission Check
            	if (!(cs.hasPermission("buybox.admin") && cs.isOp())) {
            		cs.sendMessage("BuyBox Help:     " + ChatColor.YELLOW + "/buybox /bbox /bbx");
            		cs.sendMessage("Use " + ChatColor.YELLOW + "/bbx info" + ChatColor.WHITE + " for more information or " + ChatColor.YELLOW + "/bbx list" + ChatColor.WHITE + " for locations");
            	}
            	cs.sendMessage(ChatColor.BLUE + bbx.getConfig().getString("CityName") + " is currently buying " + ChatColor.WHITE + bbx.getConfig().getString("ItemsPerPlayer") + " " + bbx.getConfig().getString("ItemInNeed") + ChatColor.BLUE + " at " + ChatColor.WHITE + bbx.getConfig().getDouble("PricePerItem") + " " + BuyBox.econ.currencyNamePlural() + ChatColor.BLUE + " each.");
            	// Reverse Permission Check
            	if (!(cs.hasPermission("buybox.admin") && cs.isOp())) {
            		if (bbx.itemsleftHash.get(playername) == null) {
	            		cs.sendMessage(ChatColor.BLUE + "You may sell " + ChatColor.WHITE + bbx.getConfig().getInt("ItemsPerPlayer") + ChatColor.BLUE + " more on this purchase order");
	            	}
	            	else {
	            		cs.sendMessage(ChatColor.BLUE + "You may sell " + ChatColor.WHITE + bbx.itemsleftHash.get(playername) + ChatColor.BLUE + " more on this purchase order");
	            	}
            	}
            	// Permission Check
            	if (cs.hasPermission("buybox.admin") && cs.isOp()) {
            		cs.sendMessage("BuyBox Admin Command Help:     " + ChatColor.YELLOW + "/buybox /bbox /bbx");
            		cs.sendMessage(ChatColor.YELLOW + "/bbx price [#] " + ChatColor.WHITE + " Set the purchase price of a single item");
            		cs.sendMessage(ChatColor.YELLOW + "/bbx amount [#] " + ChatColor.WHITE + " Set the max items per player; resets count");
            		cs.sendMessage(ChatColor.YELLOW + "/bbx mat [material or item ID] " + ChatColor.WHITE + " Set the item for purchase");
            		cs.sendMessage(ChatColor.YELLOW + "/bbx reset " + ChatColor.WHITE + " Resets all player items-left to the max amount");
            		cs.sendMessage(ChatColor.WHITE + "Please use " + ChatColor.YELLOW + "/bbx reset " + ChatColor.WHITE + "after changing commodity or price");
            		cs.sendMessage(ChatColor.YELLOW + "/bbx create " + ChatColor.WHITE + " Create BuyBox; old locations will be overwritten");
            		cs.sendMessage(ChatColor.YELLOW + "/bbx city " + ChatColor.WHITE + " Sets your city name; No spaces allowed");
            		cs.sendMessage(ChatColor.YELLOW + "/bbx reload " + ChatColor.WHITE + " Reload config, locations and player POs");
            		
            	}
            return true;	
            }
        } else {
        	cs.sendMessage(ChatColor.RED + "Command not recognized. Try " + ChatColor.YELLOW + "/bbx help");
        }
		return true;
	}

}
