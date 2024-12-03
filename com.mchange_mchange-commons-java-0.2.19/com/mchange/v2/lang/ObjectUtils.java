/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.lang;

public final class ObjectUtils {
    public static boolean eqOrBothNull(Object object, Object object2) {
        if (object == object2) {
            return true;
        }
        if (object == null) {
            return false;
        }
        return object.equals(object2);
    }

    public static int hashOrZero(Object object) {
        return object == null ? 0 : object.hashCode();
    }

    private ObjectUtils() {
    }
}

