/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.util;

import java.beans.Introspector;
import java.lang.reflect.Method;

public final class BeanUtils {
    public static String capitalize(String name) {
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1))) {
            return name;
        }
        if (name.length() > 1) {
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
        return name.toUpperCase();
    }

    public static String uncapitalize(String name) {
        return Introspector.decapitalize(name);
    }

    public static boolean isAccessorPresent(String prefix, String property, Class<?> bean) {
        try {
            bean.getMethod(prefix + BeanUtils.capitalize(property), new Class[0]);
            return true;
        }
        catch (NoSuchMethodException ex) {
            return false;
        }
    }

    public static Method getAccessor(String prefix, String property, Class<?> bean) {
        try {
            return bean.getMethod(prefix + BeanUtils.capitalize(property), new Class[0]);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    private BeanUtils() {
    }
}

