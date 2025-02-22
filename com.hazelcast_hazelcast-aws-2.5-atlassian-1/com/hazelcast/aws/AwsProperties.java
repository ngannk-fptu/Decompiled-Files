/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.config.properties.PropertyDefinition
 *  com.hazelcast.config.properties.PropertyTypeConverter
 *  com.hazelcast.config.properties.SimplePropertyDefinition
 *  com.hazelcast.config.properties.ValueValidator
 *  com.hazelcast.core.TypeConverter
 */
package com.hazelcast.aws;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.config.properties.SimplePropertyDefinition;
import com.hazelcast.config.properties.ValueValidator;
import com.hazelcast.core.TypeConverter;

public enum AwsProperties {
    ACCESS_KEY("access-key", PropertyTypeConverter.STRING, true),
    SECRET_KEY("secret-key", PropertyTypeConverter.STRING, true),
    REGION("region", PropertyTypeConverter.STRING, true),
    IAM_ROLE("iam-role", PropertyTypeConverter.STRING, true),
    HOST_HEADER("host-header", PropertyTypeConverter.STRING, true),
    SECURITY_GROUP_NAME("security-group-name", PropertyTypeConverter.STRING, true),
    TAG_KEY("tag-key", PropertyTypeConverter.STRING, true),
    TAG_VALUE("tag-value", PropertyTypeConverter.STRING, true),
    CONNECTION_TIMEOUT_SECONDS("connection-timeout-seconds", PropertyTypeConverter.INTEGER, true),
    CONNECTION_RETRIES("connection-retries", PropertyTypeConverter.INTEGER, true),
    PORT("hz-port", PropertyTypeConverter.STRING, true);

    private final PropertyDefinition propertyDefinition;

    private AwsProperties(String key, PropertyTypeConverter typeConverter, boolean optional, ValueValidator validator) {
        this.propertyDefinition = new SimplePropertyDefinition(key, optional, (TypeConverter)typeConverter, validator);
    }

    private AwsProperties(String key, PropertyTypeConverter typeConverter, boolean optional) {
        this.propertyDefinition = new SimplePropertyDefinition(key, optional, (TypeConverter)typeConverter);
    }

    public PropertyDefinition getDefinition() {
        return this.propertyDefinition;
    }
}

