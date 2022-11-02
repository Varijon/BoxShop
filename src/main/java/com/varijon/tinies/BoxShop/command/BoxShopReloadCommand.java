package com.varijon.tinies.BoxShop.command;

import java.util.ArrayList;
import java.util.List;

import com.varijon.tinies.BoxShop.config.BoxShopConfigManager;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class BoxShopReloadCommand extends CommandBase {

	private List aliases;
	
	public BoxShopReloadCommand()
	{
	   this.aliases = new ArrayList();
	   this.aliases.add("bsreload");
	}
	
	@Override
	public int compareTo(ICommand arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "boxshopreload";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "boxshopreload";
	}

	@Override
	public List<String> getAliases() {
		// TODO Auto-generated method stub
		return this.aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		if(sender.canUseCommand(4, "boxshop.reload"))
		{
			if(BoxShopConfigManager.loadConfiguration())
			{
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Configuration reloaded succesfully"));						
			}
			else
			{
				sender.sendMessage(new TextComponentString(TextFormatting.RED + "Failed to reload configuration"));
			}
			return;
		}
		else
		{
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "You don't have permission to use this command"));
			return;
		}
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) 
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return false;
	}
	


}
