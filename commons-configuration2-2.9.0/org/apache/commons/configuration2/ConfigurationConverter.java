/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationMap;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.lang3.StringUtils;

public final class ConfigurationConverter {
    private static final char DEFAULT_SEPARATOR = ',';

    private ConfigurationConverter() {
    }

    public static Configuration getConfiguration(Properties props) {
        return new MapConfiguration(props);
    }

    public static Properties getProperties(ImmutableConfiguration config) {
        boolean useDelimiterHandler;
        ListDelimiterHandler listHandler;
        Properties props = new Properties();
        if (config instanceof AbstractConfiguration) {
            listHandler = ((AbstractConfiguration)config).getListDelimiterHandler();
            useDelimiterHandler = true;
        } else {
            listHandler = null;
            useDelimiterHandler = false;
        }
        Iterator<String> keys = config.getKeys();
        while (keys.hasNext()) {
            String propValue;
            String key = keys.next();
            List<Object> list = config.getList(key);
            if (useDelimiterHandler) {
                try {
                    propValue = String.valueOf(listHandler.escapeList(list, ListDelimiterHandler.NOOP_TRANSFORMER));
                }
                catch (Exception ex) {
                    useDelimiterHandler = false;
                    propValue = ConfigurationConverter.listToString(list);
                }
            } else {
                propValue = ConfigurationConverter.listToString(list);
            }
            props.setProperty(key, propValue);
        }
        return props;
    }

    public static Properties getProperties(Configuration config) {
        return ConfigurationConverter.getProperties((ImmutableConfiguration)config);
    }

    public static Map<Object, Object> getMap(Configuration config) {
        return new ConfigurationMap(config);
    }

    private static String listToString(List<?> list) {
        return StringUtils.join(list, (char)',');
    }
}

