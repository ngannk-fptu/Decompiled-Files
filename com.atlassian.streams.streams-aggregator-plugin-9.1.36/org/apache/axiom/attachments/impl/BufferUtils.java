/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.attachments.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import javax.activation.DataHandler;
import org.apache.axiom.attachments.utils.BAAOutputStream;
import org.apache.axiom.ext.io.ReadFromSupport;
import org.apache.axiom.util.activation.DataSourceUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BufferUtils {
    private static final Log log = LogFactory.getLog(BufferUtils.class);
    public static final int BUFFER_LEN = 4096;
    static boolean ENABLE_FILE_CHANNEL = true;
    static boolean ENABLE_BAAOS_OPT = true;
    private static byte[] _cacheBuffer = new byte[4096];
    private static boolean _cacheBufferInUse = false;
    private static ByteBuffer _cacheByteBuffer = ByteBuffer.allocate(4096);
    private static boolean _cacheByteBufferInUse = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void inputStream2OutputStream(InputStream is, OutputStream os) throws IOException {
        if (ENABLE_FILE_CHANNEL && os instanceof FileOutputStream && BufferUtils.inputStream2FileOutputStream(is, (FileOutputStream)os)) {
            return;
        }
        if (ENABLE_BAAOS_OPT && os instanceof ReadFromSupport) {
            ((ReadFromSupport)((Object)os)).readFrom(is, Long.MAX_VALUE);
            return;
        }
        byte[] buffer = BufferUtils.getTempBuffer();
        try {
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
        finally {
            BufferUtils.releaseTempBuffer(buffer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int inputStream2OutputStream(InputStream is, OutputStream os, int limit) throws IOException {
        if (ENABLE_BAAOS_OPT && os instanceof ReadFromSupport) {
            return (int)((ReadFromSupport)((Object)os)).readFrom(is, limit);
        }
        byte[] buffer = BufferUtils.getTempBuffer();
        int totalWritten = 0;
        int bytesRead = 0;
        try {
            do {
                int len;
                if ((bytesRead = is.read(buffer, 0, len = limit - totalWritten > 4096 ? 4096 : limit - totalWritten)) <= 0) continue;
                os.write(buffer, 0, bytesRead);
                if (bytesRead <= 0) continue;
                totalWritten += bytesRead;
            } while (totalWritten < limit && (bytesRead > 0 || is.available() > 0));
            int n = totalWritten;
            return n;
        }
        finally {
            BufferUtils.releaseTempBuffer(buffer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean inputStream2FileOutputStream(InputStream is, FileOutputStream fos) throws IOException {
        FileChannel channel = null;
        FileLock lock = null;
        ByteBuffer bb = null;
        try {
            channel = fos.getChannel();
            if (channel != null) {
                lock = channel.tryLock();
            }
            bb = BufferUtils.getTempByteBuffer();
        }
        catch (Throwable t) {
            // empty catch block
        }
        if (lock == null || bb == null || !bb.hasArray()) {
            BufferUtils.releaseTempByteBuffer(bb);
            return false;
        }
        try {
            int bytesRead = is.read(bb.array());
            while (bytesRead != -1) {
                int written = 0;
                if (bytesRead < 4096) {
                    ByteBuffer temp = ByteBuffer.allocate(bytesRead);
                    temp.put(bb.array(), 0, bytesRead);
                    temp.position(0);
                    written = channel.write(temp);
                } else {
                    bb.position(0);
                    written = channel.write(bb);
                    bb.clear();
                }
                bytesRead = is.read(bb.array());
            }
        }
        finally {
            lock.release();
            BufferUtils.releaseTempByteBuffer(bb);
        }
        return true;
    }

    public static long inputStream2BAAOutputStream(InputStream is, BAAOutputStream baaos, long limit) throws IOException {
        return baaos.receive(is, limit);
    }

    public static int doesDataHandlerExceedLimit(DataHandler dh, int limit) {
        if (limit == 0) {
            return -1;
        }
        long size = DataSourceUtils.getSize(dh.getDataSource());
        if (size != -1L) {
            return size > (long)limit ? 1 : 0;
        }
        try {
            dh.writeTo((OutputStream)new SizeLimitedOutputStream(limit));
        }
        catch (SizeLimitExceededException ex) {
            return 1;
        }
        catch (IOException ex) {
            log.warn((Object)ex.getMessage());
            return -1;
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static synchronized byte[] getTempBuffer() {
        byte[] byArray = _cacheBuffer;
        synchronized (_cacheBuffer) {
            if (!_cacheBufferInUse) {
                _cacheBufferInUse = true;
                // ** MonitorExit[var0] (shouldn't be in output)
                return _cacheBuffer;
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return new byte[4096];
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void releaseTempBuffer(byte[] buffer) {
        byte[] byArray = _cacheBuffer;
        synchronized (_cacheBuffer) {
            if (buffer == _cacheBuffer) {
                _cacheBufferInUse = false;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static synchronized ByteBuffer getTempByteBuffer() {
        ByteBuffer byteBuffer = _cacheByteBuffer;
        synchronized (byteBuffer) {
            if (!_cacheByteBufferInUse) {
                _cacheByteBufferInUse = true;
                return _cacheByteBuffer;
            }
        }
        return ByteBuffer.allocate(4096);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void releaseTempByteBuffer(ByteBuffer buffer) {
        ByteBuffer byteBuffer = _cacheByteBuffer;
        synchronized (byteBuffer) {
            if (buffer == _cacheByteBuffer) {
                _cacheByteBufferInUse = false;
            }
        }
    }

    private static class SizeLimitedOutputStream
    extends OutputStream {
        private final int maxSize;
        private int size;

        public SizeLimitedOutputStream(int maxSize) {
            this.maxSize = maxSize;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            this.size += len;
            this.checkSize();
        }

        public void write(byte[] b) throws IOException {
            this.size += b.length;
            this.checkSize();
        }

        public void write(int b) throws IOException {
            ++this.size;
            this.checkSize();
        }

        private void checkSize() throws SizeLimitExceededException {
            if (this.size > this.maxSize) {
                throw new SizeLimitExceededException();
            }
        }
    }

    private static class SizeLimitExceededException
    extends IOException {
        private static final long serialVersionUID = -6644887187061182165L;

        private SizeLimitExceededException() {
        }
    }
}

