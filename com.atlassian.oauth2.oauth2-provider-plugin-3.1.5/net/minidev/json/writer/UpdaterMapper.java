/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.writer;

import java.io.IOException;
import java.lang.reflect.Type;
import net.minidev.json.parser.ParseException;
import net.minidev.json.writer.JsonReader;
import net.minidev.json.writer.JsonReaderI;

public class UpdaterMapper<T>
extends JsonReaderI<T> {
    final T obj;
    final JsonReaderI<?> mapper;

    public UpdaterMapper(JsonReader base, T obj) {
        super(base);
        if (obj == null) {
            throw new NullPointerException("can not update null Object");
        }
        this.obj = obj;
        this.mapper = base.getMapper(obj.getClass());
    }

    public UpdaterMapper(JsonReader base, T obj, Type type) {
        super(base);
        if (obj == null) {
            throw new NullPointerException("can not update null Object");
        }
        this.obj = obj;
        this.mapper = base.getMapper(type);
    }

    @Override
    public JsonReaderI<?> startObject(String key) throws ParseException, IOException {
        Object bean = this.mapper.getValue(this.obj, key);
        if (bean == null) {
            return this.mapper.startObject(key);
        }
        return new UpdaterMapper<Object>(this.base, bean, this.mapper.getType(key));
    }

    @Override
    public JsonReaderI<?> startArray(String key) throws ParseException, IOException {
        return this.mapper.startArray(key);
    }

    @Override
    public void setValue(Object current, String key, Object value) throws ParseException, IOException {
        this.mapper.setValue(current, key, value);
    }

    @Override
    public void addValue(Object current, Object value) throws ParseException, IOException {
        this.mapper.addValue(current, value);
    }

    @Override
    public Object createObject() {
        if (this.obj != null) {
            return this.obj;
        }
        return this.mapper.createObject();
    }

    @Override
    public Object createArray() {
        if (this.obj != null) {
            return this.obj;
        }
        return this.mapper.createArray();
    }

    @Override
    public T convert(Object current) {
        if (this.obj != null) {
            return this.obj;
        }
        return (T)this.mapper.convert(current);
    }
}

