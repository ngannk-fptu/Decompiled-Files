/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.utils;

public class CompareUtils {
    public static int compareClassName(Object o1, Object o2) {
        boolean tika2;
        String n1 = o1.getClass().getName();
        String n2 = o2.getClass().getName();
        boolean tika1 = n1.startsWith("org.apache.tika.");
        if (tika1 == (tika2 = n2.startsWith("org.apache.tika."))) {
            return n1.compareTo(n2);
        }
        return tika1 ? 1 : -1;
    }
}

