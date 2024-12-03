/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.internal.HeaderHandler;
import com.amazonaws.services.s3.internal.ServerSideEncryptionResult;

public class ServerSideEncryptionHeaderHandler<T extends ServerSideEncryptionResult>
implements HeaderHandler<T> {
    @Override
    public void handle(T result, HttpResponse response) {
        result.setSSEAlgorithm(response.getHeaders().get("x-amz-server-side-encryption"));
        result.setSSECustomerAlgorithm(response.getHeaders().get("x-amz-server-side-encryption-customer-algorithm"));
        result.setSSECustomerKeyMd5(response.getHeaders().get("x-amz-server-side-encryption-customer-key-MD5"));
        String bucketKeyEnabled = response.getHeaders().get("x-amz-server-side-encryption-bucket-key-enabled");
        if (bucketKeyEnabled != null) {
            result.setBucketKeyEnabled("true".equals(bucketKeyEnabled));
        }
    }
}

