/*
 * Decompiled with CFR 0.152.
 */
package org.apache.el.util;

import java.lang.reflect.AccessibleObject;
import org.apache.el.util.Jre9Compat;

public class JreCompat {
    private static final JreCompat instance = Jre9Compat.isSupported() ? new Jre9Compat() : new JreCompat();

    public static JreCompat getInstance() {
        return instance;
    }

    public boolean canAccess(Object base, AccessibleObject accessibleObject) {
        return true;
    }
}

