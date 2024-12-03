/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.util;

@Deprecated
public final class ReflectHelper {
    private ReflectHelper() {
    }

    @Deprecated
    public static Class classForName(String name, Class caller) throws ClassNotFoundException {
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                return contextClassLoader.loadClass(name);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return Class.forName(name, true, caller.getClassLoader());
    }
}

