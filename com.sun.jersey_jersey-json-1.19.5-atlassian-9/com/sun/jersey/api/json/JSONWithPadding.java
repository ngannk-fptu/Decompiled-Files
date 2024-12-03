/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.map.JsonSerializableWithType
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.codehaus.jackson.map.TypeSerializer
 */
package com.sun.jersey.api.json;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializableWithType;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;

public class JSONWithPadding
implements JsonSerializableWithType {
    public static final String DEFAULT_CALLBACK_NAME = "callback";
    private final String callbackName;
    private final Object jsonSource;

    public JSONWithPadding(Object jsonSource) {
        this(jsonSource, DEFAULT_CALLBACK_NAME);
    }

    public JSONWithPadding(Object jsonSource, String callbackName) {
        if (jsonSource == null) {
            throw new IllegalArgumentException("JSON source MUST not be null");
        }
        this.jsonSource = jsonSource;
        this.callbackName = callbackName == null ? DEFAULT_CALLBACK_NAME : callbackName;
    }

    public String getCallbackName() {
        return this.callbackName;
    }

    public Object getJsonSource() {
        return this.jsonSource;
    }

    public void serialize(JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (this.jsonSource == null) {
            provider.getNullValueSerializer().serialize(null, jgen, provider);
        } else {
            Class<?> cls = this.jsonSource.getClass();
            provider.findTypedValueSerializer(cls, true).serialize(this.jsonSource, jgen, provider);
        }
    }

    public void serializeWithType(JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
        this.serialize(jgen, provider);
    }
}

