package bd.pablo.redwins.machine.lib.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Economy {

    boolean contains(UUID uuid, double amount);
    boolean contains(Player player, double amount);
    boolean contains(OfflinePlayer player, double amount);

    void add(UUID uuid, double amount);
    void add(Player player, double amount);
    void add(OfflinePlayer player, double amount);

    void remove(UUID uuid, double amount);
    void remove(Player player, double amount);
    void remove(OfflinePlayer player, double amount);

    void set(UUID uuid, double amount);
    void set(Player player, double amount);
    void set(OfflinePlayer player, double amount);
}
