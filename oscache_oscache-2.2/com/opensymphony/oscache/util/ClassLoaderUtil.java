/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.util;

public class ClassLoaderUtil {
    static /* synthetic */ Class class$com$opensymphony$oscache$util$ClassLoaderUtil;

    public static Class loadClass(String className, Class callingClass) throws ClassNotFoundException {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e) {
            try {
                return Class.forName(className);
            }
            catch (ClassNotFoundException ex) {
                try {
                    return (class$com$opensymphony$oscache$util$ClassLoaderUtil == null ? (class$com$opensymphony$oscache$util$ClassLoaderUtil = ClassLoaderUtil.class$("com.opensymphony.oscache.util.ClassLoaderUtil")) : class$com$opensymphony$oscache$util$ClassLoaderUtil).getClassLoader().loadClass(className);
                }
                catch (ClassNotFoundException exc) {
                    return callingClass.getClassLoader().loadClass(className);
                }
            }
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

