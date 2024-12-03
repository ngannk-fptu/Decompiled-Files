/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth.serviceprovider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemPropertyUtils {
    private static final Logger LOG = LoggerFactory.getLogger(SystemPropertyUtils.class);

    static long parsePositiveLongFromSystemProperty(String propertyName, long defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.trim().isEmpty()) {
            try {
                long longValue = Long.parseLong(propertyValue);
                if (longValue >= 0L) {
                    return longValue;
                }
                LOG.warn(String.format("Value of system property '%s' is negative ('%s') defaulting to %s", propertyName, longValue, defaultValue));
            }
            catch (NumberFormatException e) {
                LOG.warn(String.format("Failed to parse long value from system property '%s' (was: '%s'), defaulting to %s", propertyName, propertyValue, defaultValue), (Throwable)e);
            }
        }
        return defaultValue;
    }
}

