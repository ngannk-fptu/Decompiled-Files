/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.pdfbox.io;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.logging.Log;

public final class IOUtils {
    private IOUtils() {
    }

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        IOUtils.copy(in, baout);
        return baout.toByteArray();
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += (long)n;
        }
        return count;
    }

    public static long populateBuffer(InputStream in, byte[] buffer) throws IOException {
        int bufferWritePos;
        int remaining;
        int bytesRead;
        for (remaining = buffer.length; remaining > 0 && (bytesRead = in.read(buffer, bufferWritePos = buffer.length - remaining, remaining)) >= 0; remaining -= bytesRead) {
        }
        return buffer.length - remaining;
    }

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

    public static IOException closeAndLogException(Closeable closeable, Log logger, String resourceName, IOException initialException) {
        block2: {
            try {
                closeable.close();
            }
            catch (IOException ioe) {
                logger.warn((Object)("Error closing " + resourceName), (Throwable)ioe);
                if (initialException != null) break block2;
                return ioe;
            }
        }
        return initialException;
    }
}

