package bd.pablo.redwins.machine.lib.metadata;

import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class CraftMetaManager implements MetaManager{

    protected Map<String,Meta> metaMap = new HashMap<>();

    {
        MetaListener.add(this);
    }

    public void registerMeta(String key, Meta meta)  {
        if(key==null||key.isEmpty())return;
        unRegisterMeta(key, meta.getPlugin());
        if(meta.getPlugin()==null) try {
            throw new InvalidPluginException("Plugin não encontrado para registrar como meta");
        } catch (InvalidPluginException e) {
            throw new RuntimeException(e);
        }
        metaMap.put(key,meta);
    }

    public Meta unRegisterMeta(String key, Plugin plugin){
        Meta meta = metaMap.get(key);
        if(meta==null)return null;
        if(!meta.getPlugin().getName().equalsIgnoreCase(plugin.getName())){
            try {
                throw new InvalidPluginException("Plugin invalido para remover, precisa ser o mesmo plugin que o registrou");
            } catch (InvalidPluginException e) {
                throw new RuntimeException(e);
            }
        }
        metaMap.remove(key);
        return meta;
    }

    public Meta findMeta(String key){
        return metaMap.get(key);
    }

    public String[] names(){
        return metaMap.keySet().toArray(new String[0]);
    }

    public Meta[] metas(){
        return metaMap.values().toArray(new Meta[0]);
    }

    public Map.Entry<String,Meta>[] all(){
        return metaMap.entrySet().toArray(new Map.Entry[0]);
    }

    @Override
    public MetaManager clone() {
        CraftMetaManager craftMetaManager = new CraftMetaManager();
        craftMetaManager.metaMap = new HashMap<>(this.metaMap);
        return craftMetaManager;
    }
}
