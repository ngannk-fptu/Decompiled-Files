/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.Base64Variants
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonToken
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import java.util.HashMap;
import org.codehaus.jackson.Base64Variants;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.map.util.ArrayBuilders;
import org.codehaus.jackson.map.util.ObjectBuffer;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PrimitiveArrayDeserializers {
    HashMap<JavaType, JsonDeserializer<Object>> _allDeserializers = new HashMap();
    static final PrimitiveArrayDeserializers instance = new PrimitiveArrayDeserializers();

    protected PrimitiveArrayDeserializers() {
        this.add(Boolean.TYPE, new BooleanDeser());
        this.add(Byte.TYPE, new ByteDeser());
        this.add(Short.TYPE, new ShortDeser());
        this.add(Integer.TYPE, new IntDeser());
        this.add(Long.TYPE, new LongDeser());
        this.add(Float.TYPE, new FloatDeser());
        this.add(Double.TYPE, new DoubleDeser());
        this.add(String.class, new StringDeser());
        this.add(Character.TYPE, new CharDeser());
    }

    public static HashMap<JavaType, JsonDeserializer<Object>> getAll() {
        return PrimitiveArrayDeserializers.instance._allDeserializers;
    }

    private void add(Class<?> cls, JsonDeserializer<?> deser) {
        this._allDeserializers.put(TypeFactory.defaultInstance().constructType(cls), deser);
    }

    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromArray(jp, ctxt);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    static final class DoubleDeser
    extends Base<double[]> {
        public DoubleDeser() {
            super(double[].class);
        }

        @Override
        public double[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (!jp.isExpectedStartArrayToken()) {
                return this.handleNonArray(jp, ctxt);
            }
            ArrayBuilders.DoubleBuilder builder = ctxt.getArrayBuilders().getDoubleBuilder();
            double[] chunk = (double[])builder.resetAndStart();
            int ix = 0;
            while (jp.nextToken() != JsonToken.END_ARRAY) {
                double value = this._parseDoublePrimitive(jp, ctxt);
                if (ix >= chunk.length) {
                    chunk = builder.appendCompletedChunk(chunk, ix);
                    ix = 0;
                }
                chunk[ix++] = value;
            }
            return builder.completeAndClearBuffer(chunk, ix);
        }

        private final double[] handleNonArray(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (jp.getCurrentToken() == JsonToken.VALUE_STRING && ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) && jp.getText().length() == 0) {
                return null;
            }
            if (!ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
                throw ctxt.mappingException(this._valueClass);
            }
            return new double[]{this._parseDoublePrimitive(jp, ctxt)};
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    static final class FloatDeser
    extends Base<float[]> {
        public FloatDeser() {
            super(float[].class);
        }

        @Override
        public float[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (!jp.isExpectedStartArrayToken()) {
                return this.handleNonArray(jp, ctxt);
            }
            ArrayBuilders.FloatBuilder builder = ctxt.getArrayBuilders().getFloatBuilder();
            float[] chunk = (float[])builder.resetAndStart();
            int ix = 0;
            while (jp.nextToken() != JsonToken.END_ARRAY) {
                float value = this._parseFloatPrimitive(jp, ctxt);
                if (ix >= chunk.length) {
                    chunk = builder.appendCompletedChunk(chunk, ix);
                    ix = 0;
                }
                chunk[ix++] = value;
            }
            return builder.completeAndClearBuffer(chunk, ix);
        }

        private final float[] handleNonArray(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (jp.getCurrentToken() == JsonToken.VALUE_STRING && ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) && jp.getText().length() == 0) {
                return null;
            }
            if (!ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
                throw ctxt.mappingException(this._valueClass);
            }
            return new float[]{this._parseFloatPrimitive(jp, ctxt)};
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    static final class LongDeser
    extends Base<long[]> {
        public LongDeser() {
            super(long[].class);
        }

        @Override
        public long[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (!jp.isExpectedStartArrayToken()) {
                return this.handleNonArray(jp, ctxt);
            }
            ArrayBuilders.LongBuilder builder = ctxt.getArrayBuilders().getLongBuilder();
            long[] chunk = (long[])builder.resetAndStart();
            int ix = 0;
            while (jp.nextToken() != JsonToken.END_ARRAY) {
                long value = this._parseLongPrimitive(jp, ctxt);
                if (ix >= chunk.length) {
                    chunk = builder.appendCompletedChunk(chunk, ix);
                    ix = 0;
                }
                chunk[ix++] = value;
            }
            return builder.completeAndClearBuffer(chunk, ix);
        }

        private final long[] handleNonArray(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (jp.getCurrentToken() == JsonToken.VALUE_STRING && ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) && jp.getText().length() == 0) {
                return null;
            }
            if (!ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
                throw ctxt.mappingException(this._valueClass);
            }
            return new long[]{this._parseLongPrimitive(jp, ctxt)};
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    static final class IntDeser
    extends Base<int[]> {
        public IntDeser() {
            super(int[].class);
        }

        @Override
        public int[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (!jp.isExpectedStartArrayToken()) {
                return this.handleNonArray(jp, ctxt);
            }
            ArrayBuilders.IntBuilder builder = ctxt.getArrayBuilders().getIntBuilder();
            int[] chunk = (int[])builder.resetAndStart();
            int ix = 0;
            while (jp.nextToken() != JsonToken.END_ARRAY) {
                int value = this._parseIntPrimitive(jp, ctxt);
                if (ix >= chunk.length) {
                    chunk = builder.appendCompletedChunk(chunk, ix);
                    ix = 0;
                }
                chunk[ix++] = value;
            }
            return builder.completeAndClearBuffer(chunk, ix);
        }

        private final int[] handleNonArray(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (jp.getCurrentToken() == JsonToken.VALUE_STRING && ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) && jp.getText().length() == 0) {
                return null;
            }
            if (!ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
                throw ctxt.mappingException(this._valueClass);
            }
            return new int[]{this._parseIntPrimitive(jp, ctxt)};
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    static final class ShortDeser
    extends Base<short[]> {
        public ShortDeser() {
            super(short[].class);
        }

        @Override
        public short[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (!jp.isExpectedStartArrayToken()) {
                return this.handleNonArray(jp, ctxt);
            }
            ArrayBuilders.ShortBuilder builder = ctxt.getArrayBuilders().getShortBuilder();
            short[] chunk = (short[])builder.resetAndStart();
            int ix = 0;
            while (jp.nextToken() != JsonToken.END_ARRAY) {
                short value = this._parseShortPrimitive(jp, ctxt);
                if (ix >= chunk.length) {
                    chunk = builder.appendCompletedChunk(chunk, ix);
                    ix = 0;
                }
                chunk[ix++] = value;
            }
            return builder.completeAndClearBuffer(chunk, ix);
        }

        private final short[] handleNonArray(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (jp.getCurrentToken() == JsonToken.VALUE_STRING && ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) && jp.getText().length() == 0) {
                return null;
            }
            if (!ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
                throw ctxt.mappingException(this._valueClass);
            }
            return new short[]{this._parseShortPrimitive(jp, ctxt)};
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    static final class ByteDeser
    extends Base<byte[]> {
        public ByteDeser() {
            super(byte[].class);
        }

        @Override
        public byte[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.VALUE_STRING) {
                return jp.getBinaryValue(ctxt.getBase64Variant());
            }
            if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
                Object ob = jp.getEmbeddedObject();
                if (ob == null) {
                    return null;
                }
                if (ob instanceof byte[]) {
                    return (byte[])ob;
                }
            }
            if (!jp.isExpectedStartArrayToken()) {
                return this.handleNonArray(jp, ctxt);
            }
            ArrayBuilders.ByteBuilder builder = ctxt.getArrayBuilders().getByteBuilder();
            byte[] chunk = (byte[])builder.resetAndStart();
            int ix = 0;
            while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
                byte value;
                if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
                    value = jp.getByteValue();
                } else {
                    if (t != JsonToken.VALUE_NULL) {
                        throw ctxt.mappingException(this._valueClass.getComponentType());
                    }
                    value = 0;
                }
                if (ix >= chunk.length) {
                    chunk = builder.appendCompletedChunk(chunk, ix);
                    ix = 0;
                }
                chunk[ix++] = value;
            }
            return builder.completeAndClearBuffer(chunk, ix);
        }

        private final byte[] handleNonArray(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            byte value;
            if (jp.getCurrentToken() == JsonToken.VALUE_STRING && ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) && jp.getText().length() == 0) {
                return null;
            }
            if (!ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
                throw ctxt.mappingException(this._valueClass);
            }
            JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
                value = jp.getByteValue();
            } else {
                if (t != JsonToken.VALUE_NULL) {
                    throw ctxt.mappingException(this._valueClass.getComponentType());
                }
                value = 0;
            }
            return new byte[]{value};
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    static final class BooleanDeser
    extends Base<boolean[]> {
        public BooleanDeser() {
            super(boolean[].class);
        }

        @Override
        public boolean[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (!jp.isExpectedStartArrayToken()) {
                return this.handleNonArray(jp, ctxt);
            }
            ArrayBuilders.BooleanBuilder builder = ctxt.getArrayBuilders().getBooleanBuilder();
            boolean[] chunk = (boolean[])builder.resetAndStart();
            int ix = 0;
            while (jp.nextToken() != JsonToken.END_ARRAY) {
                boolean value = this._parseBooleanPrimitive(jp, ctxt);
                if (ix >= chunk.length) {
                    chunk = builder.appendCompletedChunk(chunk, ix);
                    ix = 0;
                }
                chunk[ix++] = value;
            }
            return builder.completeAndClearBuffer(chunk, ix);
        }

        private final boolean[] handleNonArray(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (jp.getCurrentToken() == JsonToken.VALUE_STRING && ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) && jp.getText().length() == 0) {
                return null;
            }
            if (!ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
                throw ctxt.mappingException(this._valueClass);
            }
            return new boolean[]{this._parseBooleanPrimitive(jp, ctxt)};
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    static final class CharDeser
    extends Base<char[]> {
        public CharDeser() {
            super(char[].class);
        }

        @Override
        public char[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.VALUE_STRING) {
                char[] buffer = jp.getTextCharacters();
                int offset = jp.getTextOffset();
                int len = jp.getTextLength();
                char[] result = new char[len];
                System.arraycopy(buffer, offset, result, 0, len);
                return result;
            }
            if (jp.isExpectedStartArrayToken()) {
                StringBuilder sb = new StringBuilder(64);
                while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
                    if (t != JsonToken.VALUE_STRING) {
                        throw ctxt.mappingException(Character.TYPE);
                    }
                    String str = jp.getText();
                    if (str.length() != 1) {
                        throw JsonMappingException.from(jp, "Can not convert a JSON String of length " + str.length() + " into a char element of char array");
                    }
                    sb.append(str.charAt(0));
                }
                return sb.toString().toCharArray();
            }
            if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
                Object ob = jp.getEmbeddedObject();
                if (ob == null) {
                    return null;
                }
                if (ob instanceof char[]) {
                    return (char[])ob;
                }
                if (ob instanceof String) {
                    return ((String)ob).toCharArray();
                }
                if (ob instanceof byte[]) {
                    return Base64Variants.getDefaultVariant().encode((byte[])ob, false).toCharArray();
                }
            }
            throw ctxt.mappingException(this._valueClass);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    static final class StringDeser
    extends Base<String[]> {
        public StringDeser() {
            super(String[].class);
        }

        @Override
        public String[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken t;
            if (!jp.isExpectedStartArrayToken()) {
                return this.handleNonArray(jp, ctxt);
            }
            ObjectBuffer buffer = ctxt.leaseObjectBuffer();
            Object[] chunk = buffer.resetAndStart();
            int ix = 0;
            while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
                String value;
                String string = value = t == JsonToken.VALUE_NULL ? null : jp.getText();
                if (ix >= chunk.length) {
                    chunk = buffer.appendCompletedChunk(chunk);
                    ix = 0;
                }
                chunk[ix++] = value;
            }
            String[] result = buffer.completeAndClearBuffer(chunk, ix, String.class);
            ctxt.returnObjectBuffer(buffer);
            return result;
        }

        private final String[] handleNonArray(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (!ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
                String str;
                if (jp.getCurrentToken() == JsonToken.VALUE_STRING && ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) && (str = jp.getText()).length() == 0) {
                    return null;
                }
                throw ctxt.mappingException(this._valueClass);
            }
            return new String[]{jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getText()};
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static abstract class Base<T>
    extends StdDeserializer<T> {
        protected Base(Class<T> cls) {
            super(cls);
        }

        @Override
        public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
            return typeDeserializer.deserializeTypedFromArray(jp, ctxt);
        }
    }
}

