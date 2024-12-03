/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.internal;

public final class SystemProperty {
    public static final String get(String property, String def) {
        try {
            return System.getProperty(property, def);
        }
        catch (SecurityException se) {
            return def;
        }
    }
}

