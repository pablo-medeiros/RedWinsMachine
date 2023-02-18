package bd.pablo.redwins.machine.api.menus.impl;

import bd.pablo.redwins.machine.api.menus.Menu;
import bd.pablo.redwins.machine.lib.inventory.InventoryItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuMarketImpl extends MenuConfigImpl {


    public MenuMarketImpl(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void onClick(InventoryClickEvent e, Player player, InventoryItem item) {

    }
}
