A small commandless mod that enables players to sell items via a shulkerbox.

The goal was to keep it simple and userfriendly.

## Requires:

Pixelmon Reforged 8.4.2 (using its economy)

[GooeyLibs 2.2.0](https://github.com/landonjw/GooeyLibs/releases/tag/v1.12.2-2.2.0)

## Usage:

Name a shulkerbox via an anvil or command, name should be "boxshop " followed by a number, like "boxshop 1000".

This will make the shulker a boxshop when placed, allowing other players to right-click it and open a shop version of the contents.

The number is what each itemslot costs, for different prices you'll need multiple boxshops.

If an item is lower than the minimum price set in the config, it will not show up in the shop screen.

The shulker's other functions don't change, it can be moved freely and requires claims to be secure.
For example, after claiming with GriefDefender, you'll likely have to do "/accesstrust public" in your claim.

## Permissions:
>boxshop.ignoreshops

>boxshop.reload -- /bsreload

This permission allows a player to override the shop interface and open the shulker normally while in Creative.

## Notes:
While this mod has checks in place to prevent duping (like the contents changing while someone is buying), please make sure you test this in your environment before adding. 

## Video demo:
https://www.youtube.com/watch?v=7-HhLxeg2J4
