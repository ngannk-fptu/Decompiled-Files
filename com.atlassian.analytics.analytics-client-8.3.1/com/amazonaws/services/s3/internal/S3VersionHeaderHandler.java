/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.internal.HeaderHandler;
import com.amazonaws.services.s3.internal.S3VersionResult;

public class S3VersionHeaderHandler<T extends S3VersionResult>
implements HeaderHandler<T> {
    @Override
    public void handle(T result, HttpResponse response) {
        result.setVersionId(response.getHeaders().get("x-amz-version-id"));
    }
}

