/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericEnumSymbol;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.generic.IndexedRecord;

public class Conversions {
    public static Object convertToLogicalType(Object datum, Schema schema, LogicalType type, Conversion<?> conversion) {
        if (datum == null) {
            return null;
        }
        if (schema == null || type == null || conversion == null) {
            throw new IllegalArgumentException("Parameters cannot be null! Parameter values:" + Arrays.deepToString(new Object[]{datum, schema, type, conversion}));
        }
        try {
            switch (schema.getType()) {
                case RECORD: {
                    return conversion.fromRecord((IndexedRecord)datum, schema, type);
                }
                case ENUM: {
                    return conversion.fromEnumSymbol((GenericEnumSymbol)datum, schema, type);
                }
                case ARRAY: {
                    return conversion.fromArray((Collection)datum, schema, type);
                }
                case MAP: {
                    return conversion.fromMap((Map)datum, schema, type);
                }
                case FIXED: {
                    return conversion.fromFixed((GenericFixed)datum, schema, type);
                }
                case STRING: {
                    return conversion.fromCharSequence((CharSequence)datum, schema, type);
                }
                case BYTES: {
                    return conversion.fromBytes((ByteBuffer)datum, schema, type);
                }
                case INT: {
                    return conversion.fromInt((Integer)datum, schema, type);
                }
                case LONG: {
                    return conversion.fromLong((Long)datum, schema, type);
                }
                case FLOAT: {
                    return conversion.fromFloat((Float)datum, schema, type);
                }
                case DOUBLE: {
                    return conversion.fromDouble((Double)datum, schema, type);
                }
                case BOOLEAN: {
                    return conversion.fromBoolean((Boolean)datum, schema, type);
                }
            }
            return datum;
        }
        catch (ClassCastException e) {
            throw new AvroRuntimeException("Cannot convert " + datum + ':' + datum.getClass().getSimpleName() + ": expected generic type", e);
        }
    }

    public static <T> Object convertToRawType(Object datum, Schema schema, LogicalType type, Conversion<T> conversion) {
        if (datum == null) {
            return null;
        }
        if (schema == null || type == null || conversion == null) {
            throw new IllegalArgumentException("Parameters cannot be null! Parameter values:" + Arrays.deepToString(new Object[]{datum, schema, type, conversion}));
        }
        try {
            Class<T> fromClass = conversion.getConvertedType();
            switch (schema.getType()) {
                case RECORD: {
                    return conversion.toRecord(fromClass.cast(datum), schema, type);
                }
                case ENUM: {
                    return conversion.toEnumSymbol(fromClass.cast(datum), schema, type);
                }
                case ARRAY: {
                    return conversion.toArray(fromClass.cast(datum), schema, type);
                }
                case MAP: {
                    return conversion.toMap(fromClass.cast(datum), schema, type);
                }
                case FIXED: {
                    return conversion.toFixed(fromClass.cast(datum), schema, type);
                }
                case STRING: {
                    return conversion.toCharSequence(fromClass.cast(datum), schema, type);
                }
                case BYTES: {
                    return conversion.toBytes(fromClass.cast(datum), schema, type);
                }
                case INT: {
                    return conversion.toInt(fromClass.cast(datum), schema, type);
                }
                case LONG: {
                    return conversion.toLong(fromClass.cast(datum), schema, type);
                }
                case FLOAT: {
                    return conversion.toFloat(fromClass.cast(datum), schema, type);
                }
                case DOUBLE: {
                    return conversion.toDouble(fromClass.cast(datum), schema, type);
                }
                case BOOLEAN: {
                    return conversion.toBoolean(fromClass.cast(datum), schema, type);
                }
            }
            return datum;
        }
        catch (ClassCastException e) {
            throw new AvroRuntimeException("Cannot convert " + datum + ':' + datum.getClass().getSimpleName() + ": expected logical type", e);
        }
    }

    public static class DecimalConversion
    extends Conversion<BigDecimal> {
        @Override
        public Class<BigDecimal> getConvertedType() {
            return BigDecimal.class;
        }

        @Override
        public Schema getRecommendedSchema() {
            throw new UnsupportedOperationException("No recommended schema for decimal (scale is required)");
        }

        @Override
        public String getLogicalTypeName() {
            return "decimal";
        }

        @Override
        public BigDecimal fromBytes(ByteBuffer value, Schema schema, LogicalType type) {
            int scale = ((LogicalTypes.Decimal)type).getScale();
            byte[] bytes = new byte[value.remaining()];
            value.duplicate().get(bytes);
            return new BigDecimal(new BigInteger(bytes), scale);
        }

        @Override
        public ByteBuffer toBytes(BigDecimal value, Schema schema, LogicalType type) {
            value = DecimalConversion.validate((LogicalTypes.Decimal)type, value);
            return ByteBuffer.wrap(value.unscaledValue().toByteArray());
        }

        @Override
        public BigDecimal fromFixed(GenericFixed value, Schema schema, LogicalType type) {
            int scale = ((LogicalTypes.Decimal)type).getScale();
            return new BigDecimal(new BigInteger(value.bytes()), scale);
        }

        @Override
        public GenericFixed toFixed(BigDecimal value, Schema schema, LogicalType type) {
            byte fillByte = (byte)((value = DecimalConversion.validate((LogicalTypes.Decimal)type, value)).signum() < 0 ? 255 : 0);
            byte[] unscaled = value.unscaledValue().toByteArray();
            byte[] bytes = new byte[schema.getFixedSize()];
            int unscaledLength = unscaled.length;
            int offset = bytes.length - unscaledLength;
            Arrays.fill(bytes, 0, offset, fillByte);
            System.arraycopy(unscaled, 0, bytes, offset, unscaledLength);
            return new GenericData.Fixed(schema, bytes);
        }

        private static BigDecimal validate(LogicalTypes.Decimal decimal, BigDecimal value) {
            int scale = decimal.getScale();
            int valueScale = value.scale();
            boolean scaleAdjusted = false;
            if (valueScale != scale) {
                try {
                    value = value.setScale(scale, RoundingMode.UNNECESSARY);
                    scaleAdjusted = true;
                }
                catch (ArithmeticException aex) {
                    throw new AvroTypeException("Cannot encode decimal with scale " + valueScale + " as scale " + scale + " without rounding");
                }
            }
            int precision = decimal.getPrecision();
            int valuePrecision = value.precision();
            if (valuePrecision > precision) {
                if (scaleAdjusted) {
                    throw new AvroTypeException("Cannot encode decimal with precision " + valuePrecision + " as max precision " + precision + ". This is after safely adjusting scale from " + valueScale + " to required " + scale);
                }
                throw new AvroTypeException("Cannot encode decimal with precision " + valuePrecision + " as max precision " + precision);
            }
            return value;
        }
    }

    public static class UUIDConversion
    extends Conversion<UUID> {
        @Override
        public Class<UUID> getConvertedType() {
            return UUID.class;
        }

        @Override
        public Schema getRecommendedSchema() {
            return LogicalTypes.uuid().addToSchema(Schema.create(Schema.Type.STRING));
        }

        @Override
        public String getLogicalTypeName() {
            return "uuid";
        }

        @Override
        public UUID fromCharSequence(CharSequence value, Schema schema, LogicalType type) {
            return UUID.fromString(value.toString());
        }

        @Override
        public CharSequence toCharSequence(UUID value, Schema schema, LogicalType type) {
            return value.toString();
        }
    }
}

