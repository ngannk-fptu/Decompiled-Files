/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.map.util.ObjectBuffer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JacksonStdImpl
public class UntypedObjectDeserializer
extends StdDeserializer<Object> {
    private static final Object[] NO_OBJECTS = new Object[0];

    public UntypedObjectDeserializer() {
        super(Object.class);
    }

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        switch (jp.getCurrentToken()) {
            case START_OBJECT: {
                return this.mapObject(jp, ctxt);
            }
            case END_OBJECT: {
                break;
            }
            case START_ARRAY: {
                return this.mapArray(jp, ctxt);
            }
            case END_ARRAY: {
                break;
            }
            case FIELD_NAME: {
                return this.mapObject(jp, ctxt);
            }
            case VALUE_EMBEDDED_OBJECT: {
                return jp.getEmbeddedObject();
            }
            case VALUE_STRING: {
                return jp.getText();
            }
            case VALUE_NUMBER_INT: {
                if (ctxt.isEnabled(DeserializationConfig.Feature.USE_BIG_INTEGER_FOR_INTS)) {
                    return jp.getBigIntegerValue();
                }
                return jp.getNumberValue();
            }
            case VALUE_NUMBER_FLOAT: {
                if (ctxt.isEnabled(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return jp.getDecimalValue();
                }
                return jp.getDoubleValue();
            }
            case VALUE_TRUE: {
                return Boolean.TRUE;
            }
            case VALUE_FALSE: {
                return Boolean.FALSE;
            }
            case VALUE_NULL: {
                return null;
            }
        }
        throw ctxt.mappingException(Object.class);
    }

    @Override
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        switch (t) {
            case START_OBJECT: 
            case START_ARRAY: 
            case FIELD_NAME: {
                return typeDeserializer.deserializeTypedFromAny(jp, ctxt);
            }
            case VALUE_STRING: {
                return jp.getText();
            }
            case VALUE_NUMBER_INT: {
                if (ctxt.isEnabled(DeserializationConfig.Feature.USE_BIG_INTEGER_FOR_INTS)) {
                    return jp.getBigIntegerValue();
                }
                return jp.getIntValue();
            }
            case VALUE_NUMBER_FLOAT: {
                if (ctxt.isEnabled(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return jp.getDecimalValue();
                }
                return jp.getDoubleValue();
            }
            case VALUE_TRUE: {
                return Boolean.TRUE;
            }
            case VALUE_FALSE: {
                return Boolean.FALSE;
            }
            case VALUE_EMBEDDED_OBJECT: {
                return jp.getEmbeddedObject();
            }
            case VALUE_NULL: {
                return null;
            }
        }
        throw ctxt.mappingException(Object.class);
    }

    protected Object mapArray(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (ctxt.isEnabled(DeserializationConfig.Feature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
            return this.mapArrayToArray(jp, ctxt);
        }
        if (jp.nextToken() == JsonToken.END_ARRAY) {
            return new ArrayList(4);
        }
        ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] values = buffer.resetAndStart();
        int ptr = 0;
        int totalSize = 0;
        do {
            Object value = this.deserialize(jp, ctxt);
            ++totalSize;
            if (ptr >= values.length) {
                values = buffer.appendCompletedChunk(values);
                ptr = 0;
            }
            values[ptr++] = value;
        } while (jp.nextToken() != JsonToken.END_ARRAY);
        ArrayList<Object> result = new ArrayList<Object>(totalSize + (totalSize >> 3) + 1);
        buffer.completeAndClearBuffer(values, ptr, result);
        return result;
    }

    protected Object mapObject(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        if (t != JsonToken.FIELD_NAME) {
            return new LinkedHashMap(4);
        }
        String field1 = jp.getText();
        jp.nextToken();
        Object value1 = this.deserialize(jp, ctxt);
        if (jp.nextToken() != JsonToken.FIELD_NAME) {
            LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>(4);
            result.put(field1, value1);
            return result;
        }
        String field2 = jp.getText();
        jp.nextToken();
        Object value2 = this.deserialize(jp, ctxt);
        if (jp.nextToken() != JsonToken.FIELD_NAME) {
            LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>(4);
            result.put(field1, value1);
            result.put(field2, value2);
            return result;
        }
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put(field1, value1);
        result.put(field2, value2);
        do {
            String fieldName = jp.getText();
            jp.nextToken();
            result.put(fieldName, this.deserialize(jp, ctxt));
        } while (jp.nextToken() != JsonToken.END_OBJECT);
        return result;
    }

    protected Object[] mapArrayToArray(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (jp.nextToken() == JsonToken.END_ARRAY) {
            return NO_OBJECTS;
        }
        ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] values = buffer.resetAndStart();
        int ptr = 0;
        do {
            Object value = this.deserialize(jp, ctxt);
            if (ptr >= values.length) {
                values = buffer.appendCompletedChunk(values);
                ptr = 0;
            }
            values[ptr++] = value;
        } while (jp.nextToken() != JsonToken.END_ARRAY);
        return buffer.completeAndClearBuffer(values, ptr);
    }
}

