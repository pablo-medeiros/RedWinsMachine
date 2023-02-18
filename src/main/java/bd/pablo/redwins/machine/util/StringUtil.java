package bd.pablo.redwins.machine.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Date;

public class StringUtil {

    public static String location(Location location){
        return location.getWorld().getName()+"@"+location.getBlockX()+"@"+location.getBlockY()+"@"+location.getBlockZ();
    }
    public static Location location(String loc){
        String[] l = loc.split("@");
        World world = Bukkit.getWorld(l[0]);
        return new Location(world,Double.parseDouble(l[1]),Double.parseDouble(l[2]),Double.parseDouble(l[3]));
    }

    public static String escape(String a, int maxLength){
        StringBuilder sb = new StringBuilder();
        sb.append(a.substring(0, Math.min(a.length(), maxLength)));
        if(a.length()>maxLength)sb.append("...");
        return sb.toString();
    }

    public static String padStart(Object a, Character character,int length){
        String s = a.toString();
        StringBuilder sb = new StringBuilder();
        for(int i = s.length(); i < length; i++){
            sb.append(character);
        }
        sb.append(s);
        return sb.toString();
    }

    public static String pad(Object a, String character,int length){
        String s = a.toString();
        StringBuilder sb = new StringBuilder();
        int empty = length - s.length();
        int leftAndRight = empty/2;
        for(int i = 0; i < leftAndRight; i++){
            sb.append(character);
        }
        sb.append(s);
        for(int i = 0; i < leftAndRight; i++){
            sb.append(character);
        }
        return sb.toString();
    }

    public static String dateDistance(Date date, boolean plural,String days,String hours, String minutes, String seconds,String milliseconds){
        StringBuilder stringBuilder = new StringBuilder();
        long distance = System.currentTimeMillis() - date.getTime();
        if(distance >= 86400000){
            int day = Math.round(distance / 86400000);
            distance -= day * 86400000;
            stringBuilder.append(day).append(days).append(day>1&&plural ? "'s ": " ");
        }
        if(distance >= 3600000){
            int hour = Math.round(distance / 3600000);
            distance -= hour * 3600000;
            stringBuilder.append(hour).append(hours).append(hour > 1&&plural ?"'s ":" ");
        }
        if(distance >= 60000){
            int minute = Math.round(distance / 60000);
            distance -= minute * 60000;
            stringBuilder.append(minute).append(minutes).append(minute > 1&&plural ?"'s ":" ");
        }
        if(distance >= 1000){
            int second = Math.round(distance / 1000);
            distance -= second * 1000;
            stringBuilder.append(second).append(seconds).append(second > 1&&plural ?"'s ":" ");
        }
        if(distance >= 0){
            stringBuilder.append(distance).append(milliseconds);
        }
        return stringBuilder.toString();
    }


}
