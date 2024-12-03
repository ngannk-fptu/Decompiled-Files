/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.PropertyUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.rpc.xmlrpc;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Translator {
    private static final Logger log = LoggerFactory.getLogger(Translator.class);

    public static Vector makeVector(Object[] objects) {
        Vector<Object> result = new Vector<Object>(objects.length);
        for (int i = 0; i < objects.length; ++i) {
            if (objects[i] instanceof String) {
                result.add(objects[i]);
                continue;
            }
            result.add(Translator.makeStruct(objects[i]));
        }
        return result;
    }

    public static Hashtable convertMap(Map map) {
        Hashtable<String, Object> result = new Hashtable<String, Object>();
        for (Map.Entry e : map.entrySet()) {
            if (e.getKey() == null || e.getValue() == null) continue;
            result.put(String.valueOf(e.getKey()), Translator.convertValue(e.getValue()));
        }
        return result;
    }

    private static Object convertValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Map) {
            value = Translator.convertMap((Map)value);
        } else if (value.getClass().isArray()) {
            value = Translator.makeVector((Object[])value);
        } else {
            if (value instanceof Date) {
                return value;
            }
            value = value.toString();
        }
        return value;
    }

    public static Hashtable makeStruct(Object object) {
        try {
            Hashtable result = new Hashtable(PropertyUtils.describe((Object)object)){

                @Override
                public synchronized Object put(Object key, Object value) {
                    if (value == null || key == null || "class".equals(key)) {
                        return null;
                    }
                    value = Translator.convertValue(value);
                    return super.put(key, value);
                }
            };
            return result;
        }
        catch (Exception e) {
            log.error("Unable to convert bean to a Hashtable", (Throwable)e);
            return null;
        }
    }
}

