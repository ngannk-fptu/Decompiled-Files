/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.JsonException;
import groovy.json.internal.ArrayUtils;
import groovy.json.internal.CharScanner;
import groovy.json.internal.Dates;
import groovy.json.internal.Exceptions;
import groovy.json.internal.FastStringUtils;
import groovy.json.internal.Type;
import groovy.json.internal.Value;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;

public class NumberValue
extends Number
implements Value {
    private char[] buffer;
    private boolean chopped;
    private int startIndex;
    private int endIndex;
    private Type type;
    private Object value;

    public NumberValue(Type type) {
        this.type = type;
    }

    public NumberValue() {
    }

    public NumberValue(boolean chop, Type type, int startIndex, int endIndex, char[] buffer) {
        this.type = type;
        try {
            if (chop) {
                this.buffer = ArrayUtils.copyRange(buffer, startIndex, endIndex);
                this.startIndex = 0;
                this.endIndex = this.buffer.length;
                this.chopped = true;
            } else {
                this.startIndex = startIndex;
                this.endIndex = endIndex;
                this.buffer = buffer;
            }
        }
        catch (Exception ex) {
            Exceptions.handle(Exceptions.sputs("exception", ex, "start", startIndex, "end", endIndex), (Throwable)ex);
        }
        if (this.endIndex - this.startIndex == 1 && this.buffer[this.startIndex] == '-') {
            Exceptions.die("A single minus is not a valid number");
        }
    }

    public String toString() {
        if (this.startIndex == 0 && this.endIndex == this.buffer.length) {
            return FastStringUtils.noCopyStringFromChars(this.buffer);
        }
        return new String(this.buffer, this.startIndex, this.endIndex - this.startIndex);
    }

    @Override
    public final Object toValue() {
        return this.value != null ? this.value : (this.value = this.doToValue());
    }

    @Override
    public <T extends Enum> T toEnum(Class<T> cls) {
        return NumberValue.toEnum(cls, this.intValue());
    }

    public static <T extends Enum> T toEnum(Class<T> cls, int value) {
        Enum[] enumConstants;
        for (Enum e : enumConstants = (Enum[])cls.getEnumConstants()) {
            if (e.ordinal() != value) continue;
            return (T)e;
        }
        Exceptions.die("Can't convert ordinal value " + value + " into enum of type " + cls);
        return null;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    private Object doToValue() {
        switch (this.type) {
            case DOUBLE: {
                return this.bigDecimalValue();
            }
            case INTEGER: {
                if (CharScanner.isInteger(this.buffer, this.startIndex, this.endIndex - this.startIndex)) {
                    return this.intValue();
                }
                return this.longValue();
            }
        }
        Exceptions.die();
        return null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        NumberValue value1 = (NumberValue)o;
        if (this.endIndex != value1.endIndex) {
            return false;
        }
        if (this.startIndex != value1.startIndex) {
            return false;
        }
        if (!Arrays.equals(this.buffer, value1.buffer)) {
            return false;
        }
        if (this.type != value1.type) {
            return false;
        }
        return this.value != null ? this.value.equals(value1.value) : value1.value == null;
    }

    public int hashCode() {
        int result = this.type != null ? this.type.hashCode() : 0;
        result = 31 * result + (this.buffer != null ? Arrays.hashCode(this.buffer) : 0);
        result = 31 * result + this.startIndex;
        result = 31 * result + this.endIndex;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }

    @Override
    public BigDecimal bigDecimalValue() {
        try {
            return new BigDecimal(this.buffer, this.startIndex, this.endIndex - this.startIndex);
        }
        catch (NumberFormatException e) {
            throw new JsonException("unable to parse " + new String(this.buffer, this.startIndex, this.endIndex - this.startIndex), e);
        }
    }

    @Override
    public BigInteger bigIntegerValue() {
        return new BigInteger(this.toString());
    }

    @Override
    public String stringValue() {
        return this.toString();
    }

    @Override
    public String stringValueEncoded() {
        return this.toString();
    }

    @Override
    public Date dateValue() {
        return new Date(Dates.utc(this.longValue()));
    }

    @Override
    public int intValue() {
        return CharScanner.parseIntFromTo(this.buffer, this.startIndex, this.endIndex);
    }

    @Override
    public long longValue() {
        if (CharScanner.isInteger(this.buffer, this.startIndex, this.endIndex - this.startIndex)) {
            return CharScanner.parseIntFromTo(this.buffer, this.startIndex, this.endIndex);
        }
        return CharScanner.parseLongFromTo(this.buffer, this.startIndex, this.endIndex);
    }

    @Override
    public byte byteValue() {
        return (byte)this.intValue();
    }

    @Override
    public short shortValue() {
        return (short)this.intValue();
    }

    @Override
    public double doubleValue() {
        return CharScanner.parseDouble(this.buffer, this.startIndex, this.endIndex);
    }

    @Override
    public boolean booleanValue() {
        return Boolean.parseBoolean(this.toString());
    }

    @Override
    public float floatValue() {
        return CharScanner.parseFloat(this.buffer, this.startIndex, this.endIndex);
    }

    @Override
    public final void chop() {
        if (!this.chopped) {
            this.chopped = true;
            this.buffer = ArrayUtils.copyRange(this.buffer, this.startIndex, this.endIndex);
            this.startIndex = 0;
            this.endIndex = this.buffer.length;
        }
    }

    @Override
    public char charValue() {
        return this.buffer[this.startIndex];
    }
}

