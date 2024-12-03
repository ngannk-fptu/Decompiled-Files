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

    public QNameDecoder getQNameDecoder(NamespaceContext nsc) {
        return new QNameDecoder(nsc);
    }

    public IntArrayDecoder getIntArrayDecoder(int[] result, int offset, int len) {
        return new IntArrayDecoder(result, offset, len, this.getIntDecoder());
    }

    public IntArrayDecoder getIntArrayDecoder() {
        return new IntArrayDecoder(this.getIntDecoder());
    }

    public LongArrayDecoder getLongArrayDecoder(long[] result, int offset, int len) {
        return new LongArrayDecoder(result, offset, len, this.getLongDecoder());
    }

    public LongArrayDecoder getLongArrayDecoder() {
        return new LongArrayDecoder(this.getLongDecoder());
    }

    public FloatArrayDecoder getFloatArrayDecoder(float[] result, int offset, int len) {
        return new FloatArrayDecoder(result, offset, len, this.getFloatDecoder());
    }

    public FloatArrayDecoder getFloatArrayDecoder() {
        return new FloatArrayDecoder(this.getFloatDecoder());
    }

    public DoubleArrayDecoder getDoubleArrayDecoder(double[] result, int offset, int len) {
        return new DoubleArrayDecoder(result, offset, len, this.getDoubleDecoder());
    }

    public DoubleArrayDecoder getDoubleArrayDecoder() {
        return new DoubleArrayDecoder(this.getDoubleDecoder());
    }

    public static final class DoubleArrayDecoder
    extends BaseArrayDecoder {
        double[] mResult;
        final DoubleDecoder mDecoder;

        public DoubleArrayDecoder(double[] result, int start, int maxCount, DoubleDecoder doubleDecoder) {
            super(start, maxCount);
            this.mResult = result;
            this.mDecoder = doubleDecoder;
        }

        public DoubleArrayDecoder(DoubleDecoder doubleDecoder) {
            super(0, 40);
            this.mResult = new double[40];
            this.mDecoder = doubleDecoder;
        }

        @Override
        public void expand() {
            double[] old = this.mResult;
            int oldLen = old.length;
            int newSize = this.calcNewSize(oldLen);
            this.mResult = new double[newSize];
            System.arraycopy(old, this.mStart, this.mResult, 0, oldLen);
            this.mStart = 0;
            this.mEnd = newSize;
        }

        public double[] getValues() {
            double[] result = new double[this.mCount];
            System.arraycopy(this.mResult, this.mStart, result, 0, this.mCount);
            return result;
        }

        @Override
        public boolean decodeValue(String input) throws IllegalArgumentException {
            this.mDecoder.decode(input);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }

        @Override
        public boolean decodeValue(char[] buffer, int start, int end) throws IllegalArgumentException {
            this.mDecoder.decode(buffer, start, end);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }
    }

    public static final class FloatArrayDecoder
    extends BaseArrayDecoder {
        float[] mResult;
        final FloatDecoder mDecoder;

        public FloatArrayDecoder(float[] result, int start, int maxCount, FloatDecoder floatDecoder) {
            super(start, maxCount);
            this.mResult = result;
            this.mDecoder = floatDecoder;
        }

        public FloatArrayDecoder(FloatDecoder floatDecoder) {
            super(0, 40);
            this.mResult = new float[40];
            this.mDecoder = floatDecoder;
        }

        @Override
        public void expand() {
            float[] old = this.mResult;
            int oldLen = old.length;
            int newSize = this.calcNewSize(oldLen);
            this.mResult = new float[newSize];
            System.arraycopy(old, this.mStart, this.mResult, 0, oldLen);
            this.mStart = 0;
            this.mEnd = newSize;
        }

        public float[] getValues() {
            float[] result = new float[this.mCount];
            System.arraycopy(this.mResult, this.mStart, result, 0, this.mCount);
            return result;
        }

        @Override
        public boolean decodeValue(String input) throws IllegalArgumentException {
            this.mDecoder.decode(input);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }

        @Override
        public boolean decodeValue(char[] buffer, int start, int end) throws IllegalArgumentException {
            this.mDecoder.decode(buffer, start, end);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }
    }

    public static final class LongArrayDecoder
    extends BaseArrayDecoder {
        long[] mResult;
        final LongDecoder mDecoder;

        public LongArrayDecoder(long[] result, int start, int maxCount, LongDecoder longDecoder) {
            super(start, maxCount);
            this.mResult = result;
            this.mDecoder = longDecoder;
        }

        public LongArrayDecoder(LongDecoder longDecoder) {
            super(0, 40);
            this.mResult = new long[40];
            this.mDecoder = longDecoder;
        }

        @Override
        public void expand() {
            long[] old = this.mResult;
            int oldLen = old.length;
            int newSize = this.calcNewSize(oldLen);
            this.mResult = new long[newSize];
            System.arraycopy(old, this.mStart, this.mResult, 0, oldLen);
            this.mStart = 0;
            this.mEnd = newSize;
        }

        public long[] getValues() {
            long[] result = new long[this.mCount];
            System.arraycopy(this.mResult, this.mStart, result, 0, this.mCount);
            return result;
        }

        @Override
        public boolean decodeValue(String input) throws IllegalArgumentException {
            this.mDecoder.decode(input);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }

        @Override
        public boolean decodeValue(char[] buffer, int start, int end) throws IllegalArgumentException {
            this.mDecoder.decode(buffer, start, end);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }
    }

    public static final class IntArrayDecoder
    extends BaseArrayDecoder {
        int[] mResult;
        final IntDecoder mDecoder;

        public IntArrayDecoder(int[] result, int start, int maxCount, IntDecoder intDecoder) {
            super(start, maxCount);
            this.mResult = result;
            this.mDecoder = intDecoder;
        }

        public IntArrayDecoder(IntDecoder intDecoder) {
            super(0, 40);
            this.mResult = new int[40];
            this.mDecoder = intDecoder;
        }

        @Override
        public void expand() {
            int[] old = this.mResult;
            int oldLen = old.length;
            int newSize = this.calcNewSize(oldLen);
            this.mResult = new int[newSize];
            System.arraycopy(old, this.mStart, this.mResult, 0, oldLen);
            this.mStart = 0;
            this.mEnd = newSize;
        }

        public int[] getValues() {
            int[] result = new int[this.mCount];
            System.arraycopy(this.mResult, this.mStart, result, 0, this.mCount);
            return result;
        }

        @Override
        public boolean decodeValue(String input) throws IllegalArgumentException {
            this.mDecoder.decode(input);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }

        @Override
        public boolean decodeValue(char[] buffer, int start, int end) throws IllegalArgumentException {
            this.mDecoder.decode(buffer, start, end);
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

        protected BaseArrayDecoder(int start, int maxCount) {
            this.mStart = start;
            if (maxCount < 1) {
                throw new IllegalArgumentException("Number of elements to read can not be less than 1");
            }
            this.mEnd = maxCount;
        }

        @Override
        public final int getCount() {
            return this.mCount;
        }

        @Override
        public final boolean hasRoom() {
            return this.mCount < this.mEnd;
        }

        public abstract void expand();

        protected int calcNewSize(int currSize) {
            if (currSize < 4000) {
                return currSize << 2;
            }
            return currSize + currSize;
        }
    }

    public static final class QNameDecoder
    extends DecoderBase {
        final NamespaceContext mNsCtxt;
        protected QName mValue;

        public QNameDecoder(NamespaceContext nsc) {
            this.mNsCtxt = nsc;
        }

        @Override
        public String getType() {
            return "QName";
        }

        public QName getValue() {
            return this.mValue;
        }

        @Override
        public void decode(String lexical) throws IllegalArgumentException {
            int ix = lexical.indexOf(58);
            this.mValue = ix >= 0 ? this.resolveQName(lexical.substring(0, ix), lexical.substring(ix + 1)) : this.resolveQName(lexical);
        }

        @Override
        public void decode(char[] lexical, int start, int end) throws IllegalArgumentException {
            for (int i = start; i < end; ++i) {
                if (lexical[i] != ':') continue;
                this.mValue = this.resolveQName(new String(lexical, start, i - start), new String(lexical, i + 1, end - i - 1));
                return;
            }
            this.mValue = this.resolveQName(new String(lexical, start, end - start));
        }

        protected QName resolveQName(String localName) throws IllegalArgumentException {
            String uri = this.mNsCtxt.getNamespaceURI("");
            if (uri == null) {
                uri = "";
            }
            return new QName(uri, localName);
        }

        protected QName resolveQName(String prefix, String localName) throws IllegalArgumentException {
            if (prefix.length() == 0 || localName.length() == 0) {
                throw this.constructInvalidValue(prefix + ":" + localName);
            }
            String uri = this.mNsCtxt.getNamespaceURI(prefix);
            if (uri == null || uri.length() == 0) {
                throw new IllegalArgumentException("Value \"" + this.lexicalDesc(prefix + ":" + localName) + "\" not a valid QName: prefix '" + prefix + "' not bound to a namespace");
            }
            return new QName(uri, localName, prefix);
        }
    }

    public static final class DecimalDecoder
    extends DecoderBase {
        protected BigDecimal mValue;

        @Override
        public String getType() {
            return "decimal";
        }

        public BigDecimal getValue() {
            return this.mValue;
        }

        @Override
        public void decode(String lexical) throws IllegalArgumentException {
            try {
                this.mValue = new BigDecimal(lexical);
            }
            catch (NumberFormatException nex) {
                throw this.constructInvalidValue(lexical);
            }
        }

        @Override
        public void decode(char[] lexical, int start, int end) throws IllegalArgumentException {
            int len = end - start;
            try {
                this.mValue = new BigDecimal(new String(lexical, start, len));
            }
            catch (NumberFormatException nex) {
                throw this.constructInvalidValue(new String(lexical, start, len));
            }
        }
    }

    public static final class IntegerDecoder
    extends DecoderBase {
        protected BigInteger mValue;

        @Override
        public String getType() {
            return "integer";
        }

        public BigInteger getValue() {
            return this.mValue;
        }

        @Override
        public void decode(String lexical) throws IllegalArgumentException {
            try {
                this.mValue = new BigInteger(lexical);
            }
            catch (NumberFormatException nex) {
                throw this.constructInvalidValue(lexical);
            }
        }

        @Override
        public void decode(char[] lexical, int start, int end) throws IllegalArgumentException {
            String lexicalStr = new String(lexical, start, end - start);
            try {
                this.mValue = new BigInteger(lexicalStr);
            }
            catch (NumberFormatException nex) {
                throw this.constructInvalidValue(lexicalStr);
            }
        }
    }

    public static final class DoubleDecoder
    extends DecoderBase {
        protected double mValue;

        @Override
        public String getType() {
            return "double";
        }

        public double getValue() {
            return this.mValue;
        }

        @Override
        public void decode(String lexical) throws IllegalArgumentException {
            char c;
            int len = lexical.length();
            if (len == 3) {
                char c2 = lexical.charAt(0);
                if (c2 == 'I') {
                    if (lexical.charAt(1) == 'N' && lexical.charAt(2) == 'F') {
                        this.mValue = Double.POSITIVE_INFINITY;
                        return;
                    }
                } else if (c2 == 'N' && lexical.charAt(1) == 'a' && lexical.charAt(2) == 'N') {
                    this.mValue = Double.NaN;
                    return;
                }
            } else if (len == 4 && (c = lexical.charAt(0)) == '-' && lexical.charAt(1) == 'I' && lexical.charAt(2) == 'N' && lexical.charAt(3) == 'F') {
                this.mValue = Double.NEGATIVE_INFINITY;
                return;
            }
            try {
                this.mValue = Double.parseDouble(lexical);
            }
            catch (NumberFormatException nex) {
                throw this.constructInvalidValue(lexical);
            }
        }

        @Override
        public void decode(char[] lexical, int start, int end) throws IllegalArgumentException {
            char c;
            int len = end - start;
            if (len == 3) {
                c = lexical[start];
                if (c == 'I') {
                    if (lexical[start + 1] == 'N' && lexical[start + 2] == 'F') {
                        this.mValue = Double.POSITIVE_INFINITY;
                        return;
                    }
                } else if (c == 'N' && lexical[start + 1] == 'a' && lexical[start + 2] == 'N') {
                    this.mValue = Double.NaN;
                    return;
                }
            } else if (len == 4 && (c = lexical[start]) == '-' && lexical[start + 1] == 'I' && lexical[start + 2] == 'N' && lexical[start + 3] == 'F') {
                this.mValue = Double.NEGATIVE_INFINITY;
                return;
            }
            String lexicalStr = new String(lexical, start, len);
            try {
                this.mValue = Double.parseDouble(lexicalStr);
            }
            catch (NumberFormatException nex) {
                throw this.constructInvalidValue(lexicalStr);
            }
        }
    }

    public static final class FloatDecoder
    extends DecoderBase {
        protected float mValue;

        @Override
        public String getType() {
            return "float";
        }

        public float getValue() {
            return this.mValue;
        }

        @Override
        public void decode(String lexical) throws IllegalArgumentException {
            char c;
            int len = lexical.length();
            if (len == 3) {
                char c2 = lexical.charAt(0);
                if (c2 == 'I') {
                    if (lexical.charAt(1) == 'N' && lexical.charAt(2) == 'F') {
                        this.mValue = Float.POSITIVE_INFINITY;
                        return;
                    }
                } else if (c2 == 'N' && lexical.charAt(1) == 'a' && lexical.charAt(2) == 'N') {
                    this.mValue = Float.NaN;
                    return;
                }
            } else if (len == 4 && (c = lexical.charAt(0)) == '-' && lexical.charAt(1) == 'I' && lexical.charAt(2) == 'N' && lexical.charAt(3) == 'F') {
                this.mValue = Float.NEGATIVE_INFINITY;
                return;
            }
            try {
                this.mValue = Float.parseFloat(lexical);
            }
            catch (NumberFormatException nex) {
                throw this.constructInvalidValue(lexical);
            }
        }

        @Override
        public void decode(char[] lexical, int start, int end) throws IllegalArgumentException {
            char c;
            int len = end - start;
            if (len == 3) {
                c = lexical[start];
                if (c == 'I') {
                    if (lexical[start + 1] == 'N' && lexical[start + 2] == 'F') {
                        this.mValue = Float.POSITIVE_INFINITY;
                        return;
                    }
                } else if (c == 'N' && lexical[start + 1] == 'a' && lexical[start + 2] == 'N') {
                    this.mValue = Float.NaN;
                    return;
                }
            } else if (len == 4 && (c = lexical[start]) == '-' && lexical[start + 1] == 'I' && lexical[start + 2] == 'N' && lexical[start + 3] == 'F') {
                this.mValue = Float.NEGATIVE_INFINITY;
                return;
            }
            String lexicalStr = new String(lexical, start, len);
            try {
                this.mValue = Float.parseFloat(lexicalStr);
            }
            catch (NumberFormatException nex) {
                throw this.constructInvalidValue(lexicalStr);
            }
        }
    }

    public static final class LongDecoder
    extends DecoderBase {
        protected long mValue;

        @Override
        public String getType() {
            return "long";
        }

        public long getValue() {
            return this.mValue;
        }

        @Override
        public void decode(String lexical) throws IllegalArgumentException {
            int end = lexical.length();
            char ch = lexical.charAt(0);
            boolean neg = ch == '-';
            int nr = neg || ch == '+' ? this.skipSignAndZeroes(lexical, ch, true, end) : this.skipSignAndZeroes(lexical, ch, false, end);
            int ptr = this.mNextPtr;
            int charsLeft = end - ptr;
            if (charsLeft == 0) {
                this.mValue = neg ? -nr : nr;
                return;
            }
            this.verifyDigits(lexical, ptr, end);
            if (charsLeft <= 8) {
                int i = LongDecoder.parseInt(nr, lexical, ptr, ptr + charsLeft);
                this.mValue = neg ? -i : i;
                return;
            }
            --ptr;
            if (++charsLeft <= 18) {
                long l = LongDecoder.parseLong(lexical, ptr, ptr + charsLeft);
                this.mValue = neg ? -l : l;
                return;
            }
            this.mValue = this.parseUsingBD(lexical.substring(ptr, ptr + charsLeft), neg);
        }

        @Override
        public void decode(char[] lexical, int start, int end) throws IllegalArgumentException {
            char ch = lexical[start];
            boolean neg = ch == '-';
            int nr = neg || ch == '+' ? this.skipSignAndZeroes(lexical, ch, true, start, end) : this.skipSignAndZeroes(lexical, ch, false, start, end);
            int ptr = this.mNextPtr;
            int charsLeft = end - ptr;
            if (charsLeft == 0) {
                this.mValue = neg ? -nr : nr;
                return;
            }
            this.verifyDigits(lexical, start, end, ptr);
            if (charsLeft <= 8) {
                int i = LongDecoder.parseInt(nr, lexical, ptr, ptr + charsLeft);
                this.mValue = neg ? (long)(-i) : (long)i;
                return;
            }
            --ptr;
            if (++charsLeft <= 18) {
                long l = LongDecoder.parseLong(lexical, ptr, ptr + charsLeft);
                this.mValue = neg ? -l : l;
                return;
            }
            this.mValue = this.parseUsingBD(new String(lexical, ptr, charsLeft), neg);
        }

        private long parseUsingBD(String lexical, boolean neg) {
            BigInteger bi = new BigInteger(lexical);
            if (neg ? (bi = bi.negate()).compareTo(BD_MIN_LONG) >= 0 : bi.compareTo(BD_MAX_LONG) <= 0) {
                return bi.longValue();
            }
            throw new IllegalArgumentException("value \"" + this.lexicalDesc(lexical) + "\" not a valid long: overflow.");
        }
    }

    public static final class IntDecoder
    extends DecoderBase {
        protected int mValue;

        @Override
        public String getType() {
            return "int";
        }

        public int getValue() {
            return this.mValue;
        }

        @Override
        public void decode(String lexical) throws IllegalArgumentException {
            int end = lexical.length();
            char ch = lexical.charAt(0);
            boolean neg = ch == '-';
            int nr = neg || ch == '+' ? this.skipSignAndZeroes(lexical, ch, true, end) : this.skipSignAndZeroes(lexical, ch, false, end);
            int ptr = this.mNextPtr;
            int charsLeft = end - ptr;
            if (charsLeft == 0) {
                this.mValue = neg ? -nr : nr;
                return;
            }
            this.verifyDigits(lexical, ptr, end);
            if (charsLeft <= 8) {
                int i = IntDecoder.parseInt(nr, lexical, ptr, ptr + charsLeft);
                this.mValue = neg ? -i : i;
                return;
            }
            if (charsLeft == 9 && nr < 3) {
                long base = 1000000000L;
                if (nr == 2) {
                    base += 1000000000L;
                }
                int i = IntDecoder.parseInt(lexical, ptr, ptr + charsLeft);
                long l = base + (long)i;
                if (neg) {
                    if ((l = -l) >= Integer.MIN_VALUE) {
                        this.mValue = (int)l;
                        return;
                    }
                } else if (l <= Integer.MAX_VALUE) {
                    this.mValue = (int)l;
                    return;
                }
            }
            throw new IllegalArgumentException("value \"" + this.lexicalDesc(lexical) + "\" not a valid 32-bit integer: overflow.");
        }

        @Override
        public void decode(char[] lexical, int start, int end) throws IllegalArgumentException {
            char ch = lexical[start];
            boolean neg = ch == '-';
            int nr = neg || ch == '+' ? this.skipSignAndZeroes(lexical, ch, true, start, end) : this.skipSignAndZeroes(lexical, ch, false, start, end);
            int ptr = this.mNextPtr;
            int charsLeft = end - ptr;
            if (charsLeft == 0) {
                this.mValue = neg ? -nr : nr;
                return;
            }
            this.verifyDigits(lexical, start, end, ptr);
            if (charsLeft <= 8) {
                int i = IntDecoder.parseInt(nr, lexical, ptr, ptr + charsLeft);
                this.mValue = neg ? -i : i;
                return;
            }
            if (charsLeft == 9 && nr < 3) {
                long base = 1000000000L;
                if (nr == 2) {
                    base += 1000000000L;
                }
                int i = IntDecoder.parseInt(lexical, ptr, ptr + charsLeft);
                long l = base + (long)i;
                if (neg) {
                    if ((l = -l) >= Integer.MIN_VALUE) {
                        this.mValue = (int)l;
                        return;
                    }
                } else if (l <= Integer.MAX_VALUE) {
                    this.mValue = (int)l;
                    return;
                }
            }
            throw new IllegalArgumentException("value \"" + this.lexicalDesc(lexical, start, end) + "\" not a valid 32-bit integer: overflow.");
        }
    }

    public static final class BooleanDecoder
    extends DecoderBase {
        protected boolean mValue;

        @Override
        public String getType() {
            return "boolean";
        }

        public boolean getValue() {
            return this.mValue;
        }

        @Override
        public void decode(String lexical) throws IllegalArgumentException {
            int len = lexical.length();
            char c = lexical.charAt(0);
            if (c == 't') {
                if (len == 4 && lexical.charAt(1) == 'r' && lexical.charAt(2) == 'u' && lexical.charAt(3) == 'e') {
                    this.mValue = true;
                    return;
                }
            } else if (c == 'f') {
                if (len == 5 && lexical.charAt(1) == 'a' && lexical.charAt(2) == 'l' && lexical.charAt(3) == 's' && lexical.charAt(4) == 'e') {
                    this.mValue = false;
                    return;
                }
            } else if (c == '0') {
                if (len == 1) {
                    this.mValue = false;
                    return;
                }
            } else if (c == '1' && len == 1) {
                this.mValue = true;
                return;
            }
            throw this.constructInvalidValue(lexical);
        }

        @Override
        public void decode(char[] lexical, int start, int end) throws IllegalArgumentException {
            int len = end - start;
            char c = lexical[start];
            if (c == 't') {
                if (len == 4 && lexical[start + 1] == 'r' && lexical[start + 2] == 'u' && lexical[start + 3] == 'e') {
                    this.mValue = true;
                    return;
                }
            } else if (c == 'f') {
                if (len == 5 && lexical[start + 1] == 'a' && lexical[start + 2] == 'l' && lexical[start + 3] == 's' && lexical[start + 4] == 'e') {
                    this.mValue = false;
                    return;
                }
            } else if (c == '0') {
                if (len == 1) {
                    this.mValue = false;
                    return;
                }
            } else if (c == '1' && len == 1) {
                this.mValue = true;
                return;
            }
            throw this.constructInvalidValue(lexical, start, end);
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

        @Override
        public void handleEmptyValue() {
            throw new IllegalArgumentException("Empty value (all white space) not a valid lexical representation of " + this.getType());
        }

        protected void verifyDigits(String lexical, int start, int end) {
            while (start < end) {
                char ch = lexical.charAt(start);
                if (ch > '9' || ch < '0') {
                    throw this.constructInvalidValue(lexical);
                }
                ++start;
            }
        }

        protected void verifyDigits(char[] lexical, int start, int end, int ptr) {
            while (ptr < end) {
                char ch = lexical[ptr];
                if (ch > '9' || ch < '0') {
                    throw this.constructInvalidValue(lexical, start, end);
                }
                ++ptr;
            }
        }

        protected int skipSignAndZeroes(String lexical, char ch, boolean hasSign, int end) {
            int v2;
            int ptr;
            if (hasSign) {
                ptr = 1;
                if (ptr >= end) {
                    throw this.constructInvalidValue(lexical);
                }
                ch = lexical.charAt(ptr++);
            } else {
                ptr = 1;
            }
            int value = ch - 48;
            if (value < 0 || value > 9) {
                throw this.constructInvalidValue(lexical);
            }
            while (value == 0 && ptr < end && (v2 = lexical.charAt(ptr) - 48) >= 0 && v2 <= 9) {
                ++ptr;
                value = v2;
            }
            this.mNextPtr = ptr;
            return value;
        }

        protected int skipSignAndZeroes(char[] lexical, char ch, boolean hasSign, int start, int end) {
            int v2;
            int value;
            int ptr = start + 1;
            if (hasSign) {
                if (ptr >= end) {
                    throw this.constructInvalidValue(lexical, start, end);
                }
                ch = lexical[ptr++];
            }
            if ((value = ch - 48) < 0 || value > 9) {
                throw this.constructInvalidValue(lexical, start, end);
            }
            while (value == 0 && ptr < end && (v2 = lexical[ptr] - 48) >= 0 && v2 <= 9) {
                ++ptr;
                value = v2;
            }
            this.mNextPtr = ptr;
            return value;
        }

        protected static final int parseInt(char[] digitChars, int start, int end) {
            int num = digitChars[start] - 48;
            if (++start < end) {
                num = num * 10 + (digitChars[start] - 48);
                if (++start < end) {
                    num = num * 10 + (digitChars[start] - 48);
                    if (++start < end) {
                        num = num * 10 + (digitChars[start] - 48);
                        if (++start < end) {
                            num = num * 10 + (digitChars[start] - 48);
                            if (++start < end) {
                                num = num * 10 + (digitChars[start] - 48);
                                if (++start < end) {
                                    num = num * 10 + (digitChars[start] - 48);
                                    if (++start < end) {
                                        num = num * 10 + (digitChars[start] - 48);
                                        if (++start < end) {
                                            num = num * 10 + (digitChars[start] - 48);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return num;
        }

        protected static final int parseInt(int num, char[] digitChars, int start, int end) {
            num = num * 10 + (digitChars[start] - 48);
            if (++start < end) {
                num = num * 10 + (digitChars[start] - 48);
                if (++start < end) {
                    num = num * 10 + (digitChars[start] - 48);
                    if (++start < end) {
                        num = num * 10 + (digitChars[start] - 48);
                        if (++start < end) {
                            num = num * 10 + (digitChars[start] - 48);
                            if (++start < end) {
                                num = num * 10 + (digitChars[start] - 48);
                                if (++start < end) {
                                    num = num * 10 + (digitChars[start] - 48);
                                    if (++start < end) {
                                        num = num * 10 + (digitChars[start] - 48);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return num;
        }

        protected static final int parseInt(String digitChars, int start, int end) {
            int num = digitChars.charAt(start) - 48;
            if (++start < end) {
                num = num * 10 + (digitChars.charAt(start) - 48);
                if (++start < end) {
                    num = num * 10 + (digitChars.charAt(start) - 48);
                    if (++start < end) {
                        num = num * 10 + (digitChars.charAt(start) - 48);
                        if (++start < end) {
                            num = num * 10 + (digitChars.charAt(start) - 48);
                            if (++start < end) {
                                num = num * 10 + (digitChars.charAt(start) - 48);
                                if (++start < end) {
                                    num = num * 10 + (digitChars.charAt(start) - 48);
                                    if (++start < end) {
                                        num = num * 10 + (digitChars.charAt(start) - 48);
                                        if (++start < end) {
                                            num = num * 10 + (digitChars.charAt(start) - 48);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return num;
        }

        protected static final int parseInt(int num, String digitChars, int start, int end) {
            num = num * 10 + (digitChars.charAt(start) - 48);
            if (++start < end) {
                num = num * 10 + (digitChars.charAt(start) - 48);
                if (++start < end) {
                    num = num * 10 + (digitChars.charAt(start) - 48);
                    if (++start < end) {
                        num = num * 10 + (digitChars.charAt(start) - 48);
                        if (++start < end) {
                            num = num * 10 + (digitChars.charAt(start) - 48);
                            if (++start < end) {
                                num = num * 10 + (digitChars.charAt(start) - 48);
                                if (++start < end) {
                                    num = num * 10 + (digitChars.charAt(start) - 48);
                                    if (++start < end) {
                                        num = num * 10 + (digitChars.charAt(start) - 48);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return num;
        }

        protected static final long parseLong(char[] digitChars, int start, int end) {
            int start2 = end - 9;
            long val = (long)DecoderBase.parseInt(digitChars, start, start2) * 1000000000L;
            return val + (long)DecoderBase.parseInt(digitChars, start2, end);
        }

        protected static final long parseLong(String digitChars, int start, int end) {
            int start2 = end - 9;
            long val = (long)DecoderBase.parseInt(digitChars, start, start2) * 1000000000L;
            return val + (long)DecoderBase.parseInt(digitChars, start2, end);
        }

        protected IllegalArgumentException constructInvalidValue(String lexical) {
            return new IllegalArgumentException("Value \"" + lexical + "\" not a valid lexical representation of " + this.getType());
        }

        protected IllegalArgumentException constructInvalidValue(char[] lexical, int startOffset, int end) {
            return new IllegalArgumentException("Value \"" + this.lexicalDesc(lexical, startOffset, end) + "\" not a valid lexical representation of " + this.getType());
        }

        protected String lexicalDesc(char[] lexical, int startOffset, int end) {
            return this._clean(new String(lexical, startOffset, end - startOffset));
        }

        protected String lexicalDesc(String lexical) {
            return this._clean(lexical);
        }

        protected String _clean(String str) {
            return str.trim();
        }
    }
}

