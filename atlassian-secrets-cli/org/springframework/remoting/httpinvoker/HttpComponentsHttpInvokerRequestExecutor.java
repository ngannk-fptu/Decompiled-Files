/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.config.RequestConfig$Builder
 *  org.apache.http.client.methods.Configurable
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.conn.HttpClientConnectionManager
 *  org.apache.http.conn.socket.PlainConnectionSocketFactory
 *  org.apache.http.conn.ssl.SSLConnectionSocketFactory
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.http.impl.conn.PoolingHttpClientConnectionManager
 */
package org.springframework.remoting.httpinvoker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.remoting.httpinvoker.AbstractHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.util.Assert;

public class HttpComponentsHttpInvokerRequestExecutor
extends AbstractHttpInvokerRequestExecutor {
    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;
    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;
    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = 60000;
    private HttpClient httpClient;
    @Nullable
    private RequestConfig requestConfig;

    public HttpComponentsHttpInvokerRequestExecutor() {
        this(HttpComponentsHttpInvokerRequestExecutor.createDefaultHttpClient(), RequestConfig.custom().setSocketTimeout(60000).build());
    }

    public HttpComponentsHttpInvokerRequestExecutor(HttpClient httpClient) {
        this(httpClient, null);
    }

    private HttpComponentsHttpInvokerRequestExecutor(HttpClient httpClient, @Nullable RequestConfig requestConfig) {
        this.httpClient = httpClient;
        this.requestConfig = requestConfig;
    }

    private static HttpClient createDefaultHttpClient() {
        Registry<PlainConnectionSocketFactory> schemeRegistry = RegistryBuilder.create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", (PlainConnectionSocketFactory)SSLConnectionSocketFactory.getSocketFactory()).build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(schemeRegistry);
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(5);
        return HttpClientBuilder.create().setConnectionManager((HttpClientConnectionManager)connectionManager).build();
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public void setConnectTimeout(int timeout) {
        Assert.isTrue(timeout >= 0, "Timeout must be a non-negative value");
        this.requestConfig = this.cloneRequestConfig().setConnectTimeout(timeout).build();
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.requestConfig = this.cloneRequestConfig().setConnectionRequestTimeout(connectionRequestTimeout).build();
    }

    public void setReadTimeout(int timeout) {
        Assert.isTrue(timeout >= 0, "Timeout must be a non-negative value");
        this.requestConfig = this.cloneRequestConfig().setSocketTimeout(timeout).build();
    }

    private RequestConfig.Builder cloneRequestConfig() {
        return this.requestConfig != null ? RequestConfig.copy((RequestConfig)this.requestConfig) : RequestConfig.custom();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected RemoteInvocationResult doExecuteRequest(HttpInvokerClientConfiguration config, ByteArrayOutputStream baos) throws IOException, ClassNotFoundException {
        HttpPost postMethod = this.createHttpPost(config);
        this.setRequestBody(config, postMethod, baos);
        try {
            HttpResponse response = this.executeHttpPost(config, this.getHttpClient(), postMethod);
            this.validateResponse(config, response);
            InputStream responseBody = this.getResponseBody(config, response);
            RemoteInvocationResult remoteInvocationResult = this.readRemoteInvocationResult(responseBody, config.getCodebaseUrl());
            return remoteInvocationResult;
        }
        finally {
            postMethod.releaseConnection();
        }
    }

    protected HttpPost createHttpPost(HttpInvokerClientConfiguration config) throws IOException {
        Locale locale;
        LocaleContext localeContext;
        HttpPost httpPost = new HttpPost(config.getServiceUrl());
        RequestConfig requestConfig = this.createRequestConfig(config);
        if (requestConfig != null) {
            httpPost.setConfig(requestConfig);
        }
        if ((localeContext = LocaleContextHolder.getLocaleContext()) != null && (locale = localeContext.getLocale()) != null) {
            httpPost.addHeader("Accept-Language", locale.toLanguageTag());
        }
        if (this.isAcceptGzipEncoding()) {
            httpPost.addHeader("Accept-Encoding", "gzip");
        }
        return httpPost;
    }

    @Nullable
    protected RequestConfig createRequestConfig(HttpInvokerClientConfiguration config) {
        HttpClient client = this.getHttpClient();
        if (client instanceof Configurable) {
            RequestConfig clientRequestConfig = ((Configurable)client).getConfig();
            return this.mergeRequestConfig(clientRequestConfig);
        }
        return this.requestConfig;
    }

    private RequestConfig mergeRequestConfig(RequestConfig defaultRequestConfig) {
        int socketTimeout;
        int connectionRequestTimeout;
        if (this.requestConfig == null) {
            return defaultRequestConfig;
        }
        RequestConfig.Builder builder = RequestConfig.copy((RequestConfig)defaultRequestConfig);
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

    protected void setRequestBody(HttpInvokerClientConfiguration config, HttpPost httpPost, ByteArrayOutputStream baos) throws IOException {
        ByteArrayEntity entity = new ByteArrayEntity(baos.toByteArray());
        entity.setContentType(this.getContentType());
        httpPost.setEntity((HttpEntity)entity);
    }

    protected HttpResponse executeHttpPost(HttpInvokerClientConfiguration config, HttpClient httpClient, HttpPost httpPost) throws IOException {
        return httpClient.execute((HttpUriRequest)httpPost);
    }

    protected void validateResponse(HttpInvokerClientConfiguration config, HttpResponse response) throws IOException {
        StatusLine status = response.getStatusLine();
        if (status.getStatusCode() >= 300) {
            throw new NoHttpResponseException("Did not receive successful HTTP response: status code = " + status.getStatusCode() + ", status message = [" + status.getReasonPhrase() + "]");
        }
    }

    protected InputStream getResponseBody(HttpInvokerClientConfiguration config, HttpResponse httpResponse) throws IOException {
        if (this.isGzipResponse(httpResponse)) {
            return new GZIPInputStream(httpResponse.getEntity().getContent());
        }
        return httpResponse.getEntity().getContent();
    }

    protected boolean isGzipResponse(HttpResponse httpResponse) {
        Header encodingHeader = httpResponse.getFirstHeader("Content-Encoding");
        return encodingHeader != null && encodingHeader.getValue() != null && encodingHeader.getValue().toLowerCase().contains("gzip");
    }
}

