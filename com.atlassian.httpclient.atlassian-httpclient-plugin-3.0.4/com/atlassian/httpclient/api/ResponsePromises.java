/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.Promise
 */
package com.atlassian.httpclient.api;

import com.atlassian.httpclient.api.Response;
import com.atlassian.httpclient.api.ResponsePromise;
import com.atlassian.httpclient.api.UnexpectedResponseException;
import com.atlassian.httpclient.api.WrappingResponsePromise;
import io.atlassian.util.concurrent.Promise;
import java.util.function.Function;

public final class ResponsePromises {
    private ResponsePromises() {
    }

    public static ResponsePromise toResponsePromise(Promise<Response> promise) {
        return new WrappingResponsePromise(promise);
    }

    public static <T> Function<Response, T> newUnexpectedResponseFunction() {
        return response -> {
            throw new UnexpectedResponseException((Response)response);
        };
    }
}

