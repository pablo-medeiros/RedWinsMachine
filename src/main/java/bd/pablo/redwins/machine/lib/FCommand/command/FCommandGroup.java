package bd.pablo.redwins.machine.lib.FCommand.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FCommandGroup {

    String name();
    String[] aliases() default {};
    boolean acceptPlayer() default true;
    boolean acceptConsole() default true;
    String permission() default "";

}
