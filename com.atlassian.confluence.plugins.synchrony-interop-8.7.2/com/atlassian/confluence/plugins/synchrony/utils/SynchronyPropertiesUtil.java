/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.synchrony.utils;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class SynchronyPropertiesUtil {
    public static Properties computeRenamedProperties(Properties properties) {
        Properties destProperties = new Properties();
        destProperties.putAll((Map<?, ?>)properties);
        Set<String> propertyNames = destProperties.stringPropertyNames();
        propertyNames.stream().filter(key -> key.startsWith("reza.")).forEach(rezaKey -> {
            String synchronyKey = "synchrony." + rezaKey.substring(5);
            destProperties.put(synchronyKey, destProperties.remove(rezaKey));
        });
        return destProperties;
    }
}

