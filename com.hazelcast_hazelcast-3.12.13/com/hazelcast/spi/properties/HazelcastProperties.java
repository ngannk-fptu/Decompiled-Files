/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.properties;

import com.hazelcast.config.Config;
import com.hazelcast.spi.properties.HazelcastProperty;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class HazelcastProperties {
    private final Set<String> keys;
    private final Properties properties = new Properties();

    public HazelcastProperties(Config config) {
        this(config.getProperties());
    }

    public HazelcastProperties(Properties nullableProperties) {
        if (nullableProperties != null) {
            this.properties.putAll((Map<?, ?>)nullableProperties);
        }
        this.keys = Collections.unmodifiableSet(this.properties.keySet());
    }

    public Set<String> keySet() {
        return this.keys;
    }

    public String get(String key) {
        return (String)this.properties.get(key);
    }

    public String getString(HazelcastProperty property) {
        String value = this.properties.getProperty(property.getName());
        if (value != null) {
            return value;
        }
        value = property.getSystemProperty();
        if (value != null) {
            return value;
        }
        HazelcastProperty parent = property.getParent();
        if (parent != null) {
            return this.getString(parent);
        }
        String deprecatedName = property.getDeprecatedName();
        if (deprecatedName != null) {
            value = this.get(deprecatedName);
            if (value == null) {
                value = System.getProperty(deprecatedName);
            }
            if (value != null) {
                System.err.print("Don't use deprecated '" + deprecatedName + "' but use '" + property.getName() + "' instead. The former name will be removed in the next Hazelcast release.");
                return value;
            }
        }
        return property.getDefaultValue();
    }

    public boolean containsKey(HazelcastProperty property) {
        if (property == null) {
            return false;
        }
        return this.containsKey(property.getName()) || this.containsKey(property.getParent()) || this.containsKey(property.getDeprecatedName());
    }

    private boolean containsKey(String propertyName) {
        if (propertyName == null) {
            return false;
        }
        return this.properties.containsKey(propertyName) || System.getProperty(propertyName) != null;
    }

    public boolean getBoolean(HazelcastProperty property) {
        return Boolean.valueOf(this.getString(property));
    }

    public int getInteger(HazelcastProperty property) {
        return Integer.parseInt(this.getString(property));
    }

    public long getLong(HazelcastProperty property) {
        return Long.parseLong(this.getString(property));
    }

    public float getFloat(HazelcastProperty property) {
        return Float.valueOf(this.getString(property)).floatValue();
    }

    public double getDouble(HazelcastProperty property) {
        return Double.valueOf(this.getString(property));
    }

    public long getNanos(HazelcastProperty property) {
        TimeUnit timeUnit = property.getTimeUnit();
        return timeUnit.toNanos(this.getLong(property));
    }

    public long getMillis(HazelcastProperty property) {
        TimeUnit timeUnit = property.getTimeUnit();
        return timeUnit.toMillis(this.getLong(property));
    }

    public long getPositiveMillisOrDefault(HazelcastProperty property) {
        return this.getPositiveMillisOrDefault(property, Long.parseLong(property.getDefaultValue()));
    }

    public long getPositiveMillisOrDefault(HazelcastProperty property, long defaultValue) {
        long millis = this.getMillis(property);
        return millis > 0L ? millis : defaultValue;
    }

    public int getSeconds(HazelcastProperty property) {
        TimeUnit timeUnit = property.getTimeUnit();
        return (int)timeUnit.toSeconds(this.getLong(property));
    }

    public <E extends Enum> E getEnum(HazelcastProperty property, Class<E> enumClazz) {
        String value = this.getString(property);
        for (Enum enumConstant : (Enum[])enumClazz.getEnumConstants()) {
            if (!enumConstant.name().equalsIgnoreCase(value)) continue;
            return (E)enumConstant;
        }
        throw new IllegalArgumentException(String.format("value '%s' for property '%s' is not a valid %s value", value, property.getName(), enumClazz.getName()));
    }
}

