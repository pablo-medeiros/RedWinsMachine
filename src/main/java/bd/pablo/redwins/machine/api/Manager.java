package bd.pablo.redwins.machine.api;

import bd.pablo.redwins.machine.Main;
import bd.pablo.redwins.machine.api.events.MachineWorldLoadEvent;
import bd.pablo.redwins.machine.api.events.MachineWorldUnLoadEvent;
import bd.pablo.redwins.machine.api.menus.Menu;
import bd.pablo.redwins.machine.types.FuelType;
import bd.pablo.redwins.machine.types.MachineType;
import bd.pablo.redwins.machine.util.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Manager {

    private static Manager instance;

    private final List<MachineType> machineTypes = new ArrayList<>();
    private final List<FuelType> fuelTypes = new ArrayList<>();
    private final List<Machine> machines = new ArrayList<>();
    private final Map<String, Menu> menus = new HashMap<>();

    public MachineType machineTypeById(@Nonnull String id){
        for(MachineType type : machineTypes()){
            if(type.id().equalsIgnoreCase(id))return type;
        }
        return null;
    }


    public MachineType machineTypeByName(@Nonnull String name){
        for(MachineType type : machineTypes()){
            if(type.name().equalsIgnoreCase(name))return type;
        }
        return null;
    }

    public MachineType machineTypeByItem(@Nonnull ItemStack item){
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.load();
        for(MachineType type : machineTypes()){
            if(type.isSimilar(nbtItem))return type;
        }
        return null;
    }

    public FuelType fuelTypeById(@Nonnull String id){
        for(FuelType type : fuelTypes()){
            if(type.id().equalsIgnoreCase(id))return type;
        }
        return null;
    }

    public FuelType fuelTypeByName(@Nonnull String name){
        for(FuelType type : fuelTypes()){
            if(type.name().equalsIgnoreCase(name))return type;
        }
        return null;
    }

    public FuelType fuelTypeByItem(@Nonnull ItemStack item){
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.load();
        for(FuelType type : fuelTypes()){
            if(type.isSimilar(nbtItem))return type;
        }
        return null;
    }

    public void registerMachineType(MachineType type){
        machineTypes.add(type);
    }

    public void unRegisterMachineType(String id) {
        this.machineTypes.removeIf(m->m.id().equals(id));
    }


    public void registerFuelType(FuelType type){
        fuelTypes.add(type);
    }

    public void unRegisterFuelType(String id) {
        this.fuelTypes.removeIf(f->f.id().equals(id));
    }

    public void registerMenu(String name, Menu menu){
        this.menus.put(name.toLowerCase(), menu);
    }

    public void unRegisterMenu(String name){
        this.menus.remove(name);
    }

    public Menu findMenu(String name){
        return this.menus.get(name.toLowerCase());
    }

    public Menu findMenu(Inventory inventory){
        if(inventory instanceof Menu)return (Menu) inventory;
        return null;
    }

    public void registerMachine(Machine machine){
        try {
            Main.getInstance().db().insertOrUpdate(machine);
            this.machines.add(machine);
        } catch (SQLException e) {
            Main.getInstance().debug().criticalPrintln(machine,"§cErro ao inserir ou atualizar no banco de dados");
            Main.getInstance().debug().exception(e);
        }
    }

    public boolean unRegisterMachine(Machine machine){
        try {
            Main.getInstance().db().delete(machine.id());
            machine.destroy();
            this.machines.remove(machine);
            return true;
        } catch (Exception e) {
            Main.getInstance().debug().criticalPrintln(machine,"§cErro ao deletar a maquina");
            Main.getInstance().debug().exception(e);
        }
        return false;
    }

    public Machine[] getNearbyMachines(Location location, int x, int y, int z){
        int X = location.getBlockX();
        int Y = location.getBlockY();
        int Z = location.getBlockZ();
        return machines.stream().filter(machine -> {
            if(Math.abs(machine.location().getBlockX() - X) > x)return false;
            if(Math.abs(machine.location().getBlockY() - Y) > y)return false;
            return Math.abs(machine.location().getBlockZ() - Z) <= z;
        }).toArray(Machine[]::new);
    }

    public Machine[] getNearbyMachines(Location location, Function<Machine, Boolean> filter, int x, int y, int z){
        int X = location.getBlockX();
        int Y = location.getBlockY();
        int Z = location.getBlockZ();
        return machines.stream().filter(machine -> {
            if(Math.abs(machine.location().getBlockX() - X) > x)return false;
            if(Math.abs(machine.location().getBlockY() - Y) > y)return false;
            return Math.abs(machine.location().getBlockZ() - Z) <= z && filter.apply(machine);
        }).toArray(Machine[]::new);
    }

    public Machine machine(Block block){
        if(block.hasMetadata("machine")){
            try{
                return (Machine) block.getMetadata("machine").get(0);
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void loadMachines(){
        Map<World,List<Machine>> machines = new HashMap<>();
        AtomicInteger size = new AtomicInteger();
        for(World world : Bukkit.getWorlds()){
            if(world.hasMetadata("machine-load"))continue;
            new Thread(()->{
                try {
                    Thread.sleep(50);
                    Main.getInstance().db().select(world,(result)->{
                        List<Machine> worldMachine = new ArrayList<>();
                        while(true) {
                            try {
                                if (!result.next()) break;
                                Machine machine = new Machine(result);
                                worldMachine.add(machine);
                            } catch (SQLException e) {
                                Main.getInstance().debug().criticalPrintln(new String[]{"§cErro ao carregar as maquinas do mundo:","  §4"+world.getName()});
                                Main.getInstance().debug().exception(e);
                            }
                        }
                        machines.put(world, worldMachine);
                        size.getAndDecrement();
                    });
                } catch (Exception e) {
                    Main.getInstance().debug().criticalPrintln(new String[]{"§cErro ao carregar as maquinas do mundo:","  §4"+world.getName()});
                    Main.getInstance().debug().exception(e);
                }
            }).start();
            size.getAndIncrement();
            world.setMetadata("machine-load",new FixedMetadataValue(Main.getInstance(),true));
        }
        new BukkitRunnable(){

            @Override
            public void run() {
                if(size.get()==0){
                    cancel();
                    for(Map.Entry<World, List<Machine>> entry : machines.entrySet()){
                        MachineWorldLoadEvent machineWorldLoadEvent = new MachineWorldLoadEvent(entry.getKey(), entry.getValue());
                        machineWorldLoadEvent.call();
                        if(!machineWorldLoadEvent.isCancelled()){
                            for (Machine machine : machineWorldLoadEvent.getMachines()) {
                                try {
                                    Method method = Machine.class.getDeclaredMethod("configure");
                                    method.setAccessible(true);
                                    method.invoke(machine);
                                }catch (Exception e){
                                    Main.getInstance().debug().criticalPrintln(machine, "§cErro ao carregar a maquina");
                                    Main.getInstance().debug().exception(e);
                                }
                            }
                            Manager.this.machines.addAll(entry.getValue());
                            Main.log("§aMaquinas carregadas: §f"+entry.getKey().getName());
                        }else {
                            entry.getKey().removeMetadata("machine-load",Main.getInstance());
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(),40,40);
    }

    public boolean unLoadMachines(World world){
        if(world.hasMetadata("machine-load"))world.removeMetadata("machine-load",Main.getInstance());
        List<Machine> machines = new ArrayList<>();
        for(Machine machine : machines()){
            if(machine.location().getWorld().equals(world)){
                machines.add(machine);
            }
        }
        MachineWorldUnLoadEvent machineWorldUnLoadEvent = new MachineWorldUnLoadEvent(world, machines);
        machineWorldUnLoadEvent.call();
        if(!machineWorldUnLoadEvent.isCancelled()) {
            for (Machine machine : machineWorldUnLoadEvent.getMachines()) {
                this.machines.remove(machine);
                machine.disable();
            }
            Main.log("§aMaquinas descarregadas: §f"+world.getName());
            return true;
        }else {
            world.setMetadata("machine-load",new FixedMetadataValue(Main.getInstance(),true));
            return false;
        }
    }

    public Collection<MachineType> machineTypes() {
        return new ArrayList<>(this.machineTypes);
    }

    public Collection<FuelType> fuelTypes() {
        return new ArrayList<>(this.fuelTypes);
    }

    public Collection<Machine> machines() {
        return new ArrayList<>(this.machines);
    }


    public static Manager get(){
        return instance;
    }

}
