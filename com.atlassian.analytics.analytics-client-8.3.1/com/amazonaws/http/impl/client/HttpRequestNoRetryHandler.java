/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.impl.client.DefaultHttpRequestRetryHandler
 *  org.apache.http.protocol.HttpContext
 */
package com.amazonaws.http.impl.client;

import com.amazonaws.annotation.ThreadSafe;
import java.io.IOException;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

@ThreadSafe
public class HttpRequestNoRetryHandler
extends DefaultHttpRequestRetryHandler {
    public static final HttpRequestNoRetryHandler Singleton = new HttpRequestNoRetryHandler();

    private HttpRequestNoRetryHandler() {
    }

    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        return false;
    }
}

