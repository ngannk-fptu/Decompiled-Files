/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.handlers;

import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.handlers.RequestHandler;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.util.AWSRequestMetrics;
import com.amazonaws.util.TimingInfo;

final class RequestHandler2Adaptor
extends RequestHandler2 {
    private final RequestHandler old;

    RequestHandler2Adaptor(RequestHandler old) {
        if (old == null) {
            throw new IllegalArgumentException();
        }
        this.old = old;
    }

    @Override
    public void beforeRequest(Request<?> request) {
        this.old.beforeRequest(request);
    }

    @Override
    public void afterResponse(Request<?> request, Response<?> response) {
        AWSRequestMetrics awsRequestMetrics = request == null ? null : request.getAWSRequestMetrics();
        Object awsResponse = response == null ? null : response.getAwsResponse();
        TimingInfo timingInfo = awsRequestMetrics == null ? null : awsRequestMetrics.getTimingInfo();
        this.old.afterResponse(request, awsResponse, timingInfo);
    }

    @Override
    public void afterError(Request<?> request, Response<?> response, Exception e) {
        this.old.afterError(request, e);
    }

    public int hashCode() {
        return this.old.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof RequestHandler2Adaptor)) {
            return false;
        }
        RequestHandler2Adaptor that = (RequestHandler2Adaptor)o;
        return this.old.equals(that.old);
    }
}

