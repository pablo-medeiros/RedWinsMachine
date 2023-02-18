package bd.pablo.redwins.machine.lib.FCommand.events;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FCommandReceivedEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    private String name;
    private String[] args;
    private CommandSender sender;
    private boolean cancelled;
    private boolean isPlayer;
    private boolean isConsole;

    public FCommandReceivedEvent(String name, String[] args, CommandSender sender) {
        this.name = name.toLowerCase();
        this.args = args;
        this.sender = sender;
        this.isPlayer = sender instanceof Player;
        this.isConsole = sender instanceof ConsoleCommandSender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public String[] getArguments() {
        return args;
    }

    public void setArguments(String... args) {
        this.args = args;
    }

    public CommandSender getSender() {
        return sender;
    }

    public void setSender(CommandSender sender) {
        this.sender = sender;
        this.isPlayer = sender instanceof Player;
        this.isConsole = sender instanceof ConsoleCommandSender;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public boolean isConsole() {
        return isConsole;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList(){
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled=b;
    }
}
