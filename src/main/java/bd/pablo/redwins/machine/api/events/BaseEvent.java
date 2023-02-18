package bd.pablo.redwins.machine.api.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class BaseEvent extends Event implements Cancellable {


    protected boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public void call(){
        Bukkit.getPluginManager().callEvent(this);
    }
}
