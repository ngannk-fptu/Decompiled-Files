/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.typed;

public final class NumberUtil {
    public static final int MAX_INT_CLEN = 11;
    public static final int MAX_LONG_CLEN = 21;
    public static final int MAX_DOUBLE_CLEN = 32;
    public static final int MAX_FLOAT_CLEN = 32;
    private static final char NULL_CHAR = '\u0000';
    private static final int MILLION = 1000000;
    private static final int BILLION = 1000000000;
    private static final long TEN_BILLION_L = 10000000000L;
    private static final long THOUSAND_L = 1000L;
    private static final byte BYTE_HYPHEN = 45;
    private static final byte BYTE_1 = 49;
    private static final byte BYTE_2 = 50;
    private static long MIN_INT_AS_LONG = -2147483647L;
    private static long MAX_INT_AS_LONG = Integer.MAX_VALUE;
    static final char[] LEADING_TRIPLETS = new char[4000];
    static final char[] FULL_TRIPLETS = new char[4000];

    public static int writeInt(int value, char[] buffer, int offset) {
        boolean hasBillions;
        if (value < 0) {
            if (value == Integer.MIN_VALUE) {
                return NumberUtil.writeLong((long)value, buffer, offset);
            }
            buffer[offset++] = 45;
            value = -value;
        }
        if (value < 1000000) {
            if (value < 1000) {
                if (value < 10) {
                    buffer[offset++] = (char)(48 + value);
                } else {
                    offset = NumberUtil.writeLeadingTriplet(value, buffer, offset);
                }
            } else {
                int thousands = value / 1000;
                offset = NumberUtil.writeLeadingTriplet(thousands, buffer, offset);
                offset = NumberUtil.writeFullTriplet(value -= thousands * 1000, buffer, offset);
            }
            return offset;
        }
        boolean bl = hasBillions = value >= 1000000000;
        if (hasBillions) {
            if ((value -= 1000000000) >= 1000000000) {
                value -= 1000000000;
                buffer[offset++] = 50;
            } else {
                buffer[offset++] = 49;
            }
        }
        int newValue = value / 1000;
        int ones = value - newValue * 1000;
        value = newValue;
        int thousands = value - (newValue /= 1000) * 1000;
        offset = hasBillions ? NumberUtil.writeFullTriplet(newValue, buffer, offset) : NumberUtil.writeLeadingTriplet(newValue, buffer, offset);
        offset = NumberUtil.writeFullTriplet(thousands, buffer, offset);
        offset = NumberUtil.writeFullTriplet(ones, buffer, offset);
        return offset;
    }

    public static int writeInt(int value, byte[] buffer, int offset) {
        boolean hasBillions;
        if (value < 0) {
            if (value == Integer.MIN_VALUE) {
                return NumberUtil.writeLong((long)value, buffer, offset);
            }
            buffer[offset++] = 45;
            value = -value;
        }
        if (value < 1000000) {
            if (value < 1000) {
                if (value < 10) {
                    buffer[offset++] = (byte)(48 + value);
                } else {
                    offset = NumberUtil.writeLeadingTriplet(value, buffer, offset);
                }
            } else {
                int thousands = value / 1000;
                offset = NumberUtil.writeLeadingTriplet(thousands, buffer, offset);
                offset = NumberUtil.writeFullTriplet(value -= thousands * 1000, buffer, offset);
            }
            return offset;
        }
        boolean bl = hasBillions = value >= 1000000000;
        if (hasBillions) {
            if ((value -= 1000000000) >= 1000000000) {
                value -= 1000000000;
                buffer[offset++] = 50;
            } else {
                buffer[offset++] = 49;
            }
        }
        int newValue = value / 1000;
        int ones = value - newValue * 1000;
        value = newValue;
        int thousands = value - (newValue /= 1000) * 1000;
        offset = hasBillions ? NumberUtil.writeFullTriplet(newValue, buffer, offset) : NumberUtil.writeLeadingTriplet(newValue, buffer, offset);
        offset = NumberUtil.writeFullTriplet(thousands, buffer, offset);
        offset = NumberUtil.writeFullTriplet(ones, buffer, offset);
        return offset;
    }

    public static int writeLong(long value, char[] buffer, int offset) {
        int triplet;
        if (value < 0L) {
            if (value >= MIN_INT_AS_LONG) {
                return NumberUtil.writeInt((int)value, buffer, offset);
            }
            if (value == Long.MIN_VALUE) {
                return NumberUtil.getChars(String.valueOf(value), buffer, offset);
            }
            buffer[offset++] = 45;
            value = -value;
        } else if (value <= MAX_INT_AS_LONG) {
            return NumberUtil.writeInt((int)value, buffer, offset);
        }
        int origOffset = offset;
        int ptr = offset += NumberUtil.calcLongStrLength(value);
        while (value > MAX_INT_AS_LONG) {
            long newValue = value / 1000L;
            triplet = (int)(value - newValue * 1000L);
            NumberUtil.writeFullTriplet(triplet, buffer, ptr -= 3);
            value = newValue;
        }
        int ivalue = (int)value;
        while (ivalue >= 1000) {
            int newValue = ivalue / 1000;
            triplet = ivalue - newValue * 1000;
            NumberUtil.writeFullTriplet(triplet, buffer, ptr -= 3);
            ivalue = newValue;
        }
        NumberUtil.writeLeadingTriplet(ivalue, buffer, origOffset);
        return offset;
    }

