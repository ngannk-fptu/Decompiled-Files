/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.io;

public final class NumberOutput {
    private static final char NULL_CHAR = '\u0000';
    private static int MILLION = 1000000;
    private static int BILLION = 1000000000;
    private static long TEN_BILLION_L = 10000000000L;
    private static long THOUSAND_L = 1000L;
    private static long MIN_INT_AS_LONG = Integer.MIN_VALUE;
    private static long MAX_INT_AS_LONG = Integer.MAX_VALUE;
    static final String SMALLEST_LONG = String.valueOf(Long.MIN_VALUE);
    static final char[] LEADING_TRIPLETS = new char[4000];
    static final char[] FULL_TRIPLETS = new char[4000];
    static final byte[] FULL_TRIPLETS_B;
    static final String[] sSmallIntStrs;
    static final String[] sSmallIntStrs2;

    public static int outputInt(int value, char[] buffer, int offset) {
        boolean hasBillions;
        if (value < 0) {
            if (value == Integer.MIN_VALUE) {
                return NumberOutput.outputLong((long)value, buffer, offset);
            }
            buffer[offset++] = 45;
            value = -value;
        }
        if (value < MILLION) {
            if (value < 1000) {
                if (value < 10) {
                    buffer[offset++] = (char)(48 + value);
                } else {
                    offset = NumberOutput.outputLeadingTriplet(value, buffer, offset);
                }
            } else {
                int thousands = value / 1000;
                offset = NumberOutput.outputLeadingTriplet(thousands, buffer, offset);
                offset = NumberOutput.outputFullTriplet(value -= thousands * 1000, buffer, offset);
            }
            return offset;
        }
        boolean bl = hasBillions = value >= BILLION;
        if (hasBillions) {
            if ((value -= BILLION) >= BILLION) {
                value -= BILLION;
                buffer[offset++] = 50;
            } else {
                buffer[offset++] = 49;
            }
        }
        int newValue = value / 1000;
        int ones = value - newValue * 1000;
        value = newValue;
        int thousands = value - (newValue /= 1000) * 1000;
        offset = hasBillions ? NumberOutput.outputFullTriplet(newValue, buffer, offset) : NumberOutput.outputLeadingTriplet(newValue, buffer, offset);
        offset = NumberOutput.outputFullTriplet(thousands, buffer, offset);
        offset = NumberOutput.outputFullTriplet(ones, buffer, offset);
        return offset;
    }

    public static int outputInt(int value, byte[] buffer, int offset) {
        boolean hasBillions;
        if (value < 0) {
            if (value == Integer.MIN_VALUE) {
                return NumberOutput.outputLong((long)value, buffer, offset);
            }
            buffer[offset++] = 45;
            value = -value;
        }
        if (value < MILLION) {
            if (value < 1000) {
                if (value < 10) {
                    buffer[offset++] = (byte)(48 + value);
                } else {
                    offset = NumberOutput.outputLeadingTriplet(value, buffer, offset);
                }
            } else {
                int thousands = value / 1000;
                offset = NumberOutput.outputLeadingTriplet(thousands, buffer, offset);
                offset = NumberOutput.outputFullTriplet(value -= thousands * 1000, buffer, offset);
            }
            return offset;
        }
        boolean bl = hasBillions = value >= BILLION;
        if (hasBillions) {
            if ((value -= BILLION) >= BILLION) {
                value -= BILLION;
                buffer[offset++] = 50;
            } else {
                buffer[offset++] = 49;
            }
        }
        int newValue = value / 1000;
        int ones = value - newValue * 1000;
        value = newValue;
        int thousands = value - (newValue /= 1000) * 1000;
        offset = hasBillions ? NumberOutput.outputFullTriplet(newValue, buffer, offset) : NumberOutput.outputLeadingTriplet(newValue, buffer, offset);
        offset = NumberOutput.outputFullTriplet(thousands, buffer, offset);
        offset = NumberOutput.outputFullTriplet(ones, buffer, offset);
        return offset;
    }

    public static int outputLong(long value, char[] buffer, int offset) {
        int triplet;
        if (value < 0L) {
            if (value > MIN_INT_AS_LONG) {
                return NumberOutput.outputInt((int)value, buffer, offset);
            }
            if (value == Long.MIN_VALUE) {
                int len = SMALLEST_LONG.length();
                SMALLEST_LONG.getChars(0, len, buffer, offset);
                return offset + len;
            }
            buffer[offset++] = 45;
            value = -value;
        } else if (value <= MAX_INT_AS_LONG) {
            return NumberOutput.outputInt((int)value, buffer, offset);
        }
        int origOffset = offset;
        int ptr = offset += NumberOutput.calcLongStrLength(value);
        while (value > MAX_INT_AS_LONG) {
            long newValue = value / THOUSAND_L;
            triplet = (int)(value - newValue * THOUSAND_L);
            NumberOutput.outputFullTriplet(triplet, buffer, ptr -= 3);
            value = newValue;
        }
        int ivalue = (int)value;
        while (ivalue >= 1000) {
            int newValue = ivalue / 1000;
            triplet = ivalue - newValue * 1000;
            NumberOutput.outputFullTriplet(triplet, buffer, ptr -= 3);
            ivalue = newValue;
        }
        NumberOutput.outputLeadingTriplet(ivalue, buffer, origOffset);
        return offset;
    }

