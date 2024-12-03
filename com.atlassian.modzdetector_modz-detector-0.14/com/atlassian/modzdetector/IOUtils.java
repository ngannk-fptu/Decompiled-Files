/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.modzdetector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;

public class IOUtils {
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    public static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOUtils.copy(input, output);
        return output.toByteArray();
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

    public static String toString(InputStream input) throws IOException {
        int n;
        InputStreamReader r = new InputStreamReader(input);
        StringWriter sw = new StringWriter();
        char[] buffer = new char[4096];
        while ((n = r.read(buffer)) != -1) {
            sw.write(buffer, 0, n);
        }
        return sw.toString();
    }
}

