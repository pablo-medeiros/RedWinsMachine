package bd.pablo.redwins.machine.util;

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NBTItem extends Reflection {

    private ItemStack item;
    private NBTTag tag;

    public static NBTItem builder(ItemStack itemStack){
        return new NBTItem(itemStack);
    }

    public NBTItem(ItemStack itemStack){
        this.item = itemStack;
        this.tag = new NBTTag(this, null);
    }

    public NBTTag getTag() {
        return tag;
    }

    public ItemStack build(){
        try {
            Class<?> CraftItemStack = getOBCClass("inventory.CraftItemStack");
            Object itemStack = getMethod(CraftItemStack, "asNMSCopy", ItemStack.class).invoke(CraftItemStack, item);
            Object tagCompound = ((boolean) itemStack.getClass().getMethod("hasTag").invoke(itemStack)) ? itemStack.getClass().getMethod("getTag").invoke(itemStack) : getNMSClass("NBTTagCompound").newInstance();
            tag.build(tagCompound);
            itemStack.getClass().getMethod("setTag", tagCompound.getClass()).invoke(itemStack, tagCompound);
            return (ItemStack) CraftItemStack.getMethod("asCraftMirror", getNMSClass("ItemStack")).invoke(CraftItemStack, itemStack);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    public void load(){
        try{
            Class<?> CraftItemStack = getOBCClass("inventory.CraftItemStack");
            Object itemStack = getMethod(CraftItemStack, "asNMSCopy", ItemStack.class).invoke(CraftItemStack, item);
            if(!((boolean)itemStack.getClass().getMethod("hasTag").invoke(itemStack)))return;
            Object tag = itemStack.getClass().getMethod("getTag").invoke(itemStack);
            this.tag.load(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return tag.toString();
    }

    public static class NBTTag extends NBTValue{

        private Map<String, NBTValue> map = new HashMap<>();

        private NBTTag(NBTItem root, NBTValue parent) {
            super(root, parent, NBTTypeValue.MAP, null);
            super.value = this;
        }

        private Object build(Object compound) throws Exception {
            for(Map.Entry<String, NBTValue> entry : this.map.entrySet()){
                Object o = entry.getValue().value;
                if(entry.getValue().type == NBTTypeValue.MAP){
                    Object tagCompound = Reflection.getNMSClass("NBTTagCompound").newInstance();
                    o = ((NBTTag) o).build(tagCompound);
                }else if(entry.getValue().type == NBTTypeValue.LIST){
                    Object tagCompound = Reflection.getNMSClass("NBTTagList").newInstance();
                    o = ((NBTList) o).build(tagCompound);
                }
                Method m;
                try{
                    m = compound.getClass().getMethod(entry.getValue().type.setMethod,String.class,entry.getValue().type.parameterType);
                }catch (Exception e){
                    m = compound.getClass().getDeclaredMethod(entry.getValue().type.setMethod,String.class,entry.getValue().type.parameterType);
                }
                m.setAccessible(true);
                m.invoke(compound,entry.getKey(),o);
            }

            return compound;
        }

        private void load(Object compound) throws Exception{
            Field fieldMap = compound.getClass().getDeclaredField("map");
            fieldMap.setAccessible(true);
            Map<String, ?> map = (Map<String,?>) fieldMap.get(compound);
            for(Map.Entry<String,?> entry : map.entrySet()){
                byte id = (byte) entry.getValue().getClass().getMethod("getTypeId").invoke(entry.getValue());
                NBTTypeValue type = NBTTypeValue.getById(id);
                if(type == null)continue;
                NBTValue value = null;
                switch (type){
                    case STRING:
                    case INT:
                    case LONG:
                    case DOUBLE:
                    case FLOAT:
                        Field f = entry.getValue().getClass().getDeclaredField("data");
                        f.setAccessible(true);
                        Object v = f.get(entry.getValue());
                        value = new NBTValue(super.root, this, type,v);
                        break;
                    case MAP:
                        NBTTag tag = new NBTTag(super.root, this);
                        tag.load(entry.getValue());
                        value = tag;
                        break;
                    case LIST:
                        NBTList list = new NBTList(super.root, this);
                        list.load(entry.getValue());
                        value = list;
                        break;
                }
                if(value!=null)this.map.put(entry.getKey(),value);
            }
        }

        @Override
        public String toString() {
            String s = map.entrySet().stream().map(e->e.getKey()+": "+e.getValue().toString()).collect(Collectors.joining(",\n  "));
            return "NBTTag{\n" +
                    "  "+s +
                    "\n" +
                    "}";
        }

        public boolean has(String key){
            return map.containsKey(key);
        }

        public String string(String key){
            NBTValue value = map.get(key);
            if(value==null)return null;
            return value.string();
        }

        public NBTTag string(String key, String value){
            map.put(key,new NBTValue(super.root, this, NBTTypeValue.STRING,value));
            return this;
        }

        public int integer(String key){
            NBTValue value = map.get(key);
            if(value==null)return 0;
            return value.integer();
        }

        public NBTTag integer(String key, int value){
            map.put(key,new NBTValue(super.root, this, NBTTypeValue.INT,value));
            return this;
        }

        public long longer(String key){
            NBTValue value = map.get(key);
            if(value==null)return 0;
            return value.longer();
        }

        public NBTTag longer(String key, long value){
            map.put(key,new NBTValue(super.root, this, NBTTypeValue.LONG,value));
            return this;
        }

        public double doubles(String key){
            NBTValue value = map.get(key);
            if(value==null)return 0;
            return value.doubles();
        }

        public NBTTag doubles(String key, double value){
            map.put(key,new NBTValue(super.root, this, NBTTypeValue.DOUBLE,value));
            return this;
        }

        public float floats(String key){
            NBTValue value = map.get(key);
            if(value==null)return 0;
            return value.floats();
        }

        public NBTTag floats(String key, float value){
            map.put(key,new NBTValue(super.root, this, NBTTypeValue.FLOAT,value));
            return this;
        }

        public NBTList findList(String key){
            NBTValue value = map.get(key);
            if(value==null)return null;
            return value.list();
        }

        public NBTList list(String key){
            NBTList list1 = new NBTList(super.root, this);
            this.map.put(key,list1);
            return list1;
        }

        public NBTTag findTag(String key){
            NBTValue value = map.get(key);
            if(value==null)return null;
            return value.tag();
        }

        public NBTTag map(String key){
            NBTTag map = new NBTTag(super.root, this);
            this.map.put(key,map);
            return map;
        }

        public Map<String, NBTValue> toMap(){
            return new HashMap<>(this.map);
        }

        public boolean isEmpty(){
            return this.map.size()==0;
        }

        public void concat(NBTTag tag) {
            tag.map.keySet().forEach(key->{
                this.map.put(key,tag.map.get(key));
            });
        }
    }

    public static class NBTList extends NBTValue{

        private List<NBTValue> list = new ArrayList<>();

        private NBTList(NBTItem root, NBTValue parent) {
            super(root, parent, NBTTypeValue.LIST, null);
            super.value = this;
        }

        private Object build(Object compound) throws Exception {
            for(NBTValue value : list){
                Object o = value.value;
                if(value.type == NBTTypeValue.MAP){
                    Object tagCompound = Reflection.getNMSClass("NBTTagCompound").newInstance();
                    o = ((NBTTag) o).build(tagCompound);
                }else if(value.type == NBTTypeValue.LIST){
                    Object tagCompound = Reflection.getNMSClass("NBTTagList").newInstance();
                    o = ((NBTList) o).build(tagCompound);
                }else {
                    Method m = Reflection.getNMSClass("NBTBase").getDeclaredMethod("createTag",byte.class);
                    m.setAccessible(true);
                    Object base = m.invoke(null,value.type.id);
                    Field f = base.getClass().getDeclaredField("data");
                    f.setAccessible(true);
                    f.set(base,o);
                    o = base;
                }
                compound.getClass().getMethod("add",Reflection.getNMSClass("NBTBase")).invoke(compound,o);
            }
            return compound;
        }

        private void load(Object compound) throws Exception{
            Field fieldList = compound.getClass().getDeclaredField("list");
            fieldList.setAccessible(true);
            List<?> list = (List<?>) fieldList.get(compound);
            for(Object o : list){
                byte id = (byte) o.getClass().getMethod("getTypeId").invoke(o);
                NBTTypeValue type = NBTTypeValue.getById(id);
                if(type == null)continue;
                NBTValue value = null;
                switch (type){
                    case STRING:
                    case INT:
                    case LONG:
                    case DOUBLE:
                    case FLOAT:
                        Field f = o.getClass().getDeclaredField("data");
                        f.setAccessible(true);
                        Object v = f.get(o);
                        value = new NBTValue(super.root, this, type,v);
                        break;
                    case MAP:
                        NBTTag tag = new NBTTag(super.root, this);
                        tag.load(o);
                        value = tag;
                        break;
                    case LIST:
                        NBTList l = new NBTList(super.root, this);
                        l.load(o);
                        value = l;
                        break;
                }
                if(value!=null)this.list.add(value);
            }
        }

        @Override
        public String toString() {

            return "NBTList[\n" +
                    "  "+this.list.stream().map(o->o.toString()).collect(Collectors.joining(",\n  ")) +
                    "\n" +
                    "]";
        }

        public List<NBTValue> toList(){
            return new ArrayList<>(this.list);
        }

        public NBTList string(String value){
            this.list.add(new NBTValue(super.root, this, NBTTypeValue.STRING,value));
            return this;
        }

        public NBTList integer(int value){
            this.list.add(new NBTValue(super.root, this, NBTTypeValue.INT,value));
            return this;
        }


        public NBTList longer(long value){
            this.list.add(new NBTValue(super.root, this, NBTTypeValue.LONG,value));
            return this;
        }

        public NBTList doubles(double value){
            this.list.add(new NBTValue(super.root, this, NBTTypeValue.DOUBLE,value));
            return this;
        }

        public NBTList floats(float value){
            this.list.add(new NBTValue(super.root, this, NBTTypeValue.FLOAT,value));
            return this;
        }

        public NBTList list(){
            NBTList list1 = new NBTList(super.root, this);
            this.list.add(list1);
            return list1;
        }

        public NBTTag map(){
            NBTTag map = new NBTTag(super.root, this);
            this.list.add(map);
            return map;
        }

        public boolean isEmpty(){
            return this.list.size()==0;
        }

        public void concat(NBTList list){
            this.list.addAll(list.list);
        }

    }

    public static class NBTValue {

        private NBTItem root;
        private NBTValue parent;
        protected NBTTypeValue type;
        protected Object value;

        private NBTValue(NBTItem root, NBTValue parent, NBTTypeValue type, Object value) {
            this.root = root;
            this.parent = parent;
            this.type = type;
            this.value = value;
        }

        public NBTValue getParent() {
            return parent;
        }

        public boolean isParent(){
            return parent != null;
        }

        public NBTItem getRoot() {
            return root;
        }

        public NBTTypeValue getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }

        public String string(){
            return type==NBTTypeValue.STRING? (String) value :value.toString();
        }

        public int integer(){
            return type.isNumber() ? (int) value : 0;
        }

        public long longer(){
            return type.isNumber() ? (long) value : 0;
        }

        public double doubles(){
            return type.isNumber() ? (double) value : 0;
        }

        public float floats(){
            return type.isNumber() ? (float) value : 0;
        }

        public NBTList list(){
            return type == NBTTypeValue.LIST ? (NBTList) value : null;
        }


        public NBTTag tag(){
            return type == NBTTypeValue.MAP ? (NBTTag) value : null;
        }



        @Override
        public String toString() {
            return value.toString()+" ("+type.toString()+")";
        }

        public boolean isEmpty(){
            return this.value==null;
        }

    }

    private enum NBTTypeValue {
        STRING("setString","getString", String.class,(byte)8),
        INT("setInt","getInt", int.class,(byte)3),
        LONG("setLong","getLong", long.class,(byte)4),
        DOUBLE("setDouble","getDouble", double.class,(byte)6),
        FLOAT("setFloat","getFloat", float.class,(byte)5),
        MAP("set","getCompound", Reflection.getNMSClassNoThrows("NBTBase"),(byte)10),
        LIST("set","getList", Reflection.getNMSClassNoThrows("NBTBase"),(byte)9);

        private String setMethod;
        private String getMethod;
        private Class parameterType;
        private byte id;

        NBTTypeValue(String setMethod, String getMethod, Class parameterType, byte id) {
            this.setMethod = setMethod;
            this.getMethod = getMethod;
            this.parameterType = parameterType;
            this.id = id;
        }

        public static NBTTypeValue getById(byte id){
            for(NBTTypeValue value : values()){
                if(value.id == id)return value;
            }
            return null;
        }

        public boolean isNumber(){
            switch (this){
                case INT:
                case LONG:
                case DOUBLE:
                case FLOAT:
                    return true;
            }
            return false;
        }
    }
}
