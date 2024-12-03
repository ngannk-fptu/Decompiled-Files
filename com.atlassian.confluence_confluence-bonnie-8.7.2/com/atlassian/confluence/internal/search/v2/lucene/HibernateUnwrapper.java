/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import java.lang.reflect.Method;

public class HibernateUnwrapper {
    private static Method getClassMethod = null;

    public static Class<?> getUnderlyingClass(Object o) {
        if (getClassMethod == null) {
            return o.getClass();
        }
        try {
            return (Class)getClassMethod.invoke(null, o);
        }
        catch (Exception e) {
            return o.getClass();
        }
    }

    static {
        ClassLoader classLoader = HibernateUnwrapper.class.getClassLoader();
        try {
            Class<?> hibernateClass = classLoader.loadClass("net.sf.hibernate.Hibernate");
            getClassMethod = hibernateClass.getMethod("getClass", Object.class);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

