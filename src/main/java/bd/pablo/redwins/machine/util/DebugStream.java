package bd.pablo.redwins.machine.util;

import org.bukkit.Bukkit;

import java.io.*;
import java.sql.SQLException;

public class DebugStream {

    private File file;
    private PrintStream printStream;
    protected boolean enable;

    public DebugStream(File file, boolean enable) {
        this.file = file;
        this.enable = enable;
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            this.printStream = new PrintStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void println(String... message){
        if(enable){
            for(String msg : message)
                printStream.println(msg);
        }
    }

    public void criticalPrintln(String... message){
        for(String msg : message) {
            if(enable)printStream.println(msg);
            Bukkit.getConsoleSender().sendMessage("§4[Machine] §c"+msg);
        }
    }

    public void println(String prefix,String... message){
        if(enable){
            for(String msg : message)
                printStream.println(prefix+msg);
        }
    }

    public void criticalPrintln(String prefix, String... message){
        for(String msg : message) {
            if(enable)printStream.println(msg);
            Bukkit.getConsoleSender().sendMessage("§4[Machine] §c"+prefix+msg);
        }
    }

    public void exception(Exception e) {
        if(enable){
            e.printStackTrace(printStream);
        }
        e.printStackTrace(System.out);
    }
}
