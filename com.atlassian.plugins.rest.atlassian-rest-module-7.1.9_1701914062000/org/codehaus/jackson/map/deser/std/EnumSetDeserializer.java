/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import java.util.EnumSet;
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
public class EnumSetDeserializer
extends StdDeserializer<EnumSet<?>> {
    protected final Class<Enum> _enumClass;
    protected final JsonDeserializer<Enum<?>> _enumDeserializer;

    public EnumSetDeserializer(EnumResolver enumRes) {
        this(enumRes.getEnumClass(), new EnumDeserializer(enumRes));
    }

    public EnumSetDeserializer(Class<?> enumClass, JsonDeserializer<?> deser) {
        super(EnumSet.class);
        this._enumClass = enumClass;
        this._enumDeserializer = deser;
    }

    @Override
    public EnumSet<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t;
        if (!jp.isExpectedStartArrayToken()) {
            throw ctxt.mappingException(EnumSet.class);
        }
        EnumSet result = this.constructSet();
        while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
            if (t == JsonToken.VALUE_NULL) {
                throw ctxt.mappingException(this._enumClass);
            }
            Enum<?> value = this._enumDeserializer.deserialize(jp, ctxt);
            result.add(value);
        }
        return result;
    }

    @Override
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromArray(jp, ctxt);
    }

    private EnumSet constructSet() {
        return EnumSet.noneOf(this._enumClass);
    }
}

