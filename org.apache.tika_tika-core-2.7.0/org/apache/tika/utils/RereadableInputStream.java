/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 */
package org.apache.tika.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;

public class RereadableInputStream
extends InputStream {
    private static final int DEFAULT_MAX_BYTES_IN_MEMORY = 0x20000000;
    private final InputStream originalInputStream;
    private InputStream inputStream;
    private final int maxBytesInMemory;
    private boolean readingFromBuffer;
    private byte[] byteBuffer;
    private int bufferPointer;
    private int bufferHighWaterMark;
    private File storeFile;
    private boolean closed;
    private OutputStream storeOutputStream;
    private final boolean closeOriginalStreamOnClose;

    public RereadableInputStream(InputStream inputStream) {
        this(inputStream, 0x20000000, true);
    }

    public RereadableInputStream(InputStream inputStream, boolean closeOriginalStreamOnClose) {
        this(inputStream, 0x20000000, closeOriginalStreamOnClose);
    }

    public RereadableInputStream(InputStream inputStream, int maxBytesInMemory) {
        this(inputStream, maxBytesInMemory, true);
    }

    public RereadableInputStream(InputStream inputStream, int maxBytesInMemory, boolean closeOriginalStreamOnClose) {
        this.inputStream = inputStream;
        this.originalInputStream = inputStream;
        this.maxBytesInMemory = maxBytesInMemory;
        this.byteBuffer = new byte[maxBytesInMemory];
        this.closeOriginalStreamOnClose = closeOriginalStreamOnClose;
    }

    @Override
    public int read() throws IOException {
        if (this.closed) {
            throw new IOException("Stream is already closed");
        }
        int inputByte = this.inputStream.read();
        if (inputByte == -1 && this.inputStream != this.originalInputStream) {
            if (this.readingFromBuffer) {
                this.readingFromBuffer = false;
                this.inputStream.close();
            } else {
                this.inputStream.close();
                this.storeOutputStream = new BufferedOutputStream(new FileOutputStream(this.storeFile, true));
            }
            this.inputStream = this.originalInputStream;
            inputByte = this.inputStream.read();
        }
        if (inputByte != -1 && this.inputStream == this.originalInputStream) {
            this.saveByte(inputByte);
        }
        return inputByte;
    }

    private void saveByte(int inputByte) throws IOException {
        if (this.byteBuffer != null) {
            if (this.bufferPointer == this.maxBytesInMemory) {
                this.storeFile = Files.createTempFile("TIKA_streamstore_", ".tmp", new FileAttribute[0]).toFile();
                this.storeOutputStream = new BufferedOutputStream(new FileOutputStream(this.storeFile));
                this.storeOutputStream.write(this.byteBuffer, 0, this.bufferPointer);
                this.storeOutputStream.write(inputByte);
                this.byteBuffer = null;
            } else {
                this.byteBuffer[this.bufferPointer++] = (byte)inputByte;
            }
        } else {
            this.storeOutputStream.write(inputByte);
        }
    }

    public void rewind() throws IOException {
        if (this.closed) {
            throw new IOException("Stream is already closed");
        }
        if (this.storeOutputStream != null) {
            this.storeOutputStream.close();
            this.storeOutputStream = null;
        }
        if (this.inputStream != this.originalInputStream) {
            this.inputStream.close();
        }
        this.bufferPointer = this.bufferHighWaterMark = Math.max(this.bufferPointer, this.bufferHighWaterMark);
        if (this.bufferHighWaterMark > 0) {
            if (this.byteBuffer != null) {
                this.readingFromBuffer = true;
                this.inputStream = new UnsynchronizedByteArrayInputStream(this.byteBuffer, 0, this.bufferHighWaterMark);
            } else {
                this.inputStream = new BufferedInputStream(new FileInputStream(this.storeFile));
            }
        } else {
            this.inputStream = this.originalInputStream;
        }
    }

    private void closeStream() throws IOException {
        if (this.originalInputStream != this.inputStream) {
            this.inputStream.close();
        }
        if (this.closeOriginalStreamOnClose) {
            this.originalInputStream.close();
        }
    }

    @Override
    public void close() throws IOException {
        this.closeStream();
        if (this.storeOutputStream != null) {
            this.storeOutputStream.close();
            this.storeOutputStream = null;
        }
        super.close();
        if (this.storeFile != null) {
            this.storeFile.delete();
        }
        this.closed = true;
    }
}

