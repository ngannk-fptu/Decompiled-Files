/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang;

public final class NullUtils {
    public static boolean equalsOrBothNull(Object object, Object object2) {
        if (object == object2) {
            return true;
        }
        if (object == null) {
            return false;
        }
        return object.equals(object2);
    }

    private NullUtils() {
    }
}

