/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class MapFactory {
    private static Constructor concurrentHashMapConstructor;

    public static Map create(boolean allowNullKeys) {
        return MapFactory.create(16, 0.75f, 16, allowNullKeys);
    }

    public static Map create(int size, float loadFactor, int concurrencyLevel, boolean allowNullKeys) {
        Map map = null;
        if (concurrencyLevel <= 1) {
            map = new HashMap(size, loadFactor);
        } else if (concurrentHashMapConstructor != null) {
            try {
                map = (Map)concurrentHashMapConstructor.newInstance(new Integer(size), new Float(loadFactor), new Integer(concurrencyLevel));
            }
            catch (Exception ex) {
                throw new RuntimeException("this should not happen", ex);
            }
        } else {
            map = allowNullKeys ? Collections.synchronizedMap(new HashMap(size, loadFactor)) : new Hashtable(size, loadFactor);
        }
        return map;
    }

    static {
        try {
            concurrentHashMapConstructor = Class.forName("java.util.concurrent.ConcurrentHashMap").getConstructor(Integer.TYPE, Float.TYPE, Integer.TYPE);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

