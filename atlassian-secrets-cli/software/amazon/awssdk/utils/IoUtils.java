/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public final class IoUtils {
    private static final int BUFFER_SIZE = 4096;
    private static final Logger DEFAULT_LOG = LoggerFactory.getLogger(IoUtils.class);

    private IoUtils() {
    }

    public static byte[] toByteArray(InputStream is) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();){
            byte[] b = new byte[4096];
            int n = 0;
            while ((n = is.read(b)) != -1) {
                output.write(b, 0, n);
            }
            byte[] byArray = output.toByteArray();
            return byArray;
        }
    }

    public static String toUtf8String(InputStream is) throws IOException {
        return new String(IoUtils.toByteArray(is), StandardCharsets.UTF_8);
    }

    public static void closeQuietly(AutoCloseable is, Logger log) {
        block3: {
            if (is != null) {
                try {
                    is.close();
                }
                catch (Exception ex) {
                    Logger logger;
                    Logger logger2 = logger = log == null ? DEFAULT_LOG : log;
                    if (!logger.isDebugEnabled()) break block3;
                    logger.debug("Ignore failure in closing the Closeable", ex);
                }
            }
        }
    }

    public static void closeIfCloseable(Object maybeCloseable, Logger log) {
        if (maybeCloseable instanceof AutoCloseable) {
            IoUtils.closeQuietly((AutoCloseable)maybeCloseable, log);
        }
    }

    public static long copy(InputStream in, OutputStream out) throws IOException {
        return IoUtils.copy(in, out, Long.MAX_VALUE);
    }

    public static long copy(InputStream in, OutputStream out, long readLimit) throws IOException {
        byte[] buf = new byte[4096];
        long count = 0L;
        int n = 0;
        while ((n = in.read(buf)) > -1) {
            out.write(buf, 0, n);
            if ((count += (long)n) < readLimit) continue;
            throw new IOException("Read limit exceeded: " + readLimit);
        }
        return count;
    }

    public static void drainInputStream(InputStream in) {
        try {
            while (in.read() != -1) {
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void markStreamWithMaxReadLimit(InputStream s) {
        if (s.markSupported()) {
            s.mark(131072);
        }
    }

    public static void markStreamWithMaxReadLimit(InputStream s, Integer maxReadLimit) {
        Validate.isPositiveOrNull(maxReadLimit, "maxReadLimit");
        if (s.markSupported()) {
            int maxLimit = maxReadLimit == null ? 131072 : maxReadLimit;
            s.mark(maxLimit);
        }
    }
}

