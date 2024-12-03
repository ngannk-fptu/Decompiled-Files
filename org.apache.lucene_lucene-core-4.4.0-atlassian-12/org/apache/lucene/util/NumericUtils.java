/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

public final class NumericUtils {
    public static final int PRECISION_STEP_DEFAULT = 4;
    public static final byte SHIFT_START_LONG = 32;
    public static final int BUF_SIZE_LONG = 11;
    public static final byte SHIFT_START_INT = 96;
    public static final int BUF_SIZE_INT = 6;

    private NumericUtils() {
    }

    public static int longToPrefixCoded(long val, int shift, BytesRef bytes) {
        NumericUtils.longToPrefixCodedBytes(val, shift, bytes);
        return bytes.hashCode();
    }

    public static int intToPrefixCoded(int val, int shift, BytesRef bytes) {
        NumericUtils.intToPrefixCodedBytes(val, shift, bytes);
        return bytes.hashCode();
    }

    public static void longToPrefixCodedBytes(long val, int shift, BytesRef bytes) {
        if ((shift & 0xFFFFFFC0) != 0) {
            throw new IllegalArgumentException("Illegal shift value, must be 0..63");
        }
        int nChars = ((63 - shift) * 37 >> 8) + 1;
        bytes.offset = 0;
        bytes.length = nChars + 1;
        if (bytes.bytes.length < bytes.length) {
            bytes.bytes = new byte[11];
        }
        bytes.bytes[0] = (byte)(32 + shift);
        long sortableBits = val ^ Long.MIN_VALUE;
        sortableBits >>>= shift;
        while (nChars > 0) {
            bytes.bytes[nChars--] = (byte)(sortableBits & 0x7FL);
            sortableBits >>>= 7;
        }
    }

    public static void intToPrefixCodedBytes(int val, int shift, BytesRef bytes) {
        if ((shift & 0xFFFFFFE0) != 0) {
            throw new IllegalArgumentException("Illegal shift value, must be 0..31");
        }
        int nChars = ((31 - shift) * 37 >> 8) + 1;
        bytes.offset = 0;
        bytes.length = nChars + 1;
        if (bytes.bytes.length < bytes.length) {
            bytes.bytes = new byte[11];
        }
        bytes.bytes[0] = (byte)(96 + shift);
        int sortableBits = val ^ Integer.MIN_VALUE;
        sortableBits >>>= shift;
        while (nChars > 0) {
            bytes.bytes[nChars--] = (byte)(sortableBits & 0x7F);
            sortableBits >>>= 7;
        }
    }

    public static int getPrefixCodedLongShift(BytesRef val) {
        int shift = val.bytes[val.offset] - 32;
        if (shift > 63 || shift < 0) {
            throw new NumberFormatException("Invalid shift value (" + shift + ") in prefixCoded bytes (is encoded value really an INT?)");
        }
        return shift;
    }

    public static int getPrefixCodedIntShift(BytesRef val) {
        int shift = val.bytes[val.offset] - 96;
        if (shift > 31 || shift < 0) {
            throw new NumberFormatException("Invalid shift value in prefixCoded bytes (is encoded value really an INT?)");
        }
        return shift;
    }

    public static long prefixCodedToLong(BytesRef val) {
        long sortableBits = 0L;
        int limit = val.offset + val.length;
        for (int i = val.offset + 1; i < limit; ++i) {
            sortableBits <<= 7;
            byte b = val.bytes[i];
            if (b < 0) {
                throw new NumberFormatException("Invalid prefixCoded numerical value representation (byte " + Integer.toHexString(b & 0xFF) + " at position " + (i - val.offset) + " is invalid)");
            }
            sortableBits |= (long)b;
        }
        return sortableBits << NumericUtils.getPrefixCodedLongShift(val) ^ Long.MIN_VALUE;
    }

    public static int prefixCodedToInt(BytesRef val) {
        int sortableBits = 0;
        int limit = val.offset + val.length;
        for (int i = val.offset + 1; i < limit; ++i) {
            sortableBits <<= 7;
            byte b = val.bytes[i];
            if (b < 0) {
                throw new NumberFormatException("Invalid prefixCoded numerical value representation (byte " + Integer.toHexString(b & 0xFF) + " at position " + (i - val.offset) + " is invalid)");
            }
            sortableBits |= b;
        }
        return sortableBits << NumericUtils.getPrefixCodedIntShift(val) ^ Integer.MIN_VALUE;
    }

    public static long doubleToSortableLong(double val) {
        long f = Double.doubleToLongBits(val);
        if (f < 0L) {
            f ^= Long.MAX_VALUE;
        }
        return f;
    }

    public static double sortableLongToDouble(long val) {
        if (val < 0L) {
            val ^= Long.MAX_VALUE;
        }
        return Double.longBitsToDouble(val);
    }

