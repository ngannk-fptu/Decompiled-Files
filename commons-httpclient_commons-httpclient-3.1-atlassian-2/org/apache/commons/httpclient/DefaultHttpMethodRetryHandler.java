/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.NoHttpResponseException;

public class DefaultHttpMethodRetryHandler
implements HttpMethodRetryHandler {
    private static Class SSL_HANDSHAKE_EXCEPTION = null;
    private int retryCount;
    private boolean requestSentRetryEnabled;

    public DefaultHttpMethodRetryHandler(int retryCount, boolean requestSentRetryEnabled) {
        this.retryCount = retryCount;
        this.requestSentRetryEnabled = requestSentRetryEnabled;
    }

    public DefaultHttpMethodRetryHandler() {
        this(3, false);
    }

    @Override
    public boolean retryMethod(HttpMethod method, IOException exception, int executionCount) {
        if (method == null) {
            throw new IllegalArgumentException("HTTP method may not be null");
        }
        if (exception == null) {
            throw new IllegalArgumentException("Exception parameter may not be null");
        }
        if (method instanceof HttpMethodBase && ((HttpMethodBase)method).isAborted()) {
            return false;
        }
        if (executionCount > this.retryCount) {
            return false;
        }
        if (exception instanceof NoHttpResponseException) {
            return true;
        }
        if (exception instanceof InterruptedIOException) {
            return false;
        }
        if (exception instanceof UnknownHostException) {
            return false;
        }
        if (exception instanceof NoRouteToHostException) {
            return false;
        }
        if (SSL_HANDSHAKE_EXCEPTION != null && SSL_HANDSHAKE_EXCEPTION.isInstance(exception)) {
            return false;
        }
        return !method.isRequestSent() || this.requestSentRetryEnabled;
    }

    public boolean isRequestSentRetryEnabled() {
        return this.requestSentRetryEnabled;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    static {
        try {
            SSL_HANDSHAKE_EXCEPTION = Class.forName("javax.net.ssl.SSLHandshakeException");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }
}

