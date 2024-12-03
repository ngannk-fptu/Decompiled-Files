/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.json.bind.Jsonb
 *  javax.json.bind.JsonbBuilder
 *  javax.json.bind.JsonbConfig
 */
package org.springframework.http.converter.json;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import org.springframework.http.converter.json.AbstractJsonHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class JsonbHttpMessageConverter
extends AbstractJsonHttpMessageConverter {
    private Jsonb jsonb;

    public JsonbHttpMessageConverter() {
        this(JsonbBuilder.create());
    }

    public JsonbHttpMessageConverter(JsonbConfig config) {
        this.jsonb = JsonbBuilder.create((JsonbConfig)config);
    }

    public JsonbHttpMessageConverter(Jsonb jsonb) {
        Assert.notNull((Object)jsonb, "A Jsonb instance is required");
        this.jsonb = jsonb;
    }

    public void setJsonb(Jsonb jsonb) {
        Assert.notNull((Object)jsonb, "A Jsonb instance is required");
        this.jsonb = jsonb;
    }

    public Jsonb getJsonb() {
        return this.jsonb;
    }

    @Override
    protected Object readInternal(Type resolvedType, Reader reader) throws Exception {
        return this.getJsonb().fromJson(reader, resolvedType);
    }

    @Override
    protected void writeInternal(Object object, @Nullable Type type, Writer writer) throws Exception {
        if (type instanceof ParameterizedType) {
            this.getJsonb().toJson(object, type, writer);
        } else {
            this.getJsonb().toJson(object, writer);
        }
    }
}

