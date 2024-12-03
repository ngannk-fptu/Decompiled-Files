/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.internal.HeaderHandler;
import com.amazonaws.services.s3.internal.S3RestoreOutputPathResult;

public class S3RestoreOutputPathHeaderHandler<T extends S3RestoreOutputPathResult>
implements HeaderHandler<T> {
    @Override
    public void handle(T result, HttpResponse response) {
        result.setRestoreOutputPath(response.getHeaders().get("x-amz-restore-output-path"));
    }
}

