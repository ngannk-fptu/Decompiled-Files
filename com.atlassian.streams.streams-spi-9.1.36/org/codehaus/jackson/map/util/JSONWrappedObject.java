/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.util;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializableWithType;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JSONWrappedObject
implements JsonSerializableWithType {
    protected final String _prefix;
    protected final String _suffix;
    protected final Object _value;
    protected final JavaType _serializationType;

    public JSONWrappedObject(String prefix, String suffix, Object value) {
        this(prefix, suffix, value, (JavaType)null);
    }

    public JSONWrappedObject(String prefix, String suffix, Object value, JavaType asType) {
        this._prefix = prefix;
        this._suffix = suffix;
        this._value = value;
        this._serializationType = asType;
    }

    @Deprecated
    public JSONWrappedObject(String prefix, String suffix, Object value, Class<?> rawType) {
        this._prefix = prefix;
        this._suffix = suffix;
        this._value = value;
        this._serializationType = rawType == null ? null : TypeFactory.defaultInstance().constructType(rawType);
    }

    @Override
    public void serializeWithType(JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
        this.serialize(jgen, provider);
    }

    @Override
    public void serialize(JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (this._prefix != null) {
            jgen.writeRaw(this._prefix);
        }
        if (this._value == null) {
            provider.defaultSerializeNull(jgen);
        } else if (this._serializationType != null) {
            provider.findTypedValueSerializer(this._serializationType, true, null).serialize(this._value, jgen, provider);
        } else {
            Class<?> cls = this._value.getClass();
            provider.findTypedValueSerializer(cls, true, null).serialize(this._value, jgen, provider);
        }
        if (this._suffix != null) {
            jgen.writeRaw(this._suffix);
        }
    }

    public String getPrefix() {
        return this._prefix;
    }

    public String getSuffix() {
        return this._suffix;
    }

    public Object getValue() {
        return this._value;
    }

    public JavaType getSerializationType() {
        return this._serializationType;
    }
}