    public static int floatToSortableInt(float val) {
        int f = Float.floatToIntBits(val);
        if (f < 0) {
            f ^= Integer.MAX_VALUE;
        }
        return f;
    }

    public static float sortableIntToFloat(int val) {
        if (val < 0) {
            val ^= Integer.MAX_VALUE;
        }
        return Float.intBitsToFloat(val);
    }

    public static void splitLongRange(LongRangeBuilder builder, int precisionStep, long minBound, long maxBound) {
        NumericUtils.splitRange(builder, 64, precisionStep, minBound, maxBound);
    }

    public static void splitIntRange(IntRangeBuilder builder, int precisionStep, int minBound, int maxBound) {
        NumericUtils.splitRange(builder, 32, precisionStep, minBound, maxBound);
    }

    private static void splitRange(Object builder, int valSize, int precisionStep, long minBound, long maxBound) {
        if (precisionStep < 1) {
            throw new IllegalArgumentException("precisionStep must be >=1");
        }
        if (minBound > maxBound) {
            return;
        }
        int shift = 0;
        while (true) {
            boolean upperWrapped;
            long diff = 1L << shift + precisionStep;
            long mask = (1L << precisionStep) - 1L << shift;
            boolean hasLower = (minBound & mask) != 0L;
            boolean hasUpper = (maxBound & mask) != mask;
            long nextMinBound = (hasLower ? minBound + diff : minBound) & (mask ^ 0xFFFFFFFFFFFFFFFFL);
            long nextMaxBound = (hasUpper ? maxBound - diff : maxBound) & (mask ^ 0xFFFFFFFFFFFFFFFFL);
            boolean lowerWrapped = nextMinBound < minBound;
            boolean bl = upperWrapped = nextMaxBound > maxBound;
            if (shift + precisionStep >= valSize || nextMinBound > nextMaxBound || lowerWrapped || upperWrapped) break;
            if (hasLower) {
                NumericUtils.addRange(builder, valSize, minBound, minBound | mask, shift);
            }
            if (hasUpper) {
                NumericUtils.addRange(builder, valSize, maxBound & (mask ^ 0xFFFFFFFFFFFFFFFFL), maxBound, shift);
            }
            minBound = nextMinBound;
            maxBound = nextMaxBound;
            shift += precisionStep;
        }
        NumericUtils.addRange(builder, valSize, minBound, maxBound, shift);
    }

    private static void addRange(Object builder, int valSize, long minBound, long maxBound, int shift) {
        maxBound |= (1L << shift) - 1L;
        switch (valSize) {
            case 64: {
                ((LongRangeBuilder)builder).addRange(minBound, maxBound, shift);
                break;
            }
            case 32: {
                ((IntRangeBuilder)builder).addRange((int)minBound, (int)maxBound, shift);
                break;
            }
            default: {
                throw new IllegalArgumentException("valSize must be 32 or 64.");
            }
        }
    }

    public static TermsEnum filterPrefixCodedLongs(TermsEnum termsEnum) {
        return new FilteredTermsEnum(termsEnum, false){

            @Override
            protected FilteredTermsEnum.AcceptStatus accept(BytesRef term) {
                return NumericUtils.getPrefixCodedLongShift(term) == 0 ? FilteredTermsEnum.AcceptStatus.YES : FilteredTermsEnum.AcceptStatus.END;
            }
        };
    }

    public static TermsEnum filterPrefixCodedInts(TermsEnum termsEnum) {
        return new FilteredTermsEnum(termsEnum, false){

            @Override
            protected FilteredTermsEnum.AcceptStatus accept(BytesRef term) {
                return NumericUtils.getPrefixCodedIntShift(term) == 0 ? FilteredTermsEnum.AcceptStatus.YES : FilteredTermsEnum.AcceptStatus.END;
            }
        };
    }

    public static abstract class IntRangeBuilder {
        public void addRange(BytesRef minPrefixCoded, BytesRef maxPrefixCoded) {
            throw new UnsupportedOperationException();
        }

        public void addRange(int min, int max, int shift) {
            BytesRef minBytes = new BytesRef(6);
            BytesRef maxBytes = new BytesRef(6);
            NumericUtils.intToPrefixCodedBytes(min, shift, minBytes);
            NumericUtils.intToPrefixCodedBytes(max, shift, maxBytes);
            this.addRange(minBytes, maxBytes);
        }
    }

    public static abstract class LongRangeBuilder {
        public void addRange(BytesRef minPrefixCoded, BytesRef maxPrefixCoded) {
            throw new UnsupportedOperationException();
        }

        public void addRange(long min, long max, int shift) {
            BytesRef minBytes = new BytesRef(11);
            BytesRef maxBytes = new BytesRef(11);
            NumericUtils.longToPrefixCodedBytes(min, shift, minBytes);
            NumericUtils.longToPrefixCodedBytes(max, shift, maxBytes);
            this.addRange(minBytes, maxBytes);
        }
    }
}

