package bd.pablo.redwins.machine.lib.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FakeEconomy implements Economy{
    @Override
    public boolean contains(UUID uuid, double amount) {
        return true;
    }

    @Override
    public boolean contains(Player player, double amount) {
        return true;
    }

    @Override
    public boolean contains(OfflinePlayer player, double amount) {
        return false;
    }

    @Override
    public void add(UUID uuid, double amount) {
    }

    @Override
    public void add(Player player, double amount) {
    }

    @Override
    public void add(OfflinePlayer player, double amount) {

    }

    @Override
    public void remove(UUID uuid, double amount) {
    }

    @Override
    public void remove(Player player, double amount) {
    }

    @Override
    public void remove(OfflinePlayer player, double amount) {

    }

    @Override
    public void set(UUID uuid, double amount) {
    }

    @Override
    public void set(Player player, double amount) {
    }

    @Override
    public void set(OfflinePlayer player, double amount) {

    }
}
