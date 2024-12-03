/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import java.util.EnumMap;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.deser.std.EnumDeserializer;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.map.util.EnumResolver;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EnumMapDeserializer
extends StdDeserializer<EnumMap<?, ?>> {
    protected final Class<?> _enumClass;
    protected final JsonDeserializer<Enum<?>> _keyDeserializer;
    protected final JsonDeserializer<Object> _valueDeserializer;

    @Deprecated
    public EnumMapDeserializer(EnumResolver<?> enumRes, JsonDeserializer<Object> valueDeser) {
        this(enumRes.getEnumClass(), new EnumDeserializer(enumRes), valueDeser);
    }

    public EnumMapDeserializer(Class<?> enumClass, JsonDeserializer<?> keyDeserializer, JsonDeserializer<Object> valueDeser) {
        super(EnumMap.class);
        this._enumClass = enumClass;
        this._keyDeserializer = keyDeserializer;
        this._valueDeserializer = valueDeser;
    }

    @Override
    public EnumMap<?, ?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
            throw ctxt.mappingException(EnumMap.class);
        }
        EnumMap<?, ?> result = this.constructMap();
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            Enum<?> key = this._keyDeserializer.deserialize(jp, ctxt);
            if (key == null) {
                throw ctxt.weirdStringException(this._enumClass, "value not one of declared Enum instance names");
            }
            JsonToken t = jp.nextToken();
            Object value = t == JsonToken.VALUE_NULL ? null : this._valueDeserializer.deserialize(jp, ctxt);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromObject(jp, ctxt);
    }

    private EnumMap<?, ?> constructMap() {
        return new EnumMap(this._enumClass);
    }
}

