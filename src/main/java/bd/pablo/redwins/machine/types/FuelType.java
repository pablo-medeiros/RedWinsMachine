package bd.pablo.redwins.machine.types;

import bd.pablo.redwins.machine.util.ItemBuilder;
import bd.pablo.redwins.machine.util.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FuelType implements IType{

    private String id;
    private String name;
    private double price;
    private ItemStack displayItem;
    private long ticks;
    private List<String> blockedMachines;

    public FuelType(String id) {
        this.id = id;
    }

    public FuelType(ConfigurationSection section){
        reload(section);
    }

    public void reload(ConfigurationSection section){
        id = section.getName();
        name = section.getString("name","Nome padrão");
        price = section.getDouble("price", 1000000);
        ticks = (long) Math.ceil(section.getDouble("segs", 60)*20);
        blockedMachines = section.contains("blocked")?section.getStringList("blocked"): new ArrayList<>();
        if(section.contains("display")&&section.isConfigurationSection("display"))displayItem = new ItemBuilder(section.getConfigurationSection("display")).build();
        validateFields();
    }

    private void validateFields(){
        if(id==null||id.isEmpty()){
            id = UUID.randomUUID().toString();
        }
        if(name == null || name.isEmpty()){
            name = "Nome padrão";
        }
        if(price < 0){
            price = 1000000;
        }
        if(ticks < 0){
            ticks = 1200;
        }
        if(blockedMachines == null){
            blockedMachines = new ArrayList<>();
        }
        if(displayItem==null||displayItem.getType() == Material.AIR){
            displayItem = new ItemStack(Material.STONE);
        }
        NBTItem displayItemNBT = new NBTItem(displayItem);
        displayItemNBT.getTag().string("fuel-type-id",id);
        displayItem = displayItemNBT.build();
    }

    @Override
    public boolean isSimilar(ItemStack param) {
        NBTItem item = new NBTItem(param);
        item.load();
        return isSimilar(item);
    }

    @Override
    public boolean isSimilar(NBTItem param) {
        return param.getTag().has("fuel-type-id")&&param.getTag().string("fuel-type-id").equalsIgnoreCase(id);
    }

    @Override
    public String typeName() {
        return "fuel";
    }

    public String id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public FuelType name(String name) {
        this.name = name;
        validateFields();
        return this;
    }

    public double price() {
        return this.price;
    }

    public FuelType price(double price) {
        this.price = price;
        validateFields();
        return this;
    }

    public ItemStack displayItem() {
        return this.displayItem;
    }

    public FuelType displayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
        validateFields();
        return this;
    }

    public long ticks() {
        return this.ticks;
    }

    public FuelType ticks(long ticks) {
        this.ticks = ticks;
        validateFields();
        return this;
    }

    public List<String> blockedMachines() {
        return this.blockedMachines;
    }

    public FuelType blockedMachines(List<String> blockedMachines) {
        this.blockedMachines = blockedMachines;
        validateFields();
        return this;
    }
}
