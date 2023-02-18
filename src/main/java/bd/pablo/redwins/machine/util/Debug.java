package bd.pablo.redwins.machine.util;

import bd.pablo.redwins.machine.Main;
import bd.pablo.redwins.machine.api.Machine;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class Debug extends DebugStream {

    public Debug(boolean enable) {
        super(new File(Main.getInstance().getDataFolder(),"debug.log"), enable);

    }

    public void println(Machine machine, String... message){
        println(getPrefix(machine),message);
    }

    public void criticalPrintln(Machine machine, String... message){
        criticalPrintln(getPrefix(machine),message);
    }

    public void enable(boolean b){
        this.enable = b;
    }

    public boolean enable(){
        return this.enable;
    }
    private String getPrefix(Machine machine){
        return String.format("§8(§f%s§7-§f%s§7) §7",machine.id(),StringUtil.location(machine.location()));
    }


}

