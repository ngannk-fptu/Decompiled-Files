/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.hibernate.management.impl;

public abstract class EhcacheHibernateMbeanNames {
    public static String mbeanSafe(String s) {
        return s == null ? "" : s.replaceAll(",|:|=|\n", ".");
    }
}

