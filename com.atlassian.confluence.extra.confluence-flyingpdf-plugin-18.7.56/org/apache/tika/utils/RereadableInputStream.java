/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;

public class RereadableInputStream
extends InputStream {
    private InputStream originalInputStream;
    private InputStream inputStream;
    private int maxBytesInMemory;
    private boolean firstPass = true;
    private boolean bufferIsInFile;
    private byte[] byteBuffer;
    private int size;
    private File storeFile;
    private OutputStream storeOutputStream;
    private boolean readToEndOfStreamOnFirstRewind = true;
    private boolean closeOriginalStreamOnClose = true;

    public RereadableInputStream(InputStream inputStream, int maxBytesInMemory, boolean readToEndOfStreamOnFirstRewind, boolean closeOriginalStreamOnClose) {
        this.inputStream = inputStream;
        this.originalInputStream = inputStream;
        this.maxBytesInMemory = maxBytesInMemory;
        this.byteBuffer = new byte[maxBytesInMemory];
        this.readToEndOfStreamOnFirstRewind = readToEndOfStreamOnFirstRewind;
        this.closeOriginalStreamOnClose = closeOriginalStreamOnClose;
    }

    @Override
    public int read() throws IOException {
        int inputByte = this.inputStream.read();
        if (this.firstPass) {
            this.saveByte(inputByte);
        }
        return inputByte;
    }

    public void rewind() throws IOException {
        if (this.firstPass && this.readToEndOfStreamOnFirstRewind) {
            while (this.read() != -1) {
            }
        }
        this.closeStream();
        if (this.storeOutputStream != null) {
            this.storeOutputStream.close();
            this.storeOutputStream = null;
        }
        this.firstPass = false;
        boolean newStreamIsInMemory = this.size < this.maxBytesInMemory;
        this.inputStream = newStreamIsInMemory ? new ByteArrayInputStream(this.byteBuffer) : new BufferedInputStream(new FileInputStream(this.storeFile));
    }

    private void closeStream() throws IOException {
        if (this.inputStream != null && (this.inputStream != this.originalInputStream || this.closeOriginalStreamOnClose)) {
            this.inputStream.close();
            this.inputStream = null;
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
    }

    public int getSize() {
        return this.size;
    }

    private void saveByte(int inputByte) throws IOException {
        if (!this.bufferIsInFile) {
            boolean switchToFile;
            boolean bl = switchToFile = this.size == this.maxBytesInMemory;
            if (switchToFile) {
                this.storeFile = Files.createTempFile("TIKA_streamstore_", ".tmp", new FileAttribute[0]).toFile();
                this.bufferIsInFile = true;
                this.storeOutputStream = new BufferedOutputStream(new FileOutputStream(this.storeFile));
                this.storeOutputStream.write(this.byteBuffer, 0, this.size);
                this.storeOutputStream.write(inputByte);
                this.byteBuffer = null;
            } else {
                this.byteBuffer[this.size] = (byte)inputByte;
            }
        } else {
            this.storeOutputStream.write(inputByte);
        }
        ++this.size;
    }
}

