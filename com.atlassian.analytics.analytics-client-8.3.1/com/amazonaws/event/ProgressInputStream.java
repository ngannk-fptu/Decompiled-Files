/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.event;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.annotation.NotThreadSafe;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.event.RequestProgressInputStream;
import com.amazonaws.event.ResponseProgressInputStream;
import com.amazonaws.internal.SdkFilterInputStream;
import java.io.IOException;
import java.io.InputStream;

@NotThreadSafe
public abstract class ProgressInputStream
extends SdkFilterInputStream {
    private static final int DEFAULT_NOTIFICATION_THRESHOLD = 8192;
    private final ProgressListener listener;
    private final int notifyThresHold;
    private int unnotifiedByteCount;
    private boolean hasBeenRead;
    private boolean doneEOF;
    private long notifiedByteCount;

    @Deprecated
    public static InputStream inputStreamForRequest(InputStream is, AmazonWebServiceRequest req) {
        return req == null ? is : ProgressInputStream.inputStreamForRequest(is, req.getGeneralProgressListener());
    }

    @SdkInternalApi
    public static InputStream inputStreamForRequest(InputStream is, ProgressListener progressListener) {
        return progressListener == null ? is : new RequestProgressInputStream(is, progressListener);
    }

    public static InputStream inputStreamForResponse(InputStream is, AmazonWebServiceRequest req) {
        return req == null ? is : new ResponseProgressInputStream(is, req.getGeneralProgressListener());
    }

    public static InputStream inputStreamForResponse(InputStream is, ProgressListener progressListener) {
        return progressListener == null ? is : new ResponseProgressInputStream(is, progressListener);
    }

    public ProgressInputStream(InputStream is, ProgressListener listener) {
        this(is, listener, 8192);
    }

    public ProgressInputStream(InputStream is, ProgressListener listener, int notifyThresHold) {
        super(is);
        if (is == null || listener == null) {
            throw new IllegalArgumentException();
        }
        this.notifyThresHold = notifyThresHold;
        this.listener = listener;
    }

    protected void onFirstRead() {
    }

    protected void onEOF() {
    }

    protected void onClose() {
        this.eof();
    }

    protected void onReset() {
    }

    protected void onNotifyBytesRead() {
    }

    private void onBytesRead(int bytesRead) {
        this.unnotifiedByteCount += bytesRead;
        if (this.unnotifiedByteCount >= this.notifyThresHold) {
            this.onNotifyBytesRead();
            this.notifiedByteCount += (long)this.unnotifiedByteCount;
            this.unnotifiedByteCount = 0;
        }
    }

    @Override
    public int read() throws IOException {
        int ch;
        if (!this.hasBeenRead) {
            this.onFirstRead();
            this.hasBeenRead = true;
        }
        if ((ch = super.read()) == -1) {
            this.eof();
        } else {
            this.onBytesRead(1);
        }
        return ch;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        this.onReset();
        this.unnotifiedByteCount = 0;
        this.notifiedByteCount = 0L;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesRead;
        if (!this.hasBeenRead) {
            this.onFirstRead();
            this.hasBeenRead = true;
        }
        if ((bytesRead = super.read(b, off, len)) == -1) {
            this.eof();
        } else {
            this.onBytesRead(bytesRead);
        }
        return bytesRead;
    }

    private void eof() {
        if (this.doneEOF) {
            return;
        }
        this.onEOF();
        this.unnotifiedByteCount = 0;
        this.doneEOF = true;
    }

    public final InputStream getWrappedInputStream() {
        return this.in;
    }

    protected final int getUnnotifiedByteCount() {
        return this.unnotifiedByteCount;
    }

    protected final long getNotifiedByteCount() {
        return this.notifiedByteCount;
    }

    @Override
    public void close() throws IOException {
        this.onClose();
        super.close();
    }

    public final ProgressListener getListener() {
        return this.listener;
    }
}

