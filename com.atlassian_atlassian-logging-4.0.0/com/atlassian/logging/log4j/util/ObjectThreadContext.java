/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.ThreadContext
 *  org.apache.logging.log4j.spi.ObjectThreadContextMap
 */
package com.atlassian.logging.log4j.util;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.spi.ObjectThreadContextMap;

public class ObjectThreadContext {
    public static boolean isSupported() {
        return ThreadContext.getThreadContextMap() instanceof ObjectThreadContextMap;
    }

    public static Object get(String key) {
        return ObjectThreadContext.getObjectMap().getValue(key);
    }

    public static void put(String key, Object value) {
        ObjectThreadContext.getObjectMap().putValue(key, value);
    }

    private static ObjectThreadContextMap getObjectMap() {
        if (!ObjectThreadContext.isSupported()) {
            throw new UnsupportedOperationException();
        }
        return (ObjectThreadContextMap)ThreadContext.getThreadContextMap();
    }
}

