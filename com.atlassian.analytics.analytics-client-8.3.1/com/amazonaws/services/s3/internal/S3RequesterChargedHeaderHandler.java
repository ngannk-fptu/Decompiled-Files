/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.internal.HeaderHandler;
import com.amazonaws.services.s3.internal.S3RequesterChargedResult;

public class S3RequesterChargedHeaderHandler<T extends S3RequesterChargedResult>
implements HeaderHandler<T> {
    @Override
    public void handle(T result, HttpResponse response) {
        result.setRequesterCharged(response.getHeaders().get("x-amz-request-charged") != null);
    }
}

