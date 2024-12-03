/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.marketplace.client.encoding;

import com.google.common.base.Preconditions;

public abstract class SchemaViolation {
    private final Class<?> schemaClass;

    protected SchemaViolation(Class<?> schemaClass) {
        this.schemaClass = (Class)Preconditions.checkNotNull(schemaClass);
    }

    public Class<?> getSchemaClass() {
        return this.schemaClass;
    }

    public abstract String getMessage();

    public String toString() {
        return this.getMessage();
    }
}

