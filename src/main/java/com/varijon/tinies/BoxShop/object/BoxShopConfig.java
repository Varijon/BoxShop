package com.varijon.tinies.BoxShop.object;

import java.util.ArrayList;

public class BoxShopConfig 
{
	ArrayList<ItemConfigMinPrice> lstMinItemPrices;
	
	public BoxShopConfig(ArrayList<ItemConfigMinPrice> lstMinItemPrices) 
	{
		super();
		this.lstMinItemPrices = lstMinItemPrices;
	}

	public ArrayList<ItemConfigMinPrice> getLstMinItemPrices() {
		return lstMinItemPrices;
	}

	public void setLstMinItemPrices(ArrayList<ItemConfigMinPrice> lstMinItemPrices) {
		this.lstMinItemPrices = lstMinItemPrices;
	}
	
	
}
