/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.config.RequestConfig$Builder
 *  org.apache.http.client.methods.Configurable
 *  org.apache.http.client.methods.HttpEntityEnclosingRequestBase
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpHead
 *  org.apache.http.client.methods.HttpOptions
 *  org.apache.http.client.methods.HttpPatch
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpPut
 *  org.apache.http.client.methods.HttpTrace
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.client.protocol.HttpClientContext
 *  org.apache.http.impl.client.HttpClients
 *  org.apache.http.protocol.HttpContext
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.http.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.function.BiFunction;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequest;
import org.springframework.http.client.HttpComponentsStreamingClientHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class HttpComponentsClientHttpRequestFactory
implements ClientHttpRequestFactory,
DisposableBean {
    private HttpClient httpClient;
    @Nullable
    private RequestConfig requestConfig;
    private boolean bufferRequestBody = true;
    @Nullable
    private BiFunction<HttpMethod, URI, HttpContext> httpContextFactory;

    public HttpComponentsClientHttpRequestFactory() {
        this.httpClient = HttpClients.createSystem();
    }

    public HttpComponentsClientHttpRequestFactory(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        Assert.notNull((Object)httpClient, (String)"HttpClient must not be null");
        this.httpClient = httpClient;
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public void setConnectTimeout(int timeout) {
        Assert.isTrue((timeout >= 0 ? 1 : 0) != 0, (String)"Timeout must be a non-negative value");
        this.requestConfig = this.requestConfigBuilder().setConnectTimeout(timeout).build();
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.requestConfig = this.requestConfigBuilder().setConnectionRequestTimeout(connectionRequestTimeout).build();
    }

    public void setReadTimeout(int timeout) {
        Assert.isTrue((timeout >= 0 ? 1 : 0) != 0, (String)"Timeout must be a non-negative value");
        this.requestConfig = this.requestConfigBuilder().setSocketTimeout(timeout).build();
    }

    public void setBufferRequestBody(boolean bufferRequestBody) {
        this.bufferRequestBody = bufferRequestBody;
    }

    public void setHttpContextFactory(BiFunction<HttpMethod, URI, HttpContext> httpContextFactory) {
        this.httpContextFactory = httpContextFactory;
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        HttpClient client = this.getHttpClient();
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
        if (this.bufferRequestBody) {
            return new HttpComponentsClientHttpRequest(client, httpRequest, context);
        }
        return new HttpComponentsStreamingClientHttpRequest(client, httpRequest, context);
    }

    private RequestConfig.Builder requestConfigBuilder() {
        return this.requestConfig != null ? RequestConfig.copy((RequestConfig)this.requestConfig) : RequestConfig.custom();
    }

    @Nullable
    protected RequestConfig createRequestConfig(Object client) {
        if (client instanceof Configurable) {
            RequestConfig clientRequestConfig = ((Configurable)client).getConfig();
            return this.mergeRequestConfig(clientRequestConfig);
        }
        return this.requestConfig;
    }

    protected RequestConfig mergeRequestConfig(RequestConfig clientConfig) {
        int socketTimeout;
        int connectionRequestTimeout;
        if (this.requestConfig == null) {
            return clientConfig;
        }
        RequestConfig.Builder builder = RequestConfig.copy((RequestConfig)clientConfig);
        int connectTimeout = this.requestConfig.getConnectTimeout();
        if (connectTimeout >= 0) {
            builder.setConnectTimeout(connectTimeout);
        }
        if ((connectionRequestTimeout = this.requestConfig.getConnectionRequestTimeout()) >= 0) {
            builder.setConnectionRequestTimeout(connectionRequestTimeout);
        }
        if ((socketTimeout = this.requestConfig.getSocketTimeout()) >= 0) {
            builder.setSocketTimeout(socketTimeout);
        }
        return builder.build();
    }

    protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
        switch (httpMethod) {
            case GET: {
                return new HttpGet(uri);
            }
            case HEAD: {
                return new HttpHead(uri);
            }
            case POST: {
                return new HttpPost(uri);
            }
            case PUT: {
                return new HttpPut(uri);
            }
            case PATCH: {
                return new HttpPatch(uri);
            }
            case DELETE: {
                return new HttpDelete(uri);
            }
            case OPTIONS: {
                return new HttpOptions(uri);
            }
            case TRACE: {
                return new HttpTrace(uri);
            }
        }
        throw new IllegalArgumentException("Invalid HTTP method: " + (Object)((Object)httpMethod));
    }

    protected void postProcessHttpRequest(HttpUriRequest request) {
    }

    @Nullable
    protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        return this.httpContextFactory != null ? this.httpContextFactory.apply(httpMethod, uri) : null;
    }

    public void destroy() throws Exception {
        HttpClient httpClient = this.getHttpClient();
        if (httpClient instanceof Closeable) {
            ((Closeable)httpClient).close();
        }
    }

    private static class HttpDelete
    extends HttpEntityEnclosingRequestBase {
        public HttpDelete(URI uri) {
            this.setURI(uri);
        }

        public String getMethod() {
            return "DELETE";
        }
    }
}

