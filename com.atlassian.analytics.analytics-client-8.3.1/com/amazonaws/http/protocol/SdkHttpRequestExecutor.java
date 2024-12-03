/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpClientConnection
 *  org.apache.http.HttpException
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.conn.ManagedHttpClientConnection
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.protocol.HttpRequestExecutor
 */
package com.amazonaws.http.protocol;

import com.amazonaws.internal.SdkMetricsSocket;
import com.amazonaws.internal.SdkSSLMetricsSocket;
import com.amazonaws.util.AWSRequestMetrics;
import java.io.IOException;
import java.net.Socket;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;

public class SdkHttpRequestExecutor
extends HttpRequestExecutor {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected HttpResponse doSendRequest(HttpRequest request, HttpClientConnection conn, HttpContext context) throws IOException, HttpException {
        AWSRequestMetrics awsRequestMetrics = (AWSRequestMetrics)context.getAttribute(AWSRequestMetrics.SIMPLE_NAME);
        if (awsRequestMetrics == null) {
            return super.doSendRequest(request, conn, context);
        }
        if (conn instanceof ManagedHttpClientConnection) {
            ManagedHttpClientConnection managedConn = (ManagedHttpClientConnection)conn;
            Socket sock = managedConn.getSocket();
            if (sock instanceof SdkMetricsSocket) {
                SdkMetricsSocket sdkMetricsSocket = (SdkMetricsSocket)sock;
                sdkMetricsSocket.setMetrics(awsRequestMetrics);
            } else if (sock instanceof SdkSSLMetricsSocket) {
                SdkSSLMetricsSocket sdkSSLMetricsSocket = (SdkSSLMetricsSocket)sock;
                sdkSSLMetricsSocket.setMetrics(awsRequestMetrics);
            }
        }
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.HttpClientSendRequestTime);
        try {
            HttpResponse httpResponse = super.doSendRequest(request, conn, context);
            return httpResponse;
        }
        finally {
            awsRequestMetrics.endEvent(AWSRequestMetrics.Field.HttpClientSendRequestTime);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected HttpResponse doReceiveResponse(HttpRequest request, HttpClientConnection conn, HttpContext context) throws HttpException, IOException {
        AWSRequestMetrics awsRequestMetrics = (AWSRequestMetrics)context.getAttribute(AWSRequestMetrics.SIMPLE_NAME);
        if (awsRequestMetrics == null) {
            return super.doReceiveResponse(request, conn, context);
        }
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.HttpClientReceiveResponseTime);
        try {
            HttpResponse httpResponse = super.doReceiveResponse(request, conn, context);
            return httpResponse;
        }
        finally {
            awsRequestMetrics.endEvent(AWSRequestMetrics.Field.HttpClientReceiveResponseTime);
        }
    }
}

