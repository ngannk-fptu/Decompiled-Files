/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util;

import com.atlassian.core.util.ClassLoaderUtils;
import java.lang.reflect.Method;
import java.util.function.Predicate;

public class ObjectUtils {
    protected static Method hibernateGetClassMethod = null;

    public static boolean isIdentical(Object a, Object b) {
        return !ObjectUtils.isDifferent(a, b);
    }

    public static boolean isDifferent(Object a, Object b) {
        return (a != null || b != null) && (a == null || !a.equals(b));
    }

    public static boolean isNotEmpty(Object o) {
        return o != null && !"".equals(o);
    }

    public static Predicate<Object> getIsSetPredicate() {
        return ObjectUtils::isNotEmpty;
    }

    public static Class getTrueClass(Object o) {
        if (hibernateGetClassMethod != null) {
            try {
                return (Class)hibernateGetClassMethod.invoke(null, o);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return o.getClass();
    }

    static {
        try {
            Class hibernateClass = ClassLoaderUtils.loadClass("net.sf.hibernate.Hibernate", ObjectUtils.class);
            hibernateGetClassMethod = hibernateClass.getMethod("getClass", Object.class);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

