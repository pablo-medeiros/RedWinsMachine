package bd.pablo.redwins.machine.lib.metadata;

import org.bukkit.plugin.Plugin;

public class FixedMeta implements Meta {

    private Object value;
    private boolean persistent;
    private Plugin plugin;

    public FixedMeta(Object value, Plugin plugin) {
        this.value = value;
        this.plugin = plugin;
    }

    public FixedMeta(Object value, boolean persistent, Plugin plugin) {
        this.value = value;
        this.persistent = persistent;
        this.plugin = plugin;
    }

    public FixedMeta(String value, Plugin plugin){
        this((Object) value,plugin);
    }

    public FixedMeta(Number value, Plugin plugin){
        this((Object) value,plugin);
    }

    public FixedMeta(boolean value, Plugin plugin){
        this((Object) value,plugin);
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    public boolean isPersistent() {
        return this.persistent;
    }
}
