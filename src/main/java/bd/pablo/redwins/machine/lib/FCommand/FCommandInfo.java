package bd.pablo.redwins.machine.lib.FCommand;

import bd.pablo.redwins.machine.lib.FCommand.command.FCommand;
import bd.pablo.redwins.machine.lib.FCommand.command.FCommandGroup;
import org.bukkit.command.CommandSender;

public interface FCommandInfo {

    void execute(CommandSender sender, String[] args);

    void sendHelp(CommandSender sender);

    boolean isCommand();
    boolean isGroup();

    FCommand getCommand();
    FCommandGroup getCommandGroup();

}
