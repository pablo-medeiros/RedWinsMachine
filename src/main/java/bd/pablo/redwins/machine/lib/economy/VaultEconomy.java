package bd.pablo.redwins.machine.lib.economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

public class VaultEconomy implements Economy{

    private net.milkbowl.vault.economy.Economy econ = null;

    public VaultEconomy(){
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();
        if(econ==null)throw new RuntimeException("Vault not found");
    }

    @Override
    public boolean contains(UUID uuid, double amount) {
        return contains(Bukkit.getOfflinePlayer(uuid), amount);
    }

    @Override
    public boolean contains(Player player, double amount) {
        return contains((OfflinePlayer) player, amount);
    }

    @Override
    public boolean contains(OfflinePlayer player, double amount) {
        return econ.has(player,amount);
    }

    @Override
    public void add(UUID uuid, double amount) {
        add(Bukkit.getOfflinePlayer(uuid),amount);
    }


    @Override
    public void add(Player player, double amount) {
        add((OfflinePlayer) player,amount);
    }

    @Override
    public void add(OfflinePlayer player, double amount) {
        set(player,econ.getBalance(player)+amount);
    }

    @Override
    public void remove(UUID uuid, double amount) {
        remove(Bukkit.getOfflinePlayer(uuid),amount);
    }

    @Override
    public void remove(Player player, double amount) {
        remove((OfflinePlayer) player,amount);
    }

    @Override
    public void remove(OfflinePlayer player, double amount) {
        set(player,econ.getBalance(player)-amount);
    }

    @Override
    public void set(UUID uuid, double amount) {
        set(Bukkit.getOfflinePlayer(uuid),amount);
    }

    @Override
    public void set(Player player, double amount) {
        set((OfflinePlayer) player,amount);
    }

    @Override
    public void set(OfflinePlayer player, double amount) {
        double balance = econ.getBalance(player);
        if(balance==amount)return;
        if(balance<amount){
            econ.depositPlayer(player, amount - balance);
        }else {
            econ.withdrawPlayer(player, balance - amount);
        }
    }
}
