/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.typed;

import org.codehaus.stax2.ri.typed.AsciiValueEncoder;
import org.codehaus.stax2.ri.typed.NumberUtil;
import org.codehaus.stax2.typed.Base64Variant;

public final class ValueEncoderFactory {
    static final byte BYTE_SPACE = 32;
    protected TokenEncoder _tokenEncoder = null;
    protected IntEncoder _intEncoder = null;
    protected LongEncoder _longEncoder = null;
    protected FloatEncoder _floatEncoder = null;
    protected DoubleEncoder _doubleEncoder = null;

    public ScalarEncoder getScalarEncoder(String string) {
        if (string.length() > 64) {
            if (this._tokenEncoder == null) {
                this._tokenEncoder = new TokenEncoder();
            }
            this._tokenEncoder.reset(string);
            return this._tokenEncoder;
        }
        return new StringEncoder(string);
    }

    public ScalarEncoder getEncoder(boolean bl) {
        return this.getScalarEncoder(bl ? "true" : "false");
    }

    public IntEncoder getEncoder(int n) {
        if (this._intEncoder == null) {
            this._intEncoder = new IntEncoder();
        }
        this._intEncoder.reset(n);
        return this._intEncoder;
    }

    public LongEncoder getEncoder(long l) {
        if (this._longEncoder == null) {
            this._longEncoder = new LongEncoder();
        }
        this._longEncoder.reset(l);
        return this._longEncoder;
    }

    public FloatEncoder getEncoder(float f) {
        if (this._floatEncoder == null) {
            this._floatEncoder = new FloatEncoder();
        }
        this._floatEncoder.reset(f);
        return this._floatEncoder;
    }

    public DoubleEncoder getEncoder(double d) {
        if (this._doubleEncoder == null) {
            this._doubleEncoder = new DoubleEncoder();
        }
        this._doubleEncoder.reset(d);
        return this._doubleEncoder;
    }

    public IntArrayEncoder getEncoder(int[] nArray, int n, int n2) {
        return new IntArrayEncoder(nArray, n, n + n2);
    }

    public LongArrayEncoder getEncoder(long[] lArray, int n, int n2) {
        return new LongArrayEncoder(lArray, n, n + n2);
    }

    public FloatArrayEncoder getEncoder(float[] fArray, int n, int n2) {
        return new FloatArrayEncoder(fArray, n, n + n2);
    }

    public DoubleArrayEncoder getEncoder(double[] dArray, int n, int n2) {
        return new DoubleArrayEncoder(dArray, n, n + n2);
    }

    public Base64Encoder getEncoder(Base64Variant base64Variant, byte[] byArray, int n, int n2) {
        return new Base64Encoder(base64Variant, byArray, n, n + n2);
    }

    static final class Base64Encoder
    extends AsciiValueEncoder {
        static final char PAD_CHAR = '=';
        static final byte PAD_BYTE = 61;
        static final byte LF_CHAR = 10;
        static final byte LF_BYTE = 10;
        final Base64Variant _variant;
        final byte[] _input;
        int _inputPtr;
        final int _inputEnd;
        int _chunksBeforeLf;

        protected Base64Encoder(Base64Variant base64Variant, byte[] byArray, int n, int n2) {
            this._variant = base64Variant;
            this._input = byArray;
            this._inputPtr = n;
            this._inputEnd = n2;
            this._chunksBeforeLf = this._variant.getMaxLineLength() >> 2;
        }

        public boolean isCompleted() {
            return this._inputPtr >= this._inputEnd;
        }

        public int encodeMore(char[] cArray, int n, int n2) {
            int n3;
            int n4 = this._inputEnd - 3;
            n2 -= 5;
            while (this._inputPtr <= n4) {
                if (n > n2) {
                    return n;
                }
                n3 = this._input[this._inputPtr++] << 8;
                n3 |= this._input[this._inputPtr++] & 0xFF;
                n3 = n3 << 8 | this._input[this._inputPtr++] & 0xFF;
                n = this._variant.encodeBase64Chunk(n3, cArray, n);
                if (--this._chunksBeforeLf > 0) continue;
                cArray[n++] = 10;
                this._chunksBeforeLf = this._variant.getMaxLineLength() >> 2;
            }
            n3 = this._inputEnd - this._inputPtr;
            if (n3 > 0 && n <= n2) {
                int n5 = this._input[this._inputPtr++] << 16;
                if (n3 == 2) {
                    n5 |= (this._input[this._inputPtr++] & 0xFF) << 8;
                }
                n = this._variant.encodeBase64Partial(n5, n3, cArray, n);
            }
            return n;
        }

        public int encodeMore(byte[] byArray, int n, int n2) {
            int n3;
            int n4 = this._inputEnd - 3;
            n2 -= 5;
            while (this._inputPtr <= n4) {
                if (n > n2) {
                    return n;
                }
                n3 = this._input[this._inputPtr++] << 8;
                n3 |= this._input[this._inputPtr++] & 0xFF;
                n3 = n3 << 8 | this._input[this._inputPtr++] & 0xFF;
                n = this._variant.encodeBase64Chunk(n3, byArray, n);
                if (--this._chunksBeforeLf > 0) continue;
                byArray[n++] = 10;
                this._chunksBeforeLf = this._variant.getMaxLineLength() >> 2;
            }
            n3 = this._inputEnd - this._inputPtr;
            if (n3 > 0 && n <= n2) {
                int n5 = this._input[this._inputPtr++] << 16;
                if (n3 == 2) {
                    n5 |= (this._input[this._inputPtr++] & 0xFF) << 8;
                }
                n = this._variant.encodeBase64Partial(n5, n3, byArray, n);
            }
            return n;
        }
    }

