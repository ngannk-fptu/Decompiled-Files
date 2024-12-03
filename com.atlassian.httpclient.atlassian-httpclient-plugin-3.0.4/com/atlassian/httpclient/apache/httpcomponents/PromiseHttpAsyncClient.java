/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.Promise
 */
package com.atlassian.httpclient.apache.httpcomponents;

import io.atlassian.util.concurrent.Promise;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

interface PromiseHttpAsyncClient {
    public Promise<HttpResponse> execute(HttpUriRequest var1, HttpContext var2);
}

