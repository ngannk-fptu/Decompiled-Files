/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

@Contract(threading=ThreadingBehavior.SAFE)
public abstract class CloseableHttpClient
implements HttpClient,
Closeable {
    private final Log log = LogFactory.getLog(this.getClass());

    protected abstract CloseableHttpResponse doExecute(HttpHost var1, HttpRequest var2, HttpContext var3) throws IOException, ClientProtocolException;

    @Override
    public CloseableHttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
        return this.doExecute(target, request, context);
    }

    @Override
    public CloseableHttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
        Args.notNull(request, "HTTP request");
        return this.doExecute(CloseableHttpClient.determineTarget(request), request, context);
    }

    private static HttpHost determineTarget(HttpUriRequest request) throws ClientProtocolException {
        HttpHost target = null;
        URI requestURI = request.getURI();
        if (requestURI.isAbsolute() && (target = URIUtils.extractHost(requestURI)) == null) {
            throw new ClientProtocolException("URI does not specify a valid host name: " + requestURI);
        }
        return target;
    }

    @Override
    public CloseableHttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
        return this.execute(request, (HttpContext)null);
    }

    @Override
    public CloseableHttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
        return this.doExecute(target, request, null);
    }

    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return this.execute(request, responseHandler, null);
    }

    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        HttpHost target = CloseableHttpClient.determineTarget(request);
        return this.execute(target, request, responseHandler, context);
    }

    @Override
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return this.execute(target, request, responseHandler, null);
    }

    @Override
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        Args.notNull(responseHandler, "Response handler");
        CloseableHttpResponse response = this.execute(target, request, context);
        try {
            T result = responseHandler.handleResponse(response);
            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);
            T t = result;
            return t;
        }
        catch (ClientProtocolException t) {
            HttpEntity entity = response.getEntity();
            try {
                EntityUtils.consume(entity);
            }
            catch (Exception t2) {
                this.log.warn("Error consuming content after an exception.", t2);
            }
            throw t;
        }
        finally {
            response.close();
        }
    }
}