    public static int outputLong(long value, byte[] buffer, int offset) {
        int triplet;
        if (value < 0L) {
            if (value > MIN_INT_AS_LONG) {
                return NumberOutput.outputInt((int)value, buffer, offset);
            }
            if (value == Long.MIN_VALUE) {
                int len = SMALLEST_LONG.length();
                for (int i = 0; i < len; ++i) {
                    buffer[offset++] = (byte)SMALLEST_LONG.charAt(i);
                }
                return offset;
            }
            buffer[offset++] = 45;
            value = -value;
        } else if (value <= MAX_INT_AS_LONG) {
            return NumberOutput.outputInt((int)value, buffer, offset);
        }
        int origOffset = offset;
        int ptr = offset += NumberOutput.calcLongStrLength(value);
        while (value > MAX_INT_AS_LONG) {
            long newValue = value / THOUSAND_L;
            triplet = (int)(value - newValue * THOUSAND_L);
            NumberOutput.outputFullTriplet(triplet, buffer, ptr -= 3);
            value = newValue;
        }
        int ivalue = (int)value;
        while (ivalue >= 1000) {
            int newValue = ivalue / 1000;
            triplet = ivalue - newValue * 1000;
            NumberOutput.outputFullTriplet(triplet, buffer, ptr -= 3);
            ivalue = newValue;
        }
        NumberOutput.outputLeadingTriplet(ivalue, buffer, origOffset);
        return offset;
    }

    public static String toString(int value) {
        if (value < sSmallIntStrs.length) {
            if (value >= 0) {
                return sSmallIntStrs[value];
            }
            int v2 = -value - 1;
            if (v2 < sSmallIntStrs2.length) {
                return sSmallIntStrs2[v2];
            }
        }
        return Integer.toString(value);
    }

    public static String toString(long value) {
        if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
            return NumberOutput.toString((int)value);
        }
        return Long.toString(value);
    }

    public static String toString(double value) {
        return Double.toString(value);
    }

    private static int outputLeadingTriplet(int triplet, char[] buffer, int offset) {
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

    private static int outputLeadingTriplet(int triplet, byte[] buffer, int offset) {
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

    private static int outputFullTriplet(int triplet, char[] buffer, int offset) {
        int digitOffset = triplet << 2;
        buffer[offset++] = FULL_TRIPLETS[digitOffset++];
        buffer[offset++] = FULL_TRIPLETS[digitOffset++];
        buffer[offset++] = FULL_TRIPLETS[digitOffset];
        return offset;
    }

    private static int outputFullTriplet(int triplet, byte[] buffer, int offset) {
        int digitOffset = triplet << 2;
        buffer[offset++] = FULL_TRIPLETS_B[digitOffset++];
        buffer[offset++] = FULL_TRIPLETS_B[digitOffset++];
        buffer[offset++] = FULL_TRIPLETS_B[digitOffset];
        return offset;
    }

    private static int calcLongStrLength(long posValue) {
        int len;
        long comp = TEN_BILLION_L;
        for (len = 10; posValue >= comp && len != 19; ++len) {
            comp = (comp << 3) + (comp << 1);
        }
        return len;
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
                    NumberOutput.LEADING_TRIPLETS[ix] = l1;
                    NumberOutput.LEADING_TRIPLETS[ix + 1] = l2;
                    NumberOutput.LEADING_TRIPLETS[ix + 2] = f3;
                    NumberOutput.FULL_TRIPLETS[ix] = f1;
                    NumberOutput.FULL_TRIPLETS[ix + 1] = f2;
                    NumberOutput.FULL_TRIPLETS[ix + 2] = f3;
                    ix += 4;
                }
            }
        }
        FULL_TRIPLETS_B = new byte[4000];
        for (int i = 0; i < 4000; ++i) {
            NumberOutput.FULL_TRIPLETS_B[i] = (byte)FULL_TRIPLETS[i];
        }
        sSmallIntStrs = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        sSmallIntStrs2 = new String[]{"-1", "-2", "-3", "-4", "-5", "-6", "-7", "-8", "-9", "-10"};
    }
}

