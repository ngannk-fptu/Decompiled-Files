/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.authentication.ApplicationAuthenticationContext
 *  com.atlassian.crowd.service.client.AuthenticationMethod
 *  com.atlassian.crowd.service.client.ClientProperties
 *  com.atlassian.crowd.service.client.ClientPropertiesImpl
 *  com.atlassian.crowd.service.client.CrowdClient
 *  com.atlassian.crowd.service.factory.CrowdClientFactory
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.impl.client.CloseableHttpClient
 */
package com.atlassian.crowd.integration.rest.service.factory;

import com.atlassian.crowd.integration.rest.service.BasicAuthRestExecutor;
import com.atlassian.crowd.integration.rest.service.DefaultHttpClientProvider;
import com.atlassian.crowd.integration.rest.service.HttpClientProvider;
import com.atlassian.crowd.integration.rest.service.RestCrowdClient;
import com.atlassian.crowd.model.authentication.ApplicationAuthenticationContext;
import com.atlassian.crowd.service.client.AuthenticationMethod;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import com.atlassian.crowd.service.client.CrowdClient;
import com.atlassian.crowd.service.factory.CrowdClientFactory;
import com.google.common.base.Preconditions;
import java.util.Properties;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;

public class RestCrowdClientFactory
implements CrowdClientFactory {
    private final HttpClientProvider httpClientProvider;

    public RestCrowdClientFactory() {
        this(new DefaultHttpClientProvider());
    }

    public RestCrowdClientFactory(HttpClientProvider httpClientProvider) {
        this.httpClientProvider = (HttpClientProvider)Preconditions.checkNotNull((Object)httpClientProvider);
    }

    public CrowdClient newInstance(String url, String applicationName, String applicationPassword) {
        RestClientProperties clientProperties = new RestClientProperties(url, applicationName, applicationPassword);
        return this.newInstance(clientProperties);
    }

    public CrowdClient newInstance(ClientProperties clientProperties) {
        switch (clientProperties.getAuthenticationMethod()) {
            case BASIC_AUTH: {
                return new RestCrowdClient(BasicAuthRestExecutor.createFrom(clientProperties, this.getHttpClient(clientProperties)));
            }
        }
        throw new IllegalArgumentException("Unknown authentication method '" + clientProperties.getAuthenticationMethod() + "'");
    }

    protected CloseableHttpClient getHttpClient(ClientProperties clientProperties) {
        return this.httpClientProvider.getClient(clientProperties);
    }

    private static class RestClientProperties
    implements ClientProperties {
        private final ClientProperties delegate;
        private final String baseURL;
        private final String applicationName;
        private final String applicationPassword;

        RestClientProperties(String url, String applicationName, String applicationPassword) {
            this.baseURL = StringUtils.removeEnd((String)((String)Preconditions.checkNotNull((Object)url)), (String)"/");
            this.applicationName = (String)Preconditions.checkNotNull((Object)applicationName);
            this.applicationPassword = (String)Preconditions.checkNotNull((Object)applicationPassword);
            this.delegate = ClientPropertiesImpl.newInstanceFromProperties((Properties)new Properties());
        }

        public String getBaseURL() {
            return this.baseURL;
        }

        public String getApplicationName() {
            return this.applicationName;
        }

        public String getApplicationPassword() {
            return this.applicationPassword;
        }

        public String getSSOCookieDomainName() {
            return this.delegate.getSSOCookieDomainName();
        }

        @Nonnull
        public AuthenticationMethod getAuthenticationMethod() {
            return this.delegate.getAuthenticationMethod();
        }

        public String getApplicationAuthenticationURL() {
            return this.delegate.getApplicationAuthenticationURL();
        }

        public String getCookieTokenKey() {
            return this.delegate.getCookieTokenKey();
        }

        public String getCookieTokenKey(String defaultKey) {
            return this.delegate.getCookieTokenKey(defaultKey);
        }

        public String getSessionTokenKey() {
            return this.delegate.getSessionTokenKey();
        }

        public String getSessionLastValidation() {
            return this.delegate.getSessionLastValidation();
        }

        public long getSessionValidationInterval() {
            return this.delegate.getSessionValidationInterval();
        }

        public ApplicationAuthenticationContext getApplicationAuthenticationContext() {
            return this.delegate.getApplicationAuthenticationContext();
        }

        public String getHttpProxyPort() {
            return this.delegate.getHttpProxyPort();
        }

        public String getHttpProxyHost() {
            return this.delegate.getHttpProxyHost();
        }

        public String getHttpProxyUsername() {
            return this.delegate.getHttpProxyUsername();
        }

        public String getHttpProxyPassword() {
            return this.delegate.getHttpProxyPassword();
        }

        public String getHttpMaxConnections() {
            return this.delegate.getHttpMaxConnections();
        }

        public String getHttpTimeout() {
            return this.delegate.getHttpTimeout();
        }

        public String getSocketTimeout() {
            return this.delegate.getSocketTimeout();
        }

        public void updateProperties(Properties properties) {
            this.delegate.updateProperties(properties);
        }
    }
}

