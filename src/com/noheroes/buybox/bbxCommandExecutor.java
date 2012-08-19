package com.noheroes.buybox;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class bbxCommandExecutor implements CommandExecutor {
	
	public static BuyBox bbx;
    
    public bbxCommandExecutor(BuyBox bbx) {
        this.bbx = bbx;
    }

	@Override
	public boolean onCommand(CommandSender cs, Command command, String label,
			String[] args) {
		String com;
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
            /* Permission Check */
            if (/*!cs.hasPermission(Properties.adminPermissions) && */!cs.isOp()) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return true;
            }
            else {
                if (Utils.isInteger(args[1])) {
                    // write ItemsPerPlayer to config here;
                	bbx.getConfig().set("ItemsPerPlayer", (args[1]));
                	bbx.saveConfig();
                }
                else {
                	cs.sendMessage(ChatColor.RED + "That is not a valid amount");
                }
            }
        }
        
        if (com.equalsIgnoreCase("price")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            }
            /* Permission Check */
            if (/*!cs.hasPermission(Properties.adminPermissions) && */!cs.isOp()) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return true;
            }
            else {
                if (Utils.isDouble(args[1])) {
                    // write PricePerItem to config here;
                	bbx.getConfig().set("PricePerItem", (args[1]));
                	bbx.saveConfig();
                }
                else {
                	cs.sendMessage(ChatColor.RED + "That is not a valid price");
                }
            }
        }
        
        if (com.equalsIgnoreCase("material") || com.equalsIgnoreCase("mat")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            }
            /* Permission Check */
            if (/*!cs.hasPermission(Properties.adminPermissions) && */!cs.isOp()) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return true;
            }
            else {
                if (Utils.getMaterialFromString(args[1]) != null) {
                    // write ItemInNeed to config here;
                	bbx.getConfig().set("ItemInNeed", (Utils.getMaterialFromString(args[1])));
                	bbx.saveConfig();
                }
                else {
                	cs.sendMessage(ChatColor.RED + "That is not a valid price");
                }
            }
        }
        
        if (com.equalsIgnoreCase("reset") || com.equalsIgnoreCase("resetcount")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            }
            /* Permission Check */
            if (/*!cs.hasPermission(Properties.adminPermissions) && */!cs.isOp()) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return true;
            }
            else {
                // TODO Reset hashmap & minidb
            }
        }
        
        if (com.equalsIgnoreCase("help")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            } else {
            	cs.sendMessage(ChatColor.BLUE + "We are currently buying " + bbx.getConfig().getString("ItemsPerPlayer") + " " + bbx.getConfig().getString("ItemInNeed") + " at " + bbx.getConfig().getString("PricePerItem") + " " + bbx.econ.currencyNamePlural());
            	/* Permission Check */
            	if (/*cs.hasPermission(Properties.adminPermissions) && */cs.isOp()) {
            		cs.sendMessage("BuyBox Admin Command Help: " + ChatColor.YELLOW + "/buybox /bbox /bbx");
            		cs.sendMessage(ChatColor.YELLOW + "/bbx price [#] " + ChatColor.WHITE + " Set the purchase price of a single item");
            		cs.sendMessage(ChatColor.YELLOW + "/bbx amount [#] " + ChatColor.WHITE + " Set the maximum items a player may sell");
            		cs.sendMessage(ChatColor.YELLOW + "/bbx material [material or item ID] " + ChatColor.WHITE + " Set the item for purchase");
            		cs.sendMessage(ChatColor.WHITE + "-alternately use " + ChatColor.YELLOW + " /bbx mat [material or item ID]");
            		cs.sendMessage(ChatColor.YELLOW + "/bbx reset " + ChatColor.WHITE + " Resets all player counts to the maximum amount");
            		cs.sendMessage(ChatColor.WHITE + "Please use " + ChatColor.YELLOW + "/bbx reset " + ChatColor.WHITE + "after changing commodities");
            	}
            return true;	
            }
        }
		return true;
	}

}
