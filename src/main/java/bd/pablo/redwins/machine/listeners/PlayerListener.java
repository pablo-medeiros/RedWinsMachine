package bd.pablo.redwins.machine.listeners;

import bd.pablo.redwins.machine.Main;
import bd.pablo.redwins.machine.api.Machine;
import bd.pablo.redwins.machine.api.Manager;
import bd.pablo.redwins.machine.api.events.*;
import bd.pablo.redwins.machine.types.FuelType;
import bd.pablo.redwins.machine.types.MachineType;
import bd.pablo.redwins.machine.util.NBTItem;
import bd.pablo.redwins.machine.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.Map;

public class PlayerListener implements Listener {

    @EventHandler(ignoreCancelled = true,priority = EventPriority.LOWEST)
    public void onPlayerPlaceBlock(BlockPlaceEvent e){
        ItemStack item = e.getItemInHand();
        if(item==null||item.getType()== Material.AIR)return;

        NBTItem displayItemNBT = new NBTItem(item);
        displayItemNBT.load();
        String id = displayItemNBT.getTag().string("machine-type-id");
        if(id==null)return;
        MachineType type = Manager.get().machineTypeById(id);
        if(type==null){
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cTipo de maquina não encontrado, contate um adiministrador do servidor");
            Main.getInstance().debug().println(new String[]{e.getPlayer().getName()+"-error: onPlayerPlaceBlock 34 : "+id});
            return;
        }
        Machine[] nearby = Manager.get().getNearbyMachines(e.getBlock().getLocation(),(m)->{
            if(!m.ownerUUID().equals(e.getPlayer().getUniqueId()))return false;
            return m.type().equals(type);
        },5,5,5);
        e.setCancelled(true);
        if(nearby.length>0){
            Machine machine = nearby[0];
            long amount = e.getPlayer().isSneaking() ? item.getAmount() : 1;
            MachineStackEvent machineStackEvent = new MachineStackEvent(e.getPlayer(),machine,type,machine.stack(),amount,machine.stack()+amount);
            machineStackEvent.call();
            if(machineStackEvent.isCancelled())return;
            machine.stack(machineStackEvent.getFinalAmount());
            if(item.getAmount()>machineStackEvent.getIncrementAmount()){
                item.setAmount((int) (item.getAmount()-machineStackEvent.getIncrementAmount()));
            }else {
                e.getPlayer().setItemInHand(null);
            }
        }else {
            long amount = 1;
            Machine machine = new Machine(type, e.getBlock().getLocation(), e.getPlayer().getUniqueId(), e.getPlayer().getName(), amount);
            MachinePlacedEvent machinePlacedEvent = new MachinePlacedEvent(e.getPlayer(),machine,amount,type);
            machinePlacedEvent.call();
            if(machinePlacedEvent.isCancelled()){
                return;
            }
            e.setCancelled(false);
            machine.stack(machinePlacedEvent.getAmount());
            Manager.get().registerMachine(machine);
            e.getPlayer().sendMessage("§aMaquina definida");
            e.getBlock().setType(type.blockType());
            e.getBlock().setData(type.blockData());
        }
    }

    @EventHandler
    public void onPlayerBlockBreak(BlockBreakEvent e){
        try{
            Machine machine = Manager.get().machine(e.getBlock());
            if(machine!=null){
                e.setCancelled(true);
                if(!machine.ownerUUID().equals(e.getPlayer().getUniqueId())&&!e.getPlayer().hasPermission("machine.admin.remove")){
                    e.getPlayer().sendMessage("§cVocê precisa ser dono da maquina");
                    return;
                }
                long amount = e.getPlayer().isSneaking() ? 1 : machine.stack();
                MachineBreakEvent machineBreakEvent = new MachineBreakEvent(e.getPlayer(),machine,amount,machine.type());
                machineBreakEvent.call();
                if(machineBreakEvent.isCancelled()){
                    return;
                }
                amount = machineBreakEvent.getAmount();
                Map<Integer, ItemStack> remaing;
                if(amount>=machine.stack()){
                    MachineType type = machine.type();
                    Manager.get().unRegisterMachine(machine);
                    ItemStack[] items = new ItemStack[(int) ((amount / 64) + (amount % 64 > 0 ? 1 : 0))];
                    for(int i = 0; i < items.length; i++){
                        items[i] = type.displayItem();
                        items[i].setAmount(amount>64?64: (int) amount);
                        amount-=items[i].getAmount();
                    }
                    remaing = e.getPlayer().getInventory().addItem(items);
                    e.getPlayer().sendMessage("§aMaquina removida");
                }else {
                    machine.decrementStack(1);
                    ItemStack item = machine.type().displayItem();
                    item.setAmount(1);
                    remaing = e.getPlayer().getInventory().addItem(item);
                    e.getPlayer().sendMessage("§aVocê removeu 1 maquina");
                }
                for (ItemStack value : remaing.values()) {
                    e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(),value);
                }
            }
        }catch (Exception e1){
            e.setCancelled(true);
            Main.getInstance().debug().println(new String[]{e.getPlayer().getName()+"-error: onPlayerBlockBreak : "+ StringUtil.location(e.getBlock().getLocation())});
            Main.getInstance().debug().exception(e1);
            e.getPlayer().sendMessage("§cErro na maquina, contate um adiministrador");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction()!= Action.RIGHT_CLICK_BLOCK)return;
        Machine machine = Manager.get().machine(e.getClickedBlock());
        if(machine==null)return;
        e.setCancelled(true);
        if(e.getItem()!=null){
            NBTItem item = new NBTItem(e.getItem());
            item.load();
            if(item.getTag().has("fuel-type-id")){
                FuelType type = Manager.get().fuelTypeById(item.getTag().string("fuel-type-id"));
                if(type!=null) {
                    if (type.blockedMachines().contains(machine.type().id()) || type.blockedMachines().contains(machine.type().name())) {
                        e.getPlayer().sendMessage("§cMaquina não pode ser abastecida por esse combustivel.");
                        return;
                    }
                    long amount = e.getPlayer().isSneaking() ? e.getItem().getAmount() : 1;
                    MachineFuelEvent machineFuelEvent = new MachineFuelEvent(machine,machine.type(),e.getPlayer(),type,amount);
                    machineFuelEvent.call();
                    if(machineFuelEvent.isCancelled()){
                        return;
                    }
                    amount = machineFuelEvent.getAmount();
                    if (amount >= e.getItem().getAmount()) {
                        e.getPlayer().setItemInHand(null);
                    } else {
                        e.getItem().setAmount((int) (e.getItem().getAmount() - amount));
                    }
                    machine.incrementFuel(machineFuelEvent.getTicks());
                    e.getPlayer().sendMessage("§aMaquina abastecida por §f" + StringUtil.dateDistance(new Date(System.currentTimeMillis() + (machineFuelEvent.getTicks() * 50)), true, "dia", "hora", "minuto", "segundo", "millisegundo"));
                    return;
                }
            }
        }
        MachineOpenPanelEvent machineOpenPanelEvent = new MachineOpenPanelEvent(e.getPlayer(),machine,machine.type(),machine.panel());
        machineOpenPanelEvent.call();
        if(!machineOpenPanelEvent.isCancelled()&&machineOpenPanelEvent.getMachinePanel()!=null){
            machineOpenPanelEvent.getMachinePanel().open(e.getPlayer());
        }
    }

}
