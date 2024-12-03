/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.typed;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;

public final class ValueDecoderFactory {
    protected BooleanDecoder mBooleanDecoder = null;
    protected IntDecoder mIntDecoder = null;
    protected LongDecoder mLongDecoder = null;
    protected FloatDecoder mFloatDecoder = null;
    protected DoubleDecoder mDoubleDecoder = null;

    public BooleanDecoder getBooleanDecoder() {
        if (this.mBooleanDecoder == null) {
            this.mBooleanDecoder = new BooleanDecoder();
        }
        return this.mBooleanDecoder;
    }

    public IntDecoder getIntDecoder() {
        if (this.mIntDecoder == null) {
            this.mIntDecoder = new IntDecoder();
        }
        return this.mIntDecoder;
    }

    public LongDecoder getLongDecoder() {
        if (this.mLongDecoder == null) {
            this.mLongDecoder = new LongDecoder();
        }
        return this.mLongDecoder;
    }

    public FloatDecoder getFloatDecoder() {
        if (this.mFloatDecoder == null) {
            this.mFloatDecoder = new FloatDecoder();
        }
        return this.mFloatDecoder;
    }

    public DoubleDecoder getDoubleDecoder() {
        if (this.mDoubleDecoder == null) {
            this.mDoubleDecoder = new DoubleDecoder();
        }
        return this.mDoubleDecoder;
    }

    public IntegerDecoder getIntegerDecoder() {
        return new IntegerDecoder();
    }

    public DecimalDecoder getDecimalDecoder() {
        return new DecimalDecoder();
    }

    public QNameDecoder getQNameDecoder(NamespaceContext namespaceContext) {
        return new QNameDecoder(namespaceContext);
    }

    public IntArrayDecoder getIntArrayDecoder(int[] nArray, int n, int n2) {
        return new IntArrayDecoder(nArray, n, n2, this.getIntDecoder());
    }

    public IntArrayDecoder getIntArrayDecoder() {
        return new IntArrayDecoder(this.getIntDecoder());
    }

    public LongArrayDecoder getLongArrayDecoder(long[] lArray, int n, int n2) {
        return new LongArrayDecoder(lArray, n, n2, this.getLongDecoder());
    }

    public LongArrayDecoder getLongArrayDecoder() {
        return new LongArrayDecoder(this.getLongDecoder());
    }

    public FloatArrayDecoder getFloatArrayDecoder(float[] fArray, int n, int n2) {
        return new FloatArrayDecoder(fArray, n, n2, this.getFloatDecoder());
    }

    public FloatArrayDecoder getFloatArrayDecoder() {
        return new FloatArrayDecoder(this.getFloatDecoder());
    }

    public DoubleArrayDecoder getDoubleArrayDecoder(double[] dArray, int n, int n2) {
        return new DoubleArrayDecoder(dArray, n, n2, this.getDoubleDecoder());
    }

    public DoubleArrayDecoder getDoubleArrayDecoder() {
        return new DoubleArrayDecoder(this.getDoubleDecoder());
    }

    public static final class DoubleArrayDecoder
    extends BaseArrayDecoder {
        double[] mResult;
        final DoubleDecoder mDecoder;

        public DoubleArrayDecoder(double[] dArray, int n, int n2, DoubleDecoder doubleDecoder) {
            super(n, n2);
            this.mResult = dArray;
            this.mDecoder = doubleDecoder;
        }

        public DoubleArrayDecoder(DoubleDecoder doubleDecoder) {
            super(0, 40);
            this.mResult = new double[40];
            this.mDecoder = doubleDecoder;
        }

        public void expand() {
            double[] dArray = this.mResult;
            int n = dArray.length;
            int n2 = this.calcNewSize(n);
            this.mResult = new double[n2];
            System.arraycopy(dArray, this.mStart, this.mResult, 0, n);
            this.mStart = 0;
            this.mEnd = n2;
        }

        public double[] getValues() {
            double[] dArray = new double[this.mCount];
            System.arraycopy(this.mResult, this.mStart, dArray, 0, this.mCount);
            return dArray;
        }

        public boolean decodeValue(String string) throws IllegalArgumentException {
            this.mDecoder.decode(string);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }

        public boolean decodeValue(char[] cArray, int n, int n2) throws IllegalArgumentException {
            this.mDecoder.decode(cArray, n, n2);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }
    }

