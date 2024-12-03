/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.io;

import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    public static long skip(InputStream input, long toSkip, byte[] buffer) throws IOException {
        long remain;
        long n;
        if (toSkip < 0L) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        for (remain = toSkip; remain > 0L && (n = (long)input.read(buffer, 0, (int)Math.min(remain, (long)buffer.length))) >= 0L; remain -= n) {
        }
        return toSkip - remain;
    }
}

