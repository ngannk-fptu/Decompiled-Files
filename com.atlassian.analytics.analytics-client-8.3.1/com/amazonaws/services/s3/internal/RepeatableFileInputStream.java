/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.internal.SdkInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Deprecated
public class RepeatableFileInputStream
extends SdkInputStream {
    private static final Log log = LogFactory.getLog(RepeatableFileInputStream.class);
    private final File file;
    private FileInputStream fis = null;
    private long bytesReadPastMarkPoint = 0L;
    private long markPoint = 0L;

    public RepeatableFileInputStream(File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        this.fis = new FileInputStream(file);
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    @Override
    public void reset() throws IOException {
        this.fis.close();
        this.abortIfNeeded();
        this.fis = new FileInputStream(this.file);
        long skipped = 0L;
        for (long toSkip = this.markPoint; toSkip > 0L; toSkip -= skipped) {
            skipped = this.fis.skip(toSkip);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Reset to mark point " + this.markPoint + " after returning " + this.bytesReadPastMarkPoint + " bytes"));
        }
        this.bytesReadPastMarkPoint = 0L;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readlimit) {
        this.abortIfNeeded();
        this.markPoint += this.bytesReadPastMarkPoint;
        this.bytesReadPastMarkPoint = 0L;
        if (log.isDebugEnabled()) {
            log.debug((Object)("Input stream marked at " + this.markPoint + " bytes"));
        }
    }

    @Override
    public int available() throws IOException {
        this.abortIfNeeded();
        return this.fis.available();
    }

    @Override
    public void close() throws IOException {
        this.fis.close();
        this.abortIfNeeded();
    }

    @Override
    public int read() throws IOException {
        this.abortIfNeeded();
        int byteRead = this.fis.read();
        if (byteRead != -1) {
            ++this.bytesReadPastMarkPoint;
            return byteRead;
        }
        return -1;
    }

    @Override
    public long skip(long n) throws IOException {
        this.abortIfNeeded();
        long skipped = this.fis.skip(n);
        this.bytesReadPastMarkPoint += skipped;
        return skipped;
    }

    @Override
    public int read(byte[] arg0, int arg1, int arg2) throws IOException {
        this.abortIfNeeded();
        int count = this.fis.read(arg0, arg1, arg2);
        this.bytesReadPastMarkPoint += (long)count;
        return count;
    }

    @Override
    public InputStream getWrappedInputStream() {
        return this.fis;
    }
}

