/*
 * Copyright (C) 2012 No Heroes.
 * See readme for license details.
 */
package com.noheroes.buybox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;



/**
*
* @author CJ WaZzy <cjwazzy at gmail.com> under Sorklin <sorklin at gmail.com> & Morthis (PIETER)
*/

public class BuyBox extends JavaPlugin {

	private Utils utils;
	private BuyBoxPlayerListener listener;
	public static Economy econ = null;
	public HashMap<String, Integer> itemsleftHash = new HashMap<String, Integer>();
	public ArrayList<Player> bbxEditMode = new ArrayList<Player>();
   
	@SuppressWarnings("unused")
	public void onEnable(){
		if (!this.setupEconomy()) {
            log(Level.SEVERE, "Vault failed to hook into any economy plugin.  Disbling plugin");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
			final FileConfiguration config = this.getConfig();
			utils = new Utils(this,  this.getDataFolder().getPath());
			HashMap<String, Integer> itemsleftHash = utils.loadAll();
			listener = new BuyBoxPlayerListener(this);
	        this.getServer().getPluginManager().registerEvents(listener, this);
	        getCommand("buybox").setExecutor(new BuyBoxCommandExecutor(this));
	        
	        
	        getLogger().info("BuyBox enabled");
        }
	}
	 
	public void onDisable(){ 
		getLogger().info("Saving Hash to Mini");
		// Save to mini
		utils.saveAll(itemsleftHash);
		getLogger().info("BuyBox disabled");	
	}
	
	public Utils getUtils() {
		return utils;
		}

	public void log(Level level, String message) {
		this.getLogger().log(level, message);
	}

	public boolean isEconEnabled() {
        return (econ != null);
	}	
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        econ = rsp.getProvider();
        return econ != null;
    }
	
	public void addPlayerToEditMode(Player player) {
        if (this.bbxEditMode.contains(player)) {
            player.sendMessage(ChatColor.RED + "You are already in create mode, right click to cancel");
            return;
        } else {
        bbxEditMode.add(player);
        player.sendMessage(ChatColor.RED + "Left click a chest to set BuyBox, right click to cancel");
        }
    }
    
    public void removePlayerFromEditMode(Player player) {
    	if (this.bbxEditMode.contains(player)) {
    		bbxEditMode.remove(player);
        }
    }
}
