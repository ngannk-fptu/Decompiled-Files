/*
 * Decompiled with CFR 0.152.
 */
package org.apache.el.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import org.apache.el.util.JreCompat;

class Jre9Compat
extends JreCompat {
    private static final Method canAccessMethod;

    Jre9Compat() {
    }

    public static boolean isSupported() {
        return canAccessMethod != null;
    }

    @Override
    public boolean canAccess(Object base, AccessibleObject accessibleObject) {
        try {
            return (Boolean)canAccessMethod.invoke((Object)accessibleObject, base);
        }
        catch (IllegalArgumentException | ReflectiveOperationException e) {
            return false;
        }
    }

    static {
        Method m1 = null;
        try {
            m1 = AccessibleObject.class.getMethod("canAccess", Object.class);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        canAccessMethod = m1;
    }
}

