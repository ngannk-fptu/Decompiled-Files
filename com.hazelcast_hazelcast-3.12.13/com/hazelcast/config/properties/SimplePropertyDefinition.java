/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.properties;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.ValueValidator;
import com.hazelcast.core.TypeConverter;

public class SimplePropertyDefinition
implements PropertyDefinition {
    private final String key;
    private final boolean optional;
    private final TypeConverter typeConverter;
    private final ValueValidator validator;

    public SimplePropertyDefinition(String key, TypeConverter typeConverter) {
        this(key, false, typeConverter, null);
    }

    public SimplePropertyDefinition(String key, boolean optional, TypeConverter typeConverter) {
        this(key, optional, typeConverter, null);
    }

    public SimplePropertyDefinition(String key, boolean optional, TypeConverter typeConverter, ValueValidator validator) {
        this.key = key;
        this.optional = optional;
        this.typeConverter = typeConverter;
        this.validator = validator;
    }

    @Override
    public TypeConverter typeConverter() {
        return this.typeConverter;
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public ValueValidator validator() {
        return this.validator;
    }

    @Override
    public boolean optional() {
        return this.optional;
    }
}

