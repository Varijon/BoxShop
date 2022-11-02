package com.varijon.tinies.BoxShop.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.config.PixelmonItemsHeld;
import com.varijon.tinies.BoxShop.object.BoxShopConfig;
import com.varijon.tinies.BoxShop.object.ItemConfigMinPrice;

public class BoxShopConfigManager 
{
	static BoxShopConfig boxShopConfig;
	
	public static boolean loadConfiguration()
	{
		String basefolder = new File("").getAbsolutePath();
        String source = basefolder + "/config/BoxShop";
		try
		{
			Gson gson = new Gson();
			
			File dir = new File(source);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			
			writeConfigFile();
			
			for(File file : dir.listFiles())
			{
				if(!file.getName().equals("config.json"))
				{
					continue;
				}
				FileReader reader = new FileReader(file);
				
				BoxShopConfig boxShopConfig = gson.fromJson(reader, BoxShopConfig.class);
								
				BoxShopConfigManager.boxShopConfig = boxShopConfig;
				reader.close();
			}
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public static void writeConfigFile()
	{
		String basefolder = new File("").getAbsolutePath();
        String source = basefolder + "/config/BoxShop";
		
		try
		{
			File dir = new File(source);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			if(dir.listFiles().length == 0)
			{
				ArrayList<ItemConfigMinPrice> lstExampleMinPrice = new ArrayList<>();
				lstExampleMinPrice.add(new ItemConfigMinPrice(PixelmonItemsHeld.destinyKnot.getRegistryName().toString(), 50000, 0, ""));
				lstExampleMinPrice.add(new ItemConfigMinPrice(PixelmonItemsHeld.everStone.getRegistryName().toString(), 50000, 0, ""));
				lstExampleMinPrice.add(new ItemConfigMinPrice(PixelmonItems.silverBottleCap.getRegistryName().toString(), 50000, 0, ""));
				
				BoxShopConfig gtsConfig = new BoxShopConfig(lstExampleMinPrice);
		
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
					
				FileWriter writer = new FileWriter(source + "/config.json");
				gson.toJson(gtsConfig, writer);
				writer.close();
			}
		}
			
		catch (Exception ex) 
		{
		    ex.printStackTrace();
		}
	}
	
	public static BoxShopConfig getConfig()
	{
		return boxShopConfig;
	}
}
