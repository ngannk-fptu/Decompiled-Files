/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.FastByteArrayOutputStream;

public class PackBits {
    public byte[] decompress(byte[] bytes, int expected) throws ImageReadException {
        int total = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = 0;
        while (total < expected) {
            byte n;
            if (i >= bytes.length) {
                throw new ImageReadException("Tiff: Unpack bits source exhausted: " + i + ", done + " + total + ", expected + " + expected);
            }
            if ((n = bytes[i++]) >= 0 && n <= 127) {
                int count = n + 1;
                total += count;
                for (int j = 0; j < count; ++j) {
                    baos.write(bytes[i++]);
                }
                continue;
            }
            if (n >= -127 && n <= -1) {
                byte b = bytes[i++];
                int count = -n + 1;
                total += count;
                for (int j = 0; j < count; ++j) {
                    baos.write(b);
                }
                continue;
            }
            if (n != -128) continue;
            throw new ImageReadException("Packbits: " + n);
        }
        return baos.toByteArray();
    }

    private int findNextDuplicate(byte[] bytes, int start) {
        if (start >= bytes.length) {
            return -1;
        }
        byte prev = bytes[start];
        for (int i = start + 1; i < bytes.length; ++i) {
            byte b = bytes[i];
            if (b == prev) {
                return i - 1;
            }
            prev = b;
        }
        return -1;
    }

    private int findRunLength(byte[] bytes, int start) {
        int i;
        byte b = bytes[start];
        for (i = start + 1; i < bytes.length && bytes[i] == b; ++i) {
        }
        return i - start;
    }

    public byte[] compress(byte[] bytes) throws IOException {
        try (FastByteArrayOutputStream baos = new FastByteArrayOutputStream(bytes.length * 2);){
            byte[] result;
            int ptr = 0;
            while (ptr < bytes.length) {
                int nextptr;
                int nextdup;
                int runlen;
                int actualLen;
                int len;
                int dup = this.findNextDuplicate(bytes, ptr);
                if (dup == ptr) {
                    len = this.findRunLength(bytes, dup);
                    actualLen = Math.min(len, 128);
                    baos.write(-(actualLen - 1));
                    baos.write(bytes[ptr]);
                    ptr += actualLen;
                    continue;
                }
                len = dup - ptr;
                if (dup > 0 && (runlen = this.findRunLength(bytes, dup)) < 3 && (nextdup = this.findNextDuplicate(bytes, nextptr = ptr + len + runlen)) != nextptr) {
                    dup = nextdup;
                    len = dup - ptr;
                }
                if (dup < 0) {
                    len = bytes.length - ptr;
                }
                actualLen = Math.min(len, 128);
                baos.write(actualLen - 1);
                for (int i = 0; i < actualLen; ++i) {
                    baos.write(bytes[ptr]);
                    ++ptr;
                }
            }
            byte[] byArray = result = baos.toByteArray();
            return byArray;
        }
    }
}

