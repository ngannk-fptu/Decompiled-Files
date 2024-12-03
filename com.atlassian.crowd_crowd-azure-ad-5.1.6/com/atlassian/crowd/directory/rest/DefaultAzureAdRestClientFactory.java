/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.primitives.Ints
 *  com.sun.jersey.api.client.Client
 *  com.sun.jersey.api.client.config.ClientConfig
 *  com.sun.jersey.api.client.config.DefaultClientConfig
 *  com.sun.jersey.api.client.filter.ClientFilter
 *  org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.rest;

import com.atlassian.crowd.directory.authentication.AzureAdRefreshTokenFilter;
import com.atlassian.crowd.directory.authentication.AzureAdTokenRefresher;
import com.atlassian.crowd.directory.authentication.MsGraphApiAuthenticator;
import com.atlassian.crowd.directory.authentication.impl.MsalAuthenticatorFactory;
import com.atlassian.crowd.directory.rest.AzureAdPagingWrapper;
import com.atlassian.crowd.directory.rest.AzureAdRestClient;
import com.atlassian.crowd.directory.rest.AzureAdRestClientFactory;
import com.atlassian.crowd.directory.rest.endpoint.AzureApiUriResolver;
import com.atlassian.crowd.directory.rest.util.IoUtilsWrapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.primitives.Ints;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAzureAdRestClientFactory
implements AzureAdRestClientFactory {
    private final MsalAuthenticatorFactory msalAuthenticatorFactory;
    private final IoUtilsWrapper ioUtilsWrapper;
    private static final Logger log = LoggerFactory.getLogger(DefaultAzureAdRestClientFactory.class);

    public DefaultAzureAdRestClientFactory(MsalAuthenticatorFactory msalAuthenticatorFactory, IoUtilsWrapper ioUtilsWrapper) {
        this.msalAuthenticatorFactory = msalAuthenticatorFactory;
        this.ioUtilsWrapper = ioUtilsWrapper;
    }

    @Override
    public AzureAdRestClient create(String clientId, String clientSecret, String tenantId, AzureApiUriResolver endpointDataProvider, long connectionTimeout, long readTimeout) {
        Preconditions.checkNotNull((Object)Strings.emptyToNull((String)tenantId), (Object)"Tenant ID not specified");
        Client jerseyClient = this.createJerseyClient(clientId, clientSecret, tenantId, endpointDataProvider, connectionTimeout, readTimeout);
        return new AzureAdRestClient(jerseyClient, endpointDataProvider, this.ioUtilsWrapper);
    }

    @VisibleForTesting
    Client createJerseyClient(String clientId, String clientSecret, String tenantId, AzureApiUriResolver azureApiUriResolver, long connectionTimeout, long readTimeout) {
        DefaultClientConfig config = new DefaultClientConfig();
        config.getSingletons().add(new JacksonJaxbJsonProvider());
        Client jerseyClient = Client.create((ClientConfig)config);
        jerseyClient.setConnectTimeout(Integer.valueOf(this.loggedSaturatedCast(connectionTimeout, "connection")));
        jerseyClient.setReadTimeout(Integer.valueOf(this.loggedSaturatedCast(readTimeout, "read")));
        MsGraphApiAuthenticator msalAuthenticator = this.msalAuthenticatorFactory.create(clientId, clientSecret, tenantId, azureApiUriResolver);
        jerseyClient.addFilter((ClientFilter)this.createAzureAdTokenFilter(msalAuthenticator));
        return jerseyClient;
    }

    private int loggedSaturatedCast(long valueAsLong, String timeoutType) {
        int saturatedValueAsInt = Ints.saturatedCast((long)valueAsLong);
        if (valueAsLong != (long)saturatedValueAsInt) {
            log.debug("Specified value {} for {} timeout cannot be represented as an integer, performing saturated cast to {}", new Object[]{valueAsLong, timeoutType, saturatedValueAsInt});
        }
        return saturatedValueAsInt;
    }

    @Override
    public AzureAdPagingWrapper create(AzureAdRestClient restClient) {
        return new AzureAdPagingWrapper(restClient);
    }

    private AzureAdRefreshTokenFilter createAzureAdTokenFilter(MsGraphApiAuthenticator msalAuthenticator) {
        return new AzureAdRefreshTokenFilter(this.createAzureAdTokenRefresher(msalAuthenticator));
    }

    private AzureAdTokenRefresher createAzureAdTokenRefresher(final MsGraphApiAuthenticator msalAuthenticator) {
        return new AzureAdTokenRefresher((LoadingCache<String, String>)CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<String, String>(){

            public String load(String key) throws Exception {
                Preconditions.checkArgument((boolean)"AZURE_AD_TOKEN".equals(key));
                return msalAuthenticator.getApiToken().accessToken();
            }
        }));
    }
}