    public static final class FloatArrayDecoder
    extends BaseArrayDecoder {
        float[] mResult;
        final FloatDecoder mDecoder;

        public FloatArrayDecoder(float[] fArray, int n, int n2, FloatDecoder floatDecoder) {
            super(n, n2);
            this.mResult = fArray;
            this.mDecoder = floatDecoder;
        }

        public FloatArrayDecoder(FloatDecoder floatDecoder) {
            super(0, 40);
            this.mResult = new float[40];
            this.mDecoder = floatDecoder;
        }

        public void expand() {
            float[] fArray = this.mResult;
            int n = fArray.length;
            int n2 = this.calcNewSize(n);
            this.mResult = new float[n2];
            System.arraycopy(fArray, this.mStart, this.mResult, 0, n);
            this.mStart = 0;
            this.mEnd = n2;
        }

        public float[] getValues() {
            float[] fArray = new float[this.mCount];
            System.arraycopy(this.mResult, this.mStart, fArray, 0, this.mCount);
            return fArray;
        }

        public boolean decodeValue(String string) throws IllegalArgumentException {
            this.mDecoder.decode(string);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }

        public boolean decodeValue(char[] cArray, int n, int n2) throws IllegalArgumentException {
            this.mDecoder.decode(cArray, n, n2);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }
    }

    public static final class LongArrayDecoder
    extends BaseArrayDecoder {
        long[] mResult;
        final LongDecoder mDecoder;

        public LongArrayDecoder(long[] lArray, int n, int n2, LongDecoder longDecoder) {
            super(n, n2);
            this.mResult = lArray;
            this.mDecoder = longDecoder;
        }

        public LongArrayDecoder(LongDecoder longDecoder) {
            super(0, 40);
            this.mResult = new long[40];
            this.mDecoder = longDecoder;
        }

        public void expand() {
            long[] lArray = this.mResult;
            int n = lArray.length;
            int n2 = this.calcNewSize(n);
            this.mResult = new long[n2];
            System.arraycopy(lArray, this.mStart, this.mResult, 0, n);
            this.mStart = 0;
            this.mEnd = n2;
        }

        public long[] getValues() {
            long[] lArray = new long[this.mCount];
            System.arraycopy(this.mResult, this.mStart, lArray, 0, this.mCount);
            return lArray;
        }

        public boolean decodeValue(String string) throws IllegalArgumentException {
            this.mDecoder.decode(string);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }

        public boolean decodeValue(char[] cArray, int n, int n2) throws IllegalArgumentException {
            this.mDecoder.decode(cArray, n, n2);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }
    }

    public static final class IntArrayDecoder
    extends BaseArrayDecoder {
        int[] mResult;
        final IntDecoder mDecoder;

        public IntArrayDecoder(int[] nArray, int n, int n2, IntDecoder intDecoder) {
            super(n, n2);
            this.mResult = nArray;
            this.mDecoder = intDecoder;
        }

        public IntArrayDecoder(IntDecoder intDecoder) {
            super(0, 40);
            this.mResult = new int[40];
            this.mDecoder = intDecoder;
        }

        public void expand() {
            int[] nArray = this.mResult;
            int n = nArray.length;
            int n2 = this.calcNewSize(n);
            this.mResult = new int[n2];
            System.arraycopy(nArray, this.mStart, this.mResult, 0, n);
            this.mStart = 0;
            this.mEnd = n2;
        }

        public int[] getValues() {
            int[] nArray = new int[this.mCount];
            System.arraycopy(this.mResult, this.mStart, nArray, 0, this.mCount);
            return nArray;
        }

        public boolean decodeValue(String string) throws IllegalArgumentException {
            this.mDecoder.decode(string);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }

        public boolean decodeValue(char[] cArray, int n, int n2) throws IllegalArgumentException {
            this.mDecoder.decode(cArray, n, n2);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }
    }

