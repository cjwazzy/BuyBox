/*
 * Copyright (C) 2012 No Heroes.
 * See readme for license details.
 */
package com.noheroes.buybox;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;

/*import com.noheroes.buybox.Listeners.BuyBoxBlockListener;*/


/**
*
* @author CJ WaZzy <cjwazzy at gmail.com> under Sorklin <sorklin at gmail.com> & Morthis (PIETER)
*/

public class BuyBox extends JavaPlugin {

	private static final Logger log = Logger.getLogger("Minecraft");
	public static Economy econ = null;
	public boolean isEconEnabled() {
        return (econ != null);
	}
	
	private BuyBoxPlayerListener listener;
    /*private BuyBoxBlockListener blockListener;*/
	
	public void onEnable(){ 
		/*playerListener = new BuyBoxPlayerListener (this);*/
        /*blockListener = new BuyBoxBlockListener (this);*/
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
	 
	private void log(Level severe, String string) {
		// TODO Auto-generated method stub
		
	}

	public void onDisable(){ 
		getLogger().info("BuyBox disabled");
		
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
