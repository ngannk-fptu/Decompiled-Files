/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.Escaper;
import com.google.gson.JsonElement;
import com.google.gson.internal.$Gson$Preconditions;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonPrimitive
extends JsonElement {
    private static final Class<?>[] PRIMITIVE_TYPES = new Class[]{Integer.TYPE, Long.TYPE, Short.TYPE, Float.TYPE, Double.TYPE, Byte.TYPE, Boolean.TYPE, Character.TYPE, Integer.class, Long.class, Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class};
    private static final BigInteger INTEGER_MAX = BigInteger.valueOf(Integer.MAX_VALUE);
    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
    private Object value;

    public JsonPrimitive(Boolean bool) {
        this.setValue(bool);
    }

    public JsonPrimitive(Number number) {
        this.setValue(number);
    }

    public JsonPrimitive(String string) {
        this.setValue(string);
    }

    public JsonPrimitive(Character c) {
        this.setValue(c);
    }

    JsonPrimitive(Object primitive) {
        this.setValue(primitive);
    }

    void setValue(Object primitive) {
        if (primitive instanceof Character) {
            char c = ((Character)primitive).charValue();
            this.value = String.valueOf(c);
        } else {
            $Gson$Preconditions.checkArgument(primitive instanceof Number || JsonPrimitive.isPrimitiveOrString(primitive));
            this.value = primitive;
        }
    }

    public boolean isBoolean() {
        return this.value instanceof Boolean;
    }

    Boolean getAsBooleanWrapper() {
        return (Boolean)this.value;
    }

    public boolean getAsBoolean() {
        return this.isBoolean() ? this.getAsBooleanWrapper() : Boolean.parseBoolean(this.getAsString());
    }

    public boolean isNumber() {
        return this.value instanceof Number;
    }

    public Number getAsNumber() {
        return this.value instanceof String ? (Number)JsonPrimitive.stringToNumber((String)this.value) : (Number)((Number)this.value);
    }

    static Number stringToNumber(String value) {
        try {
            long longValue = Long.parseLong(value);
            if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                return (int)longValue;
            }
            return longValue;
        }
        catch (NumberFormatException ignored) {
            try {
                return new BigDecimal(value);
            }
            catch (NumberFormatException ignored2) {
                return Double.parseDouble(value);
            }
        }
    }

    public boolean isString() {
        return this.value instanceof String;
    }

    public String getAsString() {
        if (this.isNumber()) {
            return this.getAsNumber().toString();
        }
        if (this.isBoolean()) {
            return this.getAsBooleanWrapper().toString();
        }
        return (String)this.value;
    }

    public double getAsDouble() {
        return this.isNumber() ? this.getAsNumber().doubleValue() : Double.parseDouble(this.getAsString());
    }

    public BigDecimal getAsBigDecimal() {
        return this.value instanceof BigDecimal ? (BigDecimal)this.value : new BigDecimal(this.value.toString());
    }

    public BigInteger getAsBigInteger() {
        return this.value instanceof BigInteger ? (BigInteger)this.value : new BigInteger(this.value.toString());
    }

    public float getAsFloat() {
        return this.isNumber() ? this.getAsNumber().floatValue() : Float.parseFloat(this.getAsString());
    }

    public long getAsLong() {
        return this.isNumber() ? this.getAsNumber().longValue() : Long.parseLong(this.getAsString());
    }

    public short getAsShort() {
        return this.isNumber() ? this.getAsNumber().shortValue() : Short.parseShort(this.getAsString());
    }

    public int getAsInt() {
        return this.isNumber() ? this.getAsNumber().intValue() : Integer.parseInt(this.getAsString());
    }

    public byte getAsByte() {
        return this.isNumber() ? this.getAsNumber().byteValue() : Byte.parseByte(this.getAsString());
    }

    public char getAsCharacter() {
        return this.getAsString().charAt(0);
    }

    Object getAsObject() {
        if (this.value instanceof BigInteger) {
            BigInteger big = (BigInteger)this.value;
            if (big.compareTo(INTEGER_MAX) < 0) {
                return big.intValue();
            }
            if (big.compareTo(LONG_MAX) < 0) {
                return big.longValue();
            }
        }
        return this.value;
    }

    protected void toString(Appendable sb, Escaper escaper) throws IOException {
        if (this.isString()) {
            sb.append('\"');
            sb.append(escaper.escapeJsonString(this.value.toString()));
            sb.append('\"');
        } else {
            sb.append(this.value.toString());
        }
    }

    private static boolean isPrimitiveOrString(Object target) {
        if (target instanceof String) {
            return true;
        }
        Class<?> classOfPrimitive = target.getClass();
        for (Class<?> standardPrimitive : PRIMITIVE_TYPES) {
            if (!standardPrimitive.isAssignableFrom(classOfPrimitive)) continue;
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.value == null) {
            return 31;
        }
        if (JsonPrimitive.isIntegral(this)) {
            long value = this.getAsNumber().longValue();
            return (int)(value ^ value >>> 32);
        }
        if (JsonPrimitive.isFloatingPoint(this)) {
            long value = Double.doubleToLongBits(this.getAsNumber().doubleValue());
            return (int)(value ^ value >>> 32);
        }
        return this.value.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        JsonPrimitive other = (JsonPrimitive)obj;
        if (this.value == null) {
            return other.value == null;
        }
        if (JsonPrimitive.isIntegral(this) && JsonPrimitive.isIntegral(other)) {
            return this.getAsNumber().longValue() == other.getAsNumber().longValue();
        }
        if (JsonPrimitive.isFloatingPoint(this) && JsonPrimitive.isFloatingPoint(other)) {
            double b;
            double a = this.getAsNumber().doubleValue();
            return a == (b = other.getAsNumber().doubleValue()) || Double.isNaN(a) && Double.isNaN(b);
        }
        return this.value.equals(other.value);
    }

    private static boolean isIntegral(JsonPrimitive primitive) {
        if (primitive.value instanceof Number) {
            Number number = (Number)primitive.value;
            return number instanceof BigInteger || number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte;
        }
        return false;
    }

    private static boolean isFloatingPoint(JsonPrimitive primitive) {
        if (primitive.value instanceof Number) {
            Number number = (Number)primitive.value;
            return number instanceof BigDecimal || number instanceof Double || number instanceof Float;
        }
        return false;
    }
}

