/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.transport.jms;

import java.util.Map;

public class MapUtils {
    public static int removeIntProperty(Map properties, String key, int defaultValue) {
        int value = defaultValue;
        if (properties != null && properties.containsKey(key)) {
            try {
                value = (Integer)properties.remove(key);
            }
            catch (Exception ignore) {
                // empty catch block
            }
        }
        return value;
    }

    public static long removeLongProperty(Map properties, String key, long defaultValue) {
        long value = defaultValue;
        if (properties != null && properties.containsKey(key)) {
            try {
                value = (Long)properties.remove(key);
            }
            catch (Exception ignore) {
                // empty catch block
            }
        }
        return value;
    }

    public static String removeStringProperty(Map properties, String key, String defaultValue) {
        String value = defaultValue;
        if (properties != null && properties.containsKey(key)) {
            try {
                value = (String)properties.remove(key);
            }
            catch (Exception ignore) {
                // empty catch block
            }
        }
        return value;
    }

    public static boolean removeBooleanProperty(Map properties, String key, boolean defaultValue) {
        boolean value = defaultValue;
        if (properties != null && properties.containsKey(key)) {
            try {
                value = (Boolean)properties.remove(key);
            }
            catch (Exception ignore) {
                // empty catch block
            }
        }
        return value;
    }

    public static Object removeObjectProperty(Map properties, String key, Object defaultValue) {
        Object value = defaultValue;
        if (properties != null && properties.containsKey(key)) {
            value = properties.remove(key);
        }
        return value;
    }
}

