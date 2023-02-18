package bd.pablo.redwins.machine.lib.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiFunction;

public abstract class FillInventory extends CraftInventory{

    protected FillInteract fillInteract;

    public FillInventory(String name, int lines, FillInteract.FillType type) {
        this(name,lines,true,type);
    }

    public FillInventory(String name, int lines, boolean noInteract, FillInteract.FillType type) {
        super(name, lines, noInteract);
        this.fillInteract = new FillInteract(this,type);
    }


    public FillInventory fill(ItemStack item){
        fillInteract.fill(this,item);
        return this;
    }


    public FillInventory fill(InventoryItem item){
        fillInteract.fill(this,item);
        return this;
    }


    public FillInventory fill(ItemStack item, BiFunction<Player,InventoryItem, Boolean> fn){
        fillInteract.fill(this,item,fn);
        return this;
    }

    public boolean next(InventoryItem item){
        return fillInteract.next(this,item);
    }

    public int getNext(){
        return fillInteract.getNext();
    }

    public void resetFill(){
        fillInteract.resetFill(this);
    }
}

