/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.PropertyUtils
 *  org.apache.commons.collections.FastHashMap
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.validator.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Msg;
import org.apache.commons.validator.Var;

public class ValidatorUtils {
    private static final Log LOG = LogFactory.getLog(ValidatorUtils.class);

    public static String replace(String value, String key, String replaceValue) {
        if (value == null || key == null || replaceValue == null) {
            return value;
        }
        int pos = value.indexOf(key);
        if (pos < 0) {
            return value;
        }
        int length = value.length();
        int start = pos;
        int end = pos + key.length();
        value = length == key.length() ? replaceValue : (end == length ? value.substring(0, start) + replaceValue : value.substring(0, start) + replaceValue + ValidatorUtils.replace(value.substring(end), key, replaceValue));
        return value;
    }

    public static String getValueAsString(Object bean, String property) {
        Object value = null;
        try {
            value = PropertyUtils.getProperty((Object)bean, (String)property);
        }
        catch (IllegalAccessException e) {
            LOG.error((Object)e.getMessage(), (Throwable)e);
        }
        catch (InvocationTargetException e) {
            LOG.error((Object)e.getMessage(), (Throwable)e);
        }
        catch (NoSuchMethodException e) {
            LOG.error((Object)e.getMessage(), (Throwable)e);
        }
        if (value == null) {
            return null;
        }
        if (value instanceof String[]) {
            return ((String[])value).length > 0 ? value.toString() : "";
        }
        if (value instanceof Collection) {
            return ((Collection)value).isEmpty() ? "" : value.toString();
        }
        return value.toString();
    }

    @Deprecated
    public static FastHashMap copyFastHashMap(FastHashMap map) {
        FastHashMap results = new FastHashMap();
        for (Map.Entry entry : map.entrySet()) {
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Msg) {
                results.put((Object)key, ((Msg)value).clone());
                continue;
            }
            if (value instanceof Arg) {
                results.put((Object)key, ((Arg)value).clone());
                continue;
            }
            if (value instanceof Var) {
                results.put((Object)key, ((Var)value).clone());
                continue;
            }
            results.put((Object)key, value);
        }
        results.setFast(true);
        return results;
    }

    public static Map<String, Object> copyMap(Map<String, Object> map) {
        HashMap<String, Object> results = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Msg) {
                results.put(key, ((Msg)value).clone());
                continue;
            }
            if (value instanceof Arg) {
                results.put(key, ((Arg)value).clone());
                continue;
            }
            if (value instanceof Var) {
                results.put(key, ((Var)value).clone());
                continue;
            }
            results.put(key, value);
        }
        return results;
    }
}

