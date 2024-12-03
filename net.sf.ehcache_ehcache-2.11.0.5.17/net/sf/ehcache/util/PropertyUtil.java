/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertyUtil {
    private static final Logger LOG = LoggerFactory.getLogger((String)PropertyUtil.class.getName());
    private static final String DEFAULT_PROPERTY_SEPARATOR = ",";

    private PropertyUtil() {
    }

    public static String extractAndLogProperty(String name, Properties properties) {
        if (properties == null || properties.size() == 0) {
            return null;
        }
        String foundValue = (String)properties.get(name);
        if (foundValue != null) {
            foundValue = foundValue.trim();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Value found for " + name + ": " + foundValue);
        }
        return foundValue;
    }

    public static String extractAndLogProperty(String name, Map properties) {
        if (properties == null || properties.size() == 0) {
            return null;
        }
        String foundValue = (String)properties.get(name);
        if (foundValue != null) {
            foundValue = foundValue.trim();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Value found for " + name + ": " + foundValue);
        }
        return foundValue;
    }

    public static Properties parseProperties(String propertiesString, String propertySeparator) {
        if (propertiesString == null) {
            LOG.debug("propertiesString is null.");
            return null;
        }
        if (propertySeparator == null) {
            propertySeparator = DEFAULT_PROPERTY_SEPARATOR;
        }
        Properties properties = new Properties();
        String propertyLines = propertiesString.trim();
        propertyLines = propertyLines.replaceAll(propertySeparator, "\n");
        try {
            properties.load(new StringReader(propertyLines));
        }
        catch (IOException e) {
            LOG.error("Cannot load properties from " + propertiesString);
        }
        return properties;
    }

    public static boolean parseBoolean(String value) {
        return value != null && value.equalsIgnoreCase("true");
    }
}

