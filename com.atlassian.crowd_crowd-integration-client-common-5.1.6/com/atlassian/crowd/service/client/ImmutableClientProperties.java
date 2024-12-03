/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.service.client;

import com.atlassian.crowd.model.authentication.ApplicationAuthenticationContext;
import com.atlassian.crowd.service.client.AuthenticationMethod;
import com.atlassian.crowd.service.client.ClientProperties;
import java.util.Properties;
import javax.annotation.Nonnull;

public class ImmutableClientProperties
implements ClientProperties {
    private final String applicationName;
    private final String applicationPassword;
    private final String applicationAuthenticationURL;
    private final String cookieTokenKey;
    private final String sessionTokenKey;
    private final String sessionLastValidation;
    private final long sessionValidationInterval;
    private final ApplicationAuthenticationContext applicationAuthenticationContext;
    private final String httpProxyPort;
    private final String httpProxyHost;
    private final String httpProxyUsername;
    private final String httpProxyPassword;
    private final String httpMaxConnections;
    private final String httpTimeout;
    private final String socketTimeout;
    private final String baseURL;
    private final String ssoCookieDomainName;
    private final AuthenticationMethod authenticationMethod;

    private ImmutableClientProperties(Builder builder) {
        this.applicationName = builder.applicationName;
        this.applicationPassword = builder.applicationPassword;
        this.applicationAuthenticationURL = builder.applicationAuthenticationURL;
        this.cookieTokenKey = builder.cookieTokenKey;
        this.sessionTokenKey = builder.sessionTokenKey;
        this.sessionLastValidation = builder.sessionLastValidation;
        this.sessionValidationInterval = builder.sessionValidationInterval;
        this.applicationAuthenticationContext = builder.applicationAuthenticationContext;
        this.httpProxyPort = builder.httpProxyPort;
        this.httpProxyHost = builder.httpProxyHost;
        this.httpProxyUsername = builder.httpProxyUsername;
        this.httpProxyPassword = builder.httpProxyPassword;
        this.httpMaxConnections = builder.httpMaxConnections;
        this.httpTimeout = builder.httpTimeout;
        this.socketTimeout = builder.socketTimeout;
        this.baseURL = builder.baseURL;
        this.ssoCookieDomainName = builder.ssoCookieDomainName;
        this.authenticationMethod = builder.authenticationMethod;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ImmutableClientProperties data) {
        return new Builder(data);
    }

    @Override
    public void updateProperties(Properties properties) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCookieTokenKey(String defaultKey) {
        return this.cookieTokenKey;
    }

    @Override
    public String getSSOCookieDomainName() {
        return this.ssoCookieDomainName;
    }

    @Override
    public String getApplicationName() {
        return this.applicationName;
    }

    @Override
    public String getApplicationPassword() {
        return this.applicationPassword;
    }

    @Override
    public String getApplicationAuthenticationURL() {
        return this.applicationAuthenticationURL;
    }

    @Override
    public String getCookieTokenKey() {
        return this.cookieTokenKey;
    }

    @Override
    public String getSessionTokenKey() {
        return this.sessionTokenKey;
    }

    @Override
    public String getSessionLastValidation() {
        return this.sessionLastValidation;
    }

    @Override
    public long getSessionValidationInterval() {
        return this.sessionValidationInterval;
    }

    @Override
    public ApplicationAuthenticationContext getApplicationAuthenticationContext() {
        return new ApplicationAuthenticationContext(this.applicationAuthenticationContext);
    }

    @Override
    public String getHttpProxyPort() {
        return this.httpProxyPort;
    }

    @Override
    public String getHttpProxyHost() {
        return this.httpProxyHost;
    }

    @Override
    public String getHttpProxyUsername() {
        return this.httpProxyUsername;
    }

    @Override
    public String getHttpProxyPassword() {
        return this.httpProxyPassword;
    }

    @Override
    public String getHttpMaxConnections() {
        return this.httpMaxConnections;
    }

    @Override
    public String getHttpTimeout() {
        return this.httpTimeout;
    }

    @Override
    public String getSocketTimeout() {
        return this.socketTimeout;
    }

    @Override
    public String getBaseURL() {
        return this.baseURL;
    }

    @Override
    @Nonnull
    public AuthenticationMethod getAuthenticationMethod() {
        return this.authenticationMethod;
    }

    public static final class Builder {
        private String applicationName;
        private String applicationPassword;
        private String applicationAuthenticationURL;
        private String cookieTokenKey;
        private String sessionTokenKey;
        private String sessionLastValidation;
        private long sessionValidationInterval;
        private ApplicationAuthenticationContext applicationAuthenticationContext;
        private String httpProxyPort;
        private String httpProxyHost;
        private String httpProxyUsername;
        private String httpProxyPassword;
        private String httpMaxConnections;
        private String httpTimeout;
        private String socketTimeout;
        private String baseURL;
        private String ssoCookieDomainName;
        private AuthenticationMethod authenticationMethod;

        private Builder() {
        }

        private Builder(ImmutableClientProperties initialData) {
            this.applicationName = initialData.getApplicationName();
            this.applicationPassword = initialData.getApplicationPassword();
            this.applicationAuthenticationURL = initialData.getApplicationAuthenticationURL();
            this.cookieTokenKey = initialData.getCookieTokenKey();
            this.sessionTokenKey = initialData.getSessionTokenKey();
            this.sessionLastValidation = initialData.getSessionLastValidation();
            this.sessionValidationInterval = initialData.getSessionValidationInterval();
            this.applicationAuthenticationContext = initialData.getApplicationAuthenticationContext();
            this.httpProxyPort = initialData.getHttpProxyPort();
            this.httpProxyHost = initialData.getHttpProxyHost();
            this.httpProxyUsername = initialData.getHttpProxyUsername();
            this.httpProxyPassword = initialData.getHttpProxyPassword();
            this.httpMaxConnections = initialData.getHttpMaxConnections();
            this.httpTimeout = initialData.getHttpTimeout();
            this.socketTimeout = initialData.getSocketTimeout();
            this.baseURL = initialData.getBaseURL();
            this.ssoCookieDomainName = initialData.getSSOCookieDomainName();
            this.authenticationMethod = initialData.getAuthenticationMethod();
        }

        public Builder setApplicationName(String applicationName) {
            this.applicationName = applicationName;
            return this;
        }

        public Builder setApplicationPassword(String applicationPassword) {
            this.applicationPassword = applicationPassword;
            return this;
        }

        public Builder setApplicationAuthenticationURL(String applicationAuthenticationURL) {
            this.applicationAuthenticationURL = applicationAuthenticationURL;
            return this;
        }

        public Builder setCookieTokenKey(String cookieTokenKey) {
            this.cookieTokenKey = cookieTokenKey;
            return this;
        }

        public Builder setSessionTokenKey(String sessionTokenKey) {
            this.sessionTokenKey = sessionTokenKey;
            return this;
        }

        public Builder setSessionLastValidation(String sessionLastValidation) {
            this.sessionLastValidation = sessionLastValidation;
            return this;
        }

        public Builder setSessionValidationInterval(long sessionValidationInterval) {
            this.sessionValidationInterval = sessionValidationInterval;
            return this;
        }

        public Builder setApplicationAuthenticationContext(ApplicationAuthenticationContext applicationAuthenticationContext) {
            this.applicationAuthenticationContext = applicationAuthenticationContext;
            return this;
        }

        public Builder setHttpProxyPort(String httpProxyPort) {
            this.httpProxyPort = httpProxyPort;
            return this;
        }

        public Builder setHttpProxyHost(String httpProxyHost) {
            this.httpProxyHost = httpProxyHost;
            return this;
        }

        public Builder setHttpProxyUsername(String httpProxyUsername) {
            this.httpProxyUsername = httpProxyUsername;
            return this;
        }

        public Builder setHttpProxyPassword(String httpProxyPassword) {
            this.httpProxyPassword = httpProxyPassword;
            return this;
        }

        public Builder setHttpMaxConnections(String httpMaxConnections) {
            this.httpMaxConnections = httpMaxConnections;
            return this;
        }

        public Builder setHttpTimeout(String httpTimeout) {
            this.httpTimeout = httpTimeout;
            return this;
        }

        public Builder setSocketTimeout(String socketTimeout) {
            this.socketTimeout = socketTimeout;
            return this;
        }

        public Builder setBaseURL(String baseURL) {
            this.baseURL = baseURL;
            return this;
        }

        public Builder setSsoCookieDomainName(String ssoCookieDomainName) {
            this.ssoCookieDomainName = ssoCookieDomainName;
            return this;
        }

        public Builder setAuthenticationMethod(AuthenticationMethod authenticationMethod) {
            this.authenticationMethod = authenticationMethod;
            return this;
        }

        public ImmutableClientProperties build() {
            return new ImmutableClientProperties(this);
        }
    }
}

