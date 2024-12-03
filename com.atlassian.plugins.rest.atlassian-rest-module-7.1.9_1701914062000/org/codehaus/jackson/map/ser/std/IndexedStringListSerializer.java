/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser.std;

import java.io.IOException;
import java.util.List;
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
public final class IndexedStringListSerializer
extends StaticListSerializerBase<List<String>>
implements ResolvableSerializer {
    protected JsonSerializer<String> _serializer;

    public IndexedStringListSerializer(BeanProperty property) {
        this(property, null);
    }

    public IndexedStringListSerializer(BeanProperty property, JsonSerializer<?> ser) {
        super(List.class, property);
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
    public void serialize(List<String> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeStartArray();
        if (this._serializer == null) {
            this.serializeContents(value, jgen, provider);
        } else {
            this.serializeUsingCustom(value, jgen, provider);
        }
        jgen.writeEndArray();
    }

    @Override
    public void serializeWithType(List<String> value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonGenerationException {
        typeSer.writeTypePrefixForArray(value, jgen);
        if (this._serializer == null) {
            this.serializeContents(value, jgen, provider);
        } else {
            this.serializeUsingCustom(value, jgen, provider);
        }
        typeSer.writeTypeSuffixForArray(value, jgen);
    }

    private final void serializeContents(List<String> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        int i;
        try {
            int len = value.size();
            for (i = 0; i < len; ++i) {
                String str = value.get(i);
                if (str == null) {
                    provider.defaultSerializeNull(jgen);
                    continue;
                }
                jgen.writeString(str);
            }
        }
        catch (Exception e) {
            this.wrapAndThrow(provider, (Throwable)e, value, i);
        }
    }

    private final void serializeUsingCustom(List<String> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        int i = 0;
        try {
            int len = value.size();
            JsonSerializer<String> ser = this._serializer;
            for (i = 0; i < len; ++i) {
                String str = value.get(i);
                if (str == null) {
                    provider.defaultSerializeNull(jgen);
                    continue;
                }
                ser.serialize(str, jgen, provider);
            }
        }
        catch (Exception e) {
            this.wrapAndThrow(provider, (Throwable)e, value, i);
        }
    }
}

