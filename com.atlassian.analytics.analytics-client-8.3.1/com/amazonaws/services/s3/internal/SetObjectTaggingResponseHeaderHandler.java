/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.internal.HeaderHandler;
import com.amazonaws.services.s3.model.SetObjectTaggingResult;

public class SetObjectTaggingResponseHeaderHandler
implements HeaderHandler<SetObjectTaggingResult> {
    @Override
    public void handle(SetObjectTaggingResult result, HttpResponse response) {
        result.setVersionId(response.getHeaders().get("x-amz-version-id"));
    }
}

