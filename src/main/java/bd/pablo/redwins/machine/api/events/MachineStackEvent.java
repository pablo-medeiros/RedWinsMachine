package bd.pablo.redwins.machine.api.events;

import bd.pablo.redwins.machine.api.Machine;
import bd.pablo.redwins.machine.types.MachineType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class MachineStackEvent extends MachineEvent{

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Player player;
    private long currentAmount;
    private long incrementAmount;
    private long finalAmount;

    public MachineStackEvent(Player player, Machine machine, MachineType type, long currentAmount, long incrementAmount, long finalAmount) {
        super(machine, type);
        this.player = player;
        this.currentAmount = currentAmount;
        this.incrementAmount = incrementAmount;
        this.finalAmount = finalAmount;
    }

    public Player getPlayer() {
        return this.player;
    }

    public long getCurrentAmount() {
        return currentAmount;
    }

    public long getIncrementAmount() {
        return incrementAmount;
    }

    public long getFinalAmount() {
        return finalAmount;
    }

    public void setIncrementAmount(long incrementAmount) {
        this.incrementAmount = incrementAmount;
        this.finalAmount = this.currentAmount + incrementAmount;
    }

    public void setFinalAmount(long finalAmount) {
        this.finalAmount = finalAmount;
        this.incrementAmount = finalAmount - this.currentAmount;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
