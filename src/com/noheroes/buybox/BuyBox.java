/*
 * Copyright (C) 2012 No Heroes.
 * See readme for license details.
 */
package com.noheroes.buybox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

	public static Economy econ = null;
	private BuyBoxPlayerListener listener;
	public Map<String, Integer> Itemsleft = new HashMap<String, Integer>();
	public ArrayList<Player> bbxEditMode = new ArrayList<Player>();
   
	public void onEnable(){
		@SuppressWarnings("unused")
		final FileConfiguration config = this.getConfig();
		try{
			Itemsleft = SLAPI.load("itemsleft.bin"); 
	    }catch(Exception e){
	        //handle the exception
	        e.printStackTrace();
	    }
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
		try{
			SLAPI.save(Itemsleft,"itemsleft.bin");
	    }catch(Exception e){
	        e.printStackTrace();
	    }
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
        bbxEditMode.remove(player);
    }

	
}
