/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.mapping.model;

import org.springframework.data.mapping.PersistentProperty;

public interface FieldNamingStrategy {
    public String getFieldName(PersistentProperty<?> var1);
}

