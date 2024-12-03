/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.FlushModeType
 *  javax.persistence.PersistenceException
 */
package org.hibernate.jpa.internal.util;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceException;
import org.hibernate.AssertionFailure;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;

public abstract class ConfigurationHelper {
    public static void overrideProperties(Properties properties, Map<?, ?> overrides) {
        for (Map.Entry<?, ?> entry : overrides.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) continue;
            properties.put(entry.getKey(), entry.getValue());
        }
    }

    public static FlushMode getFlushMode(Object value, FlushMode defaultFlushMode) {
        FlushMode flushMode = value instanceof FlushMode ? (FlushMode)((Object)value) : (value instanceof FlushModeType ? ConfigurationHelper.getFlushMode((FlushModeType)value) : (value instanceof String ? ConfigurationHelper.getFlushMode((String)value) : defaultFlushMode));
        if (flushMode == null) {
            throw new PersistenceException("Unable to parse org.hibernate.flushMode: " + value);
        }
        return flushMode;
    }

    public static FlushMode getFlushMode(Object value) {
        return ConfigurationHelper.getFlushMode(value, null);
    }

    private static FlushMode getFlushMode(String flushMode) {
        if (flushMode == null) {
            return null;
        }
        flushMode = flushMode.toUpperCase(Locale.ROOT);
        return FlushMode.valueOf(flushMode);
    }

    private static FlushMode getFlushMode(FlushModeType flushMode) {
        switch (flushMode) {
            case AUTO: {
                return FlushMode.AUTO;
            }
            case COMMIT: {
                return FlushMode.COMMIT;
            }
        }
        throw new AssertionFailure("Unknown FlushModeType: " + flushMode);
    }

    public static Integer getInteger(Object value) {
        if (value instanceof Integer) {
            return (Integer)value;
        }
        return Integer.valueOf((String)value);
    }

    public static Boolean getBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        return Boolean.valueOf((String)value);
    }

    public static CacheMode getCacheMode(Object value) {
        if (value instanceof CacheMode) {
            return (CacheMode)((Object)value);
        }
        return CacheMode.valueOf((String)value);
    }
}

