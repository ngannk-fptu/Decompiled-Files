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
import groovy.json.internal.JsonStringDecoder;
import groovy.json.internal.Type;
import groovy.json.internal.Value;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;

public class CharSequenceValue
implements Value,
CharSequence {
    private final Type type;
    private final boolean checkDate;
    private final boolean decodeStrings;
    private char[] buffer;
    private boolean chopped;
    private int startIndex;
    private int endIndex;
    private Object value;

    public CharSequenceValue(boolean chop, Type type, int startIndex, int endIndex, char[] buffer, boolean encoded, boolean checkDate) {
        this.type = type;
        this.checkDate = checkDate;
        this.decodeStrings = encoded;
        if (chop) {
            try {
                this.buffer = ArrayUtils.copyRange(buffer, startIndex, endIndex);
            }
            catch (Exception ex) {
                Exceptions.handle(ex);
            }
            this.startIndex = 0;
            this.endIndex = this.buffer.length;
            this.chopped = true;
        } else {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.buffer = buffer;
        }
    }

    @Override
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
        switch (this.type) {
            case STRING: {
                return CharSequenceValue.toEnum(cls, this.stringValue());
            }
            case INTEGER: {
                return CharSequenceValue.toEnum(cls, this.intValue());
            }
            case NULL: {
                return null;
            }
        }
        Exceptions.die("toEnum " + cls + " value was " + this.stringValue());
        return null;
    }

    public static <T extends Enum> T toEnum(Class<T> cls, String value) {
        try {
            return Enum.valueOf(cls, value);
        }
        catch (Exception ex) {
            return Enum.valueOf(cls, value.toUpperCase().replace('-', '_'));
        }
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
                return this.doubleValue();
            }
            case INTEGER: {
                if (CharScanner.isInteger(this.buffer, this.startIndex, this.endIndex - this.startIndex)) {
                    return this.intValue();
                }
                return this.longValue();
            }
            case STRING: {
                if (this.checkDate) {
                    Date date = null;
                    if (Dates.isISO8601QuickCheck(this.buffer, this.startIndex, this.endIndex)) {
                        if (Dates.isJsonDate(this.buffer, this.startIndex, this.endIndex)) {
                            date = Dates.fromJsonDate(this.buffer, this.startIndex, this.endIndex);
                        } else if (Dates.isISO8601(this.buffer, this.startIndex, this.endIndex)) {
                            date = Dates.fromISO8601(this.buffer, this.startIndex, this.endIndex);
                        } else {
                            return this.stringValue();
                        }
                        if (date == null) {
                            return this.stringValue();
                        }
                        return date;
                    }
                }
                return this.stringValue();
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
        CharSequenceValue value1 = (CharSequenceValue)o;
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
    public final int length() {
        return this.buffer.length;
    }

    @Override
    public final char charAt(int index) {
        return this.buffer[index];
    }

    @Override
    public final CharSequence subSequence(int start, int end) {
        return new CharSequenceValue(false, this.type, start, end, this.buffer, this.decodeStrings, this.checkDate);
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return new BigDecimal(this.buffer, this.startIndex, this.endIndex - this.startIndex);
    }

    @Override
    public BigInteger bigIntegerValue() {
        return new BigInteger(this.toString());
    }

    @Override
    public String stringValue() {
        if (this.decodeStrings) {
            return JsonStringDecoder.decodeForSure(this.buffer, this.startIndex, this.endIndex);
        }
        return this.toString();
    }

    @Override
    public String stringValueEncoded() {
        return JsonStringDecoder.decode(this.buffer, this.startIndex, this.endIndex);
    }

    @Override
    public Date dateValue() {
        if (this.type == Type.STRING) {
            if (Dates.isISO8601QuickCheck(this.buffer, this.startIndex, this.endIndex)) {
                if (Dates.isJsonDate(this.buffer, this.startIndex, this.endIndex)) {
                    return Dates.fromJsonDate(this.buffer, this.startIndex, this.endIndex);
                }
                if (Dates.isISO8601(this.buffer, this.startIndex, this.endIndex)) {
                    return Dates.fromISO8601(this.buffer, this.startIndex, this.endIndex);
                }
                throw new JsonException("Unable to convert " + this.stringValue() + " to date ");
            }
            throw new JsonException("Unable to convert " + this.stringValue() + " to date ");
        }
        return new Date(Dates.utc(this.longValue()));
    }

    @Override
    public int intValue() {
        int sign = 1;
        if (this.buffer[this.startIndex] == '-') {
            ++this.startIndex;
            sign = -1;
        }
        return CharScanner.parseIntFromTo(this.buffer, this.startIndex, this.endIndex) * sign;
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

