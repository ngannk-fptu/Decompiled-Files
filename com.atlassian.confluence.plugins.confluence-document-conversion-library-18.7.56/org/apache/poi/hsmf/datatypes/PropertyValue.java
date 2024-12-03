/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.datatypes;

import java.math.BigInteger;
import java.util.Calendar;
import org.apache.poi.hsmf.datatypes.ByteChunk;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.hsmf.datatypes.Types;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LocaleUtil;

public class PropertyValue {
    private MAPIProperty property;
    private Types.MAPIType actualType;
    private long flags;
    protected byte[] data;

    public PropertyValue(MAPIProperty property, long flags, byte[] data) {
        this(property, flags, data, property.usualType);
    }

    public PropertyValue(MAPIProperty property, long flags, byte[] data, Types.MAPIType actualType) {
        this.property = property;
        this.flags = flags;
        this.data = data;
        this.actualType = actualType;
    }

    public MAPIProperty getProperty() {
        return this.property;
    }

    public long getFlags() {
        return this.flags;
    }

    public Object getValue() {
        return this.data;
    }

    public byte[] getRawValue() {
        return this.data;
    }

    public Types.MAPIType getActualType() {
        return this.actualType;
    }

    public void setRawValue(byte[] value) {
        this.data = value;
    }

    public String toString() {
        Object v = this.getValue();
        if (v == null) {
            return "(No value available)";
        }
        if (v instanceof byte[]) {
            return ByteChunk.toDebugFriendlyString((byte[])v);
        }
        return v.toString();
    }

    public static class TimePropertyValue
    extends PropertyValue {
        private static final long OFFSET = 11644473600000L;

        public TimePropertyValue(MAPIProperty property, long flags, byte[] data) {
            super(property, flags, data, Types.TIME);
        }

        @Override
        public Calendar getValue() {
            long time = LittleEndian.getLong(this.data);
            time = time / 10L / 1000L - 11644473600000L;
            Calendar timeC = LocaleUtil.getLocaleCalendar();
            timeC.setTimeInMillis(time);
            return timeC;
        }

        public void setValue(Calendar value) {
            if (this.data.length != 8) {
                this.data = new byte[8];
            }
            long time = value.getTimeInMillis();
            time = (time + 11644473600000L) * 10L * 1000L;
            LittleEndian.putLong(this.data, 0, time);
        }
    }

    public static class CurrencyPropertyValue
    extends PropertyValue {
        private static final BigInteger SHIFT = BigInteger.valueOf(10000L);

        public CurrencyPropertyValue(MAPIProperty property, long flags, byte[] data) {
            super(property, flags, data, Types.CURRENCY);
        }

        @Override
        public BigInteger getValue() {
            long unshifted = LittleEndian.getLong(this.data);
            return BigInteger.valueOf(unshifted).divide(SHIFT);
        }

        public void setValue(BigInteger value) {
            if (this.data.length != 8) {
                this.data = new byte[8];
            }
            long shifted = value.multiply(SHIFT).longValue();
            LittleEndian.putLong(this.data, 0, shifted);
        }
    }

    public static class DoublePropertyValue
    extends PropertyValue {
        public DoublePropertyValue(MAPIProperty property, long flags, byte[] data) {
            super(property, flags, data, Types.DOUBLE);
        }

        @Override
        public Double getValue() {
            return LittleEndian.getDouble(this.data);
        }

        public void setValue(double value) {
            if (this.data.length != 8) {
                this.data = new byte[8];
            }
            LittleEndian.putDouble(this.data, 0, value);
        }
    }

    public static class FloatPropertyValue
    extends PropertyValue {
        public FloatPropertyValue(MAPIProperty property, long flags, byte[] data) {
            super(property, flags, data, Types.FLOAT);
        }

        @Override
        public Float getValue() {
            return Float.valueOf(LittleEndian.getFloat(this.data));
        }

        public void setValue(float value) {
            if (this.data.length != 4) {
                this.data = new byte[4];
            }
            LittleEndian.putFloat(this.data, 0, value);
        }
    }

    public static class LongLongPropertyValue
    extends PropertyValue {
        public LongLongPropertyValue(MAPIProperty property, long flags, byte[] data) {
            super(property, flags, data, Types.LONG_LONG);
        }

        @Override
        public Long getValue() {
            return LittleEndian.getLong(this.data);
        }

        public void setValue(long value) {
            if (this.data.length != 8) {
                this.data = new byte[8];
            }
            LittleEndian.putLong(this.data, 0, value);
        }
    }

    public static class LongPropertyValue
    extends PropertyValue {
        public LongPropertyValue(MAPIProperty property, long flags, byte[] data) {
            super(property, flags, data, Types.LONG);
        }

        @Override
        public Integer getValue() {
            return LittleEndian.getInt(this.data);
        }

        public void setValue(int value) {
            if (this.data.length != 4) {
                this.data = new byte[4];
            }
            LittleEndian.putInt(this.data, 0, value);
        }
    }

    public static class ShortPropertyValue
    extends PropertyValue {
        public ShortPropertyValue(MAPIProperty property, long flags, byte[] data) {
            super(property, flags, data, Types.SHORT);
        }

        @Override
        public Short getValue() {
            return LittleEndian.getShort(this.data);
        }

        public void setValue(short value) {
            if (this.data.length != 2) {
                this.data = new byte[2];
            }
            LittleEndian.putShort(this.data, 0, value);
        }
    }

    public static class BooleanPropertyValue
    extends PropertyValue {
        public BooleanPropertyValue(MAPIProperty property, long flags, byte[] data) {
            super(property, flags, data, Types.BOOLEAN);
        }

        @Override
        public Boolean getValue() {
            short val = LittleEndian.getShort(this.data);
            return val > 0;
        }

        public void setValue(boolean value) {
            if (this.data.length != 2) {
                this.data = new byte[2];
            }
            if (value) {
                LittleEndian.putShort(this.data, 0, (short)1);
            }
        }
    }

    public static class NullPropertyValue
    extends PropertyValue {
        public NullPropertyValue(MAPIProperty property, long flags, byte[] data) {
            super(property, flags, data, Types.NULL);
        }

        @Override
        public Void getValue() {
            return null;
        }
    }
}

