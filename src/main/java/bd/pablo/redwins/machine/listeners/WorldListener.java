package bd.pablo.redwins.machine.listeners;

import bd.pablo.redwins.machine.Main;
import bd.pablo.redwins.machine.api.Manager;
import bd.pablo.redwins.machine.api.events.MachineBreakEvent;
import bd.pablo.redwins.machine.api.events.MachinePlacedEvent;
import bd.pablo.redwins.machine.lib.metadata.Meta;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;

public class WorldListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onWorldUnload(WorldUnloadEvent e){
        e.setCancelled(!Manager.get().unLoadMachines(e.getWorld()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onWorldUnload(WorldLoadEvent e){
        Manager.get().loadMachines();
    }

    @EventHandler
    public void onMachinePlaced(MachinePlacedEvent e){
        ArmorStand stand = e.getMachine().location().getWorld().spawn(e.getMachine().location(), ArmorStand.class);
        // Aqui tu define a cabeça
        e.getMachine().meta().registerMeta("skulls", new Meta() {
            @Override
            public Object value() {
                return stand;
            }

            @Override
            public boolean isPersistent() {
                return true;
            }

            @Override
            public Plugin getPlugin() {
                return Main.getInstance();
            }
        });
    }

}
