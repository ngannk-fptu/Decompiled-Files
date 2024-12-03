/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.internal.util.config.ConfigurationException;

public final class ConfigurationHelper {
    private static final String PLACEHOLDER_START = "${";

    private ConfigurationHelper() {
    }

    public static String getString(String name, Map values) {
        Object value = values.get(name);
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static String getString(String name, Map values, String defaultValue) {
        String value = ConfigurationHelper.getString(name, values);
        return value == null ? defaultValue : value;
    }

    public static String getString(String name, Map values, String defaultValue, String ... otherSupportedValues) {
        String value = ConfigurationHelper.getString(name, values, defaultValue);
        if (!defaultValue.equals(value) && ArrayHelper.indexOf(otherSupportedValues, value) == -1) {
            throw new ConfigurationException("Unsupported configuration [name=" + name + ", value=" + value + "]. Choose value between: '" + defaultValue + "', '" + String.join((CharSequence)"', '", otherSupportedValues) + "'.");
        }
        return value;
    }

    public static boolean getBoolean(String name, Map values) {
        return ConfigurationHelper.getBoolean(name, values, false);
    }

    public static boolean getBoolean(String name, Map values, boolean defaultValue) {
        Object raw = values.get(name);
        Boolean value = ConfigurationHelper.toBoolean(raw, defaultValue);
        if (value == null) {
            throw new ConfigurationException("Could not determine how to handle configuration raw [name=" + name + ", value=" + raw + "] as boolean");
        }
        return value;
    }

    public static Boolean toBoolean(Object value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (Boolean.class.isInstance(value)) {
            return (Boolean)value;
        }
        if (String.class.isInstance(value)) {
            return Boolean.parseBoolean((String)value);
        }
        return null;
    }

    public static Boolean getBooleanWrapper(String name, Map values, Boolean defaultValue) {
        Object value = values.get(name);
        if (value == null) {
            return defaultValue;
        }
        if (Boolean.class.isInstance(value)) {
            return (Boolean)value;
        }
        if (String.class.isInstance(value)) {
            return Boolean.valueOf((String)value);
        }
        throw new ConfigurationException("Could not determine how to handle configuration value [name=" + name + ", value=" + value + "] as boolean");
    }

    public static int getInt(String name, Map values, int defaultValue) {
        Object value = values.get(name);
        if (value == null) {
            return defaultValue;
        }
        if (Integer.class.isInstance(value)) {
            return (Integer)value;
        }
        if (String.class.isInstance(value)) {
            return Integer.parseInt((String)value);
        }
        throw new ConfigurationException("Could not determine how to handle configuration value [name=" + name + ", value=" + value + "(" + value.getClass().getName() + ")] as int");
    }

    public static Integer getInteger(String name, Map values) {
        Object value = values.get(name);
        if (value == null) {
            return null;
        }
        if (Integer.class.isInstance(value)) {
            return (Integer)value;
        }
        if (String.class.isInstance(value)) {
            String trimmed = value.toString().trim();
            if (trimmed.isEmpty()) {
                return null;
            }
            return Integer.valueOf(trimmed);
        }
        throw new ConfigurationException("Could not determine how to handle configuration value [name=" + name + ", value=" + value + "(" + value.getClass().getName() + ")] as Integer");
    }

    public static long getLong(String name, Map values, int defaultValue) {
        Object value = values.get(name);
        if (value == null) {
            return defaultValue;
        }
        if (Long.class.isInstance(value)) {
            return (Long)value;
        }
        if (String.class.isInstance(value)) {
            return Long.parseLong((String)value);
        }
        throw new ConfigurationException("Could not determine how to handle configuration value [name=" + name + ", value=" + value + "(" + value.getClass().getName() + ")] as long");
    }

    public static Map clone(Map<?, ?> configurationValues) {
        if (configurationValues == null) {
            return null;
        }
        if (Properties.class.isInstance(configurationValues)) {
            return (Properties)((Properties)configurationValues).clone();
        }
        HashMap clone = new HashMap();
        for (Map.Entry<?, ?> entry : configurationValues.entrySet()) {
            clone.put(entry.getKey(), entry.getValue());
        }
        return clone;
    }

    public static Properties maskOut(Properties props, String key) {
        Properties clone = (Properties)props.clone();
        if (clone.get(key) != null) {
            clone.setProperty(key, "****");
        }
        return clone;
    }

    public static String extractPropertyValue(String propertyName, Properties properties) {
        String value = properties.getProperty(propertyName);
        if (value == null) {
            return null;
        }
        if ((value = value.trim()).isEmpty()) {
            return null;
        }
        return value;
    }

    public static String extractPropertyValue(String propertyName, Map properties) {
        String value = (String)properties.get(propertyName);
        if (value == null) {
            return null;
        }
        if ((value = value.trim()).isEmpty()) {
            return null;
        }
        return value;
    }

    public static Map toMap(String propertyName, String delim, Properties properties) {
        HashMap<String, String> map = new HashMap<String, String>();
        String value = ConfigurationHelper.extractPropertyValue(propertyName, properties);
        if (value != null) {
            StringTokenizer tokens = new StringTokenizer(value, delim);
            while (tokens.hasMoreTokens()) {
                map.put(tokens.nextToken(), tokens.hasMoreElements() ? tokens.nextToken() : "");
            }
        }
        return map;
    }

    public static Map toMap(String propertyName, String delim, Map properties) {
        HashMap<String, String> map = new HashMap<String, String>();
        String value = ConfigurationHelper.extractPropertyValue(propertyName, properties);
        if (value != null) {
            StringTokenizer tokens = new StringTokenizer(value, delim);
            while (tokens.hasMoreTokens()) {
                map.put(tokens.nextToken(), tokens.hasMoreElements() ? tokens.nextToken() : "");
            }
        }
        return map;
    }

    public static String[] toStringArray(String propertyName, String delim, Properties properties) {
        return ConfigurationHelper.toStringArray(ConfigurationHelper.extractPropertyValue(propertyName, properties), delim);
    }

    public static String[] toStringArray(String stringForm, String delim) {
        if (stringForm != null) {
            return StringHelper.split(delim, stringForm);
        }
        return ArrayHelper.EMPTY_STRING_ARRAY;
    }

    public static void resolvePlaceHolders(Map<?, ?> configurationValues) {
        Iterator<Map.Entry<?, ?>> itr = configurationValues.entrySet().iterator();
        while (itr.hasNext()) {
            String resolved;
            Map.Entry<?, ?> entry = itr.next();
            Object value = entry.getValue();
            if (value == null || !String.class.isInstance(value) || value.equals(resolved = ConfigurationHelper.resolvePlaceHolder((String)value))) continue;
            if (resolved == null) {
                itr.remove();
                continue;
            }
            entry.setValue(resolved);
        }
    }

    public static String resolvePlaceHolder(String property) {
        String rtn;
        if (property.indexOf(PLACEHOLDER_START) < 0) {
            return property;
        }
        StringBuilder buff = new StringBuilder();
        char[] chars = property.toCharArray();
        for (int pos = 0; pos < chars.length; ++pos) {
            if (chars[pos] == '$' && chars[pos + 1] == '{') {
                int x;
                String systemPropertyName = "";
                for (x = pos + 2; x < chars.length && chars[x] != '}'; ++x) {
                    systemPropertyName = systemPropertyName + chars[x];
                    if (x != chars.length - 1) continue;
                    throw new IllegalArgumentException("unmatched placeholder start [" + property + "]");
                }
                String systemProperty = ConfigurationHelper.extractFromSystem(systemPropertyName);
                buff.append(systemProperty == null ? "" : systemProperty);
                pos = x + 1;
                if (pos >= chars.length) break;
            }
            buff.append(chars[pos]);
        }
        return (rtn = buff.toString()).isEmpty() ? null : rtn;
    }

    private static String extractFromSystem(String systemPropertyName) {
        try {
            return System.getProperty(systemPropertyName);
        }
        catch (Throwable t) {
            return null;
        }
    }
}

