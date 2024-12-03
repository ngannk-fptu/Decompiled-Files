/*
 * Decompiled with CFR 0.152.
 */
package com.oracle.webservices.api.message;

public class ReadOnlyPropertyException
extends IllegalArgumentException {
    private final String propertyName;

    public ReadOnlyPropertyException(String propertyName) {
        super(propertyName + " is a read-only property.");
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return this.propertyName;
    }
}