    public static abstract class BaseArrayDecoder
    extends TypedArrayDecoder {
        protected static final int INITIAL_RESULT_BUFFER_SIZE = 40;
        protected static final int SMALL_RESULT_BUFFER_SIZE = 4000;
        protected int mStart;
        protected int mEnd;
        protected int mCount = 0;

        protected BaseArrayDecoder(int n, int n2) {
            this.mStart = n;
            if (n2 < 1) {
                throw new IllegalArgumentException("Number of elements to read can not be less than 1");
            }
            this.mEnd = n2;
        }

        public final int getCount() {
            return this.mCount;
        }

        public final boolean hasRoom() {
            return this.mCount < this.mEnd;
        }

        public abstract void expand();

        protected int calcNewSize(int n) {
            if (n < 4000) {
                return n << 2;
            }
            return n + n;
        }
    }

    public static final class QNameDecoder
    extends DecoderBase {
        final NamespaceContext mNsCtxt;
        protected QName mValue;

        public QNameDecoder(NamespaceContext namespaceContext) {
            this.mNsCtxt = namespaceContext;
        }

        public String getType() {
            return "QName";
        }

        public QName getValue() {
            return this.mValue;
        }

        public void decode(String string) throws IllegalArgumentException {
            int n = string.indexOf(58);
            this.mValue = n >= 0 ? this.resolveQName(string.substring(0, n), string.substring(n + 1)) : this.resolveQName(string);
        }

        public void decode(char[] cArray, int n, int n2) throws IllegalArgumentException {
            for (int i = n; i < n2; ++i) {
                if (cArray[i] != ':') continue;
                this.mValue = this.resolveQName(new String(cArray, n, i - n), new String(cArray, i + 1, n2 - i - 1));
                return;
            }
            this.mValue = this.resolveQName(new String(cArray, n, n2 - n));
        }

        protected QName resolveQName(String string) throws IllegalArgumentException {
            String string2 = this.mNsCtxt.getNamespaceURI("");
            if (string2 == null) {
                string2 = "";
            }
            return new QName(string2, string);
        }

        protected QName resolveQName(String string, String string2) throws IllegalArgumentException {
            if (string.length() == 0 || string2.length() == 0) {
                throw this.constructInvalidValue(string + ":" + string2);
            }
            String string3 = this.mNsCtxt.getNamespaceURI(string);
            if (string3 == null || string3.length() == 0) {
                throw new IllegalArgumentException("Value \"" + this.lexicalDesc(string + ":" + string2) + "\" not a valid QName: prefix '" + string + "' not bound to a namespace");
            }
            return new QName(string3, string2, string);
        }
    }

    public static final class DecimalDecoder
    extends DecoderBase {
        protected BigDecimal mValue;

        public String getType() {
            return "decimal";
        }

        public BigDecimal getValue() {
            return this.mValue;
        }

        public void decode(String string) throws IllegalArgumentException {
            try {
                this.mValue = new BigDecimal(string);
            }
            catch (NumberFormatException numberFormatException) {
                throw this.constructInvalidValue(string);
            }
        }

        public void decode(char[] cArray, int n, int n2) throws IllegalArgumentException {
            int n3 = n2 - n;
            try {
                this.mValue = new BigDecimal(new String(cArray, n, n3));
            }
            catch (NumberFormatException numberFormatException) {
                throw this.constructInvalidValue(new String(cArray, n, n3));
            }
        }
    }

