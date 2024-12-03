/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

public enum NullPrecedence {
    NONE,
    FIRST,
    LAST;


    public static NullPrecedence parse(String name) {
        if ("none".equalsIgnoreCase(name)) {
            return NONE;
        }
        if ("first".equalsIgnoreCase(name)) {
            return FIRST;
        }
        if ("last".equalsIgnoreCase(name)) {
            return LAST;
        }
        return null;
    }

    public static NullPrecedence parse(String name, NullPrecedence defaultValue) {
        NullPrecedence value = NullPrecedence.parse(name);
        return value != null ? value : defaultValue;
    }
}

