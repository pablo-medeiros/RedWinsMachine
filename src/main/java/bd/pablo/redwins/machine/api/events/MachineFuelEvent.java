package bd.pablo.redwins.machine.api.events;

import bd.pablo.redwins.machine.api.Machine;
import bd.pablo.redwins.machine.types.FuelType;
import bd.pablo.redwins.machine.types.MachineType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class MachineFuelEvent extends MachineEvent{

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Player player;
    private FuelType fuel;
    private long amount;
    private long ticks;

    public MachineFuelEvent(Machine machine, MachineType type, Player player, FuelType fuel, long amount) {
        super(machine, type);
        this.player = player;
        this.fuel = fuel;
        setAmount(amount);
    }

    public Player getPlayer() {
        return this.player;
    }

    public FuelType getFuel() {
        return fuel;
    }

    public void setFuel(FuelType fuel) {
        this.fuel = fuel;
        setAmount(amount);
    }

    public long getTicks() {
        return ticks;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
        this.ticks = fuel.ticks()*amount;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
