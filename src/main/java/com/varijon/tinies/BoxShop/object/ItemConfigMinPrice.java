package com.varijon.tinies.BoxShop.object;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

public class ItemConfigMinPrice 
{
	String itemName;
	int minPrice;
	int itemMeta;
	String itemNBT;
	transient ItemStack itemStack;
		
	public ItemConfigMinPrice(String itemName, int minPrice, int itemMeta, String itemNBT) {
		super();
		this.itemName = itemName;
		this.minPrice = minPrice;
		this.itemMeta = itemMeta;
		this.itemNBT = itemNBT;
	}
	public int getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(int minPrice) {
		this.minPrice = minPrice;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public int getItemMeta() {
		return itemMeta;
	}
	public void setItemMeta(int itemMeta) {
		this.itemMeta = itemMeta;
	}
	public String getItemNBT() {
		return itemNBT;
	}
	public void setItemNBT(String itemNBT) {
		this.itemNBT = itemNBT;
	}
	
	public ItemStack createOrGetItemStack()
	{
		if(itemStack != null)
		{
			return itemStack;
		}
		else
		{
			if(itemNBT.equals(""))
			{			
				this.itemStack = new ItemStack(Item.getByNameOrId(itemName), 1, itemMeta);
			}
			else
			{
				try {
					this.itemStack = new ItemStack(Item.getByNameOrId(itemName), 1, itemMeta, JsonToNBT.getTagFromJson((itemNBT)));
				} catch (NBTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return itemStack;
	}
}
