package com.varijon.tinies.BoxShop.handler;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.math.NumberUtils;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.economy.IPixelmonBankAccount;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.UsernameCache;
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
		if(!checkForShulker(event.getPlacedBlock().getBlock()))
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
		if(checkForShulker(event.getWorld().getBlockState(event.getPos()).getBlock()))
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
				UIManager.openUIForcefully(player, getBoxShopMenu(shulker, cost, ownerUUID));
				
			}
		}
	}
	public GooeyPage getBoxShopMenu(TileEntityShulkerBox shulker, int cost, String owner)
	{
        ChestTemplate.Builder templateBuilder = ChestTemplate.builder(3);
        int count = 0;
		for(int x = 0; x < 3; x++)
		{
			for(int y = 0; y < 9; y++)
			{
				int slotNumber = count;
				GooeyButton itemButton = GooeyButton.builder()
		                .display(shulker.getStackInSlot(slotNumber))
		                .onClick((action) -> 
		        		{
		        			if(action.getButton().getDisplay().getItem() != Items.AIR)
		        			{
		        				if(shulker.getWorld().getTileEntity(shulker.getPos()) == shulker)
                    			{
	                				UIManager.closeUI(action.getPlayer());
	                				UIManager.openUIForcefully(action.getPlayer(), getBuyConfirmMenu(action.getButton().getDisplay(), cost, owner, shulker, slotNumber));
                    			}
		        				else
		        				{
	                				UIManager.closeUI(action.getPlayer());
		        				}
		        			}
		        		})
		                .build();
		        templateBuilder.set(x, y, itemButton);
		        count++;
			}
		}

		ChestTemplate template = templateBuilder
                .build();

        String playerName = UsernameCache.getLastKnownUsername(UUID.fromString(owner));
        
        if(playerName == null)
        {
        	playerName = "Someone";
        }
        
        GooeyPage pageBuilder = GooeyPage.builder()
                .title(TextFormatting.DARK_BLUE + playerName + "'s Shop" + TextFormatting.GREEN + " Cost: " + TextFormatting.RED + cost )
                .template(template)
                .build();

        return pageBuilder;
	}
	
	public GooeyPage getBuyConfirmMenu(ItemStack buyingItem, int cost, String owner, TileEntityShulkerBox shulker, int slot)
	{
		GooeyButton emptySlot = GooeyButton.builder()
                .display(new ItemStack(Blocks.STAINED_GLASS_PANE,1,0))
                .title("")
                .build();
		
		GooeyButton confirmButton = GooeyButton.builder()
                .display(new ItemStack(Blocks.STAINED_GLASS_PANE,1,5))
                .title(TextFormatting.GREEN + "Click here to buy for " + TextFormatting.RED + cost + TextFormatting.GREEN + "!")
                .onClick((action) -> 
        		{
        			String buyItemName = buyingItem.getDisplayName();
        			if(shulker.getWorld().getTileEntity(shulker.getPos()) == shulker)
        			{
        				if(ItemStack.areItemStacksEqual(shulker.getStackInSlot(slot), buyingItem))
        				{
                			if(!action.getPlayer().inventory.addItemStackToInventory(buyingItem))
                			{
                				UIManager.closeUI(action.getPlayer());
                				action.getPlayer().sendMessage(new TextComponentString(TextFormatting.RED + "Your inventory is full, make space first!"));
                			}
                			else
                			{
                				Optional<? extends IPixelmonBankAccount> buyerAccountOpt =	Pixelmon.moneyManager.getBankAccount(action.getPlayer());

                				if(!buyerAccountOpt.isPresent())
                				{
                					action.getPlayer().sendMessage(new TextComponentString(TextFormatting.RED + "Bank account not found!"));
                					return;
                				}
                				IPixelmonBankAccount buyerAccount = buyerAccountOpt.get();
                				
                				//PlayerPartyStorage partyBuyer = Pixelmon.storageManager.getParty(action.getPlayer());
                				if(buyerAccount.getMoney() >= cost)
                				{
                					buyerAccount.changeMoney(-cost);
                					buyerAccount.updatePlayer(buyerAccount.getMoney());                					
                				}
                				else
                				{
	                				UIManager.closeUI(action.getPlayer());
                    				action.getPlayer().sendMessage(new TextComponentString(TextFormatting.RED + "You don't have enough money!"));
                    				if(shulker.getWorld().getTileEntity(shulker.getPos()) == shulker)
                        			{
                    					UIManager.openUIForcefully(action.getPlayer(), getBoxShopMenu(shulker, cost, owner));
                        			}
                    				return;
                				}
                				                				
                				Optional<? extends IPixelmonBankAccount> sellerAccountOpt =	Pixelmon.moneyManager.getBankAccount(UUID.fromString(owner));

                				if(!sellerAccountOpt.isPresent())
                				{
                					action.getPlayer().sendMessage(new TextComponentString(TextFormatting.RED + "Seller bank account not found!"));
                					return;
                				}
                				IPixelmonBankAccount sellerAccount = sellerAccountOpt.get();
                				
                				//PlayerPartyStorage partyReceiver = Pixelmon.storageManager.getParty(UUID.fromString(owner));
                				sellerAccount.changeMoney(cost);
                				EntityPlayerMP targetPlayer = server.getPlayerList().getPlayerByUUID(UUID.fromString(owner));
                				if(targetPlayer != null)
                				{
                					targetPlayer.sendMessage(new TextComponentString(TextFormatting.GOLD + action.getPlayer().getName() + TextFormatting.GREEN + " bought your " + TextFormatting.GOLD + buyItemName + TextFormatting.GREEN + " for " + TextFormatting.RED + cost + TextFormatting.GREEN + "!"));
                				}
                				sellerAccount.updatePlayer(sellerAccount.getMoney());

                				UIManager.closeUI(action.getPlayer());
                				action.getPlayer().sendMessage(new TextComponentString(TextFormatting.GREEN + "You bought " + TextFormatting.GOLD + buyItemName + TextFormatting.GREEN + " for " + TextFormatting.RED + cost + TextFormatting.GREEN + "!"));
            					shulker.setInventorySlotContents(slot, new ItemStack(Items.AIR));
                				action.getPlayer().inventoryContainer.detectAndSendChanges();
                				if(shulker.getWorld().getTileEntity(shulker.getPos()) == shulker)
                    			{
                					UIManager.openUIForcefully(action.getPlayer(), getBoxShopMenu(shulker, cost, owner));
                    			}
                			}
        				}
        				else
        				{
            				UIManager.closeUI(action.getPlayer());
            				UIManager.openUIForcefully(action.getPlayer(), getBoxShopMenu(shulker, cost, owner));			
        				}
        			}
        			else
        			{
        				UIManager.closeUI(action.getPlayer());
        			}
        		})
                .build();

		GooeyButton cancelButton = GooeyButton.builder()
                .display(new ItemStack(Blocks.STAINED_GLASS_PANE,1,14))
                .title(TextFormatting.RED + "Click here to cancel!")
                .onClick((action) -> 
        		{
        			if(shulker.getWorld().getTileEntity(shulker.getPos()) == shulker)
        			{
        				UIManager.closeUI(action.getPlayer());
        				UIManager.openUIForcefully(action.getPlayer(), getBoxShopMenu(shulker, cost, owner));
        			}
        			else
        			{
        				UIManager.closeUI(action.getPlayer());
        			}
        		})
                .build();
		
		GooeyButton itemToBuy = GooeyButton.builder()
                .display(buyingItem)
                .build();
		
		
        ChestTemplate template = ChestTemplate.builder(3)
        		.fill(emptySlot)
        		.set(0, 4, itemToBuy)
        		.set(1, 2, confirmButton)
        		.set(1, 6, cancelButton)
                .build();

        String playerName = UsernameCache.getLastKnownUsername(UUID.fromString(owner));
        
        if(playerName == null)
        {
        	playerName = "Someone";
        }
        
        GooeyPage pageBuilder = GooeyPage.builder()
                .title(TextFormatting.DARK_BLUE + playerName + "'s Shop" + TextFormatting.GREEN + " Cost: " + TextFormatting.RED + cost )
                .template(template)
                .build();

        return pageBuilder;
	}
	
	private boolean checkForShulker(Block block)
	{
		boolean isShulker = false;
		
		if(block == Blocks.BLACK_SHULKER_BOX ||
				block == Blocks.BLUE_SHULKER_BOX ||
				block == Blocks.BROWN_SHULKER_BOX ||
				block == Blocks.CYAN_SHULKER_BOX ||
				block == Blocks.GRAY_SHULKER_BOX ||
				block == Blocks.GREEN_SHULKER_BOX ||
				block == Blocks.LIGHT_BLUE_SHULKER_BOX ||
				block == Blocks.LIME_SHULKER_BOX ||
				block == Blocks.MAGENTA_SHULKER_BOX ||
				block == Blocks.ORANGE_SHULKER_BOX ||
				block == Blocks.PINK_SHULKER_BOX ||
				block == Blocks.PURPLE_SHULKER_BOX ||
				block == Blocks.RED_SHULKER_BOX ||
				block == Blocks.SILVER_SHULKER_BOX ||
				block == Blocks.WHITE_SHULKER_BOX ||
				block == Blocks.YELLOW_SHULKER_BOX)
		{
			isShulker = true;
		}
		
		return isShulker;
	}
}
