package bd.pablo.redwins.machine.lib.metadata;

import org.bukkit.plugin.Plugin;

public interface Meta {

    Object value();
    @Deprecated
    boolean isPersistent();

    default String asString(){
        return (String) value();
    }
    default int asInt(){
        return isInt()?(int) value():0;
    }
    default long asLong(){
        return isLong()?(long) value():0;
    }
    default double asDouble(){
        return isDouble()?(double) value():0;
    }
    default float asFloat(){
        return isFloat()?(float) value():0;
    }
    default boolean asBoolean(){
        return isBoolean()&&(boolean) value();
    }

    default boolean isString(){
        return value() instanceof String;
    }
    default boolean isInt(){
        return  value() instanceof Number && value() instanceof Integer;
    }
    default boolean isLong(){
        return  value() instanceof Number && value() instanceof Long;
    }
    default boolean isDouble(){
        return  value() instanceof Number && value() instanceof Double;
    }
    default boolean isFloat(){
        return  value() instanceof Number && value() instanceof Float;
    }
    default boolean isBoolean(){
        return  value() instanceof Number && value() instanceof Boolean;
    }

    Plugin getPlugin();
}
