/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonToken
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import java.lang.reflect.Array;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.deser.std.ContainerDeserializerBase;
import org.codehaus.jackson.map.type.ArrayType;
import org.codehaus.jackson.map.util.ObjectBuffer;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JacksonStdImpl
public class ObjectArrayDeserializer
extends ContainerDeserializerBase<Object[]> {
    protected final JavaType _arrayType;
    protected final boolean _untyped;
    protected final Class<?> _elementClass;
    protected final JsonDeserializer<Object> _elementDeserializer;
    protected final TypeDeserializer _elementTypeDeserializer;

    public ObjectArrayDeserializer(ArrayType arrayType, JsonDeserializer<Object> elemDeser, TypeDeserializer elemTypeDeser) {
        super(Object[].class);
        this._arrayType = arrayType;
        this._elementClass = arrayType.getContentType().getRawClass();
        this._untyped = this._elementClass == Object.class;
        this._elementDeserializer = elemDeser;
        this._elementTypeDeserializer = elemTypeDeser;
    }

    @Override
    public JavaType getContentType() {
        return this._arrayType.getContentType();
    }

    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return this._elementDeserializer;
    }

    @Override
    public Object[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t;
        if (!jp.isExpectedStartArrayToken()) {
            return this.handleNonArray(jp, ctxt);
        }
        ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] chunk = buffer.resetAndStart();
        int ix = 0;
        TypeDeserializer typeDeser = this._elementTypeDeserializer;
        while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
            Object value = t == JsonToken.VALUE_NULL ? null : (typeDeser == null ? this._elementDeserializer.deserialize(jp, ctxt) : this._elementDeserializer.deserializeWithType(jp, ctxt, typeDeser));
            if (ix >= chunk.length) {
                chunk = buffer.appendCompletedChunk(chunk);
                ix = 0;
            }
            chunk[ix++] = value;
        }
        Object[] result = this._untyped ? buffer.completeAndClearBuffer(chunk, ix) : buffer.completeAndClearBuffer(chunk, ix, this._elementClass);
        ctxt.returnObjectBuffer(buffer);
        return result;
    }

    public Object[] deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return (Object[])typeDeserializer.deserializeTypedFromArray(jp, ctxt);
    }

    protected Byte[] deserializeFromBase64(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        byte[] b = jp.getBinaryValue(ctxt.getBase64Variant());
        Byte[] result = new Byte[b.length];
        int len = b.length;
        for (int i = 0; i < len; ++i) {
            result[i] = b[i];
        }
        return result;
    }

    private final Object[] handleNonArray(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String str;
        if (jp.getCurrentToken() == JsonToken.VALUE_STRING && ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) && (str = jp.getText()).length() == 0) {
            return null;
        }
        if (!ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            if (jp.getCurrentToken() == JsonToken.VALUE_STRING && this._elementClass == Byte.class) {
                return this.deserializeFromBase64(jp, ctxt);
            }
            throw ctxt.mappingException(this._arrayType.getRawClass());
        }
        JsonToken t = jp.getCurrentToken();
        Object value = t == JsonToken.VALUE_NULL ? null : (this._elementTypeDeserializer == null ? this._elementDeserializer.deserialize(jp, ctxt) : this._elementDeserializer.deserializeWithType(jp, ctxt, this._elementTypeDeserializer));
        Object[] result = this._untyped ? new Object[1] : (Object[])Array.newInstance(this._elementClass, 1);
        result[0] = value;
        return result;
    }
}

