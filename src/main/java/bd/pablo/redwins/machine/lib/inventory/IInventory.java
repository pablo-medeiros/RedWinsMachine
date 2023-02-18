package bd.pablo.redwins.machine.lib.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.Collection;

public interface IInventory extends InventoryHolder {

    void onOpen(InventoryOpenEvent e);

    void onClose(InventoryCloseEvent e);

    void onClick(InventoryClickEvent e);
    void onClick(InventoryClickEvent e, Player player, InventoryItem item);

    boolean noInteract();

    Collection<InventoryItem> items();

    void close();
}
