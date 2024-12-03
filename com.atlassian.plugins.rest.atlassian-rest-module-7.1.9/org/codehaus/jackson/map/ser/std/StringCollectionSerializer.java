/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser.std;

import java.io.IOException;
import java.util.Collection;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ResolvableSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.ser.std.StaticListSerializerBase;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JacksonStdImpl
public class StringCollectionSerializer
extends StaticListSerializerBase<Collection<String>>
implements ResolvableSerializer {
    protected JsonSerializer<String> _serializer;

    @Deprecated
    public StringCollectionSerializer(BeanProperty property) {
        this(property, null);
    }

    public StringCollectionSerializer(BeanProperty property, JsonSerializer<?> ser) {
        super(Collection.class, property);
        this._serializer = ser;
    }

    @Override
    protected JsonNode contentSchema() {
        return this.createSchemaNode("string", true);
    }

    @Override
    public void resolve(SerializerProvider provider) throws JsonMappingException {
        JsonSerializer<Object> ser;
        if (this._serializer == null && !this.isDefaultSerializer(ser = provider.findValueSerializer(String.class, this._property))) {
            this._serializer = ser;
        }
    }

    @Override
    public void serialize(Collection<String> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeStartArray();
        if (this._serializer == null) {
            this.serializeContents(value, jgen, provider);
        } else {
            this.serializeUsingCustom(value, jgen, provider);
        }
        jgen.writeEndArray();
    }

    @Override
    public void serializeWithType(Collection<String> value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonGenerationException {
        typeSer.writeTypePrefixForArray(value, jgen);
        if (this._serializer == null) {
            this.serializeContents(value, jgen, provider);
        } else {
            this.serializeUsingCustom(value, jgen, provider);
        }
        typeSer.writeTypeSuffixForArray(value, jgen);
    }

    private final void serializeContents(Collection<String> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._serializer != null) {
            this.serializeUsingCustom(value, jgen, provider);
            return;
        }
        int i = 0;
        for (String str : value) {
            try {
                if (str == null) {
                    provider.defaultSerializeNull(jgen);
                } else {
                    jgen.writeString(str);
                }
                ++i;
            }
            catch (Exception e) {
                this.wrapAndThrow(provider, (Throwable)e, value, i);
            }
        }
    }

    private void serializeUsingCustom(Collection<String> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        JsonSerializer<String> ser = this._serializer;
        int i = 0;
        for (String str : value) {
            try {
                if (str == null) {
                    provider.defaultSerializeNull(jgen);
                    continue;
                }
                ser.serialize(str, jgen, provider);
            }
            catch (Exception e) {
                this.wrapAndThrow(provider, (Throwable)e, value, i);
            }
        }
    }
}

