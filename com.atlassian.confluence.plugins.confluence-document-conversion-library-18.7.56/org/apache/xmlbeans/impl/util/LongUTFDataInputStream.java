/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;

public class LongUTFDataInputStream
extends DataInputStream {
    public LongUTFDataInputStream(InputStream in) {
        super(in);
    }

    public int readUnsignedShortOrInt() throws IOException {
        return LongUTFDataInputStream.readUnsignedShortOrInt(this);
    }

    public static int readUnsignedShortOrInt(DataInputStream dis) throws IOException {
        int value = dis.readUnsignedShort();
        if (value == 65534) {
            value = dis.readInt();
        }
        return value;
    }

    public String readLongUTF() throws IOException {
        int utfLen = this.readUnsignedShortOrInt();
        StringBuilder sb = new StringBuilder(utfLen / 2);
        byte[] bytearr = new byte[4096];
        IOCall give = (readBuf, fillBuf, readLen) -> {
            if (readLen[0] + 1 > utfLen) {
                throw new UTFDataFormatException("malformed input: partial character at end");
            }
            if (readBuf[0] >= fillBuf[0]) {
                fillBuf[0] = Math.min(bytearr.length, utfLen - readLen[0]);
                this.readFully(bytearr, 0, fillBuf[0]);
                readBuf[0] = 0;
            }
            readLen[0] = readLen[0] + 1;
            int n = readBuf[0];
            readBuf[0] = n + 1;
            return bytearr[n];
        };
        int[] readLen2 = new int[]{0};
        int[] readBuf2 = new int[]{0};
        int[] fillBuf2 = new int[]{0};
        block5: while (readLen2[0] < utfLen) {
            int c = give.onebyte(readBuf2, fillBuf2, readLen2) & 0xFF;
            switch (c >> 4) {
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 7: {
                    sb.append((char)c);
                    continue block5;
                }
                case 12: 
                case 13: {
                    byte char2 = give.onebyte(readBuf2, fillBuf2, readLen2);
                    if ((char2 & 0xC0) != 128) {
                        throw new UTFDataFormatException("malformed input around byte " + readLen2[0]);
                    }
                    sb.append((char)((c & 0x1F) << 6 | char2 & 0x3F));
                    continue block5;
                }
                case 14: {
                    byte char2 = give.onebyte(readBuf2, fillBuf2, readLen2);
                    byte char3 = give.onebyte(readBuf2, fillBuf2, readLen2);
                    if ((char2 & 0xC0) != 128 || (char3 & 0xC0) != 128) {
                        throw new UTFDataFormatException("malformed input around byte " + (readLen2[0] - 1));
                    }
                    sb.append((char)((c & 0xF) << 12 | (char2 & 0x3F) << 6 | char3 & 0x3F));
                    continue block5;
                }
            }
            throw new UTFDataFormatException("malformed input around byte " + readLen2[0]);
        }
        return sb.toString();
    }

    private static interface IOCall {
        public byte onebyte(int[] var1, int[] var2, int[] var3) throws IOException;
    }
}

