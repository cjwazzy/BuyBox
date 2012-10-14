package com.noheroes.buybox;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.miniDC.Arguments;
import com.miniDC.Mini;

public class Utils {
	private BuyBox bbx;
    private String folder;
    private Mini minidb;
    
    public Utils(BuyBox bbx, String folder) {
    	this.bbx = bbx;
        this.folder = folder;
        minidb = new Mini(folder, "buybox.mini");
    }
	
    public void saveAll(HashMap<String, Integer> itemsleftHash) {
    	Arguments arg;
    	if (bbx.itemsleftHash == null) {
    		return;
    	}
        Set<String> keySet = bbx.itemsleftHash.keySet();
        for (String playername : keySet) {
            arg = ItemsLeftToArg(playername);
            minidb.addIndex(playername, arg);
        }
        minidb.update();
    }
    
    public void save(String playername) {
        Arguments arg = ItemsLeftToArg(playername);
        minidb.addIndex(playername, arg);
        minidb.update();
    }
    
    public void clearMini(HashMap<String, Integer> itemsleftHash) {
    	Set<String> keySet = minidb.getIndices().keySet();
        for (String playername : keySet) {
	        minidb.removeIndex(playername);
        }
        minidb.update();
    }
    
    public HashMap<String, Integer> loadMiniToHash() {
    	minidb = new Mini(folder, "buybox.mini");
    	Set<String> keySet = minidb.getIndices().keySet();
        if ((keySet == null) || (keySet.isEmpty())) {
            return null;
        }
        HashMap<String, Integer> itemsleftHash = new HashMap<String, Integer>();
        Arguments arg;
        Integer itemsleft;
        for (String key : keySet) {
            arg = minidb.getArguments(key);
            itemsleft = argToItemsLeft(arg);
            if (itemsleft != null) {
            	itemsleftHash.put(key, itemsleft);
            }
        }
        return itemsleftHash;
    }
    
	public List<Location> loadLocs() {
		List<Location> buyBoxLocsList = new LinkedList<Location>();
		if (!(bbx.getConfig().contains("Boxes"))) {
			bbx.log(Level.WARNING, "No BuyBoxes found in config74");
			return buyBoxLocsList;
		}
		Set<String> keySet = bbx.getConfig().getConfigurationSection("Boxes").getKeys(false);
		if ((keySet == null) || (keySet.isEmpty())) {
			bbx.log(Level.WARNING, "No BuyBoxes found in config79");
            return buyBoxLocsList;
        }
		for (String key : keySet) {
			String wName = bbx.getConfig().getString("Boxes." + key + ".World");
	        World world = Bukkit.getWorld(wName);
	        if (world == null) {
	                bbx.log(Level.WARNING, "World " + wName + " does not exist for the BuyBox location");
	                break;
	        }
	        Location loc = new Location(world, bbx.getConfig().getInt("Boxes." + key + ".X"), bbx.getConfig().getInt("Boxes." + key + ".Y"), bbx.getConfig().getInt("Boxes." + key + ".Z"));
			buyBoxLocsList.add(loc);
		}
		return buyBoxLocsList;
	}
    
    public boolean isBuyBox(Location loc) {
    	if (bbx.buyBoxLocs == null) {
    		return false;
    	}
    	return bbx.buyBoxLocs.contains(loc);	
    }
    
    private Integer argToItemsLeft(Arguments arg) {
        Integer itemsleft;
        try {
            itemsleft = Integer.valueOf(arg.getValue("itemsleft"));
            return itemsleft;
        } catch (NumberFormatException ex) {
            bbx.log(Level.SEVERE, "Error loading itemsleft from mini: invalid integer.  Player " + arg.getKey() + "'s purchase order has been reset instead of set to " + arg.getValue("itemsleft"));
            return null;
        }
    }
    
    private Arguments ItemsLeftToArg(String playername) {
        Arguments arg = new Arguments(playername);
        arg.setValue("itemsleft", bbx.itemsleftHash.get(playername));
        return arg;
        }
    
	public static boolean isInteger(String string) {
	    try {
	        Integer.valueOf(string);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}
	
	public static boolean isDouble(String string) {
	    try {
	        Double.valueOf(string);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}
	
    public static Material getMaterialFromString(String mat) {
        Material material;
        // Check if material is referenced by enum
        material = Material.getMaterial(mat.toUpperCase());
        // Material was matched
        if (material != null) {
            return material;
        }
        // Material was not matched, check if material is referenced by ID
        else {
            Integer materialID;
            try {
                materialID = Integer.valueOf(mat);
            }
            catch (NumberFormatException ex) {
                // mat is not an integer or valid material
                return null;
            }
            // Check if material ID exists
            material = Material.getMaterial(materialID);
            if (material == null) {
                return null;
            }
            return material;
        }
    }

}
