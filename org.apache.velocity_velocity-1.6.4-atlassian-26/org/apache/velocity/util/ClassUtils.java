/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.velocity.util;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.util.ClassConstructionException;
import org.apache.velocity.util.ClassResolutionException;

public class ClassUtils {
    private ClassUtils() {
    }

    public static Class getClass(String clazz) throws ClassNotFoundException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            try {
                return Class.forName(clazz, true, loader);
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        return Class.forName(clazz);
    }

    public static Object getNewInstance(String clazz) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return ClassUtils.getClass(clazz).newInstance();
    }

    public static InputStream getResourceAsStream(Class claz, String name) {
        InputStream result;
        while (name.startsWith("/")) {
            name = name.substring(1);
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = claz.getClassLoader();
            result = classLoader.getResourceAsStream(name);
        } else {
            result = classLoader.getResourceAsStream(name);
            if (result == null && (classLoader = claz.getClassLoader()) != null) {
                result = classLoader.getResourceAsStream(name);
            }
        }
        return result;
    }

    public static <T> T getNewInstance(String className, Class<T> expectedInterface, Object ... params) throws ClassConstructionException {
        if (StringUtils.isNotEmpty((CharSequence)className)) {
            Class clazz;
            try {
                clazz = ClassUtils.getClass(className);
            }
            catch (ClassNotFoundException cnfe) {
                throw new ClassResolutionException("The specified class for (" + className + ") does not exist or is not accessible to the current classloader.", cnfe);
            }
            catch (Exception e) {
                throw new ClassResolutionException("The specified class (" + className + ") can not be loaded.");
            }
            if (!expectedInterface.isAssignableFrom(clazz)) {
                throw new ClassResolutionException("The specified class (" + className + ") does not implement " + expectedInterface.getName() + ".");
            }
            try {
                return clazz.getConstructor(ClassUtils.getParameterTypes(params)).newInstance(params);
            }
            catch (InstantiationException e) {
                throw new ClassConstructionException(e);
            }
            catch (IllegalAccessException e) {
                throw new ClassConstructionException(e);
            }
            catch (InvocationTargetException e) {
                throw new ClassConstructionException(e);
            }
            catch (NoSuchMethodException e) {
                throw new ClassConstructionException(e);
            }
        }
        throw new ClassConstructionException("No class name specified");
    }

    private static Class<?>[] getParameterTypes(Object[] params) {
        if (params == null || params.length == 0) {
            return new Class[0];
        }
        Class[] types = new Class[params.length];
        for (int i = 0; i < params.length; ++i) {
            types[i] = params[i].getClass();
        }
        return types;
    }
}