    static final class DoubleArrayEncoder
    extends ArrayEncoder {
        final double[] _values;

        protected DoubleArrayEncoder(double[] dArray, int n, int n2) {
            super(n, n2);
            this._values = dArray;
        }

        public int encodeMore(char[] cArray, int n, int n2) {
            int n3 = n2 - 33;
            while (n <= n3 && this._ptr < this._end) {
                cArray[n++] = 32;
                n = NumberUtil.writeDouble(this._values[this._ptr++], cArray, n);
            }
            return n;
        }

        public int encodeMore(byte[] byArray, int n, int n2) {
            int n3 = n2 - 33;
            while (n <= n3 && this._ptr < this._end) {
                byArray[n++] = 32;
                n = NumberUtil.writeDouble(this._values[this._ptr++], byArray, n);
            }
            return n;
        }
    }

    static final class FloatArrayEncoder
    extends ArrayEncoder {
        final float[] _values;

        protected FloatArrayEncoder(float[] fArray, int n, int n2) {
            super(n, n2);
            this._values = fArray;
        }

        public int encodeMore(char[] cArray, int n, int n2) {
            int n3 = n2 - 33;
            while (n <= n3 && this._ptr < this._end) {
                cArray[n++] = 32;
                n = NumberUtil.writeFloat(this._values[this._ptr++], cArray, n);
            }
            return n;
        }

        public int encodeMore(byte[] byArray, int n, int n2) {
            int n3 = n2 - 33;
            while (n <= n3 && this._ptr < this._end) {
                byArray[n++] = 32;
                n = NumberUtil.writeFloat(this._values[this._ptr++], byArray, n);
            }
            return n;
        }
    }

    static final class LongArrayEncoder
    extends ArrayEncoder {
        final long[] _values;

        protected LongArrayEncoder(long[] lArray, int n, int n2) {
            super(n, n2);
            this._values = lArray;
        }

        public int encodeMore(char[] cArray, int n, int n2) {
            int n3 = n2 - 22;
            while (n <= n3 && this._ptr < this._end) {
                cArray[n++] = 32;
                n = NumberUtil.writeLong(this._values[this._ptr++], cArray, n);
            }
            return n;
        }

        public int encodeMore(byte[] byArray, int n, int n2) {
            int n3 = n2 - 22;
            while (n <= n3 && this._ptr < this._end) {
                byArray[n++] = 32;
                n = NumberUtil.writeLong(this._values[this._ptr++], byArray, n);
            }
            return n;
        }
    }

    static final class IntArrayEncoder
    extends ArrayEncoder {
        final int[] _values;

        protected IntArrayEncoder(int[] nArray, int n, int n2) {
            super(n, n2);
            this._values = nArray;
        }

        public int encodeMore(char[] cArray, int n, int n2) {
            int n3 = n2 - 12;
            while (n <= n3 && this._ptr < this._end) {
                cArray[n++] = 32;
                n = NumberUtil.writeInt(this._values[this._ptr++], cArray, n);
            }
            return n;
        }

        public int encodeMore(byte[] byArray, int n, int n2) {
            int n3 = n2 - 12;
            while (n <= n3 && this._ptr < this._end) {
                byArray[n++] = 32;
                n = NumberUtil.writeInt(this._values[this._ptr++], byArray, n);
            }
            return n;
        }
    }

