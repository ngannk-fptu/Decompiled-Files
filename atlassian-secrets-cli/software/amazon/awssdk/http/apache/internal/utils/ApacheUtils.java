/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.auth.AuthScheme
 *  org.apache.http.auth.AuthScope
 *  org.apache.http.auth.Credentials
 *  org.apache.http.auth.NTCredentials
 *  org.apache.http.client.AuthCache
 *  org.apache.http.client.CredentialsProvider
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.config.RequestConfig$Builder
 *  org.apache.http.client.protocol.HttpClientContext
 *  org.apache.http.impl.auth.BasicScheme
 *  org.apache.http.impl.client.BasicAuthCache
 *  org.apache.http.impl.client.BasicCredentialsProvider
 */
package software.amazon.awssdk.http.apache.internal.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.ReflectionMethodInvoker;

@SdkInternalApi
public final class ApacheUtils {
    private static final Logger logger = Logger.loggerFor(ApacheUtils.class);
    private static final ReflectionMethodInvoker<RequestConfig.Builder, RequestConfig.Builder> NORMALIZE_URI_INVOKER = new ReflectionMethodInvoker<RequestConfig.Builder, RequestConfig.Builder>(RequestConfig.Builder.class, RequestConfig.Builder.class, "setNormalizeUri", Boolean.TYPE);

    private ApacheUtils() {
    }

    public static HttpEntity newBufferedHttpEntity(HttpEntity entity) {
        try {
            return new BufferedHttpEntity(entity);
        }
        catch (IOException e) {
            throw new UncheckedIOException("Unable to create HTTP entity: " + e.getMessage(), e);
        }
    }

    public static HttpClientContext newClientContext(ProxyConfiguration proxyConfiguration) {
        HttpClientContext clientContext = new HttpClientContext();
        ApacheUtils.addPreemptiveAuthenticationProxy(clientContext, proxyConfiguration);
        RequestConfig.Builder builder = RequestConfig.custom();
        ApacheUtils.disableNormalizeUri(builder);
        clientContext.setRequestConfig(builder.build());
        return clientContext;
    }

    public static void disableNormalizeUri(RequestConfig.Builder requestConfigBuilder) {
        if (NORMALIZE_URI_INVOKER.isInitialized()) {
            try {
                NORMALIZE_URI_INVOKER.invoke(requestConfigBuilder, false);
            }
            catch (NoSuchMethodException ignored) {
                ApacheUtils.noSuchMethodThrownByNormalizeUriInvoker();
            }
        }
    }

    public static CredentialsProvider newProxyCredentialsProvider(ProxyConfiguration proxyConfiguration) {
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(ApacheUtils.newAuthScope(proxyConfiguration), ApacheUtils.newNtCredentials(proxyConfiguration));
        return provider;
    }

    private static Credentials newNtCredentials(ProxyConfiguration proxyConfiguration) {
        return new NTCredentials(proxyConfiguration.username(), proxyConfiguration.password(), proxyConfiguration.ntlmWorkstation(), proxyConfiguration.ntlmDomain());
    }

    private static AuthScope newAuthScope(ProxyConfiguration proxyConfiguration) {
        return new AuthScope(proxyConfiguration.host(), proxyConfiguration.port());
    }

    private static void addPreemptiveAuthenticationProxy(HttpClientContext clientContext, ProxyConfiguration proxyConfiguration) {
        if (proxyConfiguration.preemptiveBasicAuthenticationEnabled().booleanValue()) {
            HttpHost targetHost = new HttpHost(proxyConfiguration.host(), proxyConfiguration.port());
            CredentialsProvider credsProvider = ApacheUtils.newProxyCredentialsProvider(proxyConfiguration);
            BasicAuthCache authCache = new BasicAuthCache();
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, (AuthScheme)basicAuth);
            clientContext.setCredentialsProvider(credsProvider);
            clientContext.setAuthCache((AuthCache)authCache);
        }
    }

    private static void noSuchMethodThrownByNormalizeUriInvoker() {
        logger.warn(() -> "NoSuchMethodException was thrown when disabling normalizeUri. This indicates you are using an old version (< 4.5.8) of Apache http client. It is recommended to use http client version >= 4.5.9 to avoid the breaking change introduced in apache client 4.5.7 and the latency in exception handling. See https://github.com/aws/aws-sdk-java/issues/1919 for more information");
    }

    static {
        try {
            NORMALIZE_URI_INVOKER.initialize();
        }
        catch (NoSuchMethodException ignored) {
            ApacheUtils.noSuchMethodThrownByNormalizeUriInvoker();
        }
    }
}

