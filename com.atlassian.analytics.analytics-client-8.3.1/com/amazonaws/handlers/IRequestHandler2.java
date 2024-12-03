/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.handlers;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.handlers.HandlerAfterAttemptContext;
import com.amazonaws.handlers.HandlerBeforeAttemptContext;
import com.amazonaws.http.HttpResponse;

@SdkInternalApi
public interface IRequestHandler2 {
    public AmazonWebServiceRequest beforeExecution(AmazonWebServiceRequest var1);

    public AmazonWebServiceRequest beforeMarshalling(AmazonWebServiceRequest var1);

    public void beforeRequest(Request<?> var1);

    public void beforeAttempt(HandlerBeforeAttemptContext var1);

    public HttpResponse beforeUnmarshalling(Request<?> var1, HttpResponse var2);

    public void afterAttempt(HandlerAfterAttemptContext var1);

    public void afterResponse(Request<?> var1, Response<?> var2);

    public void afterError(Request<?> var1, Response<?> var2, Exception var3);
}

