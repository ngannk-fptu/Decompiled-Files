/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.axis.AxisProperties;
import org.apache.axis.utils.ByteArrayOutputStream;

public class ByteArray
extends OutputStream {
    protected static double DEFAULT_CACHE_INCREMENT = 2.5;
    protected static int DEFAULT_RESIDENT_SIZE = 0x20000000;
    protected static boolean DEFAULT_ENABLE_BACKING_STORE = true;
    protected static int WORKING_BUFFER_SIZE = 8192;
    protected ByteArrayOutputStream cache = null;
    protected int max_size = 0;
    protected File bs_handle = null;
    protected OutputStream bs_stream = null;
    protected long count = 0L;
    protected boolean enableBackingStore = DEFAULT_ENABLE_BACKING_STORE;

    public boolean isEnableBackingStore() {
        return this.enableBackingStore;
    }

    public void setEnableBackingStore(boolean enableBackingStore) {
        this.enableBackingStore = enableBackingStore;
    }

    public static boolean isDEFAULT_ENABLE_BACKING_STORE() {
        return DEFAULT_ENABLE_BACKING_STORE;
    }

    public static void setDEFAULT_ENABLE_BACKING_STORE(boolean DEFAULT_ENABLE_BACKING_STORE) {
        ByteArray.DEFAULT_ENABLE_BACKING_STORE = DEFAULT_ENABLE_BACKING_STORE;
    }

    public static int getDEFAULT_RESIDENT_SIZE() {
        return DEFAULT_RESIDENT_SIZE;
    }

    public static void setDEFAULT_RESIDENT_SIZE(int DEFAULT_RESIDENT_SIZE) {
        ByteArray.DEFAULT_RESIDENT_SIZE = DEFAULT_RESIDENT_SIZE;
    }

    public static double getDEFAULT_CACHE_INCREMENT() {
        return DEFAULT_CACHE_INCREMENT;
    }

    public static void setDEFAULT_CACHE_INCREMENT(double DEFAULT_CACHE_INCREMENT) {
        ByteArray.DEFAULT_CACHE_INCREMENT = DEFAULT_CACHE_INCREMENT;
    }

    public ByteArray() {
        this(DEFAULT_RESIDENT_SIZE);
    }

    public ByteArray(int max_resident_size) {
        this(0, max_resident_size);
    }

    public ByteArray(int probable_size, int max_resident_size) {
        if (probable_size > max_resident_size) {
            probable_size = 0;
        }
        if (probable_size < WORKING_BUFFER_SIZE) {
            probable_size = WORKING_BUFFER_SIZE;
        }
        this.cache = new ByteArrayOutputStream(probable_size);
        this.max_size = max_resident_size;
    }

    public void write(byte[] bytes) throws IOException {
        this.write(bytes, 0, bytes.length);
    }

    public void write(byte[] bytes, int start, int length) throws IOException {
        this.count += (long)length;
        if (this.cache != null) {
            this.increaseCapacity(length);
        }
        if (this.cache != null) {
            this.cache.write(bytes, start, length);
        } else if (this.bs_stream != null) {
            this.bs_stream.write(bytes, start, length);
        } else {
            throw new IOException("ByteArray does not have a backing store!");
        }
    }

    public void write(int b) throws IOException {
        ++this.count;
        if (this.cache != null) {
            this.increaseCapacity(1);
        }
        if (this.cache != null) {
            this.cache.write(b);
        } else if (this.bs_stream != null) {
            this.bs_stream.write(b);
        } else {
            throw new IOException("ByteArray does not have a backing store!");
        }
    }

    public void close() throws IOException {
        if (this.bs_stream != null) {
            this.bs_stream.close();
            this.bs_stream = null;
        }
    }

    public long size() {
        return this.count;
    }

    public void flush() throws IOException {
        if (this.bs_stream != null) {
            this.bs_stream.flush();
        }
    }

    protected void increaseCapacity(int count) throws IOException {
        if (this.cache == null) {
            return;
        }
        if (count + this.cache.size() <= this.max_size) {
            return;
        }
        if (!this.enableBackingStore) {
            throw new IOException("ByteArray can not increase capacity by " + count + " due to max size limit of " + this.max_size);
        }
        this.switchToBackingStore();
    }

    public synchronized void discardBuffer() {
        this.cache = null;
        if (this.bs_stream != null) {
            try {
                this.bs_stream.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            this.bs_stream = null;
        }
        this.discardBackingStore();
    }

    protected InputStream makeInputStream() throws IOException, FileNotFoundException {
        this.close();
        if (this.cache != null) {
            return new ByteArrayInputStream(this.cache.toByteArray());
        }
        if (this.bs_handle != null) {
            return this.createBackingStoreInputStream();
        }
        return null;
    }

    protected void finalize() {
        this.discardBuffer();
    }

    protected void switchToBackingStore() throws IOException {
        this.bs_handle = File.createTempFile("Axis", ".msg");
        this.bs_handle.createNewFile();
        this.bs_handle.deleteOnExit();
        this.bs_stream = new FileOutputStream(this.bs_handle);
        this.bs_stream.write(this.cache.toByteArray());
        this.cache = null;
    }

    public String getBackingStoreFileName() throws IOException {
        String fileName = null;
        if (this.bs_handle != null) {
            fileName = this.bs_handle.getCanonicalPath();
        }
        return fileName;
    }

    protected void discardBackingStore() {
        if (this.bs_handle != null) {
            this.bs_handle.delete();
            this.bs_handle = null;
        }
    }

    protected InputStream createBackingStoreInputStream() throws FileNotFoundException {
        try {
            return new BufferedInputStream(new FileInputStream(this.bs_handle.getCanonicalPath()));
        }
        catch (IOException e) {
            throw new FileNotFoundException(this.bs_handle.getAbsolutePath());
        }
    }

    public byte[] toByteArray() throws IOException {
        int len;
        InputStream inp = this.makeInputStream();
        byte[] buf = null;
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        buf = new byte[WORKING_BUFFER_SIZE];
        while ((len = inp.read(buf, 0, WORKING_BUFFER_SIZE)) != -1) {
            baos.write(buf, 0, len);
        }
        inp.close();
        this.discardBackingStore();
        return baos.toByteArray();
    }

    public void writeTo(OutputStream os) throws IOException {
        int len;
        InputStream inp = this.makeInputStream();
        byte[] buf = null;
        buf = new byte[WORKING_BUFFER_SIZE];
        while ((len = inp.read(buf, 0, WORKING_BUFFER_SIZE)) != -1) {
            os.write(buf, 0, len);
        }
        inp.close();
        this.discardBackingStore();
    }

    static {
        String value = AxisProperties.getProperty("axis.byteBuffer.cacheIncrement", "" + DEFAULT_CACHE_INCREMENT);
        DEFAULT_CACHE_INCREMENT = Double.parseDouble(value);
        value = AxisProperties.getProperty("axis.byteBuffer.residentMaxSize", "" + DEFAULT_RESIDENT_SIZE);
        DEFAULT_RESIDENT_SIZE = Integer.parseInt(value);
        value = AxisProperties.getProperty("axis.byteBuffer.workBufferSize", "" + WORKING_BUFFER_SIZE);
        WORKING_BUFFER_SIZE = Integer.parseInt(value);
        value = AxisProperties.getProperty("axis.byteBuffer.backing", "" + DEFAULT_ENABLE_BACKING_STORE);
        DEFAULT_ENABLE_BACKING_STORE = value.equalsIgnoreCase("true") || value.equals("1") || value.equalsIgnoreCase("yes");
    }
}

