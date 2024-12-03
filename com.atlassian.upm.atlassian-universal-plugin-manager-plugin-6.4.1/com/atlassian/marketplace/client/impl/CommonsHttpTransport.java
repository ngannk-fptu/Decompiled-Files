/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Multimap
 *  io.atlassian.fugue.Option
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.Consts
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpRequestInterceptor
 *  org.apache.http.HttpResponse
 *  org.apache.http.auth.AuthScheme
 *  org.apache.http.auth.AuthScope
 *  org.apache.http.auth.Credentials
 *  org.apache.http.auth.NTCredentials
 *  org.apache.http.auth.UsernamePasswordCredentials
 *  org.apache.http.client.CredentialsProvider
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.config.RequestConfig$Builder
 *  org.apache.http.client.entity.UrlEncodedFormEntity
 *  org.apache.http.client.methods.HttpDelete
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpPatch
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpPut
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.config.SocketConfig
 *  org.apache.http.entity.ByteArrayEntity
 *  org.apache.http.entity.ContentType
 *  org.apache.http.entity.InputStreamEntity
 *  org.apache.http.impl.auth.BasicScheme
 *  org.apache.http.impl.client.BasicAuthCache
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.http.impl.client.cache.CacheConfig
 *  org.apache.http.impl.client.cache.CachingHttpClientBuilder
 *  org.apache.http.message.BasicHeader
 *  org.apache.http.message.BasicNameValuePair
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.EntityUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.MarketplaceClient;
import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.http.HttpConfiguration;
import com.atlassian.marketplace.client.http.HttpTransport;
import com.atlassian.marketplace.client.http.RequestDecorator;
import com.atlassian.marketplace.client.http.SimpleHttpResponse;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import io.atlassian.fugue.Option;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public final class CommonsHttpTransport
implements HttpTransport {
    private static final Logger logger = LoggerFactory.getLogger(MarketplaceClient.class);
    private static final ContentType APPLICATION_JSON_PATCH = ContentType.create((String)"application/json-patch+json", (Charset)Consts.UTF_8);
    private final HttpClient client;
    private final HttpConfiguration config;
    private final HttpTransport defaultOperations;

    public CommonsHttpTransport(HttpConfiguration configuration, URI baseUri) {
        this.config = (HttpConfiguration)Preconditions.checkNotNull((Object)configuration, (Object)"configuration");
        this.client = CommonsHttpTransport.httpClientBuilder(this.config, (Option<URI>)Option.some((Object)baseUri), CachingBehavior.CACHING).build();
        this.defaultOperations = new OperationsImpl((Iterable<RequestDecorator>)ImmutableList.of());
    }

    @Override
    public SimpleHttpResponse get(URI uri) throws MpacException {
        return this.defaultOperations.get(uri);
    }

    @Override
    public SimpleHttpResponse postParams(URI uri, Multimap<String, String> params) throws MpacException {
        return this.defaultOperations.postParams(uri, params);
    }

    @Override
    public SimpleHttpResponse post(URI uri, InputStream content, long length, String contentType, String acceptContentType, Optional<Consumer<HttpPost>> modifyRequest) throws MpacException {
        return this.defaultOperations.post(uri, content, length, contentType, acceptContentType, modifyRequest);
    }

    @Override
    public SimpleHttpResponse put(URI uri, byte[] content) throws MpacException {
        return this.defaultOperations.put(uri, content);
    }

    @Override
    public SimpleHttpResponse patch(URI uri, byte[] content) throws MpacException {
        return this.defaultOperations.patch(uri, content);
    }

    @Override
    public SimpleHttpResponse delete(URI uri) throws MpacException {
        return this.defaultOperations.delete(uri);
    }

    @Override
    public HttpTransport withRequestDecorator(RequestDecorator decorator) {
        return this.defaultOperations.withRequestDecorator(decorator);
    }

    @Override
    public void close() {
        if (this.client instanceof Closeable) {
            try {
                ((CloseableHttpClient)this.client).close();
            }
            catch (IOException e) {
                logger.warn("Unexpected error while closing HTTP client: " + e);
                logger.debug(e.toString(), (Throwable)e);
            }
        }
    }

    public static HttpClient createHttpClient(HttpConfiguration config, Option<URI> baseUri) {
        return CommonsHttpTransport.httpClientBuilder(config, baseUri, CachingBehavior.NO_CACHING).build();
    }

    public static HttpClientBuilder httpClientBuilder(HttpConfiguration config, Option<URI> baseUri, CachingBehavior cachingBehavior) {
        HttpClientBuilder builder;
        Object configBuilder;
        if (cachingBehavior == CachingBehavior.CACHING) {
            CachingHttpClientBuilder cachingBuilder = CachingHttpClientBuilder.create();
            configBuilder = CacheConfig.custom();
            configBuilder.setSharedCache(false);
            configBuilder.setMaxCacheEntries(config.getMaxCacheEntries());
            configBuilder.setMaxObjectSize(config.getMaxCacheObjectSize());
            cachingBuilder.setCacheConfig(configBuilder.build());
            builder = cachingBuilder;
        } else {
            builder = HttpClientBuilder.create();
        }
        builder.useSystemProperties();
        builder.setMaxConnPerRoute(config.getMaxConnections());
        RequestConfig.Builder rc = RequestConfig.custom().setConnectTimeout(config.getConnectTimeoutMillis()).setSocketTimeout(config.getReadTimeoutMillis()).setCookieSpec("ignoreCookies").setProxyPreferredAuthSchemes(CommonsHttpTransport.getProxyPreferredAuthSchemes(config));
        configBuilder = config.getMaxRedirects().iterator();
        while (configBuilder.hasNext()) {
            int maxRedirects = (Integer)configBuilder.next();
            rc.setMaxRedirects(maxRedirects);
        }
        builder.setDefaultRequestConfig(rc.build());
        Option<HttpConfiguration.ProxyHost> realProxyHost = CommonsHttpTransport.getRealProxyHost(config, baseUri);
        for (HttpConfiguration.ProxyHost ph : realProxyHost) {
            builder.setProxy(new HttpHost(ph.getHostname(), ph.getPort()));
        }
        builder.addInterceptorFirst((HttpRequestInterceptor)new DefaultRequestInterceptor(config, realProxyHost));
        builder.setDefaultCredentialsProvider((CredentialsProvider)new DefaultCredentialsProvider(config, realProxyHost));
        builder.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(config.getConnectTimeoutMillis()).build());
        return builder;
    }

    private static Option<HttpConfiguration.ProxyHost> getRealProxyHost(HttpConfiguration config, Option<URI> baseUri) {
        for (HttpConfiguration.ProxyConfiguration proxy : config.getProxyConfiguration()) {
            if (proxy.getProxyHost().isDefined()) {
                return proxy.getProxyHost();
            }
            String prefix = "https";
            for (URI u : baseUri) {
                if (u.getScheme() == null || !u.getScheme().equalsIgnoreCase("http")) continue;
                prefix = "http";
            }
            Iterator iterator = Option.option((Object)StringUtils.trimToNull((String)System.getProperty(prefix + ".proxyHost"))).iterator();
            if (!iterator.hasNext()) continue;
            String host = (String)iterator.next();
            int port = Integer.parseInt(System.getProperty(prefix + ".proxyPort", String.valueOf(80)));
            return Option.some((Object)new HttpConfiguration.ProxyHost(host, port));
        }
        return Option.none();
    }

    private static ImmutableList<String> getProxyPreferredAuthSchemes(HttpConfiguration config) {
        for (HttpConfiguration.ProxyConfiguration proxy : config.getProxyConfiguration()) {
            Iterator iterator = proxy.getAuthParams().iterator();
            if (!iterator.hasNext()) continue;
            HttpConfiguration.ProxyAuthParams auth = (HttpConfiguration.ProxyAuthParams)iterator.next();
            return ImmutableList.of((Object)auth.getAuthMethod().name().toUpperCase());
        }
        return ImmutableList.of();
    }

    private static class DefaultCredentialsProvider
    implements CredentialsProvider {
        private final Option<HttpConfiguration.ProxyHost> proxyHost;
        private final Option<Credentials> proxyCredentials;
        private final Option<Credentials> targetHostCredentials;

        DefaultCredentialsProvider(HttpConfiguration config, Option<HttpConfiguration.ProxyHost> proxyHost) {
            this.proxyCredentials = this.makeProxyCredentials(config);
            this.targetHostCredentials = this.makeTargetHostCredentials(config);
            this.proxyHost = (Option)Preconditions.checkNotNull(proxyHost);
        }

        public void setCredentials(AuthScope authscope, Credentials credentials) {
        }

        public Credentials getCredentials(AuthScope authScope) {
            for (HttpConfiguration.ProxyHost ph : this.proxyHost) {
                if (!ph.getHostname().equals(authScope.getHost()) || ph.getPort() != authScope.getPort()) continue;
                return (Credentials)this.proxyCredentials.getOrElse((Object)null);
            }
            return (Credentials)this.targetHostCredentials.getOrElse((Object)null);
        }

        public void clear() {
        }

        private Option<Credentials> makeProxyCredentials(HttpConfiguration config) {
            for (HttpConfiguration.ProxyConfiguration proxy : config.getProxyConfiguration()) {
                UsernamePasswordCredentials c;
                Iterator iterator = proxy.getAuthParams().iterator();
                if (!iterator.hasNext()) continue;
                HttpConfiguration.ProxyAuthParams auth = (HttpConfiguration.ProxyAuthParams)iterator.next();
                switch (auth.getAuthMethod()) {
                    case NTLM: {
                        c = new NTCredentials(auth.getCredentials().getUsername(), auth.getCredentials().getPassword(), (String)auth.getNtlmWorkstation().getOrElse((Object)""), (String)auth.getNtlmDomain().getOrElse((Object)""));
                        break;
                    }
                    default: {
                        c = new UsernamePasswordCredentials(auth.getCredentials().getUsername(), auth.getCredentials().getPassword());
                    }
                }
                return Option.some((Object)c);
            }
            return Option.none();
        }

        private Option<Credentials> makeTargetHostCredentials(HttpConfiguration config) {
            Iterator iterator = config.getCredentials().iterator();
            if (iterator.hasNext()) {
                HttpConfiguration.Credentials c = (HttpConfiguration.Credentials)iterator.next();
                return Option.some((Object)new UsernamePasswordCredentials(c.getUsername(), c.getPassword()));
            }
            return Option.none();
        }
    }

    private static class DefaultRequestInterceptor
    implements HttpRequestInterceptor {
        private final HttpConfiguration config;
        private final Option<HttpConfiguration.ProxyHost> proxyHost;

        DefaultRequestInterceptor(HttpConfiguration config, Option<HttpConfiguration.ProxyHost> proxyHost) {
            this.config = (HttpConfiguration)Preconditions.checkNotNull((Object)config);
            this.proxyHost = (Option)Preconditions.checkNotNull(proxyHost);
        }

        public void process(HttpRequest request, HttpContext context) {
            BasicAuthCache authCache = null;
            for (RequestDecorator rd : this.config.getRequestDecorator()) {
                Map<String, String> headers = rd.getRequestHeaders();
                if (headers == null) continue;
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    request.addHeader(header.getKey(), header.getValue());
                }
            }
            if (this.config.hasCredentials()) {
                authCache = new BasicAuthCache();
                HttpHost targetHost = (HttpHost)context.getAttribute("http.target_host");
                if (targetHost != null) {
                    authCache.put(targetHost, (AuthScheme)new BasicScheme());
                }
            }
            for (HttpConfiguration.ProxyConfiguration proxy : this.config.getProxyConfiguration()) {
                for (HttpConfiguration.ProxyAuthParams auth : proxy.getAuthParams()) {
                    if (auth.getAuthMethod() != HttpConfiguration.ProxyAuthMethod.BASIC) continue;
                    for (HttpConfiguration.ProxyHost ph : this.proxyHost) {
                        HttpHost hph = new HttpHost(ph.getHostname(), ph.getPort());
                        if (authCache == null) {
                            authCache = new BasicAuthCache();
                        }
                        BasicScheme proxyAuth = new BasicScheme();
                        try {
                            proxyAuth.processChallenge((Header)new BasicHeader("Proxy-Authenticate", "BASIC realm=default"));
                            authCache.put(hph, (AuthScheme)proxyAuth);
                        }
                        catch (Exception e) {
                            logger.warn("Error, unable to set preemptive proxy auth: " + e);
                            logger.debug(e.toString(), (Throwable)e);
                        }
                    }
                }
            }
            if (authCache != null) {
                context.setAttribute("http.auth.auth-cache", (Object)authCache);
            }
        }
    }

    private static class ResponseImpl
    implements SimpleHttpResponse {
        private final HttpResponse response;

        ResponseImpl(HttpResponse response) {
            this.response = response;
        }

        @Override
        public int getStatus() {
            return this.response.getStatusLine().getStatusCode();
        }

        @Override
        public Iterable<String> getHeader(String name) {
            ImmutableList.Builder ret = ImmutableList.builder();
            for (Header h : this.response.getHeaders(name)) {
                ret.add((Object)h.getValue());
            }
            return ret.build();
        }

        @Override
        public InputStream getContentStream() throws MpacException {
            try {
                return this.response.getEntity().getContent();
            }
            catch (IOException e) {
                throw new MpacException(e);
            }
        }

        @Override
        public boolean isEmpty() {
            Header h = this.response.getFirstHeader("Content-Length");
            return h != null && h.getValue().trim().equals("0");
        }

        @Override
        public void close() {
            if (this.response.getEntity() != null) {
                EntityUtils.consumeQuietly((HttpEntity)this.response.getEntity());
            }
        }
    }

    private class OperationsImpl
    implements HttpTransport {
        private final Iterable<RequestDecorator> decorators;

        OperationsImpl(Iterable<RequestDecorator> decorators) {
            this.decorators = ImmutableList.copyOf(decorators);
        }

        @Override
        public HttpTransport withRequestDecorator(RequestDecorator decorator) {
            return new OperationsImpl(Iterables.concat(this.decorators, (Iterable)ImmutableList.of((Object)decorator)));
        }

        @Override
        public SimpleHttpResponse get(URI uri) throws MpacException {
            HttpGet method = new HttpGet(uri);
            return this.executeMethod((HttpUriRequest)method);
        }

        @Override
        public SimpleHttpResponse postParams(URI uri, Multimap<String, String> params) throws MpacException {
            HttpPost method = new HttpPost(uri);
            ArrayList<BasicNameValuePair> formParams = new ArrayList<BasicNameValuePair>();
            for (Map.Entry param : params.entries()) {
                formParams.add(new BasicNameValuePair((String)param.getKey(), (String)param.getValue()));
            }
            try {
                method.setEntity((HttpEntity)new UrlEncodedFormEntity(formParams));
            }
            catch (UnsupportedEncodingException e) {
                throw new MpacException(e);
            }
            return this.executeMethod((HttpUriRequest)method);
        }

        @Override
        public SimpleHttpResponse post(URI uri, InputStream content, long length, String contentType, String acceptContentType, Optional<Consumer<HttpPost>> modifyRequest) throws MpacException {
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity((HttpEntity)new InputStreamEntity(content, length, ContentType.create((String)contentType, (Charset)Consts.UTF_8)));
            httpPost.addHeader("Content-Type", contentType.concat("; charset=UTF-8"));
            httpPost.addHeader("Accept", acceptContentType);
            modifyRequest.ifPresent(c -> c.accept(httpPost));
            return this.executeMethod((HttpUriRequest)httpPost);
        }

        @Override
        public SimpleHttpResponse put(URI uri, byte[] content) throws MpacException {
            HttpPut method = new HttpPut(uri);
            method.setEntity((HttpEntity)new ByteArrayEntity(content, ContentType.APPLICATION_JSON));
            return this.executeMethod((HttpUriRequest)method);
        }

        @Override
        public SimpleHttpResponse patch(URI uri, byte[] content) throws MpacException {
            HttpPatch method = new HttpPatch(uri);
            method.setEntity((HttpEntity)new ByteArrayEntity(content, APPLICATION_JSON_PATCH));
            return this.executeMethod((HttpUriRequest)method);
        }

        @Override
        public SimpleHttpResponse delete(URI uri) throws MpacException {
            HttpDelete method = new HttpDelete(uri);
            return this.executeMethod((HttpUriRequest)method);
        }

        @Override
        public void close() {
            CommonsHttpTransport.this.close();
        }

        private SimpleHttpResponse executeMethod(HttpUriRequest method) throws MpacException {
            logger.info(method.getMethod() + " " + method.getURI());
            for (RequestDecorator rd : this.decorators) {
                Map<String, String> moreHeaders = rd.getRequestHeaders();
                if (moreHeaders == null) continue;
                for (Map.Entry<String, String> header : moreHeaders.entrySet()) {
                    method.addHeader(header.getKey(), header.getValue());
                }
            }
            try {
                return new ResponseImpl(CommonsHttpTransport.this.client.execute(method));
            }
            catch (SocketException e) {
                throw new MpacException.ConnectionFailure(e);
            }
            catch (IOException e) {
                throw new MpacException(e);
            }
        }
    }

    public static enum CachingBehavior {
        NO_CACHING,
        CACHING;

    }
}

