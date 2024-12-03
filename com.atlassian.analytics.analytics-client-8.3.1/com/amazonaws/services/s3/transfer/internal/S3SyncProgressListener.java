/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer.internal;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.SyncProgressListener;
import com.amazonaws.services.s3.transfer.internal.S3ProgressListener;

public abstract class S3SyncProgressListener
extends SyncProgressListener
implements S3ProgressListener {
    @Override
    public void progressChanged(ProgressEvent progressEvent) {
    }
}

