/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class StreamUtils {
    public static final int DEFAULT_CHUNK_SIZE = 1024;

    public static long copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        return StreamUtils.copy(inputStream, outputStream, 1024);
    }

    public static long copy(InputStream inputStream, OutputStream outputStream, int bufferSize) throws IOException {
        int n;
        byte[] buffer = new byte[bufferSize];
        long count = 0L;
        while (-1 != (n = inputStream.read(buffer))) {
            outputStream.write(buffer, 0, n);
            count += (long)n;
        }
        return count;
    }

    public static long copy(Reader reader, Writer writer) throws IOException {
        return StreamUtils.copy(reader, writer, 1024);
    }

    public static long copy(Reader reader, Writer writer, int bufferSize) throws IOException {
        int n;
        char[] buffer = new char[bufferSize];
        long count = 0L;
        while (-1 != (n = reader.read(buffer))) {
            writer.write(buffer, 0, n);
            count += (long)n;
        }
        return count;
    }

    private StreamUtils() {
    }
}

