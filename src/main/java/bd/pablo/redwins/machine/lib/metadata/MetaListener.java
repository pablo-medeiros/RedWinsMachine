package bd.pablo.redwins.machine.lib.metadata;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetaListener implements Listener {

    public static List<MetaManager> managers = new ArrayList<>();

    {
        managers.clear();
    }
    @EventHandler
    public void onPluginEnable(PluginEnableEvent e){
        for(MetaManager manager : managers){
            for(Map.Entry<String, Meta> meta : manager.all()){
                if(meta.getValue().getPlugin().getName().equalsIgnoreCase(e.getPlugin().getName())&&meta.getValue().isPersistent()){
                    try {
                        Field field = meta.getValue().getClass().getDeclaredField("plugin");
                        field.setAccessible(true);
                        field.set(meta.getValue(), e.getPlugin());
                    }catch (Exception ignored){

                    }
                }
            }
        }

    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent e){
        for(MetaManager manager : managers){
            for(Map.Entry<String, Meta> meta : manager.all()){
                if(meta.getValue().getPlugin().getName().equalsIgnoreCase(e.getPlugin().getName())&&!meta.getValue().isPersistent()){
                    manager.unRegisterMeta(meta.getKey(), e.getPlugin());
                }
            }
        }

    }

    public static void add(MetaManager manager){
        managers.add(manager);
    }

    public static void remove(MetaManager manager){
        managers.remove(manager);
    }
}
