/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.http;

import com.amazonaws.http.HttpResponse;

public interface HttpResponseHandler<T> {
    public static final String X_AMZN_REQUEST_ID_HEADER = "x-amzn-RequestId";
    public static final String X_AMZN_EXTENDED_REQUEST_ID_HEADER = "x-amz-id-2";
    public static final String X_AMZ_REQUEST_ID_ALTERNATIVE_HEADER = "x-amz-request-id";

    public T handle(HttpResponse var1) throws Exception;

    public boolean needsConnectionLeftOpen();
}

