/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webhooks.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUtil {
    private static final String PROPERTY_PREFIX = "atlassian.webhooks.";
    private static final Logger log = LoggerFactory.getLogger(PropertyUtil.class);

    private PropertyUtil() {
        throw new UnsupportedOperationException("PropertyUtil is a utility class and should not be instantiated");
    }

    public static int getProperty(String name, int defaultValue) {
        String propertyName = PROPERTY_PREFIX + name;
        String value = System.getProperty(propertyName);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            }
            catch (NumberFormatException e) {
                log.info("Property {} is set to {} which is not a number. Using default value of {} instead", new Object[]{propertyName, value, defaultValue});
            }
        }
        return defaultValue;
    }
}

