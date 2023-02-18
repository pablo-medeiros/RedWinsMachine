package bd.pablo.redwins.machine.api.menus.impl;

import bd.pablo.redwins.machine.api.menus.Menu;
import bd.pablo.redwins.machine.api.menus.MenuItem;
import bd.pablo.redwins.machine.lib.inventory.FillInteract;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public abstract class MenuConfigImpl extends Menu {

    public MenuConfigImpl(ConfigurationSection section) {
        super(section.getString("title","&7Default"), section.getInt("height",6), FillInteract.FillType.NONE);
        inReload(section);
        posReload(section);
    }

    public void reload(ConfigurationSection section){
        preReload(section);
        inReload(section);
        posReload(section);
    }

    public void preReload(ConfigurationSection section){
        String title = section.getString("title","&7Default");
        int height = section.getInt("height",6);
        this.clear();
        if(!getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&',title))||getInventory().getSize()!=height*9){
            change(title,height);
        }
    }

    public void inReload(ConfigurationSection section){
        if(section.contains("fill")&&section.isConfigurationSection("fill")){
            ConfigurationSection fillSection = section.getConfigurationSection("fill");
            String typeName = fillSection.getString("type","none");
            this.fillInteract = new FillInteract(this,FillInteract.FillType.getOrDefault(typeName, FillInteract.FillType.NONE));
            if(fillSection.contains("item")&&fillSection.isConfigurationSection("item")) {
                MenuItem item = new MenuItem(fillSection.getConfigurationSection("item"));
                this.fill(item);
            }
        }
        if(section.contains("items")&&section.isConfigurationSection("items")){
            ConfigurationSection items = section.getConfigurationSection("items");
            for(String key : items.getKeys(false)){
                if(!items.isConfigurationSection(key))continue;
                ConfigurationSection item = items.getConfigurationSection(key);
                MenuItem menuItem = new MenuItem(item);
                this.item(menuItem);
            }
        }
    }

    public void posReload(ConfigurationSection section){
        this.reload();
    }
}
