/*
 * Copyright (C) 2012 No Heroes.
 * See readme for license details.
 */
package com.noheroes.buybox;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
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

	public static Economy econ = null;
	private BuyBoxPlayerListener listener;
	public Map<String, Integer> Itemsleft = new HashMap<String, Integer>(); // read from minidb
   
	public void onEnable(){
		final FileConfiguration config = this.getConfig();
		listener = new BuyBoxPlayerListener(this);
        this.getServer().getPluginManager().registerEvents(listener, this);
        getCommand("buybox").setExecutor(new bbxCommandExecutor(this));
        if (!this.setupEconomy()) {
            BuyBox.log(Level.SEVERE, "Vault failed to hook into any economy plugin.  Disbling plugin");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("BuyBox enabled");
	}
	 
	public void onDisable(){ 
		getLogger().info("BuyBox disabled");	
	}

	public static void log(Level level, String message) {
		BuyBox.log(Level.INFO, message);
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
	

	
}
