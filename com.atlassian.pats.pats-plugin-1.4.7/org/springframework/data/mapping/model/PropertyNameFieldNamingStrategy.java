/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.mapping.model;

import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.FieldNamingStrategy;

public enum PropertyNameFieldNamingStrategy implements FieldNamingStrategy
{
    INSTANCE;


    @Override
    public String getFieldName(PersistentProperty<?> property) {
        return property.getName();
    }
}

