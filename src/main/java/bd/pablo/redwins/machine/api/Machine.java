package bd.pablo.redwins.machine.api;

import bd.pablo.redwins.machine.Main;
import bd.pablo.redwins.machine.api.events.MachineOutOfFuelEvent;
import bd.pablo.redwins.machine.lib.metadata.CraftMetaManager;
import bd.pablo.redwins.machine.lib.metadata.Meta;
import bd.pablo.redwins.machine.lib.metadata.MetaManager;
import bd.pablo.redwins.machine.types.MachineType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class Machine implements MetadataValue {

    private int id;
    @Deprecated
    public int temp = 0;

    private MachineType type;
    private Location location;
    private Location dropLocation;
    private UUID ownerUUID;
    private String ownerName;
    private long stack;
    private long fuel;
    private long tick;
    private boolean enable;
    private MetaManager meta = new CraftMetaManager(){
        @Override
        public void registerMeta(String key, Meta meta) {
            super.registerMeta(key, meta);
            if(meta.isPersistent()){
                Machine.this.temp = 3;
            }
        }

        @Override
        public Meta unRegisterMeta(String key, Plugin plugin) {
            Meta meta = super.unRegisterMeta(key, plugin);
            if(meta!=null&&meta.isPersistent()){
                Machine.this.temp = 3;
            }
            return meta;
        }
    };
    private MachinePanel panel;

    public Machine(MachineType type, Location location, UUID ownerUUID, String ownerName,long stack) {
        this.id = -1;
        this.type = type;
        this.location = location;
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.stack = stack;
        this.configure();
    }

    public Machine(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("id");
        this.type = Manager.get().machineTypeById(resultSet.getString("type"));
        this.ownerUUID = UUID.fromString(resultSet.getString("owner_uuid"));
        this.ownerName = resultSet.getString("owner_name");
        this.fuel = resultSet.getLong("fuel");
        this.stack = resultSet.getLong("stack");
        String[] l = resultSet.getString("location").split("@");
        World world = Bukkit.getWorld(l[0]);
        this.location = new Location(world,Double.parseDouble(l[1]),Double.parseDouble(l[2]),Double.parseDouble(l[3]));
    }

    private void configure(){
        this.dropLocation = this.location.clone().add(0.5,1,0.5);
        Block block = this.location.getBlock();
        block.setMetadata("machine-id",new FixedMetadataValue(Main.getInstance(),this.id));
        block.setMetadata("machine-type",new FixedMetadataValue(Main.getInstance(),this.type.id()));
        block.setMetadata("machine",this);
    }

    public void destroy(){
        temp = 0;
        Block block = this.location.getBlock();
        block.setType(Material.AIR);
        this.id = -2;
        this.type = null;
        this.location = null;
        this.ownerUUID = null;
        this.ownerName = null;
        this.dropLocation = null;
        this.tick = 0;
        this.fuel = 0;
        this.stack = 0;
        disable();
    }

    public void disable(){
        Block block = this.location.getBlock();
        block.removeMetadata("machine-id",Main.getInstance());
        block.removeMetadata("machine-type",Main.getInstance());
        block.removeMetadata("machine",Main.getInstance());
        this.enable=false;
        if(panel!=null){
            panel.close();
        }
    }

    public void tick(Map<Location, ItemStack[]> drops){
        if(!enable)return;
        if(fuel<=0){
            this.enable(false);
            return;
        }
        if(tick+1 == type.dropDelay()){
            long total = type.dropAmount() * stack;
            int max = Integer.MAX_VALUE - 1;
            ItemStack[] items = new ItemStack[(int) (total / max)+((total % max)>0?1:0)];
            for(int i = 0; i < items.length; i++){
                items[i] = type.drop();
                items[i].setAmount(total > max ? max : (int) total);
                total-=items[i].getAmount();
            }
            drops.put(dropLocation,items);
            tick = 0;
        }else{
            tick++;
        }
        fuel--;
        temp = 1;
        if(fuel <= 0){
            MachineOutOfFuelEvent machineOutOfFuelEvent = new MachineOutOfFuelEvent(this,type);
            machineOutOfFuelEvent.call();
            if(machineOutOfFuelEvent.isCancelled())fuel = 1;
        }
        if(panel!=null){
            panel.reload(MachinePanel.ReloadType.FUEL);
        }
    }

    public int id() {
        return this.id;
    }

    public MachineType type() {
        return this.type;
    }

    public Location location() {
        return this.location;
    }

    public Location dropLocation() {
        return this.dropLocation;
    }

    public UUID ownerUUID() {
        return this.ownerUUID;
    }

    public String ownerName() {
        return this.ownerName;
    }

    public long fuel() {
        return this.fuel;
    }

    public Machine fuel(long fuel) {
        this.fuel = fuel;
        this.temp = 1;
        if(panel!=null){
            panel.reload(MachinePanel.ReloadType.FUEL);
        }
        return this;
    }
    public Machine incrementFuel(long fuel){
        this.fuel += fuel;
        this.temp = 1;
        if(panel!=null){
            panel.reload(MachinePanel.ReloadType.FUEL);
        }
        return this;
    }

    public Machine decrementFuel(long fuel){
        this.fuel -= fuel;
        this.temp = 1;
        if(panel!=null){
            panel.reload(MachinePanel.ReloadType.FUEL);
        }
        return this;
    }

    public long stack() {
        return this.stack;
    }

    public Machine stack(long stack) {
        this.stack = stack;
        this.temp = 2;
        if(panel!=null){
            panel.reload(MachinePanel.ReloadType.STACK);
        }
        return this;
    }

    public Machine incrementStack(long stack) {
        this.stack += stack;
        this.temp = 2;
        if(panel!=null){
            panel.reload(MachinePanel.ReloadType.STACK);
        }
        return this;
    }

    public Machine decrementStack(long stack) {
        this.stack -= stack;
        this.temp = 2;
        if(panel!=null){
            panel.reload(MachinePanel.ReloadType.STACK);
        }
        return this;
    }
    public boolean enable() {
        return this.enable;
    }

    public Machine enable(boolean b) {
        if(b && fuel<=0)return this;
        this.enable = b;
        if(panel!=null){
            panel.reload(MachinePanel.ReloadType.ENABLE);
        }
        return this;
    }

    public MetaManager meta(){
        return meta;
    }

    public MachinePanel panel() {
        if (this.panel == null) {
            this.panel = new MachinePanel(null){
                @Override
                public Machine machine() {
                    return Machine.this;
                }
            };
        }
        return this.panel;
    }

    @Override
    public Object value() {
        return this;
    }

    @Override
    public int asInt() {
        return id;
    }

    @Override
    public float asFloat() {
        return (float) type.price();
    }

    @Override
    public double asDouble() {
        return type.price();
    }

    @Override
    public long asLong() {
        return fuel;
    }

    @Override
    public short asShort() {
        return (short) id;
    }

    @Override
    public byte asByte() {
        return Byte.parseByte(type.id());
    }

    @Override
    public boolean asBoolean() {
        return id!=-2;
    }

    @Override
    public String asString() {
        return type.name();
    }

    @Override
    public Plugin getOwningPlugin() {
        return Main.getInstance();
    }

    @Override
    public void invalidate() {

    }

}
