/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.stp.hercules;

import com.atlassian.troubleshooting.stp.hercules.FileProgressMonitor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FileProgressMonitorInputStream
extends FilterInputStream {
    private final long size;
    private final FileProgressMonitor monitor;
    private long nread = 0L;

    public FileProgressMonitorInputStream(File file, FileProgressMonitor monitor) throws FileNotFoundException {
        super(new FileInputStream(file));
        this.monitor = monitor;
        this.size = file.length();
        monitor.setTotalSize(this.size);
    }

    @Override
    public int read() throws IOException {
        int c = this.in.read();
        this.handleBytesRead(c == -1 ? 0L : 1L);
        return c;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int nr = this.in.read(b, off, len);
        this.handleBytesRead(nr);
        return nr;
    }

    @Override
    public long skip(long n) throws IOException {
        long nr = this.in.skip(n);
        this.handleBytesRead(nr);
        return nr;
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    @Override
    public synchronized void reset() throws IOException {
        this.in.reset();
        this.nread = this.size - (long)this.in.available();
        this.monitor.setProgress(this.nread);
    }

    private void handleBytesRead(long bytesRead) throws InterruptedIOException {
        if (bytesRead > 0L) {
            this.nread += bytesRead;
            this.monitor.setProgress(this.nread);
        }
        if (this.monitor.isCancelled()) {
            InterruptedIOException e = new InterruptedIOException("progress");
            e.bytesTransferred = (int)this.nread;
            throw e;
        }
    }
}

