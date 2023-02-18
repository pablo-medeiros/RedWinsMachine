package bd.pablo.redwins.machine.util;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public enum MessageAPI {

    UNDEFINED("","");

    private String key;
    private List<String> messages;

    MessageAPI(String key, String... defaultMessage){
        this.messages= Arrays.asList(defaultMessage);
    }

    public String[] formattedMessage(){
        return this.messages.stream().map(msg-> ChatColor.translateAlternateColorCodes('&',msg)).toArray(String[]::new);
    }
}
