package bd.pablo.redwins.machine.api.menus;

import bd.pablo.redwins.machine.api.Manager;
import bd.pablo.redwins.machine.lib.inventory.IInventory;
import bd.pablo.redwins.machine.lib.inventory.InventoryItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuItem extends InventoryItem {
    private static final Pattern ACTION_PATTERN = Pattern.compile("(?<type>\\w+):(?<value>\\w+)");

    private int x,y;
    private Matcher action;

    public MenuItem(int x, int y) {
        super();
        this.x = x;
        this.y = y;
        setSlot();
    }

    public MenuItem(Material type, int amount) {
        super(type, amount);
    }

    public MenuItem(int type, int x, int y) {
        super(type);
        this.x = x;
        this.y = y;
        setSlot();
    }

    public MenuItem(int type, int amount, short damage) {
        super(type, amount, damage);
    }

    public MenuItem(Material type, int x, int y) {
        super(type);
        this.x = x;
        this.y = y;
        setSlot();
    }

    public MenuItem(Material type, int amount, short damage) {
        super(type, amount, damage);
    }

    public MenuItem(int type, int amount, short damage, Byte data) {
        super(type, amount, damage, data);
    }

    public MenuItem(Material type, int amount, short damage, Byte data) {
        super(type, amount, damage, data);
    }

    public MenuItem(ItemStack stack) throws IllegalArgumentException {
        super(stack);
    }

    public MenuItem(int type, int amount, int x, int y) {
        super(type, amount);
        this.x = x;
        this.y = y;
        setSlot();
    }

    public MenuItem(Material type, int amount, int x, int y) {
        super(type, amount);
        this.x = x;
        this.y = y;
        setSlot();
    }

    public MenuItem(int type, int amount, int damage, int x, int y) {
        super(type, amount, damage);
        this.x = x;
        this.y = y;
        setSlot();
    }

    public MenuItem(int type, int amount, short damage, int x, int y) {
        super(type, amount, damage);
        this.x = x;
        this.y = y;
        setSlot();
    }

    public MenuItem(Material type, int amount, int damage, int x, int y) {
        super(type, amount, damage);
        this.x = x;
        this.y = y;
        setSlot();
    }

    public MenuItem(Material type, int amount, short damage, int x, int y) {
        super(type, amount, damage);
        this.x = x;
        this.y = y;
        setSlot();
    }

    public MenuItem(int type, int amount, short damage, Byte data, int x, int y) {
        super(type, amount, damage, data);
        this.x = x;
        this.y = y;
        setSlot();
    }

    public MenuItem(Material type, int amount, short damage, Byte data, int x, int y) {
        super(type, amount, damage, data);
        this.x = x;
        this.y = y;
        setSlot();
    }

    public MenuItem(ItemStack stack, int x, int y) throws IllegalArgumentException {
        super(stack);
        this.x = x;
        this.y = y;
        setSlot();
    }

    public MenuItem(ConfigurationSection section){
        super(section);
        if(section.contains("position")){
            ConfigurationSection position = section.getConfigurationSection("position");
            this.x = position.getInt("x",1);
            this.y = position.getInt("y",1);
            setSlot();
        }
        action(section.getString("action",""));
    }

    public MenuItem() {
    }

    public MenuItem(int type) {
        super(type);
    }

    public MenuItem(Material type) {
        super(type);
    }

    public int x() {
        return this.x;
    }

    public MenuItem x(int x) {
        this.x = x;
        setSlot();
        return this;
    }

    public int y() {
        return this.y;
    }

    public MenuItem y(int y) {
        this.y = y;
        setSlot();
        return this;
    }

    public String action() {
        if(action == null)return null;
        return this.action.group("type")+':'+this.action.group("value");
    }


    public MenuItem action(String action) {
        if(action==null){
            this.action = null;
            return this;
        }
        Matcher matcher = ACTION_PATTERN.matcher(action.toLowerCase());
        if(!matcher.find()){
            this.action = null;
            return null;
        }
        this.action = matcher;
        return this;
    }

    @Override
    public boolean onClick(Player player, IInventory inventory) {
        if(action !=null){
            switch (action.group("type")){
                case "open":{
                    Menu menu = Manager.get().findMenu(action.group("value").replace("%player_name%",player.getName()).replace("%player_uuid%",player.getUniqueId().toString()));
                    if(menu!=null){
                        player.openInventory(menu.getInventory());
                    }
                    break;
                }
            }
        }
        return true;
    }

    private void setSlot(){
        int x = Math.max(Math.min(this.x,9),1)-1;
        int y = (Math.max(Math.min(this.y,6),1)-1)*9;
        this.slot(y+x);
    }

    @Override
    public MenuItem clone() {
        MenuItem menuItem = new MenuItem(this.build());
        menuItem.action = action;
        menuItem.x = x;
        menuItem.y = y;
        menuItem.meta = meta.clone();
        return menuItem;
    }
}
