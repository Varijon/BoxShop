package com.varijon.tinies.BoxShop;

import com.varijon.tinies.BoxShop.command.BoxShopReloadCommand;
import com.varijon.tinies.BoxShop.config.BoxShopConfigManager;
import com.varijon.tinies.BoxShop.handler.BoxShopHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid="boxshop", version="1.0.5", acceptableRemoteVersions="*")
public class BoxShop
{
	public static String MODID = "modid";
	public static String VERSION = "version";

		
	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{

	}
	
	@EventHandler
	public void init(FMLInitializationEvent e)
	{
		MinecraftForge.EVENT_BUS.register(new BoxShopHandler());
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		BoxShopConfigManager.loadConfiguration();
	}

	 @EventHandler
	 public void serverLoad(FMLServerStartingEvent event)
	 {	 
		 event.registerServerCommand(new BoxShopReloadCommand());
	 }
}