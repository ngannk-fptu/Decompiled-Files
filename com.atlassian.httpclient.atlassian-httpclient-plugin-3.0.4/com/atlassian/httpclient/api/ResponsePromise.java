/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.Promise
 */
package com.atlassian.httpclient.api;

import com.atlassian.httpclient.api.Response;
import com.atlassian.httpclient.api.ResponseTransformation;
import io.atlassian.util.concurrent.Promise;

public interface ResponsePromise
extends Promise<Response> {
    public <T> Promise<T> transform(ResponseTransformation<T> var1);
}

