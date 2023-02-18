package bd.pablo.redwins.machine.lib.FCommand;

import bd.pablo.redwins.machine.lib.FCommand.command.FCommand;
import bd.pablo.redwins.machine.lib.FCommand.command.FCommandExecutor;
import bd.pablo.redwins.machine.lib.FCommand.command.FCommandGroup;
import bd.pablo.redwins.machine.util.MessageAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FInfo<T extends FCommandExecutor> implements FCommandInfo{

    protected FCommand fCommand;
    protected Method method;
    protected Class<T> clazz;
    protected T instance;
    protected Group<T> group;

    protected String name;
    protected List<String> aliases;
    protected int minArgs;
    protected boolean acceptPlayer;
    protected boolean acceptConsole;
    protected MessageAPI help;
    protected String permission;

    protected FInfo(FCommand fCommand, Method method){
        this.fCommand=fCommand;
        this.method=method;
    }

    protected FInfo(FCommand fCommand, Method method, Class<T> clazz){
        this(fCommand, method);
        this.clazz=clazz;
        try {
            this.instance=clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.load();
    }

    protected FInfo(FCommand fCommand, Method method, Class<T> clazz, T instance){
        this.fCommand=fCommand;
        this.method=method;
        this.clazz=clazz;
        this.instance=instance;
        this.load();
    }

    private void load(){
        this.name=this.fCommand.name();
        this.minArgs=this.fCommand.minArgs();
        this.help=this.fCommand.help();
        this.aliases=Arrays.stream(this.fCommand.aliases()).map(s->s.toLowerCase().trim()).collect(Collectors.toList());
        this.acceptPlayer=this.fCommand.acceptPlayer();
        this.acceptConsole=this.fCommand.acceptConsole();
        this.permission=this.fCommand.permission();
    }

    public static boolean isValid(Class<?> clazz){
        return Arrays.asList(clazz.getInterfaces()).contains(FCommandExecutor.class);
    }

    public static boolean isValid(Method method){
        return method.isAnnotationPresent(FCommand.class);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!acceptConsole&&sender instanceof ConsoleCommandSender){
            sender.sendMessage("Não aceita console");
            return;
        }
        if(!acceptPlayer&&sender instanceof Player){
            sender.sendMessage("Não aceita jogador");
            return;
        }
        if(group!=null){
            if(group.permission!=null&&group.permission.length()>0&&!sender.hasPermission(group.permission)){
                notPermission(sender);
                return;
            }
        }
        if(permission!=null&&permission.length()>0&&!sender.hasPermission(permission)){
            notPermission(sender);
            return;
        }
        if(args.length>=minArgs){
            try {
                this.method.invoke(this.instance,sender,args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return;
        }
        sendHelp(sender);
    }

    @Override
    public void sendHelp(CommandSender sender) {
        if(help !=null && help != MessageAPI.UNDEFINED)sender.sendMessage(help.formattedMessage());
    }

    private void notPermission(CommandSender sender){
        sender.sendMessage("Sem permissão");
    }

    @Override
    public boolean isCommand() {
        return true;
    }

    @Override
    public boolean isGroup() {
        return false;
    }

    @Override
    public FCommand getCommand() {
        return this.fCommand;
    }

    @Override
    public FCommandGroup getCommandGroup() {
        return this.group.fCommandGroup;
    }

    public static class Group<T extends FCommandExecutor> implements FCommandInfo{
        
        protected FCommandGroup fCommandGroup;
        protected Class<T> clazz;
        protected T instance;
        protected List<FInfo> infos = new LinkedList<>();

        protected String name;
        protected List<String> aliases;
        protected boolean acceptPlayer;
        protected boolean acceptConsole;
        protected String permission;

        protected Group(Class<T> clazz){
            this.clazz=clazz;
            try {
                this.instance = clazz.getConstructor().newInstance();
                this.fCommandGroup=clazz.getAnnotation(FCommandGroup.class);
                this.name=this.fCommandGroup.name();
                this.aliases=Arrays.stream(this.fCommandGroup.aliases()).map(s->s.toLowerCase().trim()).collect(Collectors.toList());
                this.acceptPlayer=this.fCommandGroup.acceptPlayer();
                this.acceptConsole=this.fCommandGroup.acceptConsole();
                this.permission=this.fCommandGroup.permission();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected void load(){
            for(Method method : this.clazz.getMethods()){
                if(FInfo.isValid(method)){
                    register(new FInfo(method.getAnnotation(FCommand.class),method,this.clazz,this.instance));
                }
            }
            for(Method method : this.clazz.getDeclaredMethods()){
                if(FInfo.isValid(method)){
                    register(new FInfo(method.getAnnotation(FCommand.class),method,this.clazz,this.instance));
                }
            }
        }

        protected void register(FInfo info){
            this.infos.add(info);
            info.group=this;
        }

        public static boolean isValid(Class<?> clazz){
            return FInfo.isValid(clazz)&&clazz.isAnnotationPresent(FCommandGroup.class);
        }

        protected List<FInfo> getInfos() {
            return infos;
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            for(FInfo info : this.infos){
                if(info.name.equalsIgnoreCase("*")){
                    info.execute(sender,args);
                    return;
                }
            }
            sendHelp(sender);
        }

        @Override
        public void sendHelp(CommandSender sender) {
            for(FInfo info : this.infos){
                info.sendHelp(sender);
            }
        }

        @Override
        public boolean isCommand() {
            return false;
        }

        @Override
        public boolean isGroup() {
            return true;
        }

        @Override
        public FCommand getCommand() {
            return null;
        }

        @Override
        public FCommandGroup getCommandGroup() {
            return this.fCommandGroup;
        }
    }

}
