/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.methods.Configurable
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.client.protocol.HttpClientContext
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.nio.client.CloseableHttpAsyncClient
 *  org.apache.http.impl.nio.client.HttpAsyncClients
 *  org.apache.http.nio.client.HttpAsyncClient
 *  org.apache.http.protocol.HttpContext
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
 */
package org.springframework.http.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequest;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.Assert;

@Deprecated
public class HttpComponentsAsyncClientHttpRequestFactory
extends HttpComponentsClientHttpRequestFactory
implements AsyncClientHttpRequestFactory,
InitializingBean {
    private HttpAsyncClient asyncClient;

    public HttpComponentsAsyncClientHttpRequestFactory() {
        this.asyncClient = HttpAsyncClients.createSystem();
    }

    public HttpComponentsAsyncClientHttpRequestFactory(HttpAsyncClient asyncClient) {
        this.asyncClient = asyncClient;
    }

    public HttpComponentsAsyncClientHttpRequestFactory(CloseableHttpAsyncClient asyncClient) {
        this.asyncClient = asyncClient;
    }

    public HttpComponentsAsyncClientHttpRequestFactory(HttpClient httpClient, HttpAsyncClient asyncClient) {
        super(httpClient);
        this.asyncClient = asyncClient;
    }

    public HttpComponentsAsyncClientHttpRequestFactory(CloseableHttpClient httpClient, CloseableHttpAsyncClient asyncClient) {
        super((HttpClient)httpClient);
        this.asyncClient = asyncClient;
    }

    public void setAsyncClient(HttpAsyncClient asyncClient) {
        Assert.notNull((Object)asyncClient, (String)"HttpAsyncClient must not be null");
        this.asyncClient = asyncClient;
    }

    public HttpAsyncClient getAsyncClient() {
        return this.asyncClient;
    }

    @Deprecated
    public void setHttpAsyncClient(CloseableHttpAsyncClient asyncClient) {
        this.asyncClient = asyncClient;
    }

    @Deprecated
    public CloseableHttpAsyncClient getHttpAsyncClient() {
        Assert.state((boolean)(this.asyncClient instanceof CloseableHttpAsyncClient), (String)"No CloseableHttpAsyncClient - use getAsyncClient() instead");
        return (CloseableHttpAsyncClient)this.asyncClient;
    }

    public void afterPropertiesSet() {
        this.startAsyncClient();
    }

    private HttpAsyncClient startAsyncClient() {
        CloseableHttpAsyncClient closeableAsyncClient;
        HttpAsyncClient client = this.getAsyncClient();
        if (client instanceof CloseableHttpAsyncClient && !(closeableAsyncClient = (CloseableHttpAsyncClient)client).isRunning()) {
            closeableAsyncClient.start();
        }
        return client;
    }

    @Override
    public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) throws IOException {
        HttpAsyncClient client = this.startAsyncClient();
        HttpUriRequest httpRequest = this.createHttpUriRequest(httpMethod, uri);
        this.postProcessHttpRequest(httpRequest);
        HttpContext context = this.createHttpContext(httpMethod, uri);
        if (context == null) {
            context = HttpClientContext.create();
        }
        if (context.getAttribute("http.request-config") == null) {
            RequestConfig config = null;
            if (httpRequest instanceof Configurable) {
                config = ((Configurable)httpRequest).getConfig();
            }
            if (config == null) {
                config = this.createRequestConfig(client);
            }
            if (config != null) {
                context.setAttribute("http.request-config", (Object)config);
            }
        }
        return new HttpComponentsAsyncClientHttpRequest(client, httpRequest, context);
    }

    @Override
    public void destroy() throws Exception {
        try {
            super.destroy();
        }
        finally {
            HttpAsyncClient asyncClient = this.getAsyncClient();
            if (asyncClient instanceof Closeable) {
                ((Closeable)asyncClient).close();
            }
        }
    }
}

