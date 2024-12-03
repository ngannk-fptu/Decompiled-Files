/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery.multicast;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.config.properties.SimplePropertyDefinition;
import com.hazelcast.config.properties.ValueValidator;

public final class MulticastProperties {
    public static final PropertyDefinition PORT = MulticastProperties.property("port", PropertyTypeConverter.INTEGER);
    public static final PropertyDefinition GROUP = MulticastProperties.property("group", PropertyTypeConverter.STRING);

    private MulticastProperties() {
    }

    private static PropertyDefinition property(String key, PropertyTypeConverter typeConverter) {
        return MulticastProperties.property(key, typeConverter, null);
    }

    private static PropertyDefinition property(String key, PropertyTypeConverter typeConverter, ValueValidator valueValidator) {
        return new SimplePropertyDefinition(key, true, typeConverter, valueValidator);
    }
}

