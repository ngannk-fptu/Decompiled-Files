/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.properties;

import com.hazelcast.config.properties.ValueValidator;
import com.hazelcast.core.TypeConverter;

public interface PropertyDefinition {
    public TypeConverter typeConverter();

    public String key();

    public ValueValidator validator();

    public boolean optional();
}

