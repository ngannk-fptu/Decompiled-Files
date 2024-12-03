/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

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
}

