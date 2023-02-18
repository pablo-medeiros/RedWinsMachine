package bd.pablo.redwins.machine.lib.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e){
        if(e.getInventory().getHolder() instanceof IInventory){
            try {
                IInventory inventory = (IInventory) e.getInventory().getHolder();
                inventory.onOpen(e);
            }catch (Exception e1){
                e1.printStackTrace();
                e.getPlayer().closeInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(e.getInventory().getHolder() instanceof IInventory){
            try {
                IInventory inventory = (IInventory) e.getInventory().getHolder();
                inventory.onClose(e);
            }catch (Exception e1){
                e1.printStackTrace();
                e.getPlayer().closeInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getInventory().getHolder() instanceof IInventory){
            try {
                IInventory inventory = (IInventory) e.getInventory().getHolder();
                inventory.onClick(e);
                if(inventory.noInteract())e.setCancelled(true);
                if(!(e.getWhoClicked() instanceof Player))return;
                Player player = (Player)e.getWhoClicked();
                for(InventoryItem item : inventory.items()){
                    if(item.slot()==e.getRawSlot()){
                        inventory.onClick(e,player, item);
                        e.setCancelled(item.onClick(player,inventory));
                        return;
                    }
                }
            }catch (Exception e1){
                e1.printStackTrace();
                e.getWhoClicked().closeInventory();
            }
        }
    }
}
