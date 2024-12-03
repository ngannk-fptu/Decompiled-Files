/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.AbortableTransfer;
import com.amazonaws.services.s3.transfer.PersistableDownload;
import com.amazonaws.services.s3.transfer.exception.PauseException;

public interface Download
extends AbortableTransfer {
    public ObjectMetadata getObjectMetadata();

    public String getBucketName();

    public String getKey();

    public PersistableDownload pause() throws PauseException;
}

