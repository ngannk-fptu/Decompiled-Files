/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.model.CopyResult;

public interface Copy
extends Transfer {
    public CopyResult waitForCopyResult() throws AmazonClientException, AmazonServiceException, InterruptedException;
}

