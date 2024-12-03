/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerator
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.internal.rest.RuntimeIOException;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;

public class UncheckedJsonGenerator {
    private final JsonGenerator generator;

    public UncheckedJsonGenerator(JsonGenerator generator) {
        this.generator = generator;
    }

    public void writeArrayFieldStart(String fieldName) {
        try {
            this.generator.writeArrayFieldStart(fieldName);
        }
        catch (IOException e) {
            throw this.unchecked(e);
        }
    }

    public void writeEndArray() {
        try {
            this.generator.writeEndArray();
        }
        catch (IOException e) {
            throw this.unchecked(e);
        }
    }

    public void writeStartObject() {
        try {
            this.generator.writeStartObject();
        }
        catch (IOException e) {
            throw this.unchecked(e);
        }
    }

    public void writeEndObject() {
        try {
            this.generator.writeEndObject();
        }
        catch (IOException e) {
            throw this.unchecked(e);
        }
    }

    public void writeNumberField(String fieldName, int value) {
        try {
            this.generator.writeNumberField(fieldName, value);
        }
        catch (IOException e) {
            throw this.unchecked(e);
        }
    }

    public void writeNumberField(String fieldName, long value) {
        try {
            this.generator.writeNumberField(fieldName, value);
        }
        catch (IOException e) {
            throw this.unchecked(e);
        }
    }

    public void writeObject(Object pojo) {
        try {
            this.generator.writeObject(pojo);
        }
        catch (IOException e) {
            throw this.unchecked(e);
        }
    }

    public void writeObjectField(String fieldName, Object pojo) {
        try {
            this.generator.writeObjectField(fieldName, pojo);
        }
        catch (IOException e) {
            throw this.unchecked(e);
        }
    }

    public void flush() {
        try {
            this.generator.flush();
        }
        catch (IOException e) {
            throw this.unchecked(e);
        }
    }

    private RuntimeException unchecked(IOException e) {
        throw new RuntimeIOException(e);
    }
}

