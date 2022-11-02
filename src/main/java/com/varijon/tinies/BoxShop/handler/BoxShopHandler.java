package com.varijon.tinies.BoxShop.handler;

import org.apache.commons.lang3.math.NumberUtils;

import com.varijon.tinies.BoxShop.util.Util;

import ca.landonjw.gooeylibs2.api.UIManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BoxShopHandler 
{
	MinecraftServer server;
	public BoxShopHandler()
	{
		server = FMLCommonHandler.instance().getMinecraftServerInstance();		
	}

	@SubscribeEvent
	public void onBlockPlace(EntityPlaceEvent event)
	{
		if(!(event.getEntity() instanceof EntityPlayerMP))
		{
			return;
		}
		if(event.isCanceled())
		{
			return;
		}
		if(!Util.checkForShulker(event.getPlacedBlock().getBlock()))
		{
			return;
		}
		EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
		World world = player.getServerWorld();
		
		if(world.getTileEntity(event.getPos()) == null)
		{
			return;
		}
		
		TileEntityShulkerBox shulker = (TileEntityShulkerBox) world.getTileEntity(event.getPos());
		NBTTagCompound nbt = shulker.writeToNBT(new NBTTagCompound());
		
		if(!nbt.hasKey("CustomName"))
		{
			return;
		}
		if(nbt.getString("CustomName").toLowerCase().contains("boxshop"))
		{
			String[] nameArray = nbt.getString("CustomName").toLowerCase().split(" ");
			if(nameArray.length == 2)
			{
				if(NumberUtils.isParsable(nameArray[1]))
				{
					int cost = Integer.parseInt(nameArray[1]);
					if(cost < 1)
					{
						return;
					}
					nbt.getCompoundTag("ForgeData").setBoolean("isBoxShop", true);
					nbt.getCompoundTag("ForgeData").setString("ownerUUID", player.getUniqueID().toString());
					nbt.getCompoundTag("ForgeData").setInteger("itemCost", cost);
					shulker.loadFromNbt(nbt);
					world.setTileEntity(event.getPos(), shulker);
				}
				else
				{
					return;
				}
			}
			else
			{
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerInteract(RightClickBlock event)
	{
		if(!(event.getEntity() instanceof EntityPlayerMP))
		{
			return;
		}
		if(event.isCanceled())
		{
			return;
		}
		EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
		World world = player.getServerWorld();
		if(Util.checkForShulker(event.getWorld().getBlockState(event.getPos()).getBlock()))
		{
			if(world.getTileEntity(event.getPos()) == null)
			{
				return;
			}
			
			TileEntityShulkerBox shulker = (TileEntityShulkerBox) world.getTileEntity(event.getPos());
			NBTTagCompound nbt = shulker.writeToNBT(new NBTTagCompound());
			
			if(!nbt.hasKey("ForgeData"))
			{
				return;
			}
			if(nbt.getCompoundTag("ForgeData").hasKey("isBoxShop"))
			{
				int cost = nbt.getCompoundTag("ForgeData").getInteger("itemCost");
				String ownerUUID = nbt.getCompoundTag("ForgeData").getString("ownerUUID");
				
				if(player.canUseCommand(4, "boxshop.ignoreshops") && (player.isCreative() || player.isSpectator()))
				{
					return;
				}
				else
				{
					if(player.getUniqueID().toString().equals(ownerUUID))
					{
						return;
					}
				}
				event.setCanceled(true);
				UIManager.openUIForcefully(player, Util.getBoxShopMenu(shulker, cost, ownerUUID, server));
				
			}
		}
	}
}
