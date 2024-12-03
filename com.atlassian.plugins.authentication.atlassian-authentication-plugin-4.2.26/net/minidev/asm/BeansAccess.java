/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.asm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minidev.asm.ASMUtil;
import net.minidev.asm.Accessor;
import net.minidev.asm.BeansAccessBuilder;
import net.minidev.asm.BeansAccessConfig;
import net.minidev.asm.DynamicClassLoader;
import net.minidev.asm.FieldFilter;
import net.minidev.asm.ex.NoSuchFieldException;

public abstract class BeansAccess<T> {
    private HashMap<String, Accessor> map;
    private Accessor[] accs;
    private static ConcurrentHashMap<Class<?>, BeansAccess<?>> cache = new ConcurrentHashMap();

    protected void setAccessor(Accessor[] accs) {
        int i = 0;
        this.accs = accs;
        this.map = new HashMap();
        for (Accessor acc : accs) {
            acc.index = i++;
            this.map.put(acc.getName(), acc);
        }
    }

    public HashMap<String, Accessor> getMap() {
        return this.map;
    }

    public Accessor[] getAccessors() {
        return this.accs;
    }

    public static <P> BeansAccess<P> get(Class<P> type) {
        return BeansAccess.get(type, null);
    }

    public static <P> BeansAccess<P> get(Class<P> type, FieldFilter filter) {
        BeansAccess<?> access = cache.get(type);
        if (access != null) {
            return access;
        }
        Accessor[] accs = ASMUtil.getAccessors(type, filter);
        String className = type.getName();
        String accessClassName = className.startsWith("java.util.") ? "net.minidev.asm." + className + "AccAccess" : className.concat("AccAccess");
        DynamicClassLoader loader = new DynamicClassLoader(type.getClassLoader());
        Class<?> accessClass = null;
        try {
            accessClass = loader.loadClass(accessClassName);
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        LinkedList<Class<P>> parentClasses = BeansAccess.getParents(type);
        if (accessClass == null) {
            BeansAccessBuilder builder = new BeansAccessBuilder(type, accs, loader);
            for (Class clazz : parentClasses) {
                builder.addConversion((Iterable)BeansAccessConfig.classMapper.get(clazz));
            }
            accessClass = builder.bulid();
        }
        try {
            BeansAccess access2 = (BeansAccess)accessClass.newInstance();
            access2.setAccessor(accs);
            cache.putIfAbsent(type, access2);
            for (Class clazz : parentClasses) {
                BeansAccess.addAlias(access2, BeansAccessConfig.classFiledNameMapper.get(clazz));
            }
            return access2;
        }
        catch (Exception ex) {
            throw new RuntimeException("Error constructing accessor class: " + accessClassName, ex);
        }
    }

    private static LinkedList<Class<?>> getParents(Class<?> type) {
        LinkedList m = new LinkedList();
        while (type != null && !type.equals(Object.class)) {
            m.addLast(type);
            for (Class<?> c : type.getInterfaces()) {
                m.addLast(c);
            }
            type = type.getSuperclass();
        }
        m.addLast(Object.class);
        return m;
    }

    private static void addAlias(BeansAccess<?> access, HashMap<String, String> m) {
        if (m == null) {
            return;
        }
        HashMap<String, Accessor> changes = new HashMap<String, Accessor>();
        for (Map.Entry<String, String> e : m.entrySet()) {
            Accessor a1 = access.map.get(e.getValue());
            if (a1 == null) continue;
            changes.put(e.getValue(), a1);
        }
        access.map.putAll(changes);
    }

    public abstract void set(T var1, int var2, Object var3);

    public abstract Object get(T var1, int var2);

    public abstract T newInstance();

    public void set(T object, String methodName, Object value) {
        int i = this.getIndex(methodName);
        if (i == -1) {
            throw new NoSuchFieldException(methodName + " in " + object.getClass() + " to put value : " + value);
        }
        this.set(object, i, value);
    }

    public Object get(T object, String methodName) {
        return this.get(object, this.getIndex(methodName));
    }

    public int getIndex(String name) {
        Accessor ac = this.map.get(name);
        if (ac == null) {
            return -1;
        }
        return ac.index;
    }
}

