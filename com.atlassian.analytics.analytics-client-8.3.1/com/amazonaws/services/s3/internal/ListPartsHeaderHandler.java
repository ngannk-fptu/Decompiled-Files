/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.internal.HeaderHandler;
import com.amazonaws.services.s3.internal.ServiceUtils;
import com.amazonaws.services.s3.model.PartListing;

public class ListPartsHeaderHandler
implements HeaderHandler<PartListing> {
    @Override
    public void handle(PartListing result, HttpResponse response) {
        result.setAbortDate(ServiceUtils.parseRfc822Date(response.getHeaders().get("x-amz-abort-date")));
        result.setAbortRuleId(response.getHeaders().get("x-amz-abort-rule-id"));
    }
}

