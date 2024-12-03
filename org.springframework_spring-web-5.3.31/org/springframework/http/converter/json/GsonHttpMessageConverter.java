/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.http.converter.json;

import com.google.gson.Gson;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.springframework.http.converter.json.AbstractJsonHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class GsonHttpMessageConverter
extends AbstractJsonHttpMessageConverter {
    private Gson gson;

    public GsonHttpMessageConverter() {
        this.gson = new Gson();
    }

    public GsonHttpMessageConverter(Gson gson) {
        Assert.notNull((Object)gson, (String)"A Gson instance is required");
        this.gson = gson;
    }

    public void setGson(Gson gson) {
        Assert.notNull((Object)gson, (String)"A Gson instance is required");
        this.gson = gson;
    }

    public Gson getGson() {
        return this.gson;
    }

    @Override
    protected Object readInternal(Type resolvedType, Reader reader) throws Exception {
        return this.getGson().fromJson(reader, resolvedType);
    }

    @Override
    protected void writeInternal(Object object, @Nullable Type type, Writer writer) throws Exception {
        if (type instanceof ParameterizedType) {
            this.getGson().toJson(object, type, (Appendable)writer);
        } else {
            this.getGson().toJson(object, (Appendable)writer);
        }
    }
}

