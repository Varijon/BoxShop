package com.varijon.tinies.BoxShop.util;

import java.util.Optional;
import java.util.UUID;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.economy.IPixelmonBankAccount;
import com.varijon.tinies.BoxShop.config.BoxShopConfigManager;
import com.varijon.tinies.BoxShop.object.ItemConfigMinPrice;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.UsernameCache;

public class Util 
{
	public static GooeyPage getBoxShopMenu(TileEntityShulkerBox shulker, int cost, String owner, MinecraftServer server)
	{
        ChestTemplate.Builder templateBuilder = ChestTemplate.builder(3);
        int count = 0;
		for(int x = 0; x < 3; x++)
		{
			for(int y = 0; y < 9; y++)
			{
				int slotNumber = count;
				if(shulker.getStackInSlot(slotNumber).getItem() != Items.AIR)
				{
					if(cost < calculateMinimumPriceItem(shulker.getStackInSlot(slotNumber)))
					{
				        count++;
						continue;
					}
				}
				GooeyButton itemButton = GooeyButton.builder()
		                .display(shulker.getStackInSlot(slotNumber))
		                .onClick((action) -> 
		        		{
		        			if(action.getButton().getDisplay().getItem() != Items.AIR)
		        			{
		        				if(shulker.getWorld().getTileEntity(shulker.getPos()) == shulker)
                    			{
	                				UIManager.closeUI(action.getPlayer());
	                				UIManager.openUIForcefully(action.getPlayer(), getBuyConfirmMenu(action.getButton().getDisplay(), cost, owner, shulker, slotNumber, server));
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
	
	public static GooeyPage getBuyConfirmMenu(ItemStack buyingItem, int cost, String owner, TileEntityShulkerBox shulker, int slot, MinecraftServer server)
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
                    					UIManager.openUIForcefully(action.getPlayer(), getBoxShopMenu(shulker, cost, owner, server));
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
                					UIManager.openUIForcefully(action.getPlayer(), getBoxShopMenu(shulker, cost, owner, server));
                    			}
                			}
        				}
        				else
        				{
            				UIManager.closeUI(action.getPlayer());
            				UIManager.openUIForcefully(action.getPlayer(), getBoxShopMenu(shulker, cost, owner, server));			
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
        				UIManager.openUIForcefully(action.getPlayer(), getBoxShopMenu(shulker, cost, owner, server));
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
	
	public static boolean checkForShulker(Block block)
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
	
	static int calculateMinimumPriceItem(ItemStack item)
	{
		int finalPrice = 0;
		
		for(ItemConfigMinPrice itemConfig : BoxShopConfigManager.getConfig().getLstMinItemPrices())
		{
			if(itemConfig.getItemName().equals(item.getItem().getRegistryName().toString()))
			{
				if(item.hasTagCompound())
				{
					if(!item.getTagCompound().toString().equals(itemConfig.getItemNBT()))
					{
						continue;
					}
					if(itemConfig.getItemMeta() == -1)
					{
						finalPrice += itemConfig.getMinPrice();
					}
					if(itemConfig.getItemMeta() == item.getMetadata())
					{
						finalPrice += itemConfig.getMinPrice();
					}
				}
				else
				{
					if(itemConfig.getItemMeta() == -1)
					{
						finalPrice += itemConfig.getMinPrice();
					}
					if(itemConfig.getItemMeta() == item.getMetadata())
					{
						finalPrice += itemConfig.getMinPrice();
					}
				}
			}
		}
		return finalPrice;
	}
}
