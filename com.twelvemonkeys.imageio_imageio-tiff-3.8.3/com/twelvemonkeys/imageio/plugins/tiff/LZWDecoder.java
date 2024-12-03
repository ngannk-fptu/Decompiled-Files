/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.io.enc.DecodeException
 *  com.twelvemonkeys.io.enc.Decoder
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.io.enc.DecodeException;
import com.twelvemonkeys.io.enc.Decoder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

abstract class LZWDecoder
implements Decoder {
    static final int CLEAR_CODE = 256;
    static final int EOI_CODE = 257;
    private static final int MIN_BITS = 9;
    private static final int MAX_BITS = 12;
    private static final int TABLE_SIZE = 4096;
    private final LZWString[] table;
    private int tableLength;
    int bitsPerCode;
    private int oldCode = 256;
    private int maxCode;
    int bitMask;
    private int maxString;
    boolean eofReached;
    int nextData;
    int nextBits;

    protected LZWDecoder(int n) {
        this.table = new LZWString[n];
        for (int i = 0; i < 256; ++i) {
            this.table[i] = new LZWString((byte)i);
        }
        this.init();
    }

    private static int bitmaskFor(int n) {
        return (1 << n) - 1;
    }

    private void init() {
        this.tableLength = 258;
        this.bitsPerCode = 9;
        this.bitMask = LZWDecoder.bitmaskFor(this.bitsPerCode);
        this.maxCode = this.maxCode();
        this.maxString = 1;
    }

    public int decode(InputStream inputStream, ByteBuffer byteBuffer) throws IOException {
        int n;
        if (byteBuffer == null) {
            throw new NullPointerException("buffer == null");
        }
        while ((n = this.getNextCode(inputStream)) != 257) {
            if (n == 256) {
                this.init();
                n = this.getNextCode(inputStream);
                if (n == 257) break;
                if (this.table[n] == null) {
                    throw new DecodeException(String.format("Corrupted TIFF LZW: code %d (table size: %d)", n, this.tableLength));
                }
                this.table[n].writeTo(byteBuffer);
            } else {
                if (this.table[this.oldCode] == null) {
                    throw new DecodeException(String.format("Corrupted TIFF LZW: code %d (table size: %d)", this.oldCode, this.tableLength));
                }
                if (this.isInTable(n)) {
                    this.table[n].writeTo(byteBuffer);
                    this.addStringToTable(this.table[this.oldCode].concatenate(this.table[n].firstChar));
                } else {
                    LZWString lZWString = this.table[this.oldCode].concatenate(this.table[this.oldCode].firstChar);
                    lZWString.writeTo(byteBuffer);
                    this.addStringToTable(lZWString);
                }
            }
            this.oldCode = n;
            if (byteBuffer.remaining() >= this.maxString + 1) continue;
            break;
        }
        return byteBuffer.position();
    }

    private void addStringToTable(LZWString lZWString) throws IOException {
        if (this.tableLength > this.table.length) {
            throw new DecodeException(String.format("TIFF LZW with more than %d bits per code encountered (table overflow)", 12));
        }
        this.table[this.tableLength++] = lZWString;
        if (this.tableLength > this.maxCode) {
            ++this.bitsPerCode;
            if (this.bitsPerCode > 12) {
                this.bitsPerCode = 12;
            }
            this.bitMask = LZWDecoder.bitmaskFor(this.bitsPerCode);
            this.maxCode = this.maxCode();
        }
        if (lZWString.length > this.maxString) {
            this.maxString = lZWString.length;
        }
    }

    protected abstract int maxCode();

    private boolean isInTable(int n) {
        return n < this.tableLength;
    }

    protected abstract int getNextCode(InputStream var1) throws IOException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static boolean isOldBitReversedStream(InputStream inputStream) throws IOException {
        inputStream.mark(2);
        try {
            int n = inputStream.read();
            int n2 = inputStream.read();
            boolean bl = n == 0 && (n2 & 1) == 1;
            return bl;
        }
        finally {
            inputStream.reset();
        }
    }

    public static Decoder create(boolean bl) {
        return bl ? new LZWCompatibilityDecoder() : new LZWSpecDecoder();
    }

    static final class LZWString
    implements Comparable<LZWString> {
        static final LZWString EMPTY = new LZWString(0, 0, 0, null);
        final LZWString previous;
        final int length;
        final byte value;
        final byte firstChar;

        public LZWString(byte by) {
            this(by, by, 1, null);
        }

        private LZWString(byte by, byte by2, int n, LZWString lZWString) {
            this.value = by;
            this.firstChar = by2;
            this.length = n;
            this.previous = lZWString;
        }

        public final LZWString concatenate(byte by) {
            if (this == EMPTY) {
                return new LZWString(by);
            }
            return new LZWString(by, this.firstChar, this.length + 1, this);
        }

        public final void writeTo(ByteBuffer byteBuffer) {
            if (this.length == 0) {
                return;
            }
            if (this.length == 1) {
                byteBuffer.put(this.value);
            } else {
                LZWString lZWString = this;
                int n = byteBuffer.position();
                for (int i = this.length - 1; i >= 0; --i) {
                    byteBuffer.put(n + i, lZWString.value);
                    lZWString = lZWString.previous;
                }
                byteBuffer.position(n + this.length);
            }
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder("ZLWString[");
            int n = stringBuilder.length();
            LZWString lZWString = this;
            for (int i = this.length - 1; i >= 0; --i) {
                stringBuilder.insert(n, String.format("%2x", lZWString.value));
                lZWString = lZWString.previous;
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            LZWString lZWString = (LZWString)object;
            return this.firstChar == lZWString.firstChar && this.length == lZWString.length && this.value == lZWString.value && this.previous == lZWString.previous;
        }

        public int hashCode() {
            int n = this.previous != null ? this.previous.hashCode() : 0;
            n = 31 * n + this.length;
            n = 31 * n + this.value;
            n = 31 * n + this.firstChar;
            return n;
        }

        @Override
        public int compareTo(LZWString lZWString) {
            if (lZWString == this) {
                return 0;
            }
            if (this.length != lZWString.length) {
                return lZWString.length - this.length;
            }
            if (this.firstChar != lZWString.firstChar) {
                return lZWString.firstChar - this.firstChar;
            }
            LZWString lZWString2 = this;
            LZWString lZWString3 = lZWString;
            for (int i = this.length - 1; i > 0; --i) {
                if (lZWString2.value != lZWString3.value) {
                    return lZWString3.value - lZWString2.value;
                }
                lZWString2 = lZWString2.previous;
                lZWString3 = lZWString3.previous;
            }
            return 0;
        }
    }

    private static final class LZWCompatibilityDecoder
    extends LZWDecoder {
        protected LZWCompatibilityDecoder() {
            super(5120);
        }

        @Override
        protected int maxCode() {
            return this.bitMask;
        }

        @Override
        protected final int getNextCode(InputStream inputStream) throws IOException {
            if (this.eofReached) {
                return 257;
            }
            int n = inputStream.read();
            if (n < 0) {
                this.eofReached = true;
                return 257;
            }
            this.nextData |= n << this.nextBits;
            this.nextBits += 8;
            if (this.nextBits < this.bitsPerCode) {
                n = inputStream.read();
                if (n < 0) {
                    this.eofReached = true;
                    return 257;
                }
                this.nextData |= n << this.nextBits;
                this.nextBits += 8;
            }
            int n2 = this.nextData & this.bitMask;
            this.nextData >>= this.bitsPerCode;
            this.nextBits -= this.bitsPerCode;
            return n2;
        }
    }

    static final class LZWSpecDecoder
    extends LZWDecoder {
        protected LZWSpecDecoder() {
            super(4096);
        }

        @Override
        protected int maxCode() {
            return this.bitMask - 1;
        }

        @Override
        protected final int getNextCode(InputStream inputStream) throws IOException {
            if (this.eofReached) {
                return 257;
            }
            int n = inputStream.read();
            if (n < 0) {
                this.eofReached = true;
                return 257;
            }
            this.nextData = this.nextData << 8 | n;
            this.nextBits += 8;
            if (this.nextBits < this.bitsPerCode) {
                n = inputStream.read();
                if (n < 0) {
                    this.eofReached = true;
                    return 257;
                }
                this.nextData = this.nextData << 8 | n;
                this.nextBits += 8;
            }
            int n2 = this.nextData >> this.nextBits - this.bitsPerCode & this.bitMask;
            this.nextBits -= this.bitsPerCode;
            return n2;
        }
    }
}

