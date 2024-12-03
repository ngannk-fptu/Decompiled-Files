/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer.internal;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.transfer.PersistableTransfer;

public interface S3ProgressListener
extends ProgressListener {
    public void onPersistableTransfer(PersistableTransfer var1);
}

