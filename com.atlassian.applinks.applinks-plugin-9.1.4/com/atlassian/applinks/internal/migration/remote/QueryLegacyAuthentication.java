/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TrustedAppsAuthenticationProvider
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationException
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.internal.migration.remote;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TrustedAppsAuthenticationProvider;
import com.atlassian.applinks.core.rest.model.AuthenticationProviderEntity;
import com.atlassian.applinks.core.rest.model.AuthenticationProviderEntityListEntity;
import com.atlassian.applinks.internal.migration.remote.RemoteActionHandler;
import com.atlassian.applinks.internal.migration.remote.TryWithAuthentication;
import com.atlassian.applinks.internal.rest.RestUrlBuilder;
import com.atlassian.applinks.internal.rest.RestVersion;
import com.atlassian.applinks.internal.rest.client.AuthorisationUriAwareRequest;
import com.atlassian.applinks.internal.rest.client.RestRequestBuilder;
import com.atlassian.applinks.internal.status.DefaultLegacyConfig;
import com.atlassian.applinks.internal.status.LegacyConfig;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class QueryLegacyAuthentication
extends TryWithAuthentication {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryLegacyAuthentication.class);
    private DefaultLegacyConfig legacyConfig = new DefaultLegacyConfig();

    QueryLegacyAuthentication() {
    }

    @Override
    protected boolean execute(@Nonnull ApplicationLink applicationLink, @Nonnull ApplicationId applicationId, @Nonnull ApplicationLinkRequestFactory factory) throws IOException, CredentialsRequiredException, ResponseException, AuthenticationConfigurationException {
        return false;
    }

    @Override
    public boolean execute(@Nonnull ApplicationLink applicationLink, @Nonnull ApplicationId applicationId, @Nonnull Class<? extends AuthenticationProvider> providerClass) throws IOException, CredentialsRequiredException, ResponseException {
        RestUrlBuilder restUrlBuilder = new RestUrlBuilder().module("applinks").version(RestVersion.LATEST).addPath("applicationlink").addApplicationId(applicationId).addPath("authentication/provider");
        Optional<AuthorisationUriAwareRequest> request = new RestRequestBuilder(applicationLink).authentication(providerClass).methodType(Request.MethodType.GET).url(restUrlBuilder).buildOptional();
        if (!request.isPresent()) {
            return false;
        }
        QueryLegacyAuthenticationHandler handler = new QueryLegacyAuthenticationHandler();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Request.MethodType.GET.name() + " " + applicationLink.getRpcUrl() + restUrlBuilder.toString());
        }
        request.get().execute(handler);
        return handler.isSuccessful();
    }

    public LegacyConfig getLegacyConfig() {
        return this.legacyConfig;
    }

    protected static class Factory {
        protected Factory() {
        }

        QueryLegacyAuthentication getInstance() {
            return new QueryLegacyAuthentication();
        }
    }

    private class QueryLegacyAuthenticationHandler
    extends RemoteActionHandler {
        private QueryLegacyAuthenticationHandler() {
        }

        @Override
        public void handle(Response response) throws ResponseException {
            AuthenticationProviderEntityListEntity listEntity;
            List<AuthenticationProviderEntity> providers;
            super.handle(response);
            if (this.isSuccessful() && (providers = (listEntity = (AuthenticationProviderEntityListEntity)response.getEntity(AuthenticationProviderEntityListEntity.class)).getAuthenticationProviders()) != null) {
                for (AuthenticationProviderEntity provider : providers) {
                    if (BasicAuthenticationProvider.class.getName().equals(provider.getProvider())) {
                        QueryLegacyAuthentication.this.legacyConfig = QueryLegacyAuthentication.this.legacyConfig.basic(true);
                    }
                    if (!TrustedAppsAuthenticationProvider.class.getName().equals(provider.getProvider())) continue;
                    QueryLegacyAuthentication.this.legacyConfig = QueryLegacyAuthentication.this.legacyConfig.trusted(true);
                }
            }
        }
    }
}

