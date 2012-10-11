package com.noheroes.buybox;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Material;
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
    
    public HashMap<String, Integer> loadAll() {
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
