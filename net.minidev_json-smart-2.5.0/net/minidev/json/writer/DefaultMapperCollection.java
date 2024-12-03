/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.writer;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import net.minidev.json.writer.JsonReader;
import net.minidev.json.writer.JsonReaderI;

public class DefaultMapperCollection<T>
extends JsonReaderI<T> {
    Class<T> clz;

    public DefaultMapperCollection(JsonReader base, Class<T> clz) {
        super(base);
        this.clz = clz;
    }

    @Override
    public JsonReaderI<T> startObject(String key) {
        return this;
    }

    @Override
    public JsonReaderI<T> startArray(String key) {
        return this;
    }

    @Override
    public Object createObject() {
        try {
            Constructor<T> c = this.clz.getConstructor(new Class[0]);
            return c.newInstance(new Object[0]);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object createArray() {
        try {
            Constructor<T> c = this.clz.getConstructor(new Class[0]);
            return c.newInstance(new Object[0]);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public void setValue(Object current, String key, Object value) {
        ((Map)current).put(key, value);
    }

    @Override
    public void addValue(Object current, Object value) {
        ((List)current).add(value);
    }
}

