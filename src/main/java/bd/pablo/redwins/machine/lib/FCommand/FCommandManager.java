package bd.pablo.redwins.machine.lib.FCommand;

import bd.pablo.redwins.machine.lib.FCommand.command.FCommand;
import bd.pablo.redwins.machine.lib.FCommand.events.FCommandPreprocessEvent;
import bd.pablo.redwins.machine.lib.FCommand.events.FCommandReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class FCommandManager {

    private static FCommandManager INSTANCE;

    private List<FInfo> commands = new ArrayList<>();
    private List<FInfo.Group> commandGroups = new ArrayList<>();

    public FCommandInfo find(String name, String[] args){
        name=name.toLowerCase();
        for(FInfo.Group group : this.commandGroups) {
            if(group.name.equalsIgnoreCase(name)||group.aliases.contains(name)){
                if(args.length > 0){
                    for(FInfo info : (List<FInfo>)group.getInfos()){
                        if(info.name.equalsIgnoreCase(args[0])||info.aliases.contains(args[0].toLowerCase()))return info;
                    }
                }
                return group;
            }
        }
        for(FInfo info : this.commands){
            if(info.fCommand.name().equalsIgnoreCase(name)|info.aliases.contains(name))return info;
        }
        return null;
    }

    public List<FCommandInfo> getAcceptPlayers(){
        List<FCommandInfo> list = this.commands.stream().filter(cmd->cmd.fCommand.acceptPlayer()).collect(Collectors.toList());
        list.addAll(this.commandGroups.stream().filter(cmd->cmd.fCommandGroup.acceptPlayer()).collect(Collectors.toList()));
        return list;
    }

    public List<FCommandInfo> getAcceptConsole(){
        List<FCommandInfo> list = this.commands.stream().filter(cmd->cmd.fCommand.acceptConsole()).collect(Collectors.toList());
        list.addAll(this.commandGroups.stream().filter(cmd->cmd.fCommandGroup.acceptConsole()).collect(Collectors.toList()));
        return list;
    }

    public void load(JavaPlugin plugin){
        try {
            List<Class<?>> classes = getClasses(plugin);
            for(Class clazz : classes){
                if(FInfo.Group.isValid(clazz)){
                    FInfo.Group group = new FInfo.Group(clazz);
                    group.load();
                    this.commandGroups.add(group);
                }else if(FInfo.isValid(clazz)){
                    for(Method method : clazz.getMethods()){
                        if(FInfo.isValid(method)){
                            FInfo command = new FInfo(method.getAnnotation(FCommand.class), method, clazz);
                            commands.add(command);
                        }
                    }
                }
            }
            Bukkit.getPluginManager().registerEvents(new FCommandListener(),plugin);
            Bukkit.getPluginManager().registerEvents(new Listener() {

                @EventHandler
                public void onCommandReceived(FCommandReceivedEvent e){
                    FCommandInfo info = find(e.getName(),e.getArguments());
                    if(info!=null) {
                        String name = "";
                        String[] args;
                        if (info.getCommandGroup() != null) name = info.getCommandGroup().name() + " ";
                        if (info.getCommand() != null) {
                            name += info.getCommand().name();
                            args = Arrays.copyOfRange(e.getArguments(), 1, e.getArguments().length);
                        } else {
                            args = e.getArguments();
                        }
                        FCommandPreprocessEvent fCommandPreprocessEvent = new FCommandPreprocessEvent(
                                name, args, e.getSender(), info.getCommand(), info.getCommandGroup()
                        );
                        Bukkit.getPluginManager().callEvent(fCommandPreprocessEvent);
                        if (!fCommandPreprocessEvent.isCancelled()) {
                            info.execute(fCommandPreprocessEvent.getSender(), fCommandPreprocessEvent.getArguments());
                        }
                        e.setCancelled(true);
                    }
                }

                @Override
                public int hashCode() {
                    return super.hashCode();
                }
            }, plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static FCommandManager getInstance() {
        if(INSTANCE==null){
            INSTANCE=new FCommandManager();
        }
        return INSTANCE;
    }

    private List<Class<?>> getClasses(JavaPlugin plugin){
        String packageName = plugin.getClass().getPackage().getName();
        packageName = packageName.substring(0, packageName.lastIndexOf("."));
        List<Class<?>> list = new ArrayList<>();
        try {
            URL location = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
            JarFile jarFile = new JarFile(location.getPath().replace("%20"," "));
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()){
                JarEntry entry = enumeration.nextElement();
                String name = entry.getName().replaceAll("/",".");
                if(name.startsWith(packageName)&&name.endsWith(".class")){
                    name = name.substring(0, name.length()-6);
                    try {
                        Class<?> clazz = Class.forName(name);
                        list.add(clazz);
                    }catch (Exception e){
                        System.out.println("[FCommandError] "+entry.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

//    private static List<Class<?>> getClasses(String packageName)
//            throws ClassNotFoundException, IOException {
//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        assert classLoader != null;
//        String path = packageName.replace('.', '/');
//        Enumeration<URL> resources = classLoader.getResources(path);
//        List<File> dirs = new ArrayList<File>();
//        while (resources.hasMoreElements()) {
//            URL resource = resources.nextElement();
//            dirs.add(new File(resource.getFile()));
//        }
//        ArrayList<Class<?>> classes = new ArrayList<>();
//        for (File directory : dirs) {
//            classes.addAll(findClasses(directory, packageName));
//        }
//        return classes;
//    }
//    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
//        List<Class<?>> classes = new ArrayList<>();
//        if (!directory.exists()) {
//            return classes;
//        }
//        for (File file : Objects.requireNonNull(directory.listFiles())) {
//            if (file.isDirectory()) {
//                assert !file.getName().contains(".");
//                classes.addAll(findClasses(file, packageName + "." + file.getName()));
//            } else if (file.getName().endsWith(".class")) {
//                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
//            }
//        }
//        return classes;
//    }



}
