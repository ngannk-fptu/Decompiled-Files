/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonParser$NumberType
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonToken
 *  org.codehaus.jackson.io.NumberInput
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.io.NumberInput;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.deser.std.StdScalarDeserializer;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class StdDeserializer<T>
extends JsonDeserializer<T> {
    protected final Class<?> _valueClass;

    protected StdDeserializer(Class<?> vc) {
        this._valueClass = vc;
    }

    protected StdDeserializer(JavaType valueType) {
        this._valueClass = valueType == null ? null : valueType.getRawClass();
    }

    public Class<?> getValueClass() {
        return this._valueClass;
    }

    public JavaType getValueType() {
        return null;
    }

    protected boolean isDefaultSerializer(JsonDeserializer<?> deserializer) {
        return deserializer != null && deserializer.getClass().getAnnotation(JacksonStdImpl.class) != null;
    }

    @Override
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromAny(jp, ctxt);
    }

    protected final boolean _parseBooleanPrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_TRUE) {
            return true;
        }
        if (t == JsonToken.VALUE_FALSE) {
            return false;
        }
        if (t == JsonToken.VALUE_NULL) {
            return false;
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            if (jp.getNumberType() == JsonParser.NumberType.INT) {
                return jp.getIntValue() != 0;
            }
            return this._parseBooleanFromNumber(jp, ctxt);
        }
        if (t == JsonToken.VALUE_STRING) {
            String text = jp.getText().trim();
            if ("true".equals(text)) {
                return true;
            }
            if ("false".equals(text) || text.length() == 0) {
                return Boolean.FALSE;
            }
            throw ctxt.weirdStringException(this._valueClass, "only \"true\" or \"false\" recognized");
        }
        throw ctxt.mappingException(this._valueClass, t);
    }

    protected final Boolean _parseBoolean(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_TRUE) {
            return Boolean.TRUE;
        }
        if (t == JsonToken.VALUE_FALSE) {
            return Boolean.FALSE;
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            if (jp.getNumberType() == JsonParser.NumberType.INT) {
                return jp.getIntValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
            }
            return this._parseBooleanFromNumber(jp, ctxt);
        }
        if (t == JsonToken.VALUE_NULL) {
            return (Boolean)this.getNullValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            String text = jp.getText().trim();
            if ("true".equals(text)) {
                return Boolean.TRUE;
            }
            if ("false".equals(text)) {
                return Boolean.FALSE;
            }
            if (text.length() == 0) {
                return (Boolean)this.getEmptyValue();
            }
            throw ctxt.weirdStringException(this._valueClass, "only \"true\" or \"false\" recognized");
        }
        throw ctxt.mappingException(this._valueClass, t);
    }

    protected final boolean _parseBooleanFromNumber(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (jp.getNumberType() == JsonParser.NumberType.LONG) {
            return jp.getLongValue() == 0L ? Boolean.FALSE : Boolean.TRUE;
        }
        String str = jp.getText();
        if ("0.0".equals(str) || "0".equals(str)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    protected Byte _parseByte(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getByteValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            int value;
            String text = jp.getText().trim();
            try {
                int len = text.length();
                if (len == 0) {
                    return (Byte)this.getEmptyValue();
                }
                value = NumberInput.parseInt((String)text);
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(this._valueClass, "not a valid Byte value");
            }
            if (value < -128 || value > 255) {
                throw ctxt.weirdStringException(this._valueClass, "overflow, value can not be represented as 8-bit value");
            }
            return (byte)value;
        }
        if (t == JsonToken.VALUE_NULL) {
            return (Byte)this.getNullValue();
        }
        throw ctxt.mappingException(this._valueClass, t);
    }

    protected Short _parseShort(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getShortValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            int value;
            String text = jp.getText().trim();
            try {
                int len = text.length();
                if (len == 0) {
                    return (Short)this.getEmptyValue();
                }
                value = NumberInput.parseInt((String)text);
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(this._valueClass, "not a valid Short value");
            }
            if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                throw ctxt.weirdStringException(this._valueClass, "overflow, value can not be represented as 16-bit value");
            }
            return (short)value;
        }
        if (t == JsonToken.VALUE_NULL) {
            return (Short)this.getNullValue();
        }
        throw ctxt.mappingException(this._valueClass, t);
    }

    protected final short _parseShortPrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        int value = this._parseIntPrimitive(jp, ctxt);
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw ctxt.weirdStringException(this._valueClass, "overflow, value can not be represented as 16-bit value");
        }
        return (short)value;
    }

    protected final int _parseIntPrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getIntValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            String text = jp.getText().trim();
            try {
                int len = text.length();
                if (len > 9) {
                    long l = Long.parseLong(text);
                    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
                        throw ctxt.weirdStringException(this._valueClass, "Overflow: numeric value (" + text + ") out of range of int (" + Integer.MIN_VALUE + " - " + Integer.MAX_VALUE + ")");
                    }
                    return (int)l;
                }
                if (len == 0) {
                    return 0;
                }
                return NumberInput.parseInt((String)text);
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(this._valueClass, "not a valid int value");
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return 0;
        }
        throw ctxt.mappingException(this._valueClass, t);
    }

    protected final Integer _parseInteger(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getIntValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            String text = jp.getText().trim();
            try {
                int len = text.length();
                if (len > 9) {
                    long l = Long.parseLong(text);
                    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
                        throw ctxt.weirdStringException(this._valueClass, "Overflow: numeric value (" + text + ") out of range of Integer (" + Integer.MIN_VALUE + " - " + Integer.MAX_VALUE + ")");
                    }
                    return (int)l;
                }
                if (len == 0) {
                    return (Integer)this.getEmptyValue();
                }
                return NumberInput.parseInt((String)text);
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(this._valueClass, "not a valid Integer value");
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return (Integer)this.getNullValue();
        }
        throw ctxt.mappingException(this._valueClass, t);
    }

    protected final Long _parseLong(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getLongValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            String text = jp.getText().trim();
            if (text.length() == 0) {
                return (Long)this.getEmptyValue();
            }
            try {
                return NumberInput.parseLong((String)text);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                throw ctxt.weirdStringException(this._valueClass, "not a valid Long value");
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return (Long)this.getNullValue();
        }
        throw ctxt.mappingException(this._valueClass, t);
    }

    protected final long _parseLongPrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getLongValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            String text = jp.getText().trim();
            if (text.length() == 0) {
                return 0L;
            }
            try {
                return NumberInput.parseLong((String)text);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                throw ctxt.weirdStringException(this._valueClass, "not a valid long value");
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return 0L;
        }
        throw ctxt.mappingException(this._valueClass, t);
    }

    protected final Float _parseFloat(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return Float.valueOf(jp.getFloatValue());
        }
        if (t == JsonToken.VALUE_STRING) {
            String text = jp.getText().trim();
            if (text.length() == 0) {
                return (Float)this.getEmptyValue();
            }
            switch (text.charAt(0)) {
                case 'I': {
                    if (!"Infinity".equals(text) && !"INF".equals(text)) break;
                    return Float.valueOf(Float.POSITIVE_INFINITY);
                }
                case 'N': {
                    if (!"NaN".equals(text)) break;
                    return Float.valueOf(Float.NaN);
                }
                case '-': {
                    if (!"-Infinity".equals(text) && !"-INF".equals(text)) break;
                    return Float.valueOf(Float.NEGATIVE_INFINITY);
                }
            }
            try {
                return Float.valueOf(Float.parseFloat(text));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                throw ctxt.weirdStringException(this._valueClass, "not a valid Float value");
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return (Float)this.getNullValue();
        }
        throw ctxt.mappingException(this._valueClass, t);
    }

    protected final float _parseFloatPrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getFloatValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            String text = jp.getText().trim();
            if (text.length() == 0) {
                return 0.0f;
            }
            switch (text.charAt(0)) {
                case 'I': {
                    if (!"Infinity".equals(text) && !"INF".equals(text)) break;
                    return Float.POSITIVE_INFINITY;
                }
                case 'N': {
                    if (!"NaN".equals(text)) break;
                    return Float.NaN;
                }
                case '-': {
                    if (!"-Infinity".equals(text) && !"-INF".equals(text)) break;
                    return Float.NEGATIVE_INFINITY;
                }
            }
            try {
                return Float.parseFloat(text);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                throw ctxt.weirdStringException(this._valueClass, "not a valid float value");
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return 0.0f;
        }
        throw ctxt.mappingException(this._valueClass, t);
    }

    protected final Double _parseDouble(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getDoubleValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            String text = jp.getText().trim();
            if (text.length() == 0) {
                return (Double)this.getEmptyValue();
            }
            switch (text.charAt(0)) {
                case 'I': {
                    if (!"Infinity".equals(text) && !"INF".equals(text)) break;
                    return Double.POSITIVE_INFINITY;
                }
                case 'N': {
                    if (!"NaN".equals(text)) break;
                    return Double.NaN;
                }
                case '-': {
                    if (!"-Infinity".equals(text) && !"-INF".equals(text)) break;
                    return Double.NEGATIVE_INFINITY;
                }
            }
            try {
                return StdDeserializer.parseDouble(text);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                throw ctxt.weirdStringException(this._valueClass, "not a valid Double value");
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return (Double)this.getNullValue();
        }
        throw ctxt.mappingException(this._valueClass, t);
    }

    protected final double _parseDoublePrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return jp.getDoubleValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            String text = jp.getText().trim();
            if (text.length() == 0) {
                return 0.0;
            }
            switch (text.charAt(0)) {
                case 'I': {
                    if (!"Infinity".equals(text) && !"INF".equals(text)) break;
                    return Double.POSITIVE_INFINITY;
                }
                case 'N': {
                    if (!"NaN".equals(text)) break;
                    return Double.NaN;
                }
                case '-': {
                    if (!"-Infinity".equals(text) && !"-INF".equals(text)) break;
                    return Double.NEGATIVE_INFINITY;
                }
            }
            try {
                return StdDeserializer.parseDouble(text);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                throw ctxt.weirdStringException(this._valueClass, "not a valid double value");
            }
        }
        if (t == JsonToken.VALUE_NULL) {
            return 0.0;
        }
        throw ctxt.mappingException(this._valueClass, t);
    }

    protected java.util.Date _parseDate(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return new java.util.Date(jp.getLongValue());
        }
        if (t == JsonToken.VALUE_NULL) {
            return (java.util.Date)this.getNullValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            try {
                String str = jp.getText().trim();
                if (str.length() == 0) {
                    return (java.util.Date)this.getEmptyValue();
                }
                return ctxt.parseDate(str);
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(this._valueClass, "not a valid representation (error: " + iae.getMessage() + ")");
            }
        }
        throw ctxt.mappingException(this._valueClass, t);
    }

    protected static final double parseDouble(String numStr) throws NumberFormatException {
        if ("2.2250738585072012e-308".equals(numStr)) {
            return Double.MIN_NORMAL;
        }
        return Double.parseDouble(numStr);
    }

    protected JsonDeserializer<Object> findDeserializer(DeserializationConfig config, DeserializerProvider provider, JavaType type, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<Object> deser = provider.findValueDeserializer(config, type, property);
        return deser;
    }

    protected void handleUnknownProperty(JsonParser jp, DeserializationContext ctxt, Object instanceOrClass, String propName) throws IOException, JsonProcessingException {
        if (instanceOrClass == null) {
            instanceOrClass = this.getValueClass();
        }
        if (ctxt.handleUnknownProperty(jp, this, instanceOrClass, propName)) {
            return;
        }
        this.reportUnknownProperty(ctxt, instanceOrClass, propName);
        jp.skipChildren();
    }

    protected void reportUnknownProperty(DeserializationContext ctxt, Object instanceOrClass, String fieldName) throws IOException, JsonProcessingException {
        if (ctxt.isEnabled(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES)) {
            throw ctxt.unknownFieldException(instanceOrClass, fieldName);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class StackTraceElementDeserializer
    extends StdScalarDeserializer<StackTraceElement> {
        public StackTraceElementDeserializer() {
            super(StackTraceElement.class);
        }

        @Override
        public StackTraceElement deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.START_OBJECT) {
                String className = "";
                String methodName = "";
                String fileName = "";
                int lineNumber = -1;
                while ((t = jp.nextValue()) != JsonToken.END_OBJECT) {
                    String propName = jp.getCurrentName();
                    if ("className".equals(propName)) {
                        className = jp.getText();
                        continue;
                    }
                    if ("fileName".equals(propName)) {
                        fileName = jp.getText();
                        continue;
                    }
                    if ("lineNumber".equals(propName)) {
                        if (t.isNumeric()) {
                            lineNumber = jp.getIntValue();
                            continue;
                        }
                        throw JsonMappingException.from(jp, "Non-numeric token (" + t + ") for property 'lineNumber'");
                    }
                    if ("methodName".equals(propName)) {
                        methodName = jp.getText();
                        continue;
                    }
                    if ("nativeMethod".equals(propName)) continue;
                    this.handleUnknownProperty(jp, ctxt, this._valueClass, propName);
                }
                return new StackTraceElement(className, methodName, fileName, lineNumber);
            }
            throw ctxt.mappingException(this._valueClass, t);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SqlDateDeserializer
    extends StdScalarDeserializer<Date> {
        public SqlDateDeserializer() {
            super(Date.class);
        }

        @Override
        public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            java.util.Date d = this._parseDate(jp, ctxt);
            return d == null ? null : new Date(d.getTime());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static class BigIntegerDeserializer
    extends StdScalarDeserializer<BigInteger> {
        public BigIntegerDeserializer() {
            super(BigInteger.class);
        }

        @Override
        public BigInteger deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_INT) {
                switch (jp.getNumberType()) {
                    case INT: 
                    case LONG: {
                        return BigInteger.valueOf(jp.getLongValue());
                    }
                }
            } else {
                if (t == JsonToken.VALUE_NUMBER_FLOAT) {
                    return jp.getDecimalValue().toBigInteger();
                }
                if (t != JsonToken.VALUE_STRING) {
                    throw ctxt.mappingException(this._valueClass, t);
                }
            }
            String text = jp.getText().trim();
            if (text.length() == 0) {
                return null;
            }
            try {
                return new BigInteger(text);
            }
            catch (IllegalArgumentException iae) {
                throw ctxt.weirdStringException(this._valueClass, "not a valid representation");
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static class BigDecimalDeserializer
    extends StdScalarDeserializer<BigDecimal> {
        public BigDecimalDeserializer() {
            super(BigDecimal.class);
        }

        @Override
        public BigDecimal deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
                return jp.getDecimalValue();
            }
            if (t == JsonToken.VALUE_STRING) {
                String text = jp.getText().trim();
                if (text.length() == 0) {
                    return null;
                }
                try {
                    return new BigDecimal(text);
                }
                catch (IllegalArgumentException iae) {
                    throw ctxt.weirdStringException(this._valueClass, "not a valid representation");
                }
            }
            throw ctxt.mappingException(this._valueClass, t);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class NumberDeserializer
    extends StdScalarDeserializer<Number> {
        public NumberDeserializer() {
            super(Number.class);
        }

        @Override
        public Number deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_INT) {
                if (ctxt.isEnabled(DeserializationConfig.Feature.USE_BIG_INTEGER_FOR_INTS)) {
                    return jp.getBigIntegerValue();
                }
                return jp.getNumberValue();
            }
            if (t == JsonToken.VALUE_NUMBER_FLOAT) {
                if (ctxt.isEnabled(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return jp.getDecimalValue();
                }
                return jp.getDoubleValue();
            }
            if (t == JsonToken.VALUE_STRING) {
                String text = jp.getText().trim();
                try {
                    if (text.indexOf(46) >= 0) {
                        if (ctxt.isEnabled(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                            return new BigDecimal(text);
                        }
                        return new Double(text);
                    }
                    if (ctxt.isEnabled(DeserializationConfig.Feature.USE_BIG_INTEGER_FOR_INTS)) {
                        return new BigInteger(text);
                    }
                    long value = Long.parseLong(text);
                    if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
                        return (int)value;
                    }
                    return value;
                }
                catch (IllegalArgumentException iae) {
                    throw ctxt.weirdStringException(this._valueClass, "not a valid number");
                }
            }
            throw ctxt.mappingException(this._valueClass, t);
        }

        @Override
        public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
            switch (jp.getCurrentToken()) {
                case VALUE_NUMBER_INT: 
                case VALUE_NUMBER_FLOAT: 
                case VALUE_STRING: {
                    return this.deserialize(jp, ctxt);
                }
            }
            return typeDeserializer.deserializeTypedFromScalar(jp, ctxt);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class DoubleDeserializer
    extends PrimitiveOrWrapperDeserializer<Double> {
        public DoubleDeserializer(Class<Double> cls, Double nvl) {
            super(cls, nvl);
        }

        @Override
        public Double deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return this._parseDouble(jp, ctxt);
        }

        @Override
        public Double deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
            return this._parseDouble(jp, ctxt);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class FloatDeserializer
    extends PrimitiveOrWrapperDeserializer<Float> {
        public FloatDeserializer(Class<Float> cls, Float nvl) {
            super(cls, nvl);
        }

        @Override
        public Float deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return this._parseFloat(jp, ctxt);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class LongDeserializer
    extends PrimitiveOrWrapperDeserializer<Long> {
        public LongDeserializer(Class<Long> cls, Long nvl) {
            super(cls, nvl);
        }

        @Override
        public Long deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return this._parseLong(jp, ctxt);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class IntegerDeserializer
    extends PrimitiveOrWrapperDeserializer<Integer> {
        public IntegerDeserializer(Class<Integer> cls, Integer nvl) {
            super(cls, nvl);
        }

        @Override
        public Integer deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return this._parseInteger(jp, ctxt);
        }

        @Override
        public Integer deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
            return this._parseInteger(jp, ctxt);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class CharacterDeserializer
    extends PrimitiveOrWrapperDeserializer<Character> {
        public CharacterDeserializer(Class<Character> cls, Character nvl) {
            super(cls, nvl);
        }

        @Override
        public Character deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_INT) {
                int value = jp.getIntValue();
                if (value >= 0 && value <= 65535) {
                    return Character.valueOf((char)value);
                }
            } else if (t == JsonToken.VALUE_STRING) {
                String text = jp.getText();
                if (text.length() == 1) {
                    return Character.valueOf(text.charAt(0));
                }
                if (text.length() == 0) {
                    return (Character)this.getEmptyValue();
                }
            }
            throw ctxt.mappingException(this._valueClass, t);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class ShortDeserializer
    extends PrimitiveOrWrapperDeserializer<Short> {
        public ShortDeserializer(Class<Short> cls, Short nvl) {
            super(cls, nvl);
        }

        @Override
        public Short deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return this._parseShort(jp, ctxt);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class ByteDeserializer
    extends PrimitiveOrWrapperDeserializer<Byte> {
        public ByteDeserializer(Class<Byte> cls, Byte nvl) {
            super(cls, nvl);
        }

        @Override
        public Byte deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return this._parseByte(jp, ctxt);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JacksonStdImpl
    public static final class BooleanDeserializer
    extends PrimitiveOrWrapperDeserializer<Boolean> {
        public BooleanDeserializer(Class<Boolean> cls, Boolean nvl) {
            super(cls, nvl);
        }

        @Override
        public Boolean deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return this._parseBoolean(jp, ctxt);
        }

        @Override
        public Boolean deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
            return this._parseBoolean(jp, ctxt);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static abstract class PrimitiveOrWrapperDeserializer<T>
    extends StdScalarDeserializer<T> {
        final T _nullValue;

        protected PrimitiveOrWrapperDeserializer(Class<T> vc, T nvl) {
            super(vc);
            this._nullValue = nvl;
        }

        @Override
        public final T getNullValue() {
            return this._nullValue;
        }
    }
}

