/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.encoding;

import com.atlassian.marketplace.client.encoding.SchemaViolation;

public class InvalidFieldValue
extends SchemaViolation {
    private final String value;

    public InvalidFieldValue(String value, Class<?> valueClass) {
        super(valueClass);
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "\"" + this.value + "\" is not a valid value for " + this.getSchemaClass().getSimpleName();
    }

    public String getValue() {
        return this.value;
    }
}

