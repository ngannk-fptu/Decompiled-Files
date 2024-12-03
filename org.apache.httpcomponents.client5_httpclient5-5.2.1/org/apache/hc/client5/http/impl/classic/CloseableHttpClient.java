/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.ClassicHttpRequest
 *  org.apache.hc.core5.http.ClassicHttpResponse
 *  org.apache.hc.core5.http.HttpEntity
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.io.HttpClientResponseHandler
 *  org.apache.hc.core5.http.io.entity.EntityUtils
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.io.ModalCloseable
 *  org.apache.hc.core5.util.Args
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.classic;

import java.io.IOException;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.routing.RoutingSupport;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.io.ModalCloseable;
import org.apache.hc.core5.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.SAFE)
public abstract class CloseableHttpClient
implements HttpClient,
ModalCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(CloseableHttpClient.class);

    protected abstract CloseableHttpResponse doExecute(HttpHost var1, ClassicHttpRequest var2, HttpContext var3) throws IOException;

    private static HttpHost determineTarget(ClassicHttpRequest request) throws ClientProtocolException {
        try {
            return RoutingSupport.determineHost((HttpRequest)request);
        }
        catch (HttpException ex) {
            throw new ClientProtocolException(ex);
        }
    }

    @Deprecated
    public CloseableHttpResponse execute(HttpHost target, ClassicHttpRequest request, HttpContext context) throws IOException {
        return this.doExecute(target, request, context);
    }

    @Deprecated
    public CloseableHttpResponse execute(ClassicHttpRequest request, HttpContext context) throws IOException {
        Args.notNull((Object)request, (String)"HTTP request");
        return this.doExecute(CloseableHttpClient.determineTarget(request), request, context);
    }

    @Deprecated
    public CloseableHttpResponse execute(ClassicHttpRequest request) throws IOException {
        return this.doExecute(CloseableHttpClient.determineTarget(request), request, null);
    }

    @Override
    @Deprecated
    public CloseableHttpResponse execute(HttpHost target, ClassicHttpRequest request) throws IOException {
        return this.doExecute(target, request, null);
    }

    @Override
    public <T> T execute(ClassicHttpRequest request, HttpClientResponseHandler<? extends T> responseHandler) throws IOException {
        return this.execute(request, null, responseHandler);
    }

    @Override
    public <T> T execute(ClassicHttpRequest request, HttpContext context, HttpClientResponseHandler<? extends T> responseHandler) throws IOException {
        HttpHost target = CloseableHttpClient.determineTarget(request);
        return this.execute(target, request, context, responseHandler);
    }

    @Override
    public <T> T execute(HttpHost target, ClassicHttpRequest request, HttpClientResponseHandler<? extends T> responseHandler) throws IOException {
        return this.execute(target, request, null, responseHandler);
    }

    @Override
    public <T> T execute(HttpHost target, ClassicHttpRequest request, HttpContext context, HttpClientResponseHandler<? extends T> responseHandler) throws IOException {
        Args.notNull(responseHandler, (String)"Response handler");
        Throwable throwable = null;
        try (CloseableHttpResponse response = this.doExecute(target, request, context);){
            Object result = responseHandler.handleResponse((ClassicHttpResponse)response);
            HttpEntity entity = response.getEntity();
            EntityUtils.consume((HttpEntity)entity);
            Object object = result;
            return (T)object;
        }
        catch (HttpException t) {
            try {
                HttpEntity entity = response.getEntity();
                try {
                    EntityUtils.consume((HttpEntity)entity);
                }
                catch (Exception t2) {
                    LOG.warn("Error consuming content after an exception.", (Throwable)t2);
                }
                throw new ClientProtocolException(t);
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
        }
    }
}

