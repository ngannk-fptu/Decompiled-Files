/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.transfer.PauseResult;
import com.amazonaws.services.s3.transfer.PersistableUpload;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.exception.PauseException;
import com.amazonaws.services.s3.transfer.model.UploadResult;

public interface Upload
extends Transfer {
    public UploadResult waitForUploadResult() throws AmazonClientException, AmazonServiceException, InterruptedException;

    public PersistableUpload pause() throws PauseException;

    public PauseResult<PersistableUpload> tryPause(boolean var1);

    public void abort();
}

