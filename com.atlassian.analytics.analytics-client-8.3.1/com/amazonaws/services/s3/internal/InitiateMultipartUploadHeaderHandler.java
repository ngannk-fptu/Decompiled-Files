/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.internal.HeaderHandler;
import com.amazonaws.services.s3.internal.ServiceUtils;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;

public class InitiateMultipartUploadHeaderHandler
implements HeaderHandler<InitiateMultipartUploadResult> {
    @Override
    public void handle(InitiateMultipartUploadResult result, HttpResponse response) {
        result.setAbortDate(ServiceUtils.parseRfc822Date(response.getHeaders().get("x-amz-abort-date")));
        result.setAbortRuleId(response.getHeaders().get("x-amz-abort-rule-id"));
    }
}

