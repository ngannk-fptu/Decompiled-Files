/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;

public class MappedRandomAccessFile {
    private MappedByteBuffer mappedByteBuffer = null;
    private FileChannel channel = null;

    public MappedRandomAccessFile(String filename, String mode) throws IOException {
        if (mode.equals("rw")) {
            this.init(new RandomAccessFile(filename, mode).getChannel(), FileChannel.MapMode.READ_WRITE);
        } else {
            this.init(new FileInputStream(filename).getChannel(), FileChannel.MapMode.READ_ONLY);
        }
    }

    private void init(FileChannel channel, FileChannel.MapMode mapMode) throws IOException {
        if (channel.size() > Integer.MAX_VALUE) {
            throw new PdfException("The PDF file is too large. Max 2GB. Size: " + channel.size());
        }
        this.channel = channel;
        this.mappedByteBuffer = channel.map(mapMode, 0L, channel.size());
        this.mappedByteBuffer.load();
    }

    public FileChannel getChannel() {
        return this.channel;
    }

    public int read() {
        try {
            byte b = this.mappedByteBuffer.get();
            int n = b & 0xFF;
            return n;
        }
        catch (BufferUnderflowException e) {
            return -1;
        }
    }

    public int read(byte[] bytes, int off, int len) {
        int limit;
        int pos = this.mappedByteBuffer.position();
        if (pos == (limit = this.mappedByteBuffer.limit())) {
            return -1;
        }
        int newlimit = pos + len - off;
        if (newlimit > limit) {
            len = limit - pos;
        }
        this.mappedByteBuffer.get(bytes, off, len);
        return len;
    }

    public long getFilePointer() {
        return this.mappedByteBuffer.position();
    }

    public void seek(long pos) {
        this.mappedByteBuffer.position((int)pos);
    }

    public long length() {
        return this.mappedByteBuffer.limit();
    }

    public void close() throws IOException {
        MappedRandomAccessFile.clean(this.mappedByteBuffer);
        this.mappedByteBuffer = null;
        if (this.channel != null) {
            this.channel.close();
        }
        this.channel = null;
    }

    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    public static boolean clean(ByteBuffer buffer) {
        if (buffer == null || !buffer.isDirect()) {
            return false;
        }
        if (MappedRandomAccessFile.cleanJava9(buffer)) {
            return true;
        }
        return MappedRandomAccessFile.cleanOldsJDK(buffer);
    }

    private static boolean cleanJava9(ByteBuffer buffer) {
        Boolean b = AccessController.doPrivileged(() -> {
            Boolean success = Boolean.FALSE;
            try {
                Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
                theUnsafeField.setAccessible(true);
                Object theUnsafe = theUnsafeField.get(null);
                Method invokeCleanerMethod = unsafeClass.getMethod("invokeCleaner", ByteBuffer.class);
                invokeCleanerMethod.invoke(theUnsafe, buffer);
                success = Boolean.TRUE;
            }
            catch (Exception exception) {
                // empty catch block
            }
            return success;
        });
        return b;
    }

    private static boolean cleanOldsJDK(ByteBuffer buffer) {
        Boolean b = AccessController.doPrivileged(() -> {
            Boolean success = Boolean.FALSE;
            try {
                Method getCleanerMethod = buffer.getClass().getMethod("cleaner", null);
                if (!getCleanerMethod.isAccessible()) {
                    getCleanerMethod.setAccessible(true);
                }
                Object cleaner = getCleanerMethod.invoke((Object)buffer, (Object[])null);
                Method clean = cleaner.getClass().getMethod("clean", null);
                clean.invoke(cleaner, (Object[])null);
                success = Boolean.TRUE;
            }
            catch (Exception exception) {
                // empty catch block
            }
            return success;
        });
        return b;
    }
}