    public static final class IntegerDecoder
    extends DecoderBase {
        protected BigInteger mValue;

        public String getType() {
            return "integer";
        }

        public BigInteger getValue() {
            return this.mValue;
        }

        public void decode(String string) throws IllegalArgumentException {
            try {
                this.mValue = new BigInteger(string);
            }
            catch (NumberFormatException numberFormatException) {
                throw this.constructInvalidValue(string);
            }
        }

        public void decode(char[] cArray, int n, int n2) throws IllegalArgumentException {
            String string = new String(cArray, n, n2 - n);
            try {
                this.mValue = new BigInteger(string);
            }
            catch (NumberFormatException numberFormatException) {
                throw this.constructInvalidValue(string);
            }
        }
    }

    public static final class DoubleDecoder
    extends DecoderBase {
        protected double mValue;

        public String getType() {
            return "double";
        }

        public double getValue() {
            return this.mValue;
        }

        public void decode(String string) throws IllegalArgumentException {
            char c;
            int n = string.length();
            if (n == 3) {
                char c2 = string.charAt(0);
                if (c2 == 'I') {
                    if (string.charAt(1) == 'N' && string.charAt(2) == 'F') {
                        this.mValue = Double.POSITIVE_INFINITY;
                        return;
                    }
                } else if (c2 == 'N' && string.charAt(1) == 'a' && string.charAt(2) == 'N') {
                    this.mValue = Double.NaN;
                    return;
                }
            } else if (n == 4 && (c = string.charAt(0)) == '-' && string.charAt(1) == 'I' && string.charAt(2) == 'N' && string.charAt(3) == 'F') {
                this.mValue = Double.NEGATIVE_INFINITY;
                return;
            }
            try {
                this.mValue = Double.parseDouble(string);
            }
            catch (NumberFormatException numberFormatException) {
                throw this.constructInvalidValue(string);
            }
        }

        public void decode(char[] cArray, int n, int n2) throws IllegalArgumentException {
            char c;
            int n3 = n2 - n;
            if (n3 == 3) {
                c = cArray[n];
                if (c == 'I') {
                    if (cArray[n + 1] == 'N' && cArray[n + 2] == 'F') {
                        this.mValue = Double.POSITIVE_INFINITY;
                        return;
                    }
                } else if (c == 'N' && cArray[n + 1] == 'a' && cArray[n + 2] == 'N') {
                    this.mValue = Double.NaN;
                    return;
                }
            } else if (n3 == 4 && (c = cArray[n]) == '-' && cArray[n + 1] == 'I' && cArray[n + 2] == 'N' && cArray[n + 3] == 'F') {
                this.mValue = Double.NEGATIVE_INFINITY;
                return;
            }
            String string = new String(cArray, n, n3);
            try {
                this.mValue = Double.parseDouble(string);
            }
            catch (NumberFormatException numberFormatException) {
                throw this.constructInvalidValue(string);
            }
        }
    }

    public static final class FloatDecoder
    extends DecoderBase {
        protected float mValue;

        public String getType() {
            return "float";
        }

        public float getValue() {
            return this.mValue;
        }

        public void decode(String string) throws IllegalArgumentException {
            char c;
            int n = string.length();
            if (n == 3) {
                char c2 = string.charAt(0);
                if (c2 == 'I') {
                    if (string.charAt(1) == 'N' && string.charAt(2) == 'F') {
                        this.mValue = Float.POSITIVE_INFINITY;
                        return;
                    }
                } else if (c2 == 'N' && string.charAt(1) == 'a' && string.charAt(2) == 'N') {
                    this.mValue = Float.NaN;
                    return;
                }
            } else if (n == 4 && (c = string.charAt(0)) == '-' && string.charAt(1) == 'I' && string.charAt(2) == 'N' && string.charAt(3) == 'F') {
                this.mValue = Float.NEGATIVE_INFINITY;
                return;
            }
            try {
                this.mValue = Float.parseFloat(string);
            }
            catch (NumberFormatException numberFormatException) {
                throw this.constructInvalidValue(string);
            }
        }

        public void decode(char[] cArray, int n, int n2) throws IllegalArgumentException {
            char c;
            int n3 = n2 - n;
            if (n3 == 3) {
                c = cArray[n];
                if (c == 'I') {
                    if (cArray[n + 1] == 'N' && cArray[n + 2] == 'F') {
                        this.mValue = Float.POSITIVE_INFINITY;
                        return;
                    }
                } else if (c == 'N' && cArray[n + 1] == 'a' && cArray[n + 2] == 'N') {
                    this.mValue = Float.NaN;
                    return;
                }
            } else if (n3 == 4 && (c = cArray[n]) == '-' && cArray[n + 1] == 'I' && cArray[n + 2] == 'N' && cArray[n + 3] == 'F') {
                this.mValue = Float.NEGATIVE_INFINITY;
                return;
            }
            String string = new String(cArray, n, n3);
            try {
                this.mValue = Float.parseFloat(string);
            }
            catch (NumberFormatException numberFormatException) {
                throw this.constructInvalidValue(string);
            }
        }
    }

