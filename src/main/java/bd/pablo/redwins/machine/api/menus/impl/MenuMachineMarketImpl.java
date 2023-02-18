package bd.pablo.redwins.machine.api.menus.impl;

import bd.pablo.redwins.machine.Main;
import bd.pablo.redwins.machine.api.Manager;
import bd.pablo.redwins.machine.api.events.PlayerPurchaseEvent;
import bd.pablo.redwins.machine.lib.inventory.FillInteract;
import bd.pablo.redwins.machine.lib.inventory.IInventory;
import bd.pablo.redwins.machine.lib.inventory.InventoryItem;
import bd.pablo.redwins.machine.types.MachineType;
import bd.pablo.redwins.machine.types.MachineType;
import bd.pablo.redwins.machine.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class MenuMachineMarketImpl extends MenuConfigImpl {

    private List<String> displayLore = new ArrayList<>();

    public MenuMachineMarketImpl(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void inReload(ConfigurationSection section) {
        super.inReload(section);
        if(section.contains("display-lore")&&section.isList("display-lore")){
            displayLore = section.getStringList("display-lore").stream().map(s->ChatColor.translateAlternateColorCodes('&',s)).collect(Collectors.toList());
        }else {
            displayLore.clear();
        }
        FillInteract fill = new FillInteract(this, FillInteract.FillType.getOrDefault(section.getString("place_fill", "none"), FillInteract.FillType.NONE));
        Iterator<MachineType> types = Manager.get().machineTypes().iterator();
        while(fill.hasNext()&&types.hasNext()){
            MachineType type = types.next();
            if(type.price()>0) {
                MachineItem inventoryItem = new MachineItem(type.displayItem());
                inventoryItem.machineType = type;
                inventoryItem.addLore(displayLore);
                fill.next(this, inventoryItem);
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent e, Player player, InventoryItem it) {
        if(!e.isLeftClick()&&!e.isRightClick())return;
        e.setCancelled(true);
        if(it instanceof MachineItem){
            MachineItem machineItem = (MachineItem) it;
            PlayerPurchaseEvent playerPurchaseEvent = new PlayerPurchaseEvent(player,machineItem.machineType,e.isLeftClick()?1:64);
            playerPurchaseEvent.call();
            if(!playerPurchaseEvent.isCancelled()){
                if(Main.getInstance().getEconomy().contains(player,playerPurchaseEvent.getTotal())) {
                    Main.getInstance().getEconomy().remove(player, playerPurchaseEvent.getTotal());
                    ItemStack item = playerPurchaseEvent.getType().displayItem();
                    int max = item.getMaxStackSize();
                    int amount = playerPurchaseEvent.getAmount();
                    ItemStack[] items = new ItemStack[(amount / max)+(amount%max>0?1:0)];
                    for(int i = 0; i < items.length; i++){
                        items[i] = item.clone();
                        items[i].setAmount(Math.min(amount,max));
                        amount-=items[i].getAmount();
                    }
                    player.getInventory().addItem(items).values().forEach(i->{
                        player.getWorld().dropItemNaturally(player.getLocation(), i);
                    });
                }else {
                    player.sendMessage(String.format("§cSaldo insuficiente para adiquirir a maquina. $ %.2f",playerPurchaseEvent.getTotal()));
                }
            }
        }
    }

    private static class MachineItem extends InventoryItem {

        public MachineType machineType;

        public MachineItem() {
        }

        public MachineItem(int type) {
            super(type);
        }

        public MachineItem(Material type) {
            super(type);
        }

        public MachineItem(int type, int amount) {
            super(type, amount);
        }

        public MachineItem(Material type, int amount) {
            super(type, amount);
        }

        public MachineItem(int type, int amount, int damage) {
            super(type, amount, damage);
        }

        public MachineItem(int type, int amount, short damage) {
            super(type, amount, damage);
        }

        public MachineItem(Material type, int amount, int damage) {
            super(type, amount, damage);
        }

        public MachineItem(Material type, int amount, short damage) {
            super(type, amount, damage);
        }

        public MachineItem(int type, int amount, short damage, Byte data) {
            super(type, amount, damage, data);
        }

        public MachineItem(Material type, int amount, short damage, Byte data) {
            super(type, amount, damage, data);
        }

        public MachineItem(ItemStack stack) throws IllegalArgumentException {
            super(stack);
        }

        public MachineItem(ConfigurationSection section) {
            super(section);
        }

        @Override
        public boolean onClick(Player player, IInventory inventory) {
            return false;
        }
    }
}
