package bd.pablo.redwins.machine.api.events;

import bd.pablo.redwins.machine.types.IType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerPurchaseEvent extends BaseEvent{

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player purchaser;
    private IType iType;
    private int amount;
    private double pricePerItem;
    private double total;

    public PlayerPurchaseEvent(Player purchaser, IType iType) {
        this(purchaser,iType,1);
    }

    public PlayerPurchaseEvent(Player purchaser, IType iType, int amount) {
        this(purchaser,iType,amount, iType.price());
    }

    public PlayerPurchaseEvent(Player purchaser, IType iType, int amount, double pricePerItem) {
        this.purchaser = purchaser;
        this.iType = iType;
        this.amount = amount;
        this.pricePerItem = pricePerItem;
    }

    public Player getPurchaser() {
        return purchaser;
    }

    public IType getType() {
        return iType;
    }

    public int getAmount() {
        return amount;
    }

    public double getPricePerItem() {
        return pricePerItem;
    }

    public double getTotal() {
        return total;
    }

    public void setPricePerItem(double pricePerItem) {
        this.pricePerItem = pricePerItem;
        this.total = pricePerItem*amount;
    }

    public void setTotal(double total) {
        this.total = total;
        this.pricePerItem = total / amount;
    }

    public void setType(IType type) {
        this.iType = type;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
