package bd.pablo.redwins.machine.api.events;

import bd.pablo.redwins.machine.api.Machine;
import bd.pablo.redwins.machine.types.MachineType;
import org.bukkit.event.HandlerList;

public class MachineOutOfFuelEvent extends MachineEvent{

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public MachineOutOfFuelEvent(Machine machine, MachineType type) {
        super(machine, type);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
