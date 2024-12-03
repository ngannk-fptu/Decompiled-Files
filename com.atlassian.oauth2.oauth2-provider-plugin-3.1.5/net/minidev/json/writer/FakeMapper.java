/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.writer;

import net.minidev.json.writer.JsonReaderI;

public class FakeMapper
extends JsonReaderI<Object> {
    public static JsonReaderI<Object> DEFAULT = new FakeMapper();

    private FakeMapper() {
        super(null);
    }

    @Override
    public JsonReaderI<?> startObject(String key) {
        return this;
    }

    @Override
    public JsonReaderI<?> startArray(String key) {
        return this;
    }

    @Override
    public void setValue(Object current, String key, Object value) {
    }

    @Override
    public void addValue(Object current, Object value) {
    }

    @Override
    public Object createObject() {
        return null;
    }

    @Override
    public Object createArray() {
        return null;
    }
}

