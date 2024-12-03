/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.tika.config.ServiceLoader;

public class ServiceLoaderUtils {
    public static <T> void sortLoadedClasses(List<T> loaded) {
        Collections.sort(loaded, new Comparator<T>(){

            @Override
            public int compare(T c1, T c2) {
                boolean t2;
                String n1 = c1.getClass().getName();
                String n2 = c2.getClass().getName();
                boolean t1 = n1.startsWith("org.apache.tika.");
                if (t1 == (t2 = n2.startsWith("org.apache.tika."))) {
                    return n1.compareTo(n2);
                }
                if (t1) {
                    return -1;
                }
                return 1;
            }
        });
    }

    public static <T> T newInstance(String className) {
        return ServiceLoaderUtils.newInstance(className, ServiceLoader.class.getClassLoader());
    }

    public static <T> T newInstance(String className, ClassLoader loader) {
        try {
            Class<?> loadedClass;
            Class<?> castedClass = loadedClass = Class.forName(className, true, loader);
            Object instance = castedClass.newInstance();
            return (T)instance;
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}

