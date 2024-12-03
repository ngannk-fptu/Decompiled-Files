/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.buf;

public class Asn1Writer {
    public static byte[] writeSequence(byte[] ... components) {
        int len = 0;
        for (byte[] component : components) {
            len += component.length;
        }
        byte[] combined = new byte[len];
        int pos = 0;
        for (byte[] component : components) {
            System.arraycopy(component, 0, combined, pos, component.length);
            pos += component.length;
        }
        return Asn1Writer.writeTag((byte)48, combined);
    }

    public static byte[] writeInteger(int value) {
        int valueSize = 1;
        while (value >> valueSize * 8 > 0) {
            ++valueSize;
        }
        byte[] valueBytes = new byte[valueSize];
        int i = 0;
        while (valueSize > 0) {
            valueBytes[i] = (byte)(value >> 8 * (valueSize - 1));
            value >>= 8;
            --valueSize;
            ++i;
        }
        return Asn1Writer.writeTag((byte)2, valueBytes);
    }

    public static byte[] writeOctetString(byte[] data) {
        return Asn1Writer.writeTag((byte)4, data);
    }

    public static byte[] writeTag(byte tagId, byte[] data) {
        int dataSize = data.length;
        int lengthSize = 1;
        if (dataSize > 127) {
            while (dataSize >> ++lengthSize * 8 > 0) {
            }
        }
        byte[] result = new byte[1 + lengthSize + dataSize];
        result[0] = tagId;
        if (dataSize < 128) {
            result[1] = (byte)dataSize;
        } else {
            result[1] = (byte)(127 + lengthSize);
            int i = lengthSize;
            while (dataSize > 0) {
                result[i] = (byte)(dataSize & 0xFF);
                dataSize >>= 8;
                --i;
            }
        }
        System.arraycopy(data, 0, result, 1 + lengthSize, data.length);
        return result;
    }
}

