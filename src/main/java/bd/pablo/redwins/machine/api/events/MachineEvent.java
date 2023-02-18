package bd.pablo.redwins.machine.api.events;

import bd.pablo.redwins.machine.api.Machine;
import bd.pablo.redwins.machine.types.MachineType;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class MachineEvent extends BaseEvent {

    protected Machine machine;
    protected MachineType type;

    public MachineEvent(Machine machine, MachineType type) {
        this.machine = machine;
        this.type=type;
    }

    public Machine getMachine() {
        return this.machine;
    }

    public MachineType getType() {
        return type;
    }

}
