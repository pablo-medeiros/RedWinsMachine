package bd.pablo.redwins.machine.api.events;

import bd.pablo.redwins.machine.api.Machine;
import bd.pablo.redwins.machine.types.MachineType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class MachineBreakEvent extends MachineEvent{

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Player player;
    private Type removeType;
    private long amount;
    private long remaing;

    public MachineBreakEvent(Player player, Machine machine, long amount, MachineType type) {
        super(machine, type);
        this.player = player;
        this.setAmount(amount);
    }

    public Player getPlayer() {
        return this.player;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
        this.remaing = getMachine().stack() - amount;
        this.removeType = getMachine().stack()<=amount?Type.BREAK:Type.REMOVE_STACK;
    }

    public long getRemaing() {
        return remaing;
    }

    public Type getRemoveType() {
        return removeType;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }


    public static enum Type {
        BREAK, REMOVE_STACK
    }
}
