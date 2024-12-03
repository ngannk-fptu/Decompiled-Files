/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LongUTFDataOutputStream
extends DataOutputStream {
    static final int MAX_UNSIGNED_SHORT = 65534;

    public LongUTFDataOutputStream(OutputStream out) {
        super(out);
    }

    public void writeShortOrInt(int value) throws IOException {
        LongUTFDataOutputStream.writeShortOrInt(this, value);
    }

    public static void writeShortOrInt(DataOutputStream dos, int value) throws IOException {
        if (value < 65534) {
            dos.writeShort(value);
        } else {
            dos.writeShort(65534);
            dos.writeInt(value);
        }
    }

    public void writeLongUTF(String str) throws IOException {
        int utfLen = LongUTFDataOutputStream.countUTF(str);
        this.writeShortOrInt(utfLen);
        byte[] bytearr = new byte[4096];
        int strlen = str.length();
        int count = 0;
        for (int i = 0; i < strlen; ++i) {
            char c;
            if (count >= bytearr.length - 3) {
                this.write(bytearr, 0, count);
                count = 0;
            }
            if ((c = str.charAt(i)) >= '\u0001' && c <= '\u007f') {
                bytearr[count++] = (byte)c;
                continue;
            }
            if (c > '\u07ff') {
                bytearr[count++] = (byte)(0xE0 | c >> 12 & 0xF);
                bytearr[count++] = (byte)(0x80 | c >> 6 & 0x3F);
            } else {
                bytearr[count++] = (byte)(0xC0 | c >> 6 & 0x1F);
            }
            bytearr[count++] = (byte)(0x80 | c & 0x3F);
        }
        this.write(bytearr, 0, count);
    }

    public static int countUTF(String str) {
        int strlen = str.length();
        int count = 0;
        for (int i = 0; i < strlen; ++i) {
            char c = str.charAt(i);
            if (c >= '\u0001' && c <= '\u007f') {
                ++count;
                continue;
            }
            if (c > '\u07ff') {
                count += 3;
                continue;
            }
            count += 2;
        }
        return count;
    }
}

