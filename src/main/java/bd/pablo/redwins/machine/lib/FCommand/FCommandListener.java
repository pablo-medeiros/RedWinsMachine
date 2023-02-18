package bd.pablo.redwins.machine.lib.FCommand;

import bd.pablo.redwins.machine.lib.FCommand.events.FCommandReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Arrays;

public class FCommandListener implements Listener {

    @EventHandler
    public void onServerCommand(ServerCommandEvent e){
        if(!e.isCancelled()){
            e.setCancelled(sendEvent(e.getSender(),e.getCommand()));
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e){
        if(!e.isCancelled()){
            e.setCancelled(sendEvent(e.getPlayer(),e.getMessage().substring(1)));
        }
    }


    private boolean sendEvent(CommandSender sender, String message){
        String[] spl = message.split(" ");
        FCommandReceivedEvent fCommandReceivedEvent = new FCommandReceivedEvent(spl[0], Arrays.copyOfRange(spl,1,spl.length), sender);
        Bukkit.getPluginManager().callEvent(fCommandReceivedEvent);
        return fCommandReceivedEvent.isCancelled();
    }

}
