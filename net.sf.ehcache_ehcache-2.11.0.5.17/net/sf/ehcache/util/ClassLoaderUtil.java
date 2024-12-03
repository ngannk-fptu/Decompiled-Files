/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.sf.ehcache.CacheException;

public final class ClassLoaderUtil {
    private ClassLoaderUtil() {
    }

    public static Object createNewInstance(ClassLoader loader, String className) throws CacheException {
        return ClassLoaderUtil.createNewInstance(loader, className, new Class[0], new Object[0]);
    }

    public static Object createNewInstance(ClassLoader loader, String className, Class[] argTypes, Object[] args) throws CacheException {
        Object newInstance;
        Class<?> clazz;
        try {
            clazz = loader.loadClass(className);
        }
        catch (ClassNotFoundException e) {
            throw new CacheException("Unable to load class " + className + ". Initial cause was " + e.getMessage(), e);
        }
        try {
            Constructor<?> constructor = clazz.getConstructor(argTypes);
            newInstance = constructor.newInstance(args);
        }
        catch (IllegalAccessException e) {
            throw new CacheException("Unable to load class " + className + ". Initial cause was " + e.getMessage(), e);
        }
        catch (InstantiationException e) {
            throw new CacheException("Unable to load class " + className + ". Initial cause was " + e.getMessage(), e);
        }
        catch (NoSuchMethodException e) {
            throw new CacheException("Unable to load class " + className + ". Initial cause was " + e.getMessage(), e);
        }
        catch (SecurityException e) {
            throw new CacheException("Unable to load class " + className + ". Initial cause was " + e.getMessage(), e);
        }
        catch (IllegalArgumentException e) {
            throw new CacheException("Unable to load class " + className + ". Initial cause was " + e.getMessage(), e);
        }
        catch (InvocationTargetException e) {
            throw new CacheException("Unable to load class " + className + ". Initial cause was " + e.getCause().getMessage(), e.getCause());
        }
        return newInstance;
    }
}

