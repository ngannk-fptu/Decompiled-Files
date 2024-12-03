/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.attachments.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    private IOUtils() {
    }

    public static int readFully(InputStream in, byte[] b) throws IOException {
        return IOUtils.readFully(in, b, 0, b.length);
    }

    public static int readFully(InputStream in, byte[] b, int off, int len) throws IOException {
        int got;
        int total = 0;
        do {
            if ((got = in.read(b, off + total, len - total)) >= 0) continue;
            return total == 0 ? -1 : total;
        } while ((total += got) != len);
        return total;
    }

    public static byte[] getStreamAsByteArray(InputStream stream) throws IOException {
        return IOUtils.getStreamAsByteArray(stream, -1);
    }

    public static byte[] getStreamAsByteArray(InputStream stream, int length) throws IOException {
        if (length == 0) {
            return new byte[0];
        }
        boolean checkLength = true;
        if (length < 0) {
            length = Integer.MAX_VALUE;
            checkLength = false;
        }
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int nextValue = stream.read();
        if (checkLength) {
            --length;
        }
        while (-1 != nextValue && length >= 0) {
            byteStream.write(nextValue);
            nextValue = stream.read();
            if (!checkLength) continue;
            --length;
        }
        return byteStream.toByteArray();
    }
}

