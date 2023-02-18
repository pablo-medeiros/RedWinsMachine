package bd.pablo.redwins.machine.lib.FCommand.command;

import bd.pablo.redwins.machine.util.MessageAPI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FCommand {

    String name() default "*";
    String[] aliases() default {};
    int minArgs() default 0;
    boolean acceptPlayer() default true;
    boolean acceptConsole() default true;
    MessageAPI help() default MessageAPI.UNDEFINED;
    String permission() default "";

}