    public static int writeLong(long value, byte[] buffer, int offset) {
        int triplet;
        if (value < 0L) {
            if (value >= MIN_INT_AS_LONG) {
                return NumberUtil.writeInt((int)value, buffer, offset);
            }
            if (value == Long.MIN_VALUE) {
                return NumberUtil.getAsciiBytes(String.valueOf(value), buffer, offset);
            }
            buffer[offset++] = 45;
            value = -value;
        } else if (value <= MAX_INT_AS_LONG) {
            return NumberUtil.writeInt((int)value, buffer, offset);
        }
        int origOffset = offset;
        int ptr = offset += NumberUtil.calcLongStrLength(value);
        while (value > MAX_INT_AS_LONG) {
            long newValue = value / 1000L;
            triplet = (int)(value - newValue * 1000L);
            NumberUtil.writeFullTriplet(triplet, buffer, ptr -= 3);
            value = newValue;
        }
        int ivalue = (int)value;
        while (ivalue >= 1000) {
            int newValue = ivalue / 1000;
            triplet = ivalue - newValue * 1000;
            NumberUtil.writeFullTriplet(triplet, buffer, ptr -= 3);
            ivalue = newValue;
        }
        NumberUtil.writeLeadingTriplet(ivalue, buffer, origOffset);
        return offset;
    }

    public static int writeFloat(float value, char[] buffer, int offset) {
        return NumberUtil.getChars(String.valueOf(value), buffer, offset);
    }

    public static int writeFloat(float value, byte[] buffer, int offset) {
        return NumberUtil.getAsciiBytes(String.valueOf(value), buffer, offset);
    }

    public static int writeDouble(double value, char[] buffer, int offset) {
        return NumberUtil.getChars(String.valueOf(value), buffer, offset);
    }

    public static int writeDouble(double value, byte[] buffer, int offset) {
        return NumberUtil.getAsciiBytes(String.valueOf(value), buffer, offset);
    }

    private static int writeLeadingTriplet(int triplet, char[] buffer, int offset) {
        char c;
        int digitOffset = triplet << 2;
        if ((c = LEADING_TRIPLETS[digitOffset++]) != '\u0000') {
            buffer[offset++] = c;
        }
        if ((c = LEADING_TRIPLETS[digitOffset++]) != '\u0000') {
            buffer[offset++] = c;
        }
        buffer[offset++] = LEADING_TRIPLETS[digitOffset];
        return offset;
    }

    private static int writeLeadingTriplet(int triplet, byte[] buffer, int offset) {
        char c;
        int digitOffset = triplet << 2;
        if ((c = LEADING_TRIPLETS[digitOffset++]) != '\u0000') {
            buffer[offset++] = (byte)c;
        }
        if ((c = LEADING_TRIPLETS[digitOffset++]) != '\u0000') {
            buffer[offset++] = (byte)c;
        }
        buffer[offset++] = (byte)LEADING_TRIPLETS[digitOffset];
        return offset;
    }

    private static int writeFullTriplet(int triplet, char[] buffer, int offset) {
        int digitOffset = triplet << 2;
        buffer[offset++] = FULL_TRIPLETS[digitOffset++];
        buffer[offset++] = FULL_TRIPLETS[digitOffset++];
        buffer[offset++] = FULL_TRIPLETS[digitOffset];
        return offset;
    }

    private static int writeFullTriplet(int triplet, byte[] buffer, int offset) {
        int digitOffset = triplet << 2;
        buffer[offset++] = (byte)FULL_TRIPLETS[digitOffset++];
        buffer[offset++] = (byte)FULL_TRIPLETS[digitOffset++];
        buffer[offset++] = (byte)FULL_TRIPLETS[digitOffset];
        return offset;
    }

    private static int calcLongStrLength(long posValue) {
        int len;
        long comp = 10000000000L;
        for (len = 10; posValue >= comp && len != 19; ++len) {
            comp = (comp << 3) + (comp << 1);
        }
        return len;
    }

    private static int getChars(String str, char[] buffer, int ptr) {
        int len = str.length();
        str.getChars(0, len, buffer, ptr);
        return ptr + len;
    }

    private static int getAsciiBytes(String str, byte[] buffer, int ptr) {
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            buffer[ptr++] = (byte)str.charAt(i);
        }
        return ptr;
    }

    static {
        int ix = 0;
        for (int i1 = 0; i1 < 10; ++i1) {
            char f1 = (char)(48 + i1);
            char l1 = i1 == 0 ? (char)'\u0000' : f1;
            for (int i2 = 0; i2 < 10; ++i2) {
                char f2 = (char)(48 + i2);
                char l2 = i1 == 0 && i2 == 0 ? (char)'\u0000' : f2;
                for (int i3 = 0; i3 < 10; ++i3) {
                    char f3 = (char)(48 + i3);
                    NumberUtil.LEADING_TRIPLETS[ix] = l1;
                    NumberUtil.LEADING_TRIPLETS[ix + 1] = l2;
                    NumberUtil.LEADING_TRIPLETS[ix + 2] = f3;
                    NumberUtil.FULL_TRIPLETS[ix] = f1;
                    NumberUtil.FULL_TRIPLETS[ix + 1] = f2;
                    NumberUtil.FULL_TRIPLETS[ix + 2] = f3;
                    ix += 4;
                }
            }
        }
    }
}

