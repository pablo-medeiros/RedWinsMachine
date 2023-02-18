package bd.pablo.redwins.machine.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ItemBuilder  extends ItemStack{

    protected ItemMeta meta;
    protected NBTItem.NBTTag tag;

    public ItemBuilder() {
        super();
        this.meta=super.getItemMeta();
        this.tag = new NBTItem(this).getTag();
    }

    public ItemBuilder(int type) {
        this(type,1, (short) 0);
    }

    public ItemBuilder(Material type) {
        this(type,1, (short) 0);
    }

    public ItemBuilder(int type, int amount) {
        this(type, amount, (short) 0);
    }

    public ItemBuilder(Material type, int amount) {
        this(type, amount, (short) 0);
    }

    public ItemBuilder(int type, int amount, int damage) {
        this(type, amount, (short) damage);
    }
    public ItemBuilder(int type, int amount, short damage) {
        super(type, amount, damage);
        this.meta=super.getItemMeta();
        this.tag = new NBTItem(this).getTag();
    }

    public ItemBuilder(Material type, int amount, int damage) {
        this(type, amount, (short) damage);
    }

    public ItemBuilder(Material type, int amount, short damage) {
        super(type, amount, damage);
        this.meta=super.getItemMeta();
        this.tag = new NBTItem(this).getTag();
    }

    public ItemBuilder(int type, int amount, short damage, Byte data) {
        super(type, amount, damage, data);
        this.meta=super.getItemMeta();
        this.tag = new NBTItem(this).getTag();
    }

    public ItemBuilder(Material type, int amount, short damage, Byte data) {
        super(type, amount, damage, data);
        this.meta=super.getItemMeta();
        this.tag = new NBTItem(this).getTag();
    }

    public ItemBuilder(ItemStack stack) throws IllegalArgumentException {
        super(stack);
        this.meta=super.getItemMeta();
        this.tag = new NBTItem(this).getTag();
    }

    public ItemBuilder(ConfigurationSection section) {
        super(section.isString("id")?Material.matchMaterial(section.getString("id")):Material.getMaterial(section.getInt("id")),section.getInt("amount",1), (short) section.getInt("data",0));
        this.meta=super.getItemMeta();
        this.tag = new NBTItem(this).getTag();
        if(section.contains("name"))setDisplayName(section.getString("name"));
        if(section.contains("lore"))setLore(section.getStringList("lore"));
        if(section.getBoolean("glow",false)){
            addItemFlags(ItemFlag.HIDE_ENCHANTS).addEnchant(Enchantment.DURABILITY,1,true);
        }
    }

    public NBTItem.NBTTag nbt(){
        return this.tag;
    }

    public ItemStack build(){
        if(meta!=null)setItemMeta(meta.clone());
        if(!tag.isEmpty()){
            NBTItem nbtItem = new NBTItem(this);
            nbtItem.load();
            nbtItem.getTag().concat(tag);
            return nbtItem.build();
        }
        return this;
    }

    public ItemStack finish(){
        if(meta!=null)setItemMeta(meta);
        if(!tag.isEmpty()){
            NBTItem nbtItem = new NBTItem(this);
            nbtItem.load();
            nbtItem.getTag().concat(tag);
            return nbtItem.build();
        }
        return this;
    }

    public ItemBuilder replaceAllTexts(Map<String,Object> obj) {
        return replaceAllTexts((input)->{
            for(Map.Entry<String, Object> entry : obj.entrySet()){
                input = input.replace(entry.getKey(),entry.getValue().toString());
            }
            return input;
        });
    }

    public ItemBuilder replaceAllTexts(Function<String,String> fn) {
        if(hasDisplayName())setDisplayName(fn.apply(getDisplayName()));
        if(hasLore())setLore(getLore().stream().map(fn).collect(Collectors.toList()));
        if(hasOwner())setOwner(fn.apply(getOwner()));
        return this;
    }

    public boolean hasDisplayName() {
        return meta.hasDisplayName();
    }

    public String getDisplayName() {
        return meta.getDisplayName() == null ? "" : meta.getDisplayName();
    }

    public ItemBuilder setDisplayName(String name) {
        meta.setDisplayName(name.replace("&","§"));
        return this;
    }

    public boolean hasLore() {
        return meta.hasLore();
    }

    public List<String> getLore() {
        if(meta.getLore()==null)return new ArrayList<>();
        return meta.getLore();
    }

    public ItemBuilder setLore(String... strings){
        return this.setLore(Arrays.asList(strings));
    }

    public ItemBuilder addLore(String... strings){
        return this.addLore(Arrays.asList(strings));
    }

    public ItemBuilder addLore(Collection<String> strings){
        List<String> lore = getLore();
        lore.addAll(strings);
        return this.setLore(lore);
    }

    public ItemBuilder setLore(List<String> lore) {
        meta.setLore(lore.stream().map(s->s.replace("&","§")).collect(Collectors.toList()));
        return this;
    }

    public boolean hasEnchants() {
        return meta.hasEnchants();
    }

    public boolean hasEnchant(Enchantment ench) {
        return meta.hasEnchant(ench);
    }

    public int getEnchantLevel(Enchantment ench) {
        return meta.getEnchantLevel(ench);
    }

    public Map<Enchantment, Integer> getEnchants() {
        return meta.getEnchants();
    }

    public boolean addEnchant(Enchantment ench, int level, boolean ignoreLevelRestriction) {
        return meta.addEnchant(ench,level,ignoreLevelRestriction);
    }

    public boolean removeEnchant(Enchantment ench) {
        return meta.removeEnchant(ench);
    }

    public boolean hasConflictingEnchant(Enchantment ench) {
        return meta.hasConflictingEnchant(ench);
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
        meta.addItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder removeItemFlags(ItemFlag... itemFlags) {
        meta.removeItemFlags(itemFlags);
        return this;
    }

    public Set<ItemFlag> getItemFlags() {
        return meta.getItemFlags();
    }

    public boolean hasItemFlag(ItemFlag flag) {
        return meta.hasItemFlag(flag);
    }


    // Skull methods

    public ItemBuilder setOwner(String owner){
        if(!(meta instanceof SkullMeta))return this;
        SkullMeta sMeta = (SkullMeta) meta;
        sMeta.setOwner(owner!=null&&!owner.isEmpty()?owner:null);
        return this;
    }

    public boolean hasOwner(){
        if(!(meta instanceof SkullMeta))return false;
        return ((SkullMeta) meta).hasOwner();
    }

    public String getOwner(){
        if(!(meta instanceof SkullMeta))return "";
        String owner = ((SkullMeta) meta).getOwner();
        return owner == null ? "" : owner;
    }


    @Override
    public ItemMeta getItemMeta() {
        return meta;
    }

    @Override
    public boolean setItemMeta(ItemMeta itemMeta) {
        this.meta=itemMeta;
        return super.setItemMeta(itemMeta);
    }

    @Override
    public ItemBuilder clone() {
        return new ItemBuilder(this.build());
    }
}
