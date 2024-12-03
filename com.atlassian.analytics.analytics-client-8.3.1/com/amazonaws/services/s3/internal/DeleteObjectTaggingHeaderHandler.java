/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.internal.HeaderHandler;
import com.amazonaws.services.s3.model.DeleteObjectTaggingResult;

public class DeleteObjectTaggingHeaderHandler
implements HeaderHandler<DeleteObjectTaggingResult> {
    @Override
    public void handle(DeleteObjectTaggingResult result, HttpResponse response) {
        result.setVersionId(response.getHeaders().get("x-amz-version-id"));
    }
}

