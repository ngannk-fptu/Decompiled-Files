/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.util;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

public class Introspection {
    private static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.util");

    public static String getPropertyName(Method setter) {
        return Introspector.decapitalize(setter.getName().substring(3));
    }

    public static boolean isValidSetter(Method method) {
        return method.getName().startsWith("set") && method.getName().length() > 3 && method.getParameterTypes().length == 1 && method.getReturnType().getName().equals("void");
    }

    public static boolean isValidLifecycleCallback(Method method) {
        return method.getParameterTypes().length == 0 && !Modifier.isStatic(method.getModifiers()) && method.getExceptionTypes().length <= 0 && method.getReturnType().getName().equals("void");
    }

    public static Field[] getDeclaredFields(Class<?> clazz) {
        Field[] fields = null;
        fields = Globals.IS_SECURITY_ENABLED ? AccessController.doPrivileged(clazz::getDeclaredFields) : clazz.getDeclaredFields();
        return fields;
    }

    public static Method[] getDeclaredMethods(Class<?> clazz) {
        Method[] methods = null;
        methods = Globals.IS_SECURITY_ENABLED ? AccessController.doPrivileged(clazz::getDeclaredMethods) : clazz.getDeclaredMethods();
        return methods;
    }

    public static Class<?> loadClass(Context context, String className) {
        ClassLoader cl = context.getLoader().getClassLoader();
        Log log = context.getLogger();
        Class<?> clazz = null;
        try {
            clazz = cl.loadClass(className);
        }
        catch (ClassFormatError | ClassNotFoundException | NoClassDefFoundError e) {
            log.debug((Object)sm.getString("introspection.classLoadFailed", new Object[]{className}), e);
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            log.debug((Object)sm.getString("introspection.classLoadFailed", new Object[]{className}), t);
        }
        return clazz;
    }

    public static Class<?> convertPrimitiveType(Class<?> clazz) {
        if (clazz.equals(Character.TYPE)) {
            return Character.class;
        }
        if (clazz.equals(Integer.TYPE)) {
            return Integer.class;
        }
        if (clazz.equals(Boolean.TYPE)) {
            return Boolean.class;
        }
        if (clazz.equals(Double.TYPE)) {
            return Double.class;
        }
        if (clazz.equals(Byte.TYPE)) {
            return Byte.class;
        }
        if (clazz.equals(Short.TYPE)) {
            return Short.class;
        }
        if (clazz.equals(Long.TYPE)) {
            return Long.class;
        }
        if (clazz.equals(Float.TYPE)) {
            return Float.class;
        }
        return clazz;
    }
}

