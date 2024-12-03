/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.writer;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONAwareEx;
import net.minidev.json.JSONObject;
import net.minidev.json.writer.JsonReader;
import net.minidev.json.writer.JsonReaderI;

public class DefaultMapper<T>
extends JsonReaderI<T> {
    protected DefaultMapper(JsonReader base) {
        super(base);
    }

    @Override
    public JsonReaderI<JSONAwareEx> startObject(String key) {
        return this.base.DEFAULT;
    }

    @Override
    public JsonReaderI<JSONAwareEx> startArray(String key) {
        return this.base.DEFAULT;
    }

    @Override
    public Object createObject() {
        return new JSONObject();
    }

    @Override
    public Object createArray() {
        return new JSONArray();
    }

    @Override
    public void setValue(Object current, String key, Object value) {
        ((JSONObject)current).put(key, value);
    }

    @Override
    public void addValue(Object current, Object value) {
        ((JSONArray)current).add(value);
    }
}

