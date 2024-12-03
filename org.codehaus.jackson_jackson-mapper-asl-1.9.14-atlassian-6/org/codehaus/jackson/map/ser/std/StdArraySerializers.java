/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerationException
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonNode
 */
package org.codehaus.jackson.map.ser.std;

import java.io.IOException;
import java.lang.reflect.Type;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ResolvableSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.ser.std.ContainerSerializerBase;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.node.ObjectNode;

public class StdArraySerializers {
    protected StdArraySerializers() {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class DoubleArraySerializer
    extends ArraySerializerBase<double[]> {
        public DoubleArraySerializer() {
            super(double[].class, null, null);
        }

        @Override
        public ContainerSerializerBase<?> _withValueTypeSerializer(TypeSerializer vts) {
            return this;
        }

        @Override
        public void serializeContents(double[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            int len = value.length;
            for (int i = 0; i < len; ++i) {
                jgen.writeNumber(value[i]);
            }
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            ObjectNode o = this.createSchemaNode("array", true);
            o.put("items", this.createSchemaNode("number"));
            return o;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class FloatArraySerializer
    extends ArraySerializerBase<float[]> {
        public FloatArraySerializer() {
            this((TypeSerializer)null);
        }

        public FloatArraySerializer(TypeSerializer vts) {
            super(float[].class, vts, null);
        }

        @Override
        public ContainerSerializerBase<?> _withValueTypeSerializer(TypeSerializer vts) {
            return new FloatArraySerializer(vts);
        }

        @Override
        public void serializeContents(float[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            int len = value.length;
            for (int i = 0; i < len; ++i) {
                jgen.writeNumber(value[i]);
            }
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            ObjectNode o = this.createSchemaNode("array", true);
            o.put("items", this.createSchemaNode("number"));
            return o;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class LongArraySerializer
    extends ArraySerializerBase<long[]> {
        public LongArraySerializer() {
            this((TypeSerializer)null);
        }

        public LongArraySerializer(TypeSerializer vts) {
            super(long[].class, vts, null);
        }

        @Override
        public ContainerSerializerBase<?> _withValueTypeSerializer(TypeSerializer vts) {
            return new LongArraySerializer(vts);
        }

        @Override
        public void serializeContents(long[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            int len = value.length;
            for (int i = 0; i < len; ++i) {
                jgen.writeNumber(value[i]);
            }
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            ObjectNode o = this.createSchemaNode("array", true);
            o.put("items", this.createSchemaNode("number", true));
            return o;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class IntArraySerializer
    extends ArraySerializerBase<int[]> {
        public IntArraySerializer() {
            super(int[].class, null, null);
        }

        @Override
        public ContainerSerializerBase<?> _withValueTypeSerializer(TypeSerializer vts) {
            return this;
        }

        @Override
        public void serializeContents(int[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            int len = value.length;
            for (int i = 0; i < len; ++i) {
                jgen.writeNumber(value[i]);
            }
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            ObjectNode o = this.createSchemaNode("array", true);
            o.put("items", this.createSchemaNode("integer"));
            return o;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class CharArraySerializer
    extends SerializerBase<char[]> {
        public CharArraySerializer() {
            super(char[].class);
        }

        @Override
        public void serialize(char[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            if (provider.isEnabled(SerializationConfig.Feature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)) {
                jgen.writeStartArray();
                this._writeArrayContents(jgen, value);
                jgen.writeEndArray();
            } else {
                jgen.writeString(value, 0, value.length);
            }
        }

        @Override
        public void serializeWithType(char[] value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonGenerationException {
            if (provider.isEnabled(SerializationConfig.Feature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)) {
                typeSer.writeTypePrefixForArray(value, jgen);
                this._writeArrayContents(jgen, value);
                typeSer.writeTypeSuffixForArray(value, jgen);
            } else {
                typeSer.writeTypePrefixForScalar(value, jgen);
                jgen.writeString(value, 0, value.length);
                typeSer.writeTypeSuffixForScalar(value, jgen);
            }
        }

        private final void _writeArrayContents(JsonGenerator jgen, char[] value) throws IOException, JsonGenerationException {
            int len = value.length;
            for (int i = 0; i < len; ++i) {
                jgen.writeString(value, i, 1);
            }
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            ObjectNode o = this.createSchemaNode("array", true);
            ObjectNode itemSchema = this.createSchemaNode("string");
            itemSchema.put("type", "string");
            o.put("items", itemSchema);
            return o;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class ShortArraySerializer
    extends ArraySerializerBase<short[]> {
        public ShortArraySerializer() {
            this((TypeSerializer)null);
        }

        public ShortArraySerializer(TypeSerializer vts) {
            super(short[].class, vts, null);
        }

        @Override
        public ContainerSerializerBase<?> _withValueTypeSerializer(TypeSerializer vts) {
            return new ShortArraySerializer(vts);
        }

        @Override
        public void serializeContents(short[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            int len = value.length;
            for (int i = 0; i < len; ++i) {
                jgen.writeNumber((int)value[i]);
            }
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            ObjectNode o = this.createSchemaNode("array", true);
            o.put("items", this.createSchemaNode("integer"));
            return o;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class ByteArraySerializer
    extends SerializerBase<byte[]> {
        public ByteArraySerializer() {
            super(byte[].class);
        }

        @Override
        public void serialize(byte[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeBinary(value);
        }

        @Override
        public void serializeWithType(byte[] value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonGenerationException {
            typeSer.writeTypePrefixForScalar(value, jgen);
            jgen.writeBinary(value);
            typeSer.writeTypeSuffixForScalar(value, jgen);
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            ObjectNode o = this.createSchemaNode("array", true);
            ObjectNode itemSchema = this.createSchemaNode("string");
            o.put("items", itemSchema);
            return o;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class BooleanArraySerializer
    extends ArraySerializerBase<boolean[]> {
        public BooleanArraySerializer() {
            super(boolean[].class, null, null);
        }

        @Override
        public ContainerSerializerBase<?> _withValueTypeSerializer(TypeSerializer vts) {
            return this;
        }

        @Override
        public void serializeContents(boolean[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            int len = value.length;
            for (int i = 0; i < len; ++i) {
                jgen.writeBoolean(value[i]);
            }
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            ObjectNode o = this.createSchemaNode("array", true);
            o.put("items", this.createSchemaNode("boolean"));
            return o;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class StringArraySerializer
    extends ArraySerializerBase<String[]>
    implements ResolvableSerializer {
        protected JsonSerializer<Object> _elementSerializer;

        public StringArraySerializer(BeanProperty prop) {
            super(String[].class, null, prop);
        }

        @Override
        public ContainerSerializerBase<?> _withValueTypeSerializer(TypeSerializer vts) {
            return this;
        }

        @Override
        public void serializeContents(String[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            int len = value.length;
            if (len == 0) {
                return;
            }
            if (this._elementSerializer != null) {
                this.serializeContentsSlow(value, jgen, provider, this._elementSerializer);
                return;
            }
            for (int i = 0; i < len; ++i) {
                String str = value[i];
                if (str == null) {
                    jgen.writeNull();
                    continue;
                }
                jgen.writeString(value[i]);
            }
        }

        private void serializeContentsSlow(String[] value, JsonGenerator jgen, SerializerProvider provider, JsonSerializer<Object> ser) throws IOException, JsonGenerationException {
            int len = value.length;
            for (int i = 0; i < len; ++i) {
                String str = value[i];
                if (str == null) {
                    provider.defaultSerializeNull(jgen);
                    continue;
                }
                ser.serialize(value[i], jgen, provider);
            }
        }

        @Override
        public void resolve(SerializerProvider provider) throws JsonMappingException {
            JsonSerializer<Object> ser = provider.findValueSerializer(String.class, this._property);
            if (ser != null && ser.getClass().getAnnotation(JacksonStdImpl.class) == null) {
                this._elementSerializer = ser;
            }
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            ObjectNode o = this.createSchemaNode("array", true);
            o.put("items", this.createSchemaNode("string"));
            return o;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class ArraySerializerBase<T>
    extends ContainerSerializerBase<T> {
        protected final TypeSerializer _valueTypeSerializer;
        protected final BeanProperty _property;

        protected ArraySerializerBase(Class<T> cls, TypeSerializer vts, BeanProperty property) {
            super(cls);
            this._valueTypeSerializer = vts;
            this._property = property;
        }

        @Override
        public final void serialize(T value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeStartArray();
            this.serializeContents(value, jgen, provider);
            jgen.writeEndArray();
        }

        @Override
        public final void serializeWithType(T value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonGenerationException {
            typeSer.writeTypePrefixForArray(value, jgen);
            this.serializeContents(value, jgen, provider);
            typeSer.writeTypeSuffixForArray(value, jgen);
        }

        protected abstract void serializeContents(T var1, JsonGenerator var2, SerializerProvider var3) throws IOException, JsonGenerationException;
    }
}

