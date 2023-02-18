package bd.pablo.redwins.machine;

import bd.pablo.redwins.machine.api.Manager;
import bd.pablo.redwins.machine.api.menus.Menu;
import bd.pablo.redwins.machine.api.menus.impl.MenuConfigImpl;
import bd.pablo.redwins.machine.api.menus.impl.MenuFuelMarketImpl;
import bd.pablo.redwins.machine.api.menus.impl.MenuMachineMarketImpl;
import bd.pablo.redwins.machine.api.menus.impl.MenuMarketImpl;
import bd.pablo.redwins.machine.db.Database;
import bd.pablo.redwins.machine.lib.FCommand.FCommandManager;
import bd.pablo.redwins.machine.lib.economy.Economy;
import bd.pablo.redwins.machine.lib.economy.FakeEconomy;
import bd.pablo.redwins.machine.lib.economy.VaultEconomy;
import bd.pablo.redwins.machine.lib.inventory.InventoryListener;
import bd.pablo.redwins.machine.lib.metadata.MetaListener;
import bd.pablo.redwins.machine.listeners.PlayerListener;
import bd.pablo.redwins.machine.listeners.WorldListener;
import bd.pablo.redwins.machine.task.MachineTask;
import bd.pablo.redwins.machine.task.MachineUpdateTask;
import bd.pablo.redwins.machine.types.FuelType;
import bd.pablo.redwins.machine.types.MachineType;
import bd.pablo.redwins.machine.util.Debug;
import bd.pablo.redwins.machine.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Main extends JavaPlugin {

    private final MachineTask machineTask = new MachineTask();
    private final MachineUpdateTask machineUpdateTask = new MachineUpdateTask();
    private Economy economy;
    private Database db;
    private Debug debug;

    @Override
    public void onLoad() {
        saveDefaultConfig();
        db = new Database(getConfig().getConfigurationSection("storage"));
        this.debug = new Debug(getConfig().getBoolean("debug",true));
        try {
            Field field = Manager.class.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null, new Manager());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            try {
                economy = new VaultEconomy();
            }catch (Exception ignored) {
                log("§cVault não encontrado");
                economy=null;
            }
        }
        if(economy==null) {
            economy = new FakeEconomy();
        }
        try {
            db.createTable();
        } catch (SQLException e) {
            e.printStackTrace();
            setEnabled(false);
            return;
        }
        FCommandManager.getInstance().load(this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(),this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(),this);
        Bukkit.getPluginManager().registerEvents(new MetaListener(),this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(),this);
        machineTask.start();
        machineUpdateTask.start();
//        setupDev();
        reload();
        Manager.get().loadMachines();
    }

    public static void log(String... str){
        for(String string : str){
            Bukkit.getConsoleSender().sendMessage(String.format("§b[%s] §l> §r%s",getInstance().getName(), string));
        }
        getInstance().debug.println(str);
    }

    public void reload(){
        reloadConfig();
        List<String> machinesIds = new ArrayList<>();
        if(getConfig().contains("machines")&&getConfig().isConfigurationSection("machines")){
            ConfigurationSection machinesSection = getConfig().getConfigurationSection("machines");
            for(String id : machinesSection.getKeys(false)){
                machinesIds.add(id);
                MachineType type = Manager.get().machineTypeById(id);
                if(type==null){
                    type = new MachineType(machinesSection.getConfigurationSection(id));
                    Manager.get().registerMachineType(type);
                }else {
                    type.reload(machinesSection.getConfigurationSection(id));
                }
            }
        }
        for(MachineType machineType : Manager.get().machineTypes()){
            if(!machinesIds.contains(machineType.id())){
                Manager.get().unRegisterMachineType(machineType.id());
            }
        }
        List<String> fuelsId = new ArrayList<>();
        if(getConfig().contains("fuels")&&getConfig().isConfigurationSection("fuels")){
            ConfigurationSection fuelsSection = getConfig().getConfigurationSection("fuels");
            for(String id : fuelsSection.getKeys(false)){
                fuelsId.add(id);
                FuelType type = Manager.get().fuelTypeById(id);
                if(type==null){
                    type = new FuelType(fuelsSection.getConfigurationSection(id));
                    Manager.get().registerFuelType(type);
                }else {
                    type.reload(fuelsSection.getConfigurationSection(id));
                }
            }
        }
        for(FuelType fuelType : Manager.get().fuelTypes()){
            if(!fuelsId.contains(fuelType.id())){
                Manager.get().unRegisterFuelType(fuelType.id());
            }
        }
        if(getConfig().contains("market")&&getConfig().isConfigurationSection("market")){
            ConfigurationSection marketSection = getConfig().getConfigurationSection("market");
            Menu menu = Manager.get().findMenu("market");
            if(menu!=null) {
                if (menu instanceof MenuConfigImpl) {
                    ((MenuConfigImpl) menu).reload(marketSection);
                }
            }else {
                menu = new MenuMarketImpl(marketSection);
                Manager.get().registerMenu("market",menu);
            }
        }
        if(getConfig().contains("machine_market")&&getConfig().isConfigurationSection("machine_market")){
            ConfigurationSection machineMarketSection = getConfig().getConfigurationSection("machine_market");
            Menu menu = Manager.get().findMenu("machine_market");
            if(menu!=null) {
                if (menu instanceof MenuConfigImpl) {
                    ((MenuConfigImpl) menu).reload(machineMarketSection);
                }
            }else {
                menu = new MenuMachineMarketImpl(machineMarketSection);
                Manager.get().registerMenu("machine_market",menu);
            }
        }
        if(getConfig().contains("fuel_market")&&getConfig().isConfigurationSection("fuel_market")){
            ConfigurationSection machineMarketSection = getConfig().getConfigurationSection("fuel_market");
            Menu menu = Manager.get().findMenu("fuel_market");
            if(menu!=null) {
                if (menu instanceof MenuConfigImpl) {
                    ((MenuConfigImpl) menu).reload(machineMarketSection);
                }
            }else {
                menu = new MenuFuelMarketImpl(machineMarketSection);
                Manager.get().registerMenu("fuel_market",menu);
            }
        }

    }

    @Override
    public void onDisable() {
        for(World world : Bukkit.getWorlds()){
            Manager.get().unLoadMachines(world);
        }
        machineUpdateTask.stop();
        machineUpdateTask.run();
    }

    public void setupDev(){
        MachineType machineType = new MachineType("teste")
                .name("Maquina Teste")
                .price(5000)
                .blockType(Material.GOLD_BLOCK)
                .dropDelay(3*20)
                .displayItem(new ItemBuilder(Material.GOLD_BLOCK).setDisplayName("§aMaquina Teste").build())
                .drop(new ItemBuilder(Material.GOLD_INGOT));
        Manager.get().registerMachineType(machineType);
        FuelType fuelType = new FuelType("teste")
                .name("Combustivel Teste")
                .price(100)
                .ticks(60*20)
                .displayItem(new ItemBuilder(Material.COAL).setDisplayName("§eCombustivel Teste"));
        Manager.get().registerFuelType(fuelType);
    }

    public Database db() {
        return this.db;
    }

    public Debug debug() {
        return this.debug;
    }

    public Economy getEconomy() {
        return economy;
    }

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }


    {
        instance=this;
    }
}
