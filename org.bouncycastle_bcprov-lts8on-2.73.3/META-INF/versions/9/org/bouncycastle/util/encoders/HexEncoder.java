/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.encoders.Encoder;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class HexEncoder
implements Encoder {
    protected final byte[] encodingTable = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    protected final byte[] decodingTable = new byte[128];

    protected void initialiseDecodingTable() {
        int i;
        for (i = 0; i < this.decodingTable.length; ++i) {
            this.decodingTable[i] = -1;
        }
        for (i = 0; i < this.encodingTable.length; ++i) {
            this.decodingTable[this.encodingTable[i]] = (byte)i;
        }
        this.decodingTable[65] = this.decodingTable[97];
        this.decodingTable[66] = this.decodingTable[98];
        this.decodingTable[67] = this.decodingTable[99];
        this.decodingTable[68] = this.decodingTable[100];
        this.decodingTable[69] = this.decodingTable[101];
        this.decodingTable[70] = this.decodingTable[102];
    }

    public HexEncoder() {
        this.initialiseDecodingTable();
    }

    public int encode(byte[] inBuf, int inOff, int inLen, byte[] outBuf, int outOff) throws IOException {
        int inPos = inOff;
        int inEnd = inOff + inLen;
        int outPos = outOff;
        while (inPos < inEnd) {
            int b = inBuf[inPos++] & 0xFF;
            outBuf[outPos++] = this.encodingTable[b >>> 4];
            outBuf[outPos++] = this.encodingTable[b & 0xF];
        }
        return outPos - outOff;
    }

    @Override
    public int getEncodedLength(int inputLength) {
        return inputLength * 2;
    }

    @Override
    public int getMaxDecodedLength(int inputLength) {
        return inputLength / 2;
    }

    @Override
    public int encode(byte[] buf, int off, int len, OutputStream out) throws IOException {
        int inLen;
        if (len < 0) {
            return 0;
        }
        byte[] tmp = new byte[72];
        for (int remaining = len; remaining > 0; remaining -= inLen) {
            inLen = Math.min(36, remaining);
            int outLen = this.encode(buf, off, inLen, tmp, 0);
            out.write(tmp, 0, outLen);
            off += inLen;
        }
        return len * 2;
    }

    private static boolean ignore(char c) {
        return c == '\n' || c == '\r' || c == '\t' || c == ' ';
    }

    @Override
    public int decode(byte[] data, int off, int length, OutputStream out) throws IOException {
        int end;
        int outLen = 0;
        byte[] buf = new byte[36];
        int bufOff = 0;
        for (end = off + length; end > off && HexEncoder.ignore((char)data[end - 1]); --end) {
        }
        int i = off;
        while (i < end) {
            byte b2;
            while (i < end && HexEncoder.ignore((char)data[i])) {
                ++i;
            }
            byte b1 = this.decodingTable[data[i++]];
            while (i < end && HexEncoder.ignore((char)data[i])) {
                ++i;
            }
            if ((b1 | (b2 = this.decodingTable[data[i++]])) < 0) {
                throw new IOException("invalid characters encountered in Hex data");
            }
            buf[bufOff++] = (byte)(b1 << 4 | b2);
            if (bufOff == buf.length) {
                out.write(buf);
                bufOff = 0;
            }
            ++outLen;
        }
        if (bufOff > 0) {
            out.write(buf, 0, bufOff);
        }
        return outLen;
    }

    @Override
    public int decode(String data, OutputStream out) throws IOException {
        int end;
        int length = 0;
        byte[] buf = new byte[36];
        int bufOff = 0;
        for (end = data.length(); end > 0 && HexEncoder.ignore(data.charAt(end - 1)); --end) {
        }
        int i = 0;
        while (i < end) {
            byte b2;
            while (i < end && HexEncoder.ignore(data.charAt(i))) {
                ++i;
            }
            byte b1 = this.decodingTable[data.charAt(i++)];
            while (i < end && HexEncoder.ignore(data.charAt(i))) {
                ++i;
            }
            if ((b1 | (b2 = this.decodingTable[data.charAt(i++)])) < 0) {
                throw new IOException("invalid characters encountered in Hex string");
            }
            buf[bufOff++] = (byte)(b1 << 4 | b2);
            if (bufOff == buf.length) {
                out.write(buf);
                bufOff = 0;
            }
            ++length;
        }
        if (bufOff > 0) {
            out.write(buf, 0, bufOff);
        }
        return length;
    }

    byte[] decodeStrict(String str, int off, int len) throws IOException {
        if (null == str) {
            throw new NullPointerException("'str' cannot be null");
        }
        if (off < 0 || len < 0 || off > str.length() - len) {
            throw new IndexOutOfBoundsException("invalid offset and/or length specified");
        }
        if (0 != (len & 1)) {
            throw new IOException("a hexadecimal encoding must have an even number of characters");
        }
        int resultLen = len >>> 1;
        byte[] result = new byte[resultLen];
        int strPos = off;
        for (int i = 0; i < resultLen; ++i) {
            byte b2;
            byte b1;
            int n;
            if ((n = (b1 = this.decodingTable[str.charAt(strPos++)]) << 4 | (b2 = this.decodingTable[str.charAt(strPos++)])) < 0) {
                throw new IOException("invalid characters encountered in Hex string");
            }
            result[i] = (byte)n;
        }
        return result;
    }
}

