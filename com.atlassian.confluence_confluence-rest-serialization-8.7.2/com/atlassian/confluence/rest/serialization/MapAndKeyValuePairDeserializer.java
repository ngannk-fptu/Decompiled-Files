/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonToken
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.JsonDeserializer
 *  org.codehaus.jackson.map.KeyDeserializer
 *  org.codehaus.jackson.map.TypeDeserializer
 *  org.codehaus.jackson.map.deser.ValueInstantiator
 *  org.codehaus.jackson.map.deser.std.MapDeserializer
 *  org.codehaus.jackson.type.JavaType
 */
package com.atlassian.confluence.rest.serialization;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.deser.std.MapDeserializer;
import org.codehaus.jackson.type.JavaType;

public class MapAndKeyValuePairDeserializer
extends MapDeserializer {
    public MapAndKeyValuePairDeserializer(JavaType mapType, ValueInstantiator valueInstantiator, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser) {
        super(mapType, valueInstantiator, keyDeser, valueDeser, valueTypeDeser);
    }

    public Map<Object, Object> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
            return this.deserializeMapKeyValueHierarchy(jp, ctxt, null);
        }
        return super.deserialize(jp, ctxt);
    }

    public Map<Object, Object> deserialize(JsonParser jp, DeserializationContext ctxt, Map<Object, Object> result) throws IOException, JsonProcessingException {
        if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
            return this.deserializeMapKeyValueHierarchy(jp, ctxt, result);
        }
        return super.deserialize(jp, ctxt, result);
    }

    private Map<Object, Object> deserializeMapKeyValueHierarchy(JsonParser jp, DeserializationContext ctxt, Map<Object, Object> map) throws IOException {
        jp.nextToken();
        if (jp.getCurrentToken() == JsonToken.END_ARRAY) {
            return map != null ? map : Collections.emptyMap();
        }
        if (map == null) {
            map = (Map)this._valueInstantiator.createUsingDefault();
        }
        while (jp.getCurrentToken() == JsonToken.START_OBJECT) {
            this.deserializeKeyValue(jp, ctxt, map);
        }
        if (jp.getCurrentToken() != JsonToken.END_ARRAY) {
            throw ctxt.mappingException(this.getMapClass());
        }
        return map;
    }

    private void deserializeKeyValue(JsonParser jp, DeserializationContext ctxt, Map<Object, Object> map) throws IOException {
        jp.nextToken();
        String key = null;
        Map<Object, Object> value = null;
        while (jp.getCurrentToken() == JsonToken.FIELD_NAME) {
            String name = jp.getCurrentName();
            jp.nextToken();
            if (name.equals("key")) {
                key = jp.getText();
                jp.nextToken();
                continue;
            }
            if (name.equals("value")) {
                value = this.readValue(jp, ctxt);
                jp.nextToken();
                continue;
            }
            if (name.equals("fields")) {
                value = this.deserializeMapKeyValueHierarchy(jp, ctxt, null);
                continue;
            }
            throw ctxt.mappingException(this.getMapClass());
        }
        if (jp.getCurrentToken() != JsonToken.END_OBJECT) {
            throw ctxt.mappingException(this.getMapClass());
        }
        jp.nextToken();
        if (key != null) {
            map.put(key, value);
        }
    }

    private Object readValue(JsonParser jp, DeserializationContext ctxt) throws IOException {
        if (jp.getCurrentToken() == JsonToken.VALUE_NULL) {
            return null;
        }
        if (this._valueTypeDeserializer == null) {
            return this._valueDeserializer.deserialize(jp, ctxt);
        }
        return this._valueDeserializer.deserializeWithType(jp, ctxt, this._valueTypeDeserializer);
    }
}

