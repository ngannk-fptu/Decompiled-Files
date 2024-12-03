/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.output.ByteArrayOutputStream
 *  org.apache.commons.io.output.ThresholdingOutputStream
 */
package com.atlassian.core.spool;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.ThresholdingOutputStream;

public class DeferredFileOutputStream
extends ThresholdingOutputStream {
    private ByteArrayOutputStream memoryOutputStream;
    private OutputStream currentOutputStream;
    protected File outputFile;
    protected boolean closed = false;

    public DeferredFileOutputStream(int threshold, File outputFile) {
        super(threshold);
        this.outputFile = outputFile;
        this.memoryOutputStream = new ByteArrayOutputStream();
        this.currentOutputStream = this.memoryOutputStream;
    }

    protected OutputStream getStream() throws IOException {
        return this.currentOutputStream;
    }

    protected void thresholdReached() throws IOException {
        FileOutputStream fos = new FileOutputStream(this.getFile());
        this.memoryOutputStream.writeTo((OutputStream)fos);
        this.currentOutputStream = fos;
        this.memoryOutputStream = null;
    }

    public boolean isInMemory() {
        return !this.isThresholdExceeded();
    }

    public byte[] getData() {
        if (this.memoryOutputStream != null) {
            return this.memoryOutputStream.toByteArray();
        }
        return null;
    }

    public File getFile() {
        return this.outputFile;
    }

    public void close() throws IOException {
        super.close();
        this.closed = true;
    }

    public InputStream getInputStream() throws IOException {
        if (!this.closed) {
            throw new IOException("Stream not closed");
        }
        if (this.isInMemory()) {
            return new ByteArrayInputStream(this.getData());
        }
        return new FileInputStream(this.getFile());
    }

    public void writeTo(OutputStream out) throws IOException {
        IOUtils.copy((InputStream)this.getInputStream(), (OutputStream)out);
    }
}

