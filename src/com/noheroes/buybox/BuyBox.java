/*
 * Copyright (C) 2012 No Heroes.
 * See readme for license details.
 */
package com.noheroes.buybox;

import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
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
	public int itemsleft;
   
	
	public void onEnable(){
		final FileConfiguration config = this.getConfig();
		itemsleft = getConfig().getInt("ItemsPerPlayer");
		listener = new BuyBoxPlayerListener(this);
        this.getServer().getPluginManager().registerEvents(listener, this);
        if (!this.setupEconomy()) {
            this.log(Level.SEVERE, "Vault failed to hook into any economy plugin.  If you do not use an economy plugin, disable UseEconomy in the config file");
            this.log(Level.SEVERE, "Disabling plugin");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("BuyBox enabled");
	}
	 
	public void onDisable(){ 
		getLogger().info("BuyBox disabled");	
	}

	private void log(Level severe, String string) {
		// TODO Auto-generated method stub	
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
