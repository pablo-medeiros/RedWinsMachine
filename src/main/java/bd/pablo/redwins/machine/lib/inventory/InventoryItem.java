package bd.pablo.redwins.machine.lib.inventory;

import bd.pablo.redwins.machine.lib.metadata.CraftMetaManager;
import bd.pablo.redwins.machine.lib.metadata.Meta;
import bd.pablo.redwins.machine.lib.metadata.MetaManager;
import bd.pablo.redwins.machine.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class InventoryItem extends ItemBuilder implements MetaManager {

    private int slot = 0;
    private MetaManager metaManager = new CraftMetaManager();

    public InventoryItem() {
    }

    public InventoryItem(int type) {
        super(type);
    }

    public InventoryItem(Material type) {
        super(type);
    }

    public InventoryItem(int type, int amount) {
        super(type, amount);
    }

    public InventoryItem(Material type, int amount) {
        super(type, amount);
    }

    public InventoryItem(int type, int amount, int damage) {
        super(type, amount, damage);
    }

    public InventoryItem(int type, int amount, short damage) {
        super(type, amount, damage);
    }

    public InventoryItem(Material type, int amount, int damage) {
        super(type, amount, damage);
    }

    public InventoryItem(Material type, int amount, short damage) {
        super(type, amount, damage);
    }

    public InventoryItem(int type, int amount, short damage, Byte data) {
        super(type, amount, damage, data);
    }

    public InventoryItem(Material type, int amount, short damage, Byte data) {
        super(type, amount, damage, data);
    }

    public InventoryItem(ItemStack stack) throws IllegalArgumentException {
        super(stack);
    }

    public InventoryItem(ConfigurationSection section){
        super(section);
        slot = section.getInt("slot",0);
    }

    public InventoryItem slot(int slot) {
        this.slot = slot;
        return this;
    }

    public int slot() {
        return this.slot;
    }

    public abstract boolean onClick(Player player, IInventory inventory);

    @Override
    public void registerMeta(String key, Meta meta) {
        metaManager.registerMeta(key, meta);
    }

    @Override
    public Meta unRegisterMeta(String key, Plugin plugin) {
        return metaManager.unRegisterMeta(key, plugin);
    }

    @Override
    public Meta findMeta(String key) {
        return metaManager.findMeta(key);
    }

    @Override
    public String[] names() {
        return metaManager.names();
    }

    @Override
    public Meta[] metas() {
        return metaManager.metas();
    }

    @Override
    public Map.Entry<String, Meta>[] all() {
        return metaManager.all();
    }

    @Override
    public InventoryItem clone() {
        InventoryItem builder = new InventoryItem(build()){
            @Override
            public boolean onClick(Player player, IInventory inventory) {
                return InventoryItem.this.onClick(player,inventory);
            }
        };
        builder.slot = slot;
        builder.metaManager = metaManager.clone();
        return builder;
    }
}
