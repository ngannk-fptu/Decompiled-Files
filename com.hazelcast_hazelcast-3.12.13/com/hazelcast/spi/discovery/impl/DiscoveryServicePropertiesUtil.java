/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery.impl;

import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.ValueValidator;
import com.hazelcast.core.TypeConverter;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

final class DiscoveryServicePropertiesUtil {
    private DiscoveryServicePropertiesUtil() {
    }

    static Map<String, Comparable> prepareProperties(Map<String, Comparable> properties, Collection<PropertyDefinition> propertyDefinitions) {
        Map<String, Comparable> mappedProperties = MapUtil.createHashMap(propertyDefinitions.size());
        for (PropertyDefinition propertyDefinition : propertyDefinitions) {
            String propertyKey = propertyDefinition.key();
            if (!properties.containsKey(propertyKey)) {
                if (propertyDefinition.optional()) continue;
                throw new InvalidConfigurationException(String.format("Missing property '%s' on discovery strategy", propertyKey));
            }
            Comparable value = properties.get(propertyKey);
            TypeConverter typeConverter = propertyDefinition.typeConverter();
            Comparable mappedValue = typeConverter.convert(value);
            ValueValidator validator = propertyDefinition.validator();
            if (validator != null) {
                validator.validate(mappedValue);
            }
            mappedProperties.put(propertyKey, mappedValue);
        }
        DiscoveryServicePropertiesUtil.verifyNoUnknownProperties(mappedProperties, properties);
        return mappedProperties;
    }

    private static void verifyNoUnknownProperties(Map<String, Comparable> mappedProperties, Map<String, Comparable> allProperties) {
        HashSet<String> notMappedProperties = new HashSet<String>(allProperties.keySet());
        notMappedProperties.removeAll(mappedProperties.keySet());
        if (!notMappedProperties.isEmpty()) {
            throw new InvalidConfigurationException(String.format("Unknown properties: '%s' on discovery strategy", notMappedProperties));
        }
    }
}

