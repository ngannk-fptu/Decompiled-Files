/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.internal.HeaderHandler;
import com.amazonaws.services.s3.model.GetObjectTaggingResult;

public class GetObjectTaggingResponseHeaderHandler
implements HeaderHandler<GetObjectTaggingResult> {
    @Override
    public void handle(GetObjectTaggingResult result, HttpResponse response) {
        result.setVersionId(response.getHeaders().get("x-amz-version-id"));
    }
}

