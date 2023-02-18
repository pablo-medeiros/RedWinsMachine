package bd.pablo.redwins.machine.api;

import bd.pablo.redwins.machine.lib.inventory.FillInteract;
import bd.pablo.redwins.machine.lib.inventory.FillInventory;
import bd.pablo.redwins.machine.lib.inventory.InventoryItem;
import bd.pablo.redwins.machine.util.ItemBuilder;
import bd.pablo.redwins.machine.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class MachinePanel extends FillInventory {

    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm");

    private long lastUpdate;

    public MachinePanel(String name) {
        super(name!=null&&!name.isEmpty()?name:"§7Painel", 3, true, FillInteract.FillType.BORDER);
        for(ReloadType type : ReloadType.values()){
            reload(type);
        }
        reload();
    }


    public abstract Machine machine();
    public void open(Player player){
        player.openInventory(getInventory());
    }

    public synchronized void reload(ReloadType type){
        boolean e = machine().enable();
        InventoryItem item = null;
        if(type==ReloadType.FUEL||type==ReloadType.ENABLE){
            if(System.currentTimeMillis()-lastUpdate>=500||type==ReloadType.ENABLE) {
                lastUpdate = System.currentTimeMillis();
                item = item(13);
                if (item == null) {
                    item = item(13, Material.COAL);
                    item.setDisplayName("§8Combustivel");
                }
                item.setLore(
                        "§7",
                        String.format(machine().fuel() > 1000 ? "§7Litros: §f%.1f" : "§7Mililitros: §f%d", machine().fuel() > 1000 ? ((double) machine().fuel() / 1000) : machine().fuel()),
                        String.format(e ? "§7Acaba: §f%s" : "§7Desligada", e ? dateFormat.format(new Date(System.currentTimeMillis() + (machine().fuel() * 50))) : null),
                        "§7"
                );
                update(item);
                item=null;
            }
        }
        if(type==ReloadType.ENABLE){
            item = item(11);
            if(item==null){
                item = item(11,Material.INK_SACK);
            }
            item.setAmount(e?1:0);
            item.setDurability((short) (e?10:8));
            item.setDisplayName(e ? "§8Status: §aLigada" : "§8Status: §cDesligada");
        }else if(type==ReloadType.STACK){
            item = item(15);
            if(item==null){
                item = item(15, Material.PAPER);
                item.setDisplayName("§8Informações");
            }
            item.setLore("§7",
                    String.format("§7Dono: §f%s", machine().ownerName()),
                    String.format("§7Empilhadas: §f%d",machine().stack()),
                    String.format("§7ID: §f#%s", StringUtil.padStart(String.valueOf(machine().id()),'0',6)),
                    "§7");
        }
        if(item!=null)update(item);

    }

    public void reload(){
        super.reload();
    }

    @Override
    public void onClick(InventoryClickEvent e, Player player, InventoryItem item) {
        if(item.slot()==11){
            machine().enable(!machine().enable());
        }
    }

    public enum ReloadType {
        ENABLE,
        FUEL,
        STACK
    }
}
