/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpResponse
 *  org.apache.http.auth.AuthScheme
 *  org.apache.http.auth.AuthScope
 *  org.apache.http.auth.Credentials
 *  org.apache.http.auth.NTCredentials
 *  org.apache.http.client.AuthCache
 *  org.apache.http.client.CredentialsProvider
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.config.RequestConfig$Builder
 *  org.apache.http.client.methods.HttpRequestBase
 *  org.apache.http.client.protocol.HttpClientContext
 *  org.apache.http.entity.BufferedHttpEntity
 *  org.apache.http.entity.StringEntity
 *  org.apache.http.impl.auth.BasicScheme
 *  org.apache.http.impl.client.BasicAuthCache
 *  org.apache.http.impl.client.BasicCredentialsProvider
 *  org.apache.http.protocol.HttpContext
 */
package com.amazonaws.http.apache.utils;

import com.amazonaws.Request;
import com.amazonaws.SdkClientException;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.settings.HttpClientSettings;
import com.amazonaws.util.FakeIOException;
import com.amazonaws.util.ReflectionMethodInvoker;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.HttpContext;

public class ApacheUtils {
    private static final Log log = LogFactory.getLog(ApacheUtils.class);
    private static final ReflectionMethodInvoker<RequestConfig.Builder, RequestConfig.Builder> normalizeUriInvoker = new ReflectionMethodInvoker<RequestConfig.Builder, RequestConfig.Builder>(RequestConfig.Builder.class, RequestConfig.Builder.class, "setNormalizeUri", Boolean.TYPE);
    private final boolean normalizeUriMethodNotFound = false;

    public static boolean isRequestSuccessful(org.apache.http.HttpResponse response) {
        int status = response.getStatusLine().getStatusCode();
        return status / 100 == 2;
    }

    public static HttpResponse createResponse(Request<?> request, HttpRequestBase method, org.apache.http.HttpResponse apacheHttpResponse, HttpContext context) throws IOException {
        HttpResponse httpResponse = new HttpResponse(request, method, context);
        if (apacheHttpResponse.getEntity() != null) {
            httpResponse.setContent(apacheHttpResponse.getEntity().getContent());
        }
        httpResponse.setStatusCode(apacheHttpResponse.getStatusLine().getStatusCode());
        httpResponse.setStatusText(apacheHttpResponse.getStatusLine().getReasonPhrase());
        for (Header header : apacheHttpResponse.getAllHeaders()) {
            httpResponse.addHeader(header.getName(), header.getValue());
        }
        return httpResponse;
    }

    public static HttpEntity newStringEntity(String s) {
        try {
            return new StringEntity(s);
        }
        catch (UnsupportedEncodingException e) {
            throw new SdkClientException("Unable to create HTTP entity: " + e.getMessage(), e);
        }
    }

    public static HttpEntity newBufferedHttpEntity(HttpEntity entity) throws FakeIOException {
        try {
            return new BufferedHttpEntity(entity);
        }
        catch (FakeIOException e) {
            throw e;
        }
        catch (IOException e) {
            throw new SdkClientException("Unable to create HTTP entity: " + e.getMessage(), e);
        }
    }

    public static HttpClientContext newClientContext(HttpClientSettings settings, Map<String, ? extends Object> attributes) {
        HttpClientContext clientContext = new HttpClientContext();
        if (attributes != null && !attributes.isEmpty()) {
            for (Map.Entry<String, ? extends Object> entry : attributes.entrySet()) {
                clientContext.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        ApacheUtils.addPreemptiveAuthenticationProxy(clientContext, settings);
        RequestConfig.Builder builder = RequestConfig.custom();
        ApacheUtils.disableNormalizeUri(builder);
        clientContext.setRequestConfig(builder.build());
        clientContext.setAttribute("com.amazonaws.disableSocketProxy", (Object)settings.disableSocketProxy());
        return clientContext;
    }

    public static void disableNormalizeUri(RequestConfig.Builder requestConfigBuilder) {
        if (normalizeUriInvoker.isInitialized()) {
            try {
                normalizeUriInvoker.invoke(requestConfigBuilder, false);
            }
            catch (NoSuchMethodException ignored) {
                ApacheUtils.noSuchMethodThrownByNormalizeUriInvoker();
            }
        }
    }

    public static CredentialsProvider newProxyCredentialsProvider(HttpClientSettings settings) {
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(ApacheUtils.newAuthScope(settings), ApacheUtils.newNTCredentials(settings));
        return provider;
    }

    private static Credentials newNTCredentials(HttpClientSettings settings) {
        return new NTCredentials(settings.getProxyUsername(), settings.getProxyPassword(), settings.getProxyWorkstation(), settings.getProxyDomain());
    }

    private static AuthScope newAuthScope(HttpClientSettings settings) {
        return new AuthScope(settings.getProxyHost(), settings.getProxyPort());
    }

    private static void addPreemptiveAuthenticationProxy(HttpClientContext clientContext, HttpClientSettings settings) {
        if (settings.isPreemptiveBasicProxyAuth()) {
            HttpHost targetHost = new HttpHost(settings.getProxyHost(), settings.getProxyPort());
            CredentialsProvider credsProvider = ApacheUtils.newProxyCredentialsProvider(settings);
            BasicAuthCache authCache = new BasicAuthCache();
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, (AuthScheme)basicAuth);
            clientContext.setCredentialsProvider(credsProvider);
            clientContext.setAuthCache((AuthCache)authCache);
        }
    }

    private static void noSuchMethodThrownByNormalizeUriInvoker() {
        log.warn((Object)"NoSuchMethodException was thrown when disabling normalizeUri. This indicates you are using an old version (< 4.5.8) of Apache http client. It is recommended to use http client version >= 4.5.9 to avoid the breaking change introduced in apache client 4.5.7 and the latency in exception handling. See https://github.com/aws/aws-sdk-java/issues/1919 for more information");
    }

    static {
        try {
            normalizeUriInvoker.initialize();
        }
        catch (NoSuchMethodException ignored) {
            ApacheUtils.noSuchMethodThrownByNormalizeUriInvoker();
        }
    }
}