    static abstract class ArrayEncoder
    extends AsciiValueEncoder {
        int _ptr;
        final int _end;

        protected ArrayEncoder(int n, int n2) {
            this._ptr = n;
            this._end = n2;
        }

        public final boolean isCompleted() {
            return this._ptr >= this._end;
        }

        public abstract int encodeMore(char[] var1, int var2, int var3);
    }

    static final class DoubleEncoder
    extends TypedScalarEncoder {
        double _value;

        protected DoubleEncoder() {
        }

        protected void reset(double d) {
            this._value = d;
        }

        public int encodeMore(char[] cArray, int n, int n2) {
            return NumberUtil.writeDouble(this._value, cArray, n);
        }

        public int encodeMore(byte[] byArray, int n, int n2) {
            return NumberUtil.writeDouble(this._value, byArray, n);
        }
    }

    static final class FloatEncoder
    extends TypedScalarEncoder {
        float _value;

        protected FloatEncoder() {
        }

        protected void reset(float f) {
            this._value = f;
        }

        public int encodeMore(char[] cArray, int n, int n2) {
            return NumberUtil.writeFloat(this._value, cArray, n);
        }

        public int encodeMore(byte[] byArray, int n, int n2) {
            return NumberUtil.writeFloat(this._value, byArray, n);
        }
    }

    static final class LongEncoder
    extends TypedScalarEncoder {
        long _value;

        protected LongEncoder() {
        }

        protected void reset(long l) {
            this._value = l;
        }

        public int encodeMore(char[] cArray, int n, int n2) {
            return NumberUtil.writeLong(this._value, cArray, n);
        }

        public int encodeMore(byte[] byArray, int n, int n2) {
            return NumberUtil.writeLong(this._value, byArray, n);
        }
    }

    static final class IntEncoder
    extends TypedScalarEncoder {
        int _value;

        protected IntEncoder() {
        }

        protected void reset(int n) {
            this._value = n;
        }

        public int encodeMore(char[] cArray, int n, int n2) {
            return NumberUtil.writeInt(this._value, cArray, n);
        }

        public int encodeMore(byte[] byArray, int n, int n2) {
            return NumberUtil.writeInt(this._value, byArray, n);
        }
    }

    static abstract class TypedScalarEncoder
    extends ScalarEncoder {
        protected TypedScalarEncoder() {
        }

        public final boolean isCompleted() {
            return true;
        }
    }

    static final class StringEncoder
    extends ScalarEncoder {
        String _value;
        int _offset;

        protected StringEncoder(String string) {
            this._value = string;
        }

        public boolean isCompleted() {
            return this._value == null;
        }

        public int encodeMore(char[] cArray, int n, int n2) {
            int n3 = n2 - n;
            int n4 = this._value.length() - this._offset;
            if (n3 >= n4) {
                this._value.getChars(this._offset, n4, cArray, n);
                this._value = null;
                return n + n4;
            }
            this._value.getChars(this._offset, n3, cArray, n);
            this._offset += n3;
            return n2;
        }

        public int encodeMore(byte[] byArray, int n, int n2) {
            int n3 = n2 - n;
            int n4 = this._value.length() - this._offset;
            if (n3 >= n4) {
                String string = this._value;
                this._value = null;
                int n5 = string.length();
                for (int i = this._offset; i < n5; ++i) {
                    byArray[n++] = (byte)string.charAt(i);
                }
                return n;
            }
            while (n < n2) {
                byArray[n] = (byte)this._value.charAt(this._offset++);
                ++n;
            }
            return n;
        }
    }

    static final class TokenEncoder
    extends ScalarEncoder {
        String _value;

        protected TokenEncoder() {
        }

        protected void reset(String string) {
            this._value = string;
        }

        public boolean isCompleted() {
            return this._value == null;
        }

        public int encodeMore(char[] cArray, int n, int n2) {
            String string = this._value;
            this._value = null;
            int n3 = string.length();
            string.getChars(0, n3, cArray, n);
            return n += n3;
        }

        public int encodeMore(byte[] byArray, int n, int n2) {
            String string = this._value;
            this._value = null;
            int n3 = string.length();
            for (int i = 0; i < n3; ++i) {
                byArray[n++] = (byte)string.charAt(i);
            }
            return n;
        }
    }

    static abstract class ScalarEncoder
    extends AsciiValueEncoder {
        protected ScalarEncoder() {
        }
    }
}

