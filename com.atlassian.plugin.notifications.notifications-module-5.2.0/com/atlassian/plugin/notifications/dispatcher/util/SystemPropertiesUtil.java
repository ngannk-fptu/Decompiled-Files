/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.notifications.dispatcher.util;

import org.apache.commons.lang3.StringUtils;

public class SystemPropertiesUtil {
    public static int parseSystemProperty(String propertyName, int defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (StringUtils.isNotBlank((CharSequence)propertyValue) && StringUtils.isNumeric((CharSequence)propertyValue)) {
            return Integer.parseInt(propertyValue);
        }
        return defaultValue;
    }
}

