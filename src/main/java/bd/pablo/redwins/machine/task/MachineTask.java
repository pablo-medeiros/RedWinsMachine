package bd.pablo.redwins.machine.task;

import bd.pablo.redwins.machine.Main;
import bd.pablo.redwins.machine.api.Machine;
import bd.pablo.redwins.machine.api.Manager;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedHashMap;
import java.util.Map;

public class MachineTask extends TaskBase{

    public MachineTask() {
        super(true);
    }

    @Override
    protected void run() {
        Map<Location, ItemStack[]> drops = new LinkedHashMap<>();
        for(Machine machine : Manager.get().machines()){
            try {
                machine.tick(drops);
            }catch (Exception e){
                Main.getInstance().debug().criticalPrintln(machine,"§cErro no funcionamento da maquina (Task-Drop)");
                Main.getInstance().debug().exception(e);
            }
        }
        if(drops.size()>0){
            new BukkitRunnable(){
                @Override
                public void run() {
                    for(Map.Entry<Location, ItemStack[]> entry : drops.entrySet()){
                        for(ItemStack item : entry.getValue()){
                            entry.getKey().getWorld().dropItemNaturally(entry.getKey(),item);
                        }
                    }
                }
            }.runTask(Main.getInstance());
        }
    }

}