    public static final class LongDecoder
    extends DecoderBase {
        protected long mValue;

        public String getType() {
            return "long";
        }

        public long getValue() {
            return this.mValue;
        }

        public void decode(String string) throws IllegalArgumentException {
            int n = string.length();
            char c = string.charAt(0);
            boolean bl = c == '-';
            int n2 = bl || c == '+' ? this.skipSignAndZeroes(string, c, true, n) : this.skipSignAndZeroes(string, c, false, n);
            int n3 = this.mNextPtr;
            int n4 = n - n3;
            if (n4 == 0) {
                this.mValue = bl ? -n2 : n2;
                return;
            }
            this.verifyDigits(string, n3, n);
            if (n4 <= 8) {
                int n5 = LongDecoder.parseInt(n2, string, n3, n3 + n4);
                this.mValue = bl ? -n5 : n5;
                return;
            }
            --n3;
            if (++n4 <= 18) {
                long l = LongDecoder.parseLong(string, n3, n3 + n4);
                this.mValue = bl ? -l : l;
                return;
            }
            this.mValue = this.parseUsingBD(string.substring(n3, n3 + n4), bl);
        }

        public void decode(char[] cArray, int n, int n2) throws IllegalArgumentException {
            char c = cArray[n];
            boolean bl = c == '-';
            int n3 = bl || c == '+' ? this.skipSignAndZeroes(cArray, c, true, n, n2) : this.skipSignAndZeroes(cArray, c, false, n, n2);
            int n4 = this.mNextPtr;
            int n5 = n2 - n4;
            if (n5 == 0) {
                this.mValue = bl ? -n3 : n3;
                return;
            }
            this.verifyDigits(cArray, n, n2, n4);
            if (n5 <= 8) {
                int n6 = LongDecoder.parseInt(n3, cArray, n4, n4 + n5);
                this.mValue = bl ? (long)(-n6) : (long)n6;
                return;
            }
            --n4;
            if (++n5 <= 18) {
                long l = LongDecoder.parseLong(cArray, n4, n4 + n5);
                this.mValue = bl ? -l : l;
                return;
            }
            this.mValue = this.parseUsingBD(new String(cArray, n4, n5), bl);
        }

        private long parseUsingBD(String string, boolean bl) {
            BigInteger bigInteger = new BigInteger(string);
            if (bl ? (bigInteger = bigInteger.negate()).compareTo(BD_MIN_LONG) >= 0 : bigInteger.compareTo(BD_MAX_LONG) <= 0) {
                return bigInteger.longValue();
            }
            throw new IllegalArgumentException("value \"" + this.lexicalDesc(string) + "\" not a valid long: overflow.");
        }
    }

