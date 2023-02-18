package bd.pablo.redwins.machine.lib.inventory;

import bd.pablo.redwins.machine.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public abstract class CraftInventory implements IInventory,Cloneable {

    private Inventory inventory;
    protected final List<InventoryItem> items = new ArrayList<>();
    private boolean noInteract;
    private boolean reloading;

    public CraftInventory(String name, int lines) {
        this(name,lines,true);
    }

    public CraftInventory(String name, int lines, boolean noInteract) {
        this.inventory = Bukkit.createInventory(this, lines*9, ChatColor.translateAlternateColorCodes('&',name));
        this.noInteract = noInteract;
    }

    public void change(String name,int lines){
        Inventory newInventory = Bukkit.createInventory(this, lines*9, ChatColor.translateAlternateColorCodes('&',name));
        for(InventoryItem item : new ArrayList<>(items)){
            if(item.slot()<newInventory.getSize())
                newInventory.setItem(item.slot(), item.build());
        }
        for(HumanEntity humanEntity : new ArrayList<>(this.inventory.getViewers())){
            humanEntity.openInventory(newInventory);
        }
        this.inventory = newInventory;
    }

    public void onOpen(InventoryOpenEvent e){
        if(reloading){
            reload();
        }
    }
    public void onClose(InventoryCloseEvent e){}
    public void onClick(InventoryClickEvent e){}

    public void reload(){
        if(inventory.getViewers().size()==0){
            reloading=true;
            return;
        }
        inventory.clear();
        for(InventoryItem item : new ArrayList<>(items)){
            if(item.slot()<inventory.getSize()) {
                inventory.setItem(item.slot(), item.finish());
            }
        }
    }

    public void update(InventoryItem i){
        if(i.slot() < inventory.getSize()){
            inventory.setItem(i.slot(),i.finish());
        }
    }

    public void clear(){
        this.items.clear();
        this.inventory.clear();
    }

    public void remove(int slot){
        this.items.removeIf(item->item.slot()==slot);
    }

    public InventoryItem item(int slot){
        for(InventoryItem item : items){
            if(item.slot() == slot)return item;
        }
        return null;
    }
    public InventoryItem item(int slot, Material material){
        return item(slot,material, 1,(p,i)->noInteract);
    }
    public InventoryItem item(int slot, Material material, int amount){
        return item(slot,material,amount,0,(p,i)->noInteract);
    }
    public InventoryItem item(int slot, Material material, int amount, int data){
        return item(slot, material, amount, data,(p,i)->noInteract);
    }

    public InventoryItem item(int slot, BiFunction<Player, IInventory, Boolean> fn){
        return item(slot,Material.AIR,1,0,fn);
    }
    public InventoryItem item(int slot, Material material, BiFunction<Player, IInventory, Boolean> fn){
        return item(slot,material, 1,0,fn);
    }
    public InventoryItem item(int slot, Material material, int amount, BiFunction<Player, IInventory, Boolean> fn){
        return item(slot,material,amount,0,fn);
    }
    public InventoryItem item(int slot, Material material, int amount, int data, BiFunction<Player, IInventory, Boolean> fn){
        remove(slot);
        InventoryItem inventoryItem = new InventoryItem(material, amount, data) {
            @Override
            public boolean onClick(Player player, IInventory inventory) {
                return fn.apply(player,inventory);
            }
        }.slot(slot);
        this.items.add(inventoryItem);
        return inventoryItem;
    }

    public CraftInventory item(InventoryItem item){
        remove(item.slot());
        this.items.add(item);
        return  this;
    }

    public List<InventoryItem> items() {
        return new ArrayList<>(this.items);
    }

    public boolean noInteract() {
        return this.noInteract;
    }

    public CraftInventory noInteract(boolean noInteract) {
        this.noInteract = noInteract;
        return this;
    }

    @Override
    public CraftInventory clone() throws CloneNotSupportedException {
        return (CraftInventory) super.clone();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void close() {
        for(HumanEntity entity : new ArrayList<>(inventory.getViewers())){
            entity.closeInventory();
        }
    }
}
