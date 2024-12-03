/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogAdapter;

public abstract class LogFactory {
    public static Log getLog(Class<?> clazz) {
        return LogFactory.getLog(clazz.getName());
    }

    public static Log getLog(String name) {
        return LogAdapter.createLog(name);
    }

    @Deprecated
    public static LogFactory getFactory() {
        return new LogFactory(){

            @Override
            public Object getAttribute(String name) {
                return null;
            }

            @Override
            public String[] getAttributeNames() {
                return new String[0];
            }

            @Override
            public void removeAttribute(String name) {
            }

            @Override
            public void setAttribute(String name, Object value) {
            }

            @Override
            public void release() {
            }
        };
    }

    @Deprecated
    public Log getInstance(Class<?> clazz) {
        return LogFactory.getLog(clazz);
    }

    @Deprecated
    public Log getInstance(String name) {
        return LogFactory.getLog(name);
    }

    @Deprecated
    public abstract Object getAttribute(String var1);

    @Deprecated
    public abstract String[] getAttributeNames();

    @Deprecated
    public abstract void removeAttribute(String var1);

    @Deprecated
    public abstract void setAttribute(String var1, Object var2);

    @Deprecated
    public abstract void release();

    @Deprecated
    public static void release(ClassLoader classLoader) {
    }

    @Deprecated
    public static void releaseAll() {
    }

    @Deprecated
    public static String objectId(Object o) {
        return o == null ? "null" : o.getClass().getName() + "@" + System.identityHashCode(o);
    }
}

