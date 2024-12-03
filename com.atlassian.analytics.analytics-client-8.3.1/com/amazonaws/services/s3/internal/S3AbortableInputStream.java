/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.client.methods.HttpRequestBase
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.annotation.SdkTestInternalApi;
import com.amazonaws.internal.SdkFilterInputStream;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.Throwables;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpRequestBase;

@SdkInternalApi
public final class S3AbortableInputStream
extends SdkFilterInputStream {
    private static final Log LOG = LogFactory.getLog(S3AbortableInputStream.class);
    private final HttpRequestBase httpRequest;
    private final long contentLength;
    private long bytesRead;
    private long markedBytes;
    private boolean eofReached = false;

    public S3AbortableInputStream(InputStream in, HttpRequestBase httpRequest, long contentLength) {
        super(in);
        this.httpRequest = httpRequest;
        this.contentLength = contentLength;
    }

    @Override
    public void abort() {
        super.abort();
        if (this.httpRequest != null) {
            this.httpRequest.abort();
        }
        IOUtils.closeQuietly(this.in, null);
    }

    @Override
    public int available() throws IOException {
        int estimate = super.available();
        return estimate == 0 ? 1 : estimate;
    }

    @Override
    public int read() throws IOException {
        try {
            int value = super.read();
            boolean bl = this.eofReached = value == -1;
            if (!this.eofReached) {
                ++this.bytesRead;
            }
            return value;
        }
        catch (Exception e) {
            return this.onException(e);
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        try {
            int value = super.read(b, off, len);
            boolean bl = this.eofReached = value == -1;
            if (!this.eofReached) {
                this.bytesRead += (long)value;
            }
            return value;
        }
        catch (Exception e) {
            return this.onException(e);
        }
    }

    @Override
    public synchronized void mark(int readlimit) {
        super.mark(readlimit);
        this.markedBytes = this.bytesRead;
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();
        this.bytesRead = this.markedBytes;
        this.eofReached = false;
    }

    @Override
    public synchronized long skip(long n) throws IOException {
        try {
            long skipped = super.skip(n);
            if (skipped > 0L) {
                this.bytesRead += skipped;
            }
            return skipped;
        }
        catch (Exception e) {
            return this.onException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (this._readAllBytes() || this.isAborted()) {
            super.close();
        } else {
            LOG.warn((Object)"Not all bytes were read from the S3ObjectInputStream, aborting HTTP connection. This is likely an error and may result in sub-optimal behavior. Request only the bytes you need via a ranged GET or drain the input stream after use.");
            if (this.httpRequest != null) {
                this.httpRequest.abort();
            }
            IOUtils.closeQuietly(this.in, null);
        }
    }

    @SdkTestInternalApi
    long getBytesRead() {
        return this.bytesRead;
    }

    @SdkTestInternalApi
    boolean isEofReached() {
        return this.eofReached;
    }

    private int onException(Exception e) throws IOException {
        this.eofReached = true;
        if (e instanceof IOException) {
            throw (IOException)e;
        }
        if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
        }
        throw Throwables.failure(e);
    }

    private boolean _readAllBytes() {
        return this.bytesRead >= this.contentLength || this.eofReached;
    }
}

