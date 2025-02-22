/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Encoder;

public class Base32Encoder
implements Encoder {
    private static final byte[] DEAULT_ENCODING_TABLE = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 50, 51, 52, 53, 54, 55};
    private static final byte DEFAULT_PADDING = 61;
    private final byte[] encodingTable;
    private final byte padding;
    private final byte[] decodingTable = new byte[128];

    protected void initialiseDecodingTable() {
        int i;
        for (i = 0; i < this.decodingTable.length; ++i) {
            this.decodingTable[i] = -1;
        }
        for (i = 0; i < this.encodingTable.length; ++i) {
            this.decodingTable[this.encodingTable[i]] = (byte)i;
        }
    }

    public Base32Encoder() {
        this.encodingTable = DEAULT_ENCODING_TABLE;
        this.padding = (byte)61;
        this.initialiseDecodingTable();
    }

    public Base32Encoder(byte[] encodingTable, byte padding) {
        if (encodingTable.length != 32) {
            throw new IllegalArgumentException("encoding table needs to be length 32");
        }
        this.encodingTable = Arrays.clone(encodingTable);
        this.padding = padding;
        this.initialiseDecodingTable();
    }

    public int encode(byte[] inBuf, int inOff, int inLen, byte[] outBuf, int outOff) throws IOException {
        int inPos = inOff;
        int inEnd = inOff + inLen - 4;
        int outPos = outOff;
        while (inPos < inEnd) {
            this.encodeBlock(inBuf, inPos, outBuf, outPos);
            inPos += 5;
            outPos += 8;
        }
        int extra = inLen - (inPos - inOff);
        if (extra > 0) {
            byte[] in = new byte[5];
            System.arraycopy(inBuf, inPos, in, 0, extra);
            this.encodeBlock(in, 0, outBuf, outPos);
            switch (extra) {
                case 1: {
                    outBuf[outPos + 2] = this.padding;
                    outBuf[outPos + 3] = this.padding;
                    outBuf[outPos + 4] = this.padding;
                    outBuf[outPos + 5] = this.padding;
                    outBuf[outPos + 6] = this.padding;
                    outBuf[outPos + 7] = this.padding;
                    break;
                }
                case 2: {
                    outBuf[outPos + 4] = this.padding;
                    outBuf[outPos + 5] = this.padding;
                    outBuf[outPos + 6] = this.padding;
                    outBuf[outPos + 7] = this.padding;
                    break;
                }
                case 3: {
                    outBuf[outPos + 5] = this.padding;
                    outBuf[outPos + 6] = this.padding;
                    outBuf[outPos + 7] = this.padding;
                    break;
                }
                case 4: {
                    outBuf[outPos + 7] = this.padding;
                }
            }
            outPos += 8;
        }
        return outPos - outOff;
    }

    private void encodeBlock(byte[] inBuf, int inPos, byte[] outBuf, int outPos) {
        byte a1 = inBuf[inPos++];
        int a2 = inBuf[inPos++] & 0xFF;
        int a3 = inBuf[inPos++] & 0xFF;
        int a4 = inBuf[inPos++] & 0xFF;
        int a5 = inBuf[inPos] & 0xFF;
        outBuf[outPos++] = this.encodingTable[a1 >>> 3 & 0x1F];
        outBuf[outPos++] = this.encodingTable[(a1 << 2 | a2 >>> 6) & 0x1F];
        outBuf[outPos++] = this.encodingTable[a2 >>> 1 & 0x1F];
        outBuf[outPos++] = this.encodingTable[(a2 << 4 | a3 >>> 4) & 0x1F];
        outBuf[outPos++] = this.encodingTable[(a3 << 1 | a4 >>> 7) & 0x1F];
        outBuf[outPos++] = this.encodingTable[a4 >>> 2 & 0x1F];
        outBuf[outPos++] = this.encodingTable[(a4 << 3 | a5 >>> 5) & 0x1F];
        outBuf[outPos] = this.encodingTable[a5 & 0x1F];
    }

    @Override
    public int getEncodedLength(int inputLength) {
        return (inputLength + 4) / 5 * 8;
    }

    @Override
    public int getMaxDecodedLength(int inputLength) {
        return inputLength / 8 * 5;
    }

    @Override
    public int encode(byte[] buf, int off, int len, OutputStream out) throws IOException {
        int inLen;
        if (len < 0) {
            return 0;
        }
        byte[] tmp = new byte[72];
        for (int remaining = len; remaining > 0; remaining -= inLen) {
            inLen = Math.min(45, remaining);
            int outLen = this.encode(buf, off, inLen, tmp, 0);
            out.write(tmp, 0, outLen);
            off += inLen;
        }
        return (len + 2) / 3 * 4;
    }

    private boolean ignore(char c) {
        return c == '\n' || c == '\r' || c == '\t' || c == ' ';
    }

    @Override
    public int decode(byte[] data, int off, int length, OutputStream out) throws IOException {
        int finish;
        int end;
        byte[] outBuffer = new byte[55];
        int bufOff = 0;
        int outLen = 0;
        for (end = off + length; end > off && this.ignore((char)data[end - 1]); --end) {
        }
        if (end == 0) {
            return 0;
        }
        int i = 0;
        for (finish = end; finish > off && i != 8; --finish) {
            if (this.ignore((char)data[finish - 1])) continue;
            ++i;
        }
        i = this.nextI(data, off, finish);
        while (i < finish) {
            byte b8;
            byte b1 = this.decodingTable[data[i++]];
            i = this.nextI(data, i, finish);
            byte b2 = this.decodingTable[data[i++]];
            i = this.nextI(data, i, finish);
            byte b3 = this.decodingTable[data[i++]];
            i = this.nextI(data, i, finish);
            byte b4 = this.decodingTable[data[i++]];
            i = this.nextI(data, i, finish);
            byte b5 = this.decodingTable[data[i++]];
            i = this.nextI(data, i, finish);
            byte b6 = this.decodingTable[data[i++]];
            i = this.nextI(data, i, finish);
            byte b7 = this.decodingTable[data[i++]];
            i = this.nextI(data, i, finish);
            if ((b1 | b2 | b3 | b4 | b5 | b6 | b7 | (b8 = this.decodingTable[data[i++]])) < 0) {
                throw new IOException("invalid characters encountered in base32 data");
            }
            outBuffer[bufOff++] = (byte)(b1 << 3 | b2 >> 2);
            outBuffer[bufOff++] = (byte)(b2 << 6 | b3 << 1 | b4 >> 4);
            outBuffer[bufOff++] = (byte)(b4 << 4 | b5 >> 1);
            outBuffer[bufOff++] = (byte)(b5 << 7 | b6 << 2 | b7 >> 3);
            outBuffer[bufOff++] = (byte)(b7 << 5 | b8);
            if (bufOff == outBuffer.length) {
                out.write(outBuffer);
                bufOff = 0;
            }
            outLen += 5;
            i = this.nextI(data, i, finish);
        }
        if (bufOff > 0) {
            out.write(outBuffer, 0, bufOff);
        }
        int e0 = this.nextI(data, i, end);
        int e1 = this.nextI(data, e0 + 1, end);
        int e2 = this.nextI(data, e1 + 1, end);
        int e3 = this.nextI(data, e2 + 1, end);
        int e4 = this.nextI(data, e3 + 1, end);
        int e5 = this.nextI(data, e4 + 1, end);
        int e6 = this.nextI(data, e5 + 1, end);
        int e7 = this.nextI(data, e6 + 1, end);
        return outLen += this.decodeLastBlock(out, (char)data[e0], (char)data[e1], (char)data[e2], (char)data[e3], (char)data[e4], (char)data[e5], (char)data[e6], (char)data[e7]);
    }

    private int nextI(byte[] data, int i, int finish) {
        while (i < finish && this.ignore((char)data[i])) {
            ++i;
        }
        return i;
    }

    @Override
    public int decode(String data, OutputStream out) throws IOException {
        byte[] bytes = Strings.toByteArray(data);
        return this.decode(bytes, 0, bytes.length, out);
    }

    private int decodeLastBlock(OutputStream out, char c1, char c2, char c3, char c4, char c5, char c6, char c7, char c8) throws IOException {
        if (c8 == this.padding) {
            if (c7 != this.padding) {
                byte b1 = this.decodingTable[c1];
                byte b2 = this.decodingTable[c2];
                byte b3 = this.decodingTable[c3];
                byte b4 = this.decodingTable[c4];
                byte b5 = this.decodingTable[c5];
                byte b6 = this.decodingTable[c6];
                byte b7 = this.decodingTable[c7];
                if ((b1 | b2 | b3 | b4 | b5 | b6 | b7) < 0) {
                    throw new IOException("invalid characters encountered at end of base32 data");
                }
                out.write(b1 << 3 | b2 >> 2);
                out.write(b2 << 6 | b3 << 1 | b4 >> 4);
                out.write(b4 << 4 | b5 >> 1);
                out.write(b5 << 7 | b6 << 2 | b7 >> 3);
                return 4;
            }
            if (c6 != this.padding) {
                throw new IOException("invalid characters encountered at end of base32 data");
            }
            if (c5 != this.padding) {
                byte b1 = this.decodingTable[c1];
                byte b2 = this.decodingTable[c2];
                byte b3 = this.decodingTable[c3];
                byte b4 = this.decodingTable[c4];
                byte b5 = this.decodingTable[c5];
                if ((b1 | b2 | b3 | b4 | b5) < 0) {
                    throw new IOException("invalid characters encountered at end of base32 data");
                }
                out.write(b1 << 3 | b2 >> 2);
                out.write(b2 << 6 | b3 << 1 | b4 >> 4);
                out.write(b4 << 4 | b5 >> 1);
                return 3;
            }
            if (c4 != this.padding) {
                byte b1 = this.decodingTable[c1];
                byte b2 = this.decodingTable[c2];
                byte b3 = this.decodingTable[c3];
                byte b4 = this.decodingTable[c4];
                if ((b1 | b2 | b3 | b4) < 0) {
                    throw new IOException("invalid characters encountered at end of base32 data");
                }
                out.write(b1 << 3 | b2 >> 2);
                out.write(b2 << 6 | b3 << 1 | b4 >> 4);
                return 2;
            }
            if (c3 != this.padding) {
                throw new IOException("invalid characters encountered at end of base32 data");
            }
            byte b1 = this.decodingTable[c1];
            byte b2 = this.decodingTable[c2];
            if ((b1 | b2) < 0) {
                throw new IOException("invalid characters encountered at end of base32 data");
            }
            out.write(b1 << 3 | b2 >> 2);
            return 1;
        }
        byte b1 = this.decodingTable[c1];
        byte b2 = this.decodingTable[c2];
        byte b3 = this.decodingTable[c3];
        byte b4 = this.decodingTable[c4];
        byte b5 = this.decodingTable[c5];
        byte b6 = this.decodingTable[c6];
        byte b7 = this.decodingTable[c7];
        byte b8 = this.decodingTable[c8];
        if ((b1 | b2 | b3 | b4 | b5 | b6 | b7 | b8) < 0) {
            throw new IOException("invalid characters encountered at end of base32 data");
        }
        out.write(b1 << 3 | b2 >> 2);
        out.write(b2 << 6 | b3 << 1 | b4 >> 4);
        out.write(b4 << 4 | b5 >> 1);
        out.write(b5 << 7 | b6 << 2 | b7 >> 3);
        out.write(b7 << 5 | b8);
        return 5;
    }
}

