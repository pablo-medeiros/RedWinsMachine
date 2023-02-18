package bd.pablo.redwins.machine.lib.metadata;

import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public interface MetaManager {
    void registerMeta(String key, Meta meta);

    Meta unRegisterMeta(String key, Plugin plugin);

    Meta findMeta(String key);

    String[] names();

    Meta[] metas();

    Map.Entry<String,Meta>[] all();

    MetaManager clone();
}
