/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.marketplace.client.encoding;

import com.atlassian.marketplace.client.encoding.SchemaViolation;
import com.google.common.base.Preconditions;

public class MissingRequiredField
extends SchemaViolation {
    private final String name;

    public MissingRequiredField(Class<?> schemaClass, String name) {
        super(schemaClass);
        this.name = (String)Preconditions.checkNotNull((Object)name);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getMessage() {
        return "missing required property \"" + this.name + "\" in " + this.getSchemaClass().getSimpleName();
    }
}

