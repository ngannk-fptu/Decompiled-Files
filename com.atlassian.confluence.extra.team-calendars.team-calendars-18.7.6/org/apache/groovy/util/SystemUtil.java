/*
 * Decompiled with CFR 0.152.
 */
package org.apache.groovy.util;

public class SystemUtil {
    public static String setSystemPropertyFrom(String nameValue) {
        String value;
        String name;
        if (nameValue == null) {
            throw new IllegalArgumentException("argument should not be null");
        }
        int i = nameValue.indexOf("=");
        if (i == -1) {
            name = nameValue;
            value = Boolean.TRUE.toString();
        } else {
            name = nameValue.substring(0, i);
            value = nameValue.substring(i + 1, nameValue.length());
        }
        name = name.trim();
        System.setProperty(name, value);
        return name;
    }
}