    public static final class IntDecoder
    extends DecoderBase {
        protected int mValue;

        public String getType() {
            return "int";
        }

        public int getValue() {
            return this.mValue;
        }

        public void decode(String string) throws IllegalArgumentException {
            int n = string.length();
            char c = string.charAt(0);
            boolean bl = c == '-';
            int n2 = bl || c == '+' ? this.skipSignAndZeroes(string, c, true, n) : this.skipSignAndZeroes(string, c, false, n);
            int n3 = this.mNextPtr;
            int n4 = n - n3;
            if (n4 == 0) {
                this.mValue = bl ? -n2 : n2;
                return;
            }
            this.verifyDigits(string, n3, n);
            if (n4 <= 8) {
                int n5 = IntDecoder.parseInt(n2, string, n3, n3 + n4);
                this.mValue = bl ? -n5 : n5;
                return;
            }
            if (n4 == 9 && n2 < 3) {
                long l = 1000000000L;
                if (n2 == 2) {
                    l += 1000000000L;
                }
                int n6 = IntDecoder.parseInt(string, n3, n3 + n4);
                long l2 = l + (long)n6;
                if (bl) {
                    if ((l2 = -l2) >= Integer.MIN_VALUE) {
                        this.mValue = (int)l2;
                        return;
                    }
                } else if (l2 <= Integer.MAX_VALUE) {
                    this.mValue = (int)l2;
                    return;
                }
            }
            throw new IllegalArgumentException("value \"" + this.lexicalDesc(string) + "\" not a valid 32-bit integer: overflow.");
        }

        public void decode(char[] cArray, int n, int n2) throws IllegalArgumentException {
            char c = cArray[n];
            boolean bl = c == '-';
            int n3 = bl || c == '+' ? this.skipSignAndZeroes(cArray, c, true, n, n2) : this.skipSignAndZeroes(cArray, c, false, n, n2);
            int n4 = this.mNextPtr;
            int n5 = n2 - n4;
            if (n5 == 0) {
                this.mValue = bl ? -n3 : n3;
                return;
            }
            this.verifyDigits(cArray, n, n2, n4);
            if (n5 <= 8) {
                int n6 = IntDecoder.parseInt(n3, cArray, n4, n4 + n5);
                this.mValue = bl ? -n6 : n6;
                return;
            }
            if (n5 == 9 && n3 < 3) {
                long l = 1000000000L;
                if (n3 == 2) {
                    l += 1000000000L;
                }
                int n7 = IntDecoder.parseInt(cArray, n4, n4 + n5);
                long l2 = l + (long)n7;
                if (bl) {
                    if ((l2 = -l2) >= Integer.MIN_VALUE) {
                        this.mValue = (int)l2;
                        return;
                    }
                } else if (l2 <= Integer.MAX_VALUE) {
                    this.mValue = (int)l2;
                    return;
                }
            }
            throw new IllegalArgumentException("value \"" + this.lexicalDesc(cArray, n, n2) + "\" not a valid 32-bit integer: overflow.");
        }
    }

    public static final class BooleanDecoder
    extends DecoderBase {
        protected boolean mValue;

        public String getType() {
            return "boolean";
        }

        public boolean getValue() {
            return this.mValue;
        }

        public void decode(String string) throws IllegalArgumentException {
            int n = string.length();
            char c = string.charAt(0);
            if (c == 't') {
                if (n == 4 && string.charAt(1) == 'r' && string.charAt(2) == 'u' && string.charAt(3) == 'e') {
                    this.mValue = true;
                    return;
                }
            } else if (c == 'f') {
                if (n == 5 && string.charAt(1) == 'a' && string.charAt(2) == 'l' && string.charAt(3) == 's' && string.charAt(4) == 'e') {
                    this.mValue = false;
                    return;
                }
            } else if (c == '0') {
                if (n == 1) {
                    this.mValue = false;
                    return;
                }
            } else if (c == '1' && n == 1) {
                this.mValue = true;
                return;
            }
            throw this.constructInvalidValue(string);
        }

        public void decode(char[] cArray, int n, int n2) throws IllegalArgumentException {
            int n3 = n2 - n;
            char c = cArray[n];
            if (c == 't') {
                if (n3 == 4 && cArray[n + 1] == 'r' && cArray[n + 2] == 'u' && cArray[n + 3] == 'e') {
                    this.mValue = true;
                    return;
                }
            } else if (c == 'f') {
                if (n3 == 5 && cArray[n + 1] == 'a' && cArray[n + 2] == 'l' && cArray[n + 3] == 's' && cArray[n + 4] == 'e') {
                    this.mValue = false;
                    return;
                }
            } else if (c == '0') {
                if (n3 == 1) {
                    this.mValue = false;
                    return;
                }
            } else if (c == '1' && n3 == 1) {
                this.mValue = true;
                return;
            }
            throw this.constructInvalidValue(cArray, n, n2);
        }
    }

