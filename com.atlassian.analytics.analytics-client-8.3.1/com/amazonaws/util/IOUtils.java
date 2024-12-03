/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.util;

import com.amazonaws.internal.Releasable;
import com.amazonaws.util.StringUtils;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum IOUtils {

    private static final int BUFFER_SIZE = 4096;
    private static final Log defaultLog = LogFactory.getLog(IOUtils.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
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

    public static String toString(InputStream is) throws IOException {
        return new String(IOUtils.toByteArray(is), StringUtils.UTF8);
    }

    public static void closeQuietly(Closeable is, Log log) {
        block3: {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException ex) {
                    Log logger;
                    Log log2 = logger = log == null ? defaultLog : log;
                    if (!logger.isDebugEnabled()) break block3;
                    logger.debug((Object)"Ignore failure in closing the Closeable", (Throwable)ex);
                }
            }
        }
    }

    public static void release(Closeable is, Log log) {
        IOUtils.closeQuietly(is, log);
        if (is instanceof Releasable) {
            Releasable r = (Releasable)((Object)is);
            r.release();
        }
    }

    public static long copy(InputStream in, OutputStream out) throws IOException {
        return IOUtils.copy(in, out, Long.MAX_VALUE);
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
}

