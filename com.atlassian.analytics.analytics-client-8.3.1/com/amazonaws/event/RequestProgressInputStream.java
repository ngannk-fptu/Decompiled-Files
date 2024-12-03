/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.event;

import com.amazonaws.event.ProgressInputStream;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.event.SDKProgressPublisher;
import java.io.InputStream;

class RequestProgressInputStream
extends ProgressInputStream {
    RequestProgressInputStream(InputStream is, ProgressListener listener) {
        super(is, listener);
    }

    @Override
    protected void onReset() {
        SDKProgressPublisher.publishRequestReset(this.getListener(), this.getNotifiedByteCount());
    }

    @Override
    protected void onEOF() {
        this.onNotifyBytesRead();
    }

    @Override
    protected void onNotifyBytesRead() {
        SDKProgressPublisher.publishRequestBytesTransferred(this.getListener(), this.getUnnotifiedByteCount());
    }
}

