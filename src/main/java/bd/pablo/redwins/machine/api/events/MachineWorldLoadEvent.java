package bd.pablo.redwins.machine.api.events;

import bd.pablo.redwins.machine.api.Machine;
import org.bukkit.World;
import org.bukkit.event.HandlerList;

import java.util.List;

public class MachineWorldLoadEvent extends BaseEvent{

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final World world;
    private List<Machine> machines;

    public MachineWorldLoadEvent(World world, List<Machine> machines) {
        this.world = world;
        this.machines = machines;
    }

    public World getWorld() {
        return world;
    }

    public List<Machine> getMachines() {
        return machines;
    }

    public void setMachines(List<Machine> machines) {
        this.machines = machines;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
