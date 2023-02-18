package bd.pablo.redwins.machine.lib.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiFunction;

public class FillInteract {

    private FillType type;
    private Iterator<Integer> iterator;

    public FillInteract(CraftInventory inventory,FillType type) {
        this.type = type;
        resetFill(inventory);
    }

    public FillInteract fill(CraftInventory iInventory,ItemStack item){
        resetFill(iInventory);
        while(iterator.hasNext()){
            int i = iterator.next();
            iInventory.item(new InventoryItem(item){
                @Override
                public boolean onClick(Player player, IInventory inventory) {
                    return false;
                }
            }.slot(i));
        }
        return this;
    }


    public FillInteract fill(CraftInventory iInventory, InventoryItem item){
        resetFill(iInventory);
        while(iterator.hasNext()){
            int i = iterator.next();
            InventoryItem inventoryItem = item.clone();
            inventoryItem.slot(i);
            iInventory.item(inventoryItem);
        }
        return this;
    }


    public FillInteract fill(CraftInventory iInventory,ItemStack item, BiFunction<Player,InventoryItem, Boolean> fn){
        resetFill(iInventory);
        while(iterator.hasNext()){
            int i = iterator.next();
            iInventory.item(new InventoryItem(item){
                @Override
                public boolean onClick(Player player, IInventory inventory) {
                    return fn.apply(player,this);
                }
            }.slot(i));
        }
        return this;
    }

    public boolean next(CraftInventory iInventory,InventoryItem item){
        if(!iterator.hasNext())return false;
        item.slot(iterator.next());
        iInventory.item(item);
        return true;
    }

    public int getNext(){
        return iterator.hasNext()?iterator.next():-1;
    }

    public boolean hasNext(){
        return iterator.hasNext();
    }

    public void resetFill(CraftInventory iInventory){
        resetFill(iInventory.getInventory().getSize()/9);
    }

    private void resetFill(int height){
        this.iterator = Arrays.stream(type.getSlots(height)).iterator();
    }

    public int[] getSlots(CraftInventory iInventory) {
        return type.getSlots(iInventory.getInventory().getSize()/9);
    }

    public static enum FillType {
        NONE(),
        HALF(
                new int[]{1,2,3,4,5,6,7},
                new int[]{1,2,3,4,5,6,7,10,11,12,13,14,15,16},
                new int[]{10,11,12,13,14,15,16},
                new int[]{10,11,12,13,14,15,16,19,20,21,22,23,24,25},
                new int[]{10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34},
                new int[]{10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43}
        ),
        BORDER(
                new int[]{0,1,2,3,4,5,6,7,8},
                new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17},
                new int[]{0,1,2,3,4,5,6,7,8,9,17,18,19,20,21,22,23,24,25,26},
                new int[]{0,1,2,3,4,5,6,7,8,9,17,18,26,27,28,29,30,31,32,33,34,35},
                new int[]{0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,39,40,41,42,43,44},
                new int[]{0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,48,49,50,51,52,53}
        ),
        CUSTOM_1(),
        CUSTOM_2(),
        CUSTOM_3(),
        CUSTOM_4(),
        CUSTOM_5(),
        CUSTOM_6(),
        CUSTOM_7(),
        CUSTOM_8(),
        CUSTOM_9();

        private int slots[][];

        private FillType(){
            this.slots = new int[6][];
            Arrays.fill(this.slots,new int[0]);
        }
        private FillType(int[]... slots){
            this.slots = slots;
        }

        public int[] getSlots(int lines){
            return this.slots[lines-1];
        }

        public int[][] getSlots(){
            return this.slots;
        }

        public static FillType getOrDefault(String name, FillType defaultType){
            for(FillType type : values()){
                if(type.name().equalsIgnoreCase(name)||type.toString().equalsIgnoreCase(name))return type;
            }
            return defaultType;
        }

    }
}
