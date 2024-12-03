/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.handlers;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.handlers.HandlerAfterAttemptContext;
import com.amazonaws.handlers.HandlerBeforeAttemptContext;
import com.amazonaws.handlers.IRequestHandler2;
import com.amazonaws.handlers.RequestHandler;
import com.amazonaws.handlers.RequestHandler2Adaptor;
import com.amazonaws.http.HttpResponse;

public abstract class RequestHandler2
implements IRequestHandler2 {
    @Override
    public AmazonWebServiceRequest beforeExecution(AmazonWebServiceRequest request) {
        return request;
    }

    @Override
    public AmazonWebServiceRequest beforeMarshalling(AmazonWebServiceRequest request) {
        return request;
    }

    @Override
    public void beforeRequest(Request<?> request) {
    }

    @Override
    public void beforeAttempt(HandlerBeforeAttemptContext context) {
    }

    @Override
    public HttpResponse beforeUnmarshalling(Request<?> request, HttpResponse httpResponse) {
        return httpResponse;
    }

    @Override
    public void afterAttempt(HandlerAfterAttemptContext context) {
    }

    @Override
    public void afterResponse(Request<?> request, Response<?> response) {
    }

    @Override
    public void afterError(Request<?> request, Response<?> response, Exception e) {
    }

    public static RequestHandler2 adapt(RequestHandler old) {
        return new RequestHandler2Adaptor(old);
    }
}

