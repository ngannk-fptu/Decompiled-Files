/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.MDC
 */
package org.apache.log4j;

import java.util.Hashtable;
import java.util.Map;

public class MDC {
    public static void put(String key, String value) {
        org.slf4j.MDC.put((String)key, (String)value);
    }

    public static void put(String key, Object value) {
        if (value != null) {
            MDC.put(key, value.toString());
        } else {
            MDC.put(key, null);
        }
    }

    public static Object get(String key) {
        return org.slf4j.MDC.get((String)key);
    }

    public static void remove(String key) {
        org.slf4j.MDC.remove((String)key);
    }

    public static void clear() {
        org.slf4j.MDC.clear();
    }

    @Deprecated
    public static Hashtable getContext() {
        Map map = org.slf4j.MDC.getCopyOfContextMap();
        if (map != null) {
            return new Hashtable(map);
        }
        return new Hashtable();
    }
}

