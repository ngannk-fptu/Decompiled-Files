/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Configurator {
    private static final Logger LOG = LogManager.getLogger(Configurator.class);

    public static int getIntValue(String systemProperty, int defaultValue) {
        String property = System.getProperty(systemProperty);
        if (property == null || "".equals(property) || "null".equals(property)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(property);
        }
        catch (Exception e) {
            LOG.atError().log("System property -D{} does not contains a valid integer: {}", (Object)systemProperty, (Object)property);
            return defaultValue;
        }
    }
}

