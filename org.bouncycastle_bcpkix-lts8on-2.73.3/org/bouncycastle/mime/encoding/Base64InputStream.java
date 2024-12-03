/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.mime.encoding;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class Base64InputStream
extends InputStream {
    private static final byte[] decodingTable;
    InputStream in;
    int[] outBuf = new int[3];
    int bufPtr = 3;

    private int decode(int in0, int in1, int in2, int in3, int[] out) throws EOFException {
        if (in3 < 0) {
            throw new EOFException("unexpected end of file in armored stream.");
        }
        if (in2 == 61) {
            int b1 = decodingTable[in0] & 0xFF;
            int b2 = decodingTable[in1] & 0xFF;
            out[2] = (b1 << 2 | b2 >> 4) & 0xFF;
            return 2;
        }
        if (in3 == 61) {
            byte b1 = decodingTable[in0];
            byte b2 = decodingTable[in1];
            byte b3 = decodingTable[in2];
            out[1] = (b1 << 2 | b2 >> 4) & 0xFF;
            out[2] = (b2 << 4 | b3 >> 2) & 0xFF;
            return 1;
        }
        byte b1 = decodingTable[in0];
        byte b2 = decodingTable[in1];
        byte b3 = decodingTable[in2];
        byte b4 = decodingTable[in3];
        out[0] = (b1 << 2 | b2 >> 4) & 0xFF;
        out[1] = (b2 << 4 | b3 >> 2) & 0xFF;
        out[2] = (b3 << 6 | b4) & 0xFF;
        return 0;
    }

    public Base64InputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int available() throws IOException {
        return 0;
    }

    @Override
    public int read() throws IOException {
        if (this.bufPtr > 2) {
            int in0 = this.readIgnoreSpaceFirst();
            if (in0 < 0) {
                return -1;
            }
            int in1 = this.readIgnoreSpace();
            int in2 = this.readIgnoreSpace();
            int in3 = this.readIgnoreSpace();
            this.bufPtr = this.decode(in0, in1, in2, in3, this.outBuf);
        }
        return this.outBuf[this.bufPtr++];
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    private int readIgnoreSpace() throws IOException {
        int c;
        block3: while (true) {
            c = this.in.read();
            switch (c) {
                case 9: 
                case 32: {
                    continue block3;
                }
            }
            break;
        }
        return c;
    }

    private int readIgnoreSpaceFirst() throws IOException {
        int c;
        block3: while (true) {
            c = this.in.read();
            switch (c) {
                case 9: 
                case 10: 
                case 13: 
                case 32: {
                    continue block3;
                }
            }
            break;
        }
        return c;
    }

    static {
        int i;
        decodingTable = new byte[128];
        for (i = 65; i <= 90; ++i) {
            Base64InputStream.decodingTable[i] = (byte)(i - 65);
        }
        for (i = 97; i <= 122; ++i) {
            Base64InputStream.decodingTable[i] = (byte)(i - 97 + 26);
        }
        for (i = 48; i <= 57; ++i) {
            Base64InputStream.decodingTable[i] = (byte)(i - 48 + 52);
        }
        Base64InputStream.decodingTable[43] = 62;
        Base64InputStream.decodingTable[47] = 63;
    }
}

