/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.model;

import java.lang.reflect.Method;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
final class InjectorHelper {
    private InjectorHelper() {
    }

    static Method getMethod(Class<?> c, String methodname, Class<?> ... params) {
        try {
            Method m = c.getDeclaredMethod(methodname, params);
            m.setAccessible(true);
            return m;
        }
        catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }
}

