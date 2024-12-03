/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
    public static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = IOUtils.copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int)count;
    }

    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += (long)n;
        }
        return count;
    }

    public static int read(InputStream input, byte[] buffer, int offset, int length) throws IOException {
        int location;
        int remaining;
        int count;
        if (length < 0) {
            throw new IllegalArgumentException("Length must not be negative: " + length);
        }
        for (remaining = length; remaining > 0 && -1 != (count = input.read(buffer, offset + (location = length - remaining), remaining)); remaining -= count) {
        }
        return length - remaining;
    }

    public static void readFully(InputStream input, byte[] buffer, int offset, int length) throws IOException {
        int actual = IOUtils.read(input, buffer, offset, length);
        if (actual != length) {
            throw new EOFException("Length to read: " + length + " actual: " + actual);
        }
    }

    public static void readFully(InputStream input, byte[] buffer) throws IOException {
        IOUtils.readFully(input, buffer, 0, buffer.length);
    }
}

