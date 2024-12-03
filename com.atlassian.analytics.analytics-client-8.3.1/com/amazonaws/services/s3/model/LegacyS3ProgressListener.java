/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.event.DeliveryMode;
import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;

@Deprecated
public class LegacyS3ProgressListener
implements com.amazonaws.event.ProgressListener,
DeliveryMode {
    private final ProgressListener listener;
    private final boolean syncCallSafe;

    public LegacyS3ProgressListener(ProgressListener listener) {
        this.listener = listener;
        if (listener instanceof DeliveryMode) {
            DeliveryMode mode = (DeliveryMode)((Object)listener);
            this.syncCallSafe = mode.isSyncCallSafe();
        } else {
            this.syncCallSafe = false;
        }
    }

    public ProgressListener unwrap() {
        return this.listener;
    }

    @Override
    public void progressChanged(com.amazonaws.event.ProgressEvent progressEvent) {
        if (this.listener == null) {
            return;
        }
        this.listener.progressChanged(this.adaptToLegacyEvent(progressEvent));
    }

    private ProgressEvent adaptToLegacyEvent(com.amazonaws.event.ProgressEvent event) {
        long bytes = event.getBytesTransferred();
        if (bytes != 0L) {
            return new ProgressEvent(bytes);
        }
        return new ProgressEvent(event.getEventType());
    }

    @Override
    public boolean isSyncCallSafe() {
        return this.syncCallSafe;
    }
}

