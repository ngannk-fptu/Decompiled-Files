/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.encoding;

import com.atlassian.marketplace.client.encoding.SchemaViolation;

public class MalformedJson
extends SchemaViolation {
    private final String message;

    public MalformedJson(Class<?> schemaClass, String message) {
        super(schemaClass);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "Malformed JSON for " + this.getSchemaClass().getSimpleName() + ": " + this.message;
    }
}

