/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.glassfish.json.JsonArrayBuilderImpl;
import org.glassfish.json.JsonObjectBuilderImpl;
import org.glassfish.json.api.BufferPool;

class JsonBuilderFactoryImpl
implements JsonBuilderFactory {
    private final Map<String, ?> config = Collections.emptyMap();
    private final BufferPool bufferPool;

    JsonBuilderFactoryImpl(BufferPool bufferPool) {
        this.bufferPool = bufferPool;
    }

    @Override
    public JsonObjectBuilder createObjectBuilder() {
        return new JsonObjectBuilderImpl(this.bufferPool);
    }

    @Override
    public JsonObjectBuilder createObjectBuilder(JsonObject object) {
        return new JsonObjectBuilderImpl(object, this.bufferPool);
    }

    @Override
    public JsonObjectBuilder createObjectBuilder(Map<String, Object> object) {
        return new JsonObjectBuilderImpl(object, this.bufferPool);
    }

    @Override
    public JsonArrayBuilder createArrayBuilder() {
        return new JsonArrayBuilderImpl(this.bufferPool);
    }

    @Override
    public JsonArrayBuilder createArrayBuilder(JsonArray array) {
        return new JsonArrayBuilderImpl(array, this.bufferPool);
    }

    @Override
    public JsonArrayBuilder createArrayBuilder(Collection<?> collection) {
        return new JsonArrayBuilderImpl(collection, this.bufferPool);
    }

    @Override
    public Map<String, ?> getConfigInUse() {
        return this.config;
    }
}

