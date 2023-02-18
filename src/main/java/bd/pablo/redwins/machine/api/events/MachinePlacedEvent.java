package bd.pablo.redwins.machine.api.events;

import bd.pablo.redwins.machine.api.Machine;
import bd.pablo.redwins.machine.types.MachineType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class MachinePlacedEvent extends MachineEvent{

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Player player;
    private long amount;

    public MachinePlacedEvent(Player player, Machine machine, long amount, MachineType type) {
        super(machine, type);
        this.amount = amount;
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
