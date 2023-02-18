package bd.pablo.redwins.machine.commands;

import bd.pablo.redwins.machine.api.Manager;
import bd.pablo.redwins.machine.api.menus.Menu;
import bd.pablo.redwins.machine.lib.FCommand.command.FCommand;
import bd.pablo.redwins.machine.lib.FCommand.command.FCommandExecutor;
import bd.pablo.redwins.machine.lib.FCommand.command.FCommandGroup;
import bd.pablo.redwins.machine.types.MachineType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@FCommandGroup(name = "machine", aliases = {"maquinas","maquina","machines"})
public class MachineCommand implements FCommandExecutor {

    @FCommand(acceptConsole = false)
    public void cmd(Player sender, String[] args) {
        Menu menu = Manager.get().findMenu("market");
        if(menu!=null){
            sender.openInventory(menu.getInventory());
        }else {
            sender.sendMessage("§cMercado não disponivel!");
        }
    }

    @FCommand(name = "give", acceptConsole = false, minArgs = 1)
    public void give(Player sender, String[] args) {
        MachineType type = Manager.get().machineTypeById(args[0]);
        System.out.println(args[0]);
        if(type==null)
            type = Manager.get().machineTypeByName(String.join(" ",args));
        if(type!=null){
            sender.sendMessage(String.format("§eMaquina givada §f%s",type.name()));
            sender.getInventory().addItem(type.displayItem());
        }else {
            sender.sendMessage("§cMaquina não encontrada");
        }
    }


}
