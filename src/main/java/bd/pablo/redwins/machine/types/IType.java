package bd.pablo.redwins.machine.types;

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
}
