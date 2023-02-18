package bd.pablo.redwins.machine.types;

import bd.pablo.redwins.machine.util.ItemBuilder;
import bd.pablo.redwins.machine.util.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class MachineType implements IType{

    private String id;
    private String name;
    private ItemStack displayItem;
    private Material blockType;
    private byte blockData;
    private double price;
    private ItemStack drop;
    private long dropDelay;

    public MachineType(String id) {
        this.id = id;
        validateFields();
    }

    public MachineType(ConfigurationSection section){
        reload(section);
    }

    public void reload(ConfigurationSection section){
        id = section.getName();
        name = section.getString("name","Nome padrão");
        blockType = Material.matchMaterial(section.getString("block.id","STONE"));
        blockData = (byte) section.getInt("block.data",0);
        price = section.getDouble("price", 1000000);
        dropDelay = (long) Math.ceil(section.getDouble("drop.segs", 1)*20);
        if(section.contains("display")&&section.isConfigurationSection("display"))displayItem = new ItemBuilder(section.getConfigurationSection("display")).build();
        if(section.contains("drop")&&section.isConfigurationSection("drop"))drop = new ItemBuilder(section.getConfigurationSection("drop")).build();
        validateFields();
    }

    private void validateFields(){
        if(id==null||id.isEmpty()){
            id = UUID.randomUUID().toString();
        }
        if(name == null || name.isEmpty()){
            name = "Nome padrão";
        }
        if(blockType == null || !blockType.isBlock()){
            blockType = Material.STONE;
        }
        if(blockData < 0){
            blockData = 0;
        }
        if(price < 0){
            price = 1000000;
        }
        if(dropDelay < 0){
            dropDelay = 20;
        }
        if(drop==null||drop.getType() == Material.AIR){
            drop = new ItemStack(Material.COBBLESTONE);
        }
        if(displayItem==null||displayItem.getType() == Material.AIR){
            displayItem = new ItemStack(Material.STONE);
        }
        NBTItem displayItemNBT = new NBTItem(displayItem);
        displayItemNBT.getTag().string("machine-type-id",id);
        displayItem = displayItemNBT.build();
    }

    public String id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public MachineType name(String name) {
        this.name = name;
        validateFields();
        return this;
    }

    public ItemStack displayItem() {
        return this.displayItem != null ? this.displayItem.clone() : null;
    }

    public MachineType displayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
        validateFields();
        return this;
    }

    public Material blockType() {
        return this.blockType;
    }

    public MachineType blockType(Material blockType) {
        this.blockType = blockType;
        validateFields();
        return this;
    }

    public byte blockData() {
        return this.blockData;
    }

    public MachineType blockData(byte blockData) {
        this.blockData = blockData;
        validateFields();
        return this;
    }

    public double price() {
        return this.price;
    }

    public MachineType price(double price) {
        this.price = price;
        validateFields();
        return this;
    }

    public long dropDelay() {
        return this.dropDelay;
    }

    public MachineType dropDelay(long dropDelay) {
        this.dropDelay = dropDelay;
        validateFields();
        return this;
    }

    public ItemStack drop() {
        return this.drop!=null?this.drop.clone():null;
    }

    public int dropAmount(){
        return drop!=null?drop.getAmount() : 0;
    }

    public MachineType drop(ItemStack drop) {
        this.drop = drop;
        validateFields();
        return this;
    }

    @Override
    public String typeName() {
        return "machine";
    }
}