    public static abstract class DecoderBase
    extends TypedValueDecoder {
        static final long L_BILLION = 1000000000L;
        static final long L_MAX_INT = Integer.MAX_VALUE;
        static final long L_MIN_INT = Integer.MIN_VALUE;
        static final BigInteger BD_MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
        static final BigInteger BD_MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
        protected int mNextPtr;

        protected DecoderBase() {
        }

        public abstract String getType();

        public void handleEmptyValue() {
            throw new IllegalArgumentException("Empty value (all white space) not a valid lexical representation of " + this.getType());
        }

        protected void verifyDigits(String string, int n, int n2) {
            while (n < n2) {
                char c = string.charAt(n);
                if (c > '9' || c < '0') {
                    throw this.constructInvalidValue(string);
                }
                ++n;
            }
        }

        protected void verifyDigits(char[] cArray, int n, int n2, int n3) {
            while (n3 < n2) {
                char c = cArray[n3];
                if (c > '9' || c < '0') {
                    throw this.constructInvalidValue(cArray, n, n2);
                }
                ++n3;
            }
        }

        protected int skipSignAndZeroes(String string, char c, boolean bl, int n) {
            int n2;
            int n3;
            if (bl) {
                n3 = 1;
                if (n3 >= n) {
                    throw this.constructInvalidValue(string);
                }
                c = string.charAt(n3++);
            } else {
                n3 = 1;
            }
            int n4 = c - 48;
            if (n4 < 0 || n4 > 9) {
                throw this.constructInvalidValue(string);
            }
            while (n4 == 0 && n3 < n && (n2 = string.charAt(n3) - 48) >= 0 && n2 <= 9) {
                ++n3;
                n4 = n2;
            }
            this.mNextPtr = n3;
            return n4;
        }

        protected int skipSignAndZeroes(char[] cArray, char c, boolean bl, int n, int n2) {
            int n3;
            int n4;
            int n5 = n + 1;
            if (bl) {
                if (n5 >= n2) {
                    throw this.constructInvalidValue(cArray, n, n2);
                }
                c = cArray[n5++];
            }
            if ((n4 = c - 48) < 0 || n4 > 9) {
                throw this.constructInvalidValue(cArray, n, n2);
            }
            while (n4 == 0 && n5 < n2 && (n3 = cArray[n5] - 48) >= 0 && n3 <= 9) {
                ++n5;
                n4 = n3;
            }
            this.mNextPtr = n5;
            return n4;
        }

        protected static final int parseInt(char[] cArray, int n, int n2) {
            int n3 = cArray[n] - 48;
            if (++n < n2) {
                n3 = n3 * 10 + (cArray[n] - 48);
                if (++n < n2) {
                    n3 = n3 * 10 + (cArray[n] - 48);
                    if (++n < n2) {
                        n3 = n3 * 10 + (cArray[n] - 48);
                        if (++n < n2) {
                            n3 = n3 * 10 + (cArray[n] - 48);
                            if (++n < n2) {
                                n3 = n3 * 10 + (cArray[n] - 48);
                                if (++n < n2) {
                                    n3 = n3 * 10 + (cArray[n] - 48);
                                    if (++n < n2) {
                                        n3 = n3 * 10 + (cArray[n] - 48);
                                        if (++n < n2) {
                                            n3 = n3 * 10 + (cArray[n] - 48);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return n3;
        }

        protected static final int parseInt(int n, char[] cArray, int n2, int n3) {
            n = n * 10 + (cArray[n2] - 48);
            if (++n2 < n3) {
                n = n * 10 + (cArray[n2] - 48);
                if (++n2 < n3) {
                    n = n * 10 + (cArray[n2] - 48);
                    if (++n2 < n3) {
                        n = n * 10 + (cArray[n2] - 48);
                        if (++n2 < n3) {
                            n = n * 10 + (cArray[n2] - 48);
                            if (++n2 < n3) {
                                n = n * 10 + (cArray[n2] - 48);
                                if (++n2 < n3) {
                                    n = n * 10 + (cArray[n2] - 48);
                                    if (++n2 < n3) {
                                        n = n * 10 + (cArray[n2] - 48);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return n;
        }

        protected static final int parseInt(String string, int n, int n2) {
            int n3 = string.charAt(n) - 48;
            if (++n < n2) {
                n3 = n3 * 10 + (string.charAt(n) - 48);
                if (++n < n2) {
                    n3 = n3 * 10 + (string.charAt(n) - 48);
                    if (++n < n2) {
                        n3 = n3 * 10 + (string.charAt(n) - 48);
                        if (++n < n2) {
                            n3 = n3 * 10 + (string.charAt(n) - 48);
                            if (++n < n2) {
                                n3 = n3 * 10 + (string.charAt(n) - 48);
                                if (++n < n2) {
                                    n3 = n3 * 10 + (string.charAt(n) - 48);
                                    if (++n < n2) {
                                        n3 = n3 * 10 + (string.charAt(n) - 48);
                                        if (++n < n2) {
                                            n3 = n3 * 10 + (string.charAt(n) - 48);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return n3;
        }

        protected static final int parseInt(int n, String string, int n2, int n3) {
            n = n * 10 + (string.charAt(n2) - 48);
            if (++n2 < n3) {
                n = n * 10 + (string.charAt(n2) - 48);
                if (++n2 < n3) {
                    n = n * 10 + (string.charAt(n2) - 48);
                    if (++n2 < n3) {
                        n = n * 10 + (string.charAt(n2) - 48);
                        if (++n2 < n3) {
                            n = n * 10 + (string.charAt(n2) - 48);
                            if (++n2 < n3) {
                                n = n * 10 + (string.charAt(n2) - 48);
                                if (++n2 < n3) {
                                    n = n * 10 + (string.charAt(n2) - 48);
                                    if (++n2 < n3) {
                                        n = n * 10 + (string.charAt(n2) - 48);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return n;
        }

        protected static final long parseLong(char[] cArray, int n, int n2) {
            int n3 = n2 - 9;
            long l = (long)DecoderBase.parseInt(cArray, n, n3) * 1000000000L;
            return l + (long)DecoderBase.parseInt(cArray, n3, n2);
        }

        protected static final long parseLong(String string, int n, int n2) {
            int n3 = n2 - 9;
            long l = (long)DecoderBase.parseInt(string, n, n3) * 1000000000L;
            return l + (long)DecoderBase.parseInt(string, n3, n2);
        }

        protected IllegalArgumentException constructInvalidValue(String string) {
            return new IllegalArgumentException("Value \"" + string + "\" not a valid lexical representation of " + this.getType());
        }

        protected IllegalArgumentException constructInvalidValue(char[] cArray, int n, int n2) {
            return new IllegalArgumentException("Value \"" + this.lexicalDesc(cArray, n, n2) + "\" not a valid lexical representation of " + this.getType());
        }

        protected String lexicalDesc(char[] cArray, int n, int n2) {
            return this._clean(new String(cArray, n, n2 - n));
        }

        protected String lexicalDesc(String string) {
            return this._clean(string);
        }

        protected String _clean(String string) {
            return string.trim();
        }
    }
}

