package bd.pablo.redwins.machine.api.events;

import bd.pablo.redwins.machine.api.Machine;
import bd.pablo.redwins.machine.api.MachinePanel;
import bd.pablo.redwins.machine.types.MachineType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class MachineOpenPanelEvent extends MachineEvent{

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private MachinePanel machinePanel;

    public MachineOpenPanelEvent(Player player, Machine machine, MachineType type, MachinePanel machinePanel) {
        super(machine, type);
        this.player = player;
        this.machinePanel = machinePanel;
    }

    public Player getPlayer() {
        return this.player;
    }

    public MachinePanel getMachinePanel() {
        return machinePanel;
    }

    public void setMachinePanel(MachinePanel machinePanel) {
        this.machinePanel = machinePanel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
