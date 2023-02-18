package bd.pablo.redwins.machine.util;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {

    private static final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

    public static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
        Class<?> nmsClass = Class.forName("net.minecraft.server." + version +"."+ nmsClassString);
        return nmsClass;
    }

    public static Class<?> getOBCClass(String obcClass) throws ClassNotFoundException{
        return Class.forName("org.bukkit.craftbukkit." + version +"."+ obcClass);
    }

    public static Class<?> getNMSClassNoThrows(String nmsClassString) {
        Class<?> nmsClass = null;
        try {
            nmsClass = Class.forName("net.minecraft.server." + version +"."+ nmsClassString);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return nmsClass;
    }


    protected static Class<?>[] getDeclaredClasses(Class<?> clazz) {
        return clazz.getDeclaredClasses();
    }

    protected static Class<?>[] getNMSDeclaredClasses(String s) throws SecurityException, ClassNotFoundException {
        return getNMSClass(s).getDeclaredClasses();
    }

    protected static Constructor<?> getConstructor(Class<?> clazz, Class<?>... values) throws NoSuchMethodException, SecurityException {
        return clazz.getConstructor(values);
    }

    protected static Constructor<?> getDeclaredConstructor(Class<?> clazz, Class<?>... values) throws NoSuchMethodException, SecurityException {
        return clazz.getDeclaredConstructor(values);
    }

    protected static Object getInstance(Constructor<?> constructor, Object... values) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return constructor.newInstance(values);
    }

    protected static Object getInstance(Constructor<?> constructor) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return constructor.newInstance(new Object[0]);
    }

    protected static Method getMethod(Class<?> clazz, String methodname, Class<?>... values) throws NoSuchMethodException, SecurityException {
        return clazz.getMethod(methodname, values);
    }

    protected static Method getMethod(Class<?> clazz, String methodname) throws NoSuchMethodException, SecurityException {
        return clazz.getMethod(methodname);
    }

    protected static Method getDeclaredMethod(Class<?> clazz, String methodname, Class<?>... values) throws NoSuchMethodException, SecurityException {
        return clazz.getDeclaredMethod(methodname, values);
    }

    protected static Method getDeclaredMethod(Class<?> clazz, String methodname) throws NoSuchMethodException, SecurityException {
        return clazz.getDeclaredMethod(methodname);
    }

    protected static Object get(Field field, Object invoke) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException {
        return field.get(invoke);
    }

    protected static Object cast(Class<?> clazz, Object obj) {
        return clazz.cast(obj);
    }

    protected static Field getField(Class<?> clazz, String fieldname) throws NoSuchFieldException, SecurityException {
        return clazz.getField(fieldname);
    }

    protected static Object invoke(Method method,Object obj, Object... values) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return method.invoke(obj, values);
    }

    protected static Object invoke(Method method,Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return method.invoke(obj, new Object[0]);
    }

    protected static String getVersion() {
        return version;
    }

}
