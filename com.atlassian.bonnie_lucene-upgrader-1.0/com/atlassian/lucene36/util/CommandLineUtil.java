/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.FSDirectory;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class CommandLineUtil {
    private CommandLineUtil() {
    }

    public static FSDirectory newFSDirectory(String clazzName, File file) {
        try {
            Class<? extends FSDirectory> clazz = CommandLineUtil.loadFSDirectoryClass(clazzName);
            return CommandLineUtil.newFSDirectory(clazz, file);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(FSDirectory.class.getSimpleName() + " implementation not found: " + clazzName, e);
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException(clazzName + " is not a " + FSDirectory.class.getSimpleName() + " implementation", e);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(clazzName + " constructor with " + File.class.getSimpleName() + " as parameter not found", e);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error creating " + clazzName + " instance", e);
        }
    }

    public static Class<? extends Directory> loadDirectoryClass(String clazzName) throws ClassNotFoundException {
        return Class.forName(CommandLineUtil.adjustDirectoryClassName(clazzName)).asSubclass(Directory.class);
    }

    public static Class<? extends FSDirectory> loadFSDirectoryClass(String clazzName) throws ClassNotFoundException {
        return Class.forName(CommandLineUtil.adjustDirectoryClassName(clazzName)).asSubclass(FSDirectory.class);
    }

    private static String adjustDirectoryClassName(String clazzName) {
        if (clazzName == null || clazzName.trim().length() == 0) {
            throw new IllegalArgumentException("The " + FSDirectory.class.getSimpleName() + " implementation cannot be null or empty");
        }
        if (clazzName.indexOf(".") == -1) {
            clazzName = Directory.class.getPackage().getName() + "." + clazzName;
        }
        return clazzName;
    }

    public static FSDirectory newFSDirectory(Class<? extends FSDirectory> clazz, File file) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<? extends FSDirectory> ctor = clazz.getConstructor(File.class);
        return ctor.newInstance(file);
    }
}

