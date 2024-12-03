/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.utils.CompareUtils;

public class ServiceLoaderUtils {
    public static <T> void sortLoadedClasses(List<T> loaded) {
        loaded.sort(CompareUtils::compareClassName);
    }

    public static <T> T newInstance(String className) {
        return ServiceLoaderUtils.newInstance(className, ServiceLoader.class.getClassLoader());
    }

    public static <T> T newInstance(String className, ClassLoader loader) {
        try {
            return (T)Class.forName(className, true, loader).newInstance();
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(Class klass, ServiceLoader loader) {
        try {
            try {
                Constructor constructor = klass.getDeclaredConstructor(ServiceLoader.class);
                return constructor.newInstance(loader);
            }
            catch (NoSuchMethodException e) {
                return klass.newInstance();
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}

