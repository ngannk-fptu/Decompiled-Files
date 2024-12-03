/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.util.io.StreamOverflowException;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public final class Streams {
    private static int BUFFER_SIZE = 4096;

    public static void drain(InputStream inStr) throws IOException {
        byte[] bs = new byte[BUFFER_SIZE];
        while (inStr.read(bs, 0, bs.length) >= 0) {
        }
    }

    public static void pipeAll(InputStream inStr, OutputStream outStr) throws IOException {
        Streams.pipeAll(inStr, outStr, BUFFER_SIZE);
    }

    public static void pipeAll(InputStream inStr, OutputStream outStr, int bufferSize) throws IOException {
        int numRead;
        byte[] bs = new byte[bufferSize];
        while ((numRead = inStr.read(bs, 0, bs.length)) >= 0) {
            outStr.write(bs, 0, numRead);
        }
    }

    public static long pipeAllLimited(InputStream inStr, long limit, OutputStream outStr) throws IOException {
        int numRead;
        long total = 0L;
        byte[] bs = new byte[BUFFER_SIZE];
        while ((numRead = inStr.read(bs, 0, bs.length)) >= 0) {
            if (limit - total < (long)numRead) {
                throw new StreamOverflowException("Data Overflow");
            }
            total += (long)numRead;
            outStr.write(bs, 0, numRead);
        }
        return total;
    }

    public static byte[] readAll(InputStream inStr) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        Streams.pipeAll(inStr, buf);
        return buf.toByteArray();
    }

    public static byte[] readAllLimited(InputStream inStr, int limit) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        Streams.pipeAllLimited(inStr, limit, buf);
        return buf.toByteArray();
    }

    public static int readFully(InputStream inStr, byte[] buf) throws IOException {
        return Streams.readFully(inStr, buf, 0, buf.length);
    }

    public static int readFully(InputStream inStr, byte[] buf, int off, int len) throws IOException {
        int totalRead;
        int numRead;
        for (totalRead = 0; totalRead < len && (numRead = inStr.read(buf, off + totalRead, len - totalRead)) >= 0; totalRead += numRead) {
        }
        return totalRead;
    }

    public static void validateBufferArguments(byte[] buf, int off, int len) {
        if (buf == null) {
            throw new NullPointerException();
        }
        int available = buf.length - off;
        int remaining = available - len;
        if ((off | len | available | remaining) < 0) {
            throw new IndexOutOfBoundsException();
        }
    }

    public static void writeBufTo(ByteArrayOutputStream buf, OutputStream output) throws IOException {
        buf.writeTo(output);
    }
}

