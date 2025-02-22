/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.ser.std.DateSerializer;
import org.codehaus.jackson.map.ser.std.NonTypedScalarSerializerBase;
import org.codehaus.jackson.map.ser.std.ScalarSerializerBase;

public class StdSerializers {
    protected StdSerializers() {
    }

    @Deprecated
    @JacksonStdImpl
    public static final class SerializableWithTypeSerializer
    extends org.codehaus.jackson.map.ser.std.SerializableWithTypeSerializer {
    }

    @Deprecated
    @JacksonStdImpl
    public static final class SerializableSerializer
    extends org.codehaus.jackson.map.ser.std.SerializableSerializer {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class SqlTimeSerializer
    extends ScalarSerializerBase<Time> {
        public SqlTimeSerializer() {
            super(Time.class);
        }

        @Override
        public void serialize(Time value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeString(value.toString());
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode("string", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class SqlDateSerializer
    extends ScalarSerializerBase<Date> {
        public SqlDateSerializer() {
            super(Date.class);
        }

        @Override
        public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeString(value.toString());
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode("string", true);
        }
    }

    @Deprecated
    @JacksonStdImpl
    public static final class UtilDateSerializer
    extends DateSerializer {
    }

    @JacksonStdImpl
    @Deprecated
    public static final class CalendarSerializer
    extends org.codehaus.jackson.map.ser.std.CalendarSerializer {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class NumberSerializer
    extends ScalarSerializerBase<Number> {
        public static final NumberSerializer instance = new NumberSerializer();

        public NumberSerializer() {
            super(Number.class);
        }

        @Override
        public void serialize(Number value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            if (value instanceof BigDecimal) {
                jgen.writeNumber((BigDecimal)value);
            } else if (value instanceof BigInteger) {
                jgen.writeNumber((BigInteger)value);
            } else if (value instanceof Integer) {
                jgen.writeNumber(value.intValue());
            } else if (value instanceof Long) {
                jgen.writeNumber(value.longValue());
            } else if (value instanceof Double) {
                jgen.writeNumber(value.doubleValue());
            } else if (value instanceof Float) {
                jgen.writeNumber(value.floatValue());
            } else if (value instanceof Byte || value instanceof Short) {
                jgen.writeNumber(value.intValue());
            } else {
                jgen.writeNumber(value.toString());
            }
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode("number", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class DoubleSerializer
    extends NonTypedScalarSerializerBase<Double> {
        static final DoubleSerializer instance = new DoubleSerializer();

        public DoubleSerializer() {
            super(Double.class);
        }

        @Override
        public void serialize(Double value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeNumber(value);
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode("number", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class FloatSerializer
    extends ScalarSerializerBase<Float> {
        static final FloatSerializer instance = new FloatSerializer();

        public FloatSerializer() {
            super(Float.class);
        }

        @Override
        public void serialize(Float value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeNumber(value.floatValue());
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode("number", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class LongSerializer
    extends ScalarSerializerBase<Long> {
        static final LongSerializer instance = new LongSerializer();

        public LongSerializer() {
            super(Long.class);
        }

        @Override
        public void serialize(Long value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeNumber(value);
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode("number", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class IntLikeSerializer
    extends ScalarSerializerBase<Number> {
        static final IntLikeSerializer instance = new IntLikeSerializer();

        public IntLikeSerializer() {
            super(Number.class);
        }

        @Override
        public void serialize(Number value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeNumber(value.intValue());
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode("integer", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class IntegerSerializer
    extends NonTypedScalarSerializerBase<Integer> {
        public IntegerSerializer() {
            super(Integer.class);
        }

        @Override
        public void serialize(Integer value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeNumber(value);
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode("integer", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @Deprecated
    @JacksonStdImpl
    public static final class StringSerializer
    extends NonTypedScalarSerializerBase<String> {
        public StringSerializer() {
            super(String.class);
        }

        @Override
        public void serialize(String value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeString(value);
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode("string", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class BooleanSerializer
    extends NonTypedScalarSerializerBase<Boolean> {
        final boolean _forPrimitive;

        public BooleanSerializer(boolean forPrimitive) {
            super(Boolean.class);
            this._forPrimitive = forPrimitive;
        }

        @Override
        public void serialize(Boolean value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeBoolean(value);
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode("boolean", !this._forPrimitive);
        }
    }
}

