package bd.pablo.redwins.machine.types;

import bd.pablo.redwins.machine.util.NBTItem;
import org.bukkit.inventory.ItemStack;

public interface IType {

    String typeName();
    String id();
    String name();
    double price();
    ItemStack displayItem();
    IType name(String param);
    IType price(double param);
    IType displayItem(ItemStack param);

    boolean isSimilar(ItemStack param);
    boolean isSimilar(NBTItem param);
}
