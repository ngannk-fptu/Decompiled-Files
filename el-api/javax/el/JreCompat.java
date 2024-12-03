/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.lang.reflect.AccessibleObject;
import javax.el.Jre9Compat;

class JreCompat {
    private static final JreCompat instance = Jre9Compat.isSupported() ? new Jre9Compat() : new JreCompat();

    JreCompat() {
    }

    public static JreCompat getInstance() {
        return instance;
    }

    public boolean canAccess(Object base, AccessibleObject accessibleObject) {
        return true;
    }

    public boolean isExported(Class<?> type) {
        return true;
    }
}

