/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer.internal;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.event.ProgressListenerChain;
import com.amazonaws.services.s3.transfer.PersistableTransfer;
import com.amazonaws.services.s3.transfer.internal.S3ProgressListener;

public class S3ProgressListenerChain
extends ProgressListenerChain
implements S3ProgressListener {
    public S3ProgressListenerChain(ProgressListener ... listeners) {
        super(listeners);
    }

    @Override
    public void onPersistableTransfer(PersistableTransfer persistableTransfer) {
        for (ProgressListener listener : this.getListeners()) {
            if (!(listener instanceof S3ProgressListener)) continue;
            S3ProgressListener s3listener = (S3ProgressListener)listener;
            s3listener.onPersistableTransfer(persistableTransfer);
        }
    }
}

