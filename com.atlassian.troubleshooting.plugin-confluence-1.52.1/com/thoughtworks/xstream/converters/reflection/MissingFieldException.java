/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;

public class MissingFieldException
extends ObjectAccessException {
    private final String fieldName;
    private final String className;

    public MissingFieldException(String className, String fieldName) {
        super("Field not found in class.");
        this.className = className;
        this.fieldName = fieldName;
        this.add("field", className + "." + fieldName);
    }

    public String getFieldName() {
        return this.fieldName;
    }

    protected String getClassName() {
        return this.className;
    }
}

