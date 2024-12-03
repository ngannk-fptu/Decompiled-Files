/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.asm;

import java.lang.reflect.Method;
import net.minidev.asm.BeansAccess;

class DynamicClassLoader
extends ClassLoader {
    private static final String BEAN_AC = BeansAccess.class.getName();
    private static final Class<?>[] DEF_CLASS_SIG = new Class[]{String.class, byte[].class, Integer.TYPE, Integer.TYPE};

    DynamicClassLoader(ClassLoader parent) {
        super(parent);
    }

    public static <T> Class<T> directLoad(Class<? extends T> parent, String clsName, byte[] clsData) {
        DynamicClassLoader loader = new DynamicClassLoader(parent.getClassLoader());
        Class<?> clzz = loader.defineClass(clsName, clsData);
        return clzz;
    }

    public static <T> T directInstance(Class<? extends T> parent, String clsName, byte[] clsData) throws InstantiationException, IllegalAccessException {
        Class<T> clzz = DynamicClassLoader.directLoad(parent, clsName, clsData);
        return clzz.newInstance();
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.equals(BEAN_AC)) {
            return BeansAccess.class;
        }
        return super.loadClass(name, resolve);
    }

    Class<?> defineClass(String name, byte[] bytes) throws ClassFormatError {
        try {
            Method method = ClassLoader.class.getDeclaredMethod("defineClass", DEF_CLASS_SIG);
            method.setAccessible(true);
            return (Class)method.invoke((Object)this.getParent(), name, bytes, 0, bytes.length);
        }
        catch (Exception exception) {
            return this.defineClass(name, bytes, 0, bytes.length);
        }
    }
}

