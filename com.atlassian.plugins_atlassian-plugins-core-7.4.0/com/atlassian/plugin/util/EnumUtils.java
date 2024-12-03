/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.util;

public class EnumUtils {
    public static <E extends Enum<E>> E enumValueFromProperty(String propertyName, E[] values, E defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (null != propertyValue) {
            for (E value : values) {
                if (!((Enum)value).name().equalsIgnoreCase(propertyValue)) continue;
                return value;
            }
        }
        return defaultValue;
    }
}

