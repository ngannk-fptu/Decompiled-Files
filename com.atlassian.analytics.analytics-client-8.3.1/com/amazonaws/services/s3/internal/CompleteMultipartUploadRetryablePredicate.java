/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.internal.SdkPredicate;
import com.amazonaws.services.s3.model.AmazonS3Exception;

public class CompleteMultipartUploadRetryablePredicate
extends SdkPredicate<AmazonS3Exception> {
    private static final String ERROR_CODE = "InternalError";
    private static final String RETYABLE_ERROR_MESSAGE = "Please try again.";

    @Override
    public boolean test(AmazonS3Exception exception) {
        if (exception == null || exception.getErrorCode() == null || exception.getErrorMessage() == null) {
            return false;
        }
        return exception.getErrorCode().contains(ERROR_CODE) && exception.getErrorMessage().contains(RETYABLE_ERROR_MESSAGE);
    }
}

