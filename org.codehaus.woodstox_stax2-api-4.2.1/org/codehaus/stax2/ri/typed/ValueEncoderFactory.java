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

    public ScalarEncoder getScalarEncoder(String value) {
        if (value.length() > 64) {
            if (this._tokenEncoder == null) {
                this._tokenEncoder = new TokenEncoder();
            }
            this._tokenEncoder.reset(value);
            return this._tokenEncoder;
        }
        return new StringEncoder(value);
    }

    public ScalarEncoder getEncoder(boolean value) {
        return this.getScalarEncoder(value ? "true" : "false");
    }

    public IntEncoder getEncoder(int value) {
        if (this._intEncoder == null) {
            this._intEncoder = new IntEncoder();
        }
        this._intEncoder.reset(value);
        return this._intEncoder;
    }

    public LongEncoder getEncoder(long value) {
        if (this._longEncoder == null) {
            this._longEncoder = new LongEncoder();
        }
        this._longEncoder.reset(value);
        return this._longEncoder;
    }

    public FloatEncoder getEncoder(float value) {
        if (this._floatEncoder == null) {
            this._floatEncoder = new FloatEncoder();
        }
        this._floatEncoder.reset(value);
        return this._floatEncoder;
    }

    public DoubleEncoder getEncoder(double value) {
        if (this._doubleEncoder == null) {
            this._doubleEncoder = new DoubleEncoder();
        }
        this._doubleEncoder.reset(value);
        return this._doubleEncoder;
    }

    public IntArrayEncoder getEncoder(int[] values, int from, int length) {
        return new IntArrayEncoder(values, from, from + length);
    }

    public LongArrayEncoder getEncoder(long[] values, int from, int length) {
        return new LongArrayEncoder(values, from, from + length);
    }

    public FloatArrayEncoder getEncoder(float[] values, int from, int length) {
        return new FloatArrayEncoder(values, from, from + length);
    }

    public DoubleArrayEncoder getEncoder(double[] values, int from, int length) {
        return new DoubleArrayEncoder(values, from, from + length);
    }

    public Base64Encoder getEncoder(Base64Variant v, byte[] data, int from, int length) {
        return new Base64Encoder(v, data, from, from + length);
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

        protected Base64Encoder(Base64Variant v, byte[] values, int from, int end) {
            this._variant = v;
            this._input = values;
            this._inputPtr = from;
            this._inputEnd = end;
            this._chunksBeforeLf = this._variant.getMaxLineLength() >> 2;
        }

        @Override
        public boolean isCompleted() {
            return this._inputPtr >= this._inputEnd;
        }

        @Override
        public int encodeMore(char[] buffer, int outPtr, int outEnd) {
            int inEnd = this._inputEnd - 3;
            outEnd -= 5;
            while (this._inputPtr <= inEnd) {
                if (outPtr > outEnd) {
                    return outPtr;
                }
                int b24 = this._input[this._inputPtr++] << 8;
                b24 |= this._input[this._inputPtr++] & 0xFF;
                b24 = b24 << 8 | this._input[this._inputPtr++] & 0xFF;
                outPtr = this._variant.encodeBase64Chunk(b24, buffer, outPtr);
                if (--this._chunksBeforeLf > 0) continue;
                buffer[outPtr++] = 10;
                this._chunksBeforeLf = this._variant.getMaxLineLength() >> 2;
            }
            int inputLeft = this._inputEnd - this._inputPtr;
            if (inputLeft > 0 && outPtr <= outEnd) {
                int b24 = this._input[this._inputPtr++] << 16;
                if (inputLeft == 2) {
                    b24 |= (this._input[this._inputPtr++] & 0xFF) << 8;
                }
                outPtr = this._variant.encodeBase64Partial(b24, inputLeft, buffer, outPtr);
            }
            return outPtr;
        }

        @Override
        public int encodeMore(byte[] buffer, int outPtr, int outEnd) {
            int inEnd = this._inputEnd - 3;
            outEnd -= 5;
            while (this._inputPtr <= inEnd) {
                if (outPtr > outEnd) {
                    return outPtr;
                }
                int b24 = this._input[this._inputPtr++] << 8;
                b24 |= this._input[this._inputPtr++] & 0xFF;
                b24 = b24 << 8 | this._input[this._inputPtr++] & 0xFF;
                outPtr = this._variant.encodeBase64Chunk(b24, buffer, outPtr);
                if (--this._chunksBeforeLf > 0) continue;
                buffer[outPtr++] = 10;
                this._chunksBeforeLf = this._variant.getMaxLineLength() >> 2;
            }
            int inputLeft = this._inputEnd - this._inputPtr;
            if (inputLeft > 0 && outPtr <= outEnd) {
                int b24 = this._input[this._inputPtr++] << 16;
                if (inputLeft == 2) {
                    b24 |= (this._input[this._inputPtr++] & 0xFF) << 8;
                }
                outPtr = this._variant.encodeBase64Partial(b24, inputLeft, buffer, outPtr);
            }
            return outPtr;
        }
    }

    static final class DoubleArrayEncoder
    extends ArrayEncoder {
        final double[] _values;

        protected DoubleArrayEncoder(double[] values, int from, int length) {
            super(from, length);
            this._values = values;
        }

        @Override
        public int encodeMore(char[] buffer, int ptr, int end) {
            int lastOk = end - 33;
            while (ptr <= lastOk && this._ptr < this._end) {
                buffer[ptr++] = 32;
                ptr = NumberUtil.writeDouble(this._values[this._ptr++], buffer, ptr);
            }
            return ptr;
        }

        @Override
        public int encodeMore(byte[] buffer, int ptr, int end) {
            int lastOk = end - 33;
            while (ptr <= lastOk && this._ptr < this._end) {
                buffer[ptr++] = 32;
                ptr = NumberUtil.writeDouble(this._values[this._ptr++], buffer, ptr);
            }
            return ptr;
        }
    }

    static final class FloatArrayEncoder
    extends ArrayEncoder {
        final float[] _values;

        protected FloatArrayEncoder(float[] values, int from, int length) {
            super(from, length);
            this._values = values;
        }

        @Override
        public int encodeMore(char[] buffer, int ptr, int end) {
            int lastOk = end - 33;
            while (ptr <= lastOk && this._ptr < this._end) {
                buffer[ptr++] = 32;
                ptr = NumberUtil.writeFloat(this._values[this._ptr++], buffer, ptr);
            }
            return ptr;
        }

        @Override
        public int encodeMore(byte[] buffer, int ptr, int end) {
            int lastOk = end - 33;
            while (ptr <= lastOk && this._ptr < this._end) {
                buffer[ptr++] = 32;
                ptr = NumberUtil.writeFloat(this._values[this._ptr++], buffer, ptr);
            }
            return ptr;
        }
    }

    static final class LongArrayEncoder
    extends ArrayEncoder {
        final long[] _values;

        protected LongArrayEncoder(long[] values, int from, int length) {
            super(from, length);
            this._values = values;
        }

        @Override
        public int encodeMore(char[] buffer, int ptr, int end) {
            int lastOk = end - 22;
            while (ptr <= lastOk && this._ptr < this._end) {
                buffer[ptr++] = 32;
                ptr = NumberUtil.writeLong(this._values[this._ptr++], buffer, ptr);
            }
            return ptr;
        }

        @Override
        public int encodeMore(byte[] buffer, int ptr, int end) {
            int lastOk = end - 22;
            while (ptr <= lastOk && this._ptr < this._end) {
                buffer[ptr++] = 32;
                ptr = NumberUtil.writeLong(this._values[this._ptr++], buffer, ptr);
            }
            return ptr;
        }
    }

    static final class IntArrayEncoder
    extends ArrayEncoder {
        final int[] _values;

        protected IntArrayEncoder(int[] values, int from, int length) {
            super(from, length);
            this._values = values;
        }

        @Override
        public int encodeMore(char[] buffer, int ptr, int end) {
            int lastOk = end - 12;
            while (ptr <= lastOk && this._ptr < this._end) {
                buffer[ptr++] = 32;
                ptr = NumberUtil.writeInt(this._values[this._ptr++], buffer, ptr);
            }
            return ptr;
        }

        @Override
        public int encodeMore(byte[] buffer, int ptr, int end) {
            int lastOk = end - 12;
            while (ptr <= lastOk && this._ptr < this._end) {
                buffer[ptr++] = 32;
                ptr = NumberUtil.writeInt(this._values[this._ptr++], buffer, ptr);
            }
            return ptr;
        }
    }

    static abstract class ArrayEncoder
    extends AsciiValueEncoder {
        int _ptr;
        final int _end;

        protected ArrayEncoder(int ptr, int end) {
            this._ptr = ptr;
            this._end = end;
        }

        @Override
        public final boolean isCompleted() {
            return this._ptr >= this._end;
        }

        @Override
        public abstract int encodeMore(char[] var1, int var2, int var3);
    }

    static final class DoubleEncoder
    extends TypedScalarEncoder {
        double _value;

        protected DoubleEncoder() {
        }

        protected void reset(double value) {
            this._value = value;
        }

        @Override
        public int encodeMore(char[] buffer, int ptr, int end) {
            return NumberUtil.writeDouble(this._value, buffer, ptr);
        }

        @Override
        public int encodeMore(byte[] buffer, int ptr, int end) {
            return NumberUtil.writeDouble(this._value, buffer, ptr);
        }
    }

    static final class FloatEncoder
    extends TypedScalarEncoder {
        float _value;

        protected FloatEncoder() {
        }

        protected void reset(float value) {
            this._value = value;
        }

        @Override
        public int encodeMore(char[] buffer, int ptr, int end) {
            return NumberUtil.writeFloat(this._value, buffer, ptr);
        }

        @Override
        public int encodeMore(byte[] buffer, int ptr, int end) {
            return NumberUtil.writeFloat(this._value, buffer, ptr);
        }
    }

    static final class LongEncoder
    extends TypedScalarEncoder {
        long _value;

        protected LongEncoder() {
        }

        protected void reset(long value) {
            this._value = value;
        }

        @Override
        public int encodeMore(char[] buffer, int ptr, int end) {
            return NumberUtil.writeLong(this._value, buffer, ptr);
        }

        @Override
        public int encodeMore(byte[] buffer, int ptr, int end) {
            return NumberUtil.writeLong(this._value, buffer, ptr);
        }
    }

    static final class IntEncoder
    extends TypedScalarEncoder {
        int _value;

        protected IntEncoder() {
        }

        protected void reset(int value) {
            this._value = value;
        }

        @Override
        public int encodeMore(char[] buffer, int ptr, int end) {
            return NumberUtil.writeInt(this._value, buffer, ptr);
        }

        @Override
        public int encodeMore(byte[] buffer, int ptr, int end) {
            return NumberUtil.writeInt(this._value, buffer, ptr);
        }
    }

    static abstract class TypedScalarEncoder
    extends ScalarEncoder {
        protected TypedScalarEncoder() {
        }

        @Override
        public final boolean isCompleted() {
            return true;
        }
    }

    static final class StringEncoder
    extends ScalarEncoder {
        String _value;
        int _offset;

        protected StringEncoder(String value) {
            this._value = value;
        }

        @Override
        public boolean isCompleted() {
            return this._value == null;
        }

        @Override
        public int encodeMore(char[] buffer, int ptr, int end) {
            int free = end - ptr;
            int left = this._value.length() - this._offset;
            if (free >= left) {
                this._value.getChars(this._offset, left, buffer, ptr);
                this._value = null;
                return ptr + left;
            }
            this._value.getChars(this._offset, free, buffer, ptr);
            this._offset += free;
            return end;
        }

        @Override
        public int encodeMore(byte[] buffer, int ptr, int end) {
            int free = end - ptr;
            int left = this._value.length() - this._offset;
            if (free >= left) {
                String str = this._value;
                this._value = null;
                int last = str.length();
                for (int offset = this._offset; offset < last; ++offset) {
                    buffer[ptr++] = (byte)str.charAt(offset);
                }
                return ptr;
            }
            while (ptr < end) {
                buffer[ptr] = (byte)this._value.charAt(this._offset++);
                ++ptr;
            }
            return ptr;
        }
    }

    static final class TokenEncoder
    extends ScalarEncoder {
        String _value;

        protected TokenEncoder() {
        }

        protected void reset(String value) {
            this._value = value;
        }

        @Override
        public boolean isCompleted() {
            return this._value == null;
        }

        @Override
        public int encodeMore(char[] buffer, int ptr, int end) {
            String str = this._value;
            this._value = null;
            int len = str.length();
            str.getChars(0, len, buffer, ptr);
            return ptr += len;
        }

        @Override
        public int encodeMore(byte[] buffer, int ptr, int end) {
            String str = this._value;
            this._value = null;
            int len = str.length();
            for (int i = 0; i < len; ++i) {
                buffer[ptr++] = (byte)str.charAt(i);
            }
            return ptr;
        }
    }

    static abstract class ScalarEncoder
    extends AsciiValueEncoder {
        protected ScalarEncoder() {
        }
    }
}

