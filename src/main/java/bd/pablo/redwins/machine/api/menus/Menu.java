package bd.pablo.redwins.machine.api.menus;

import bd.pablo.redwins.machine.lib.inventory.FillInteract;
import bd.pablo.redwins.machine.lib.inventory.FillInventory;

public abstract class Menu extends FillInventory {

    public Menu(String name, int lines, FillInteract.FillType type) {
        super(name, lines, type);
    }

}
