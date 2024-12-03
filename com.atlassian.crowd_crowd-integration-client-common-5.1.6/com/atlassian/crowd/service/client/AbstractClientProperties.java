/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.service.client;

import com.atlassian.crowd.integration.Constants;
import com.atlassian.crowd.model.authentication.ApplicationAuthenticationContext;
import com.atlassian.crowd.service.client.AuthenticationMethod;
import com.atlassian.crowd.service.client.ClientProperties;
import javax.annotation.Nonnull;

public abstract class AbstractClientProperties
implements ClientProperties {
    protected String applicationName = null;
    protected String applicationPassword = null;
    protected String applicationAuthenticationURL = null;
    protected String cookieTokenKey = null;
    protected String sessionTokenKey = null;
    protected String sessionLastValidation = null;
    protected long sessionValidationInterval = 0L;
    protected String baseURL = null;
    protected String httpProxyPort = null;
    protected String httpProxyHost = null;
    protected String httpProxyUsername = null;
    protected String httpProxyPassword = null;
    protected String httpMaxConnections = null;
    protected String httpTimeout = null;
    protected String socketTimeout = null;
    protected String ssoCookieDomainName = null;
    protected AuthenticationMethod authenticationMethod = AuthenticationMethod.BASIC_AUTH;
    protected ApplicationAuthenticationContext applicationAuthenticationContext = null;

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
        return this.getCookieTokenKey(Constants.COOKIE_TOKEN_KEY);
    }

    @Override
    public String getCookieTokenKey(String def) {
        if (this.cookieTokenKey != null) {
            return this.cookieTokenKey;
        }
        return def;
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
        return this.applicationAuthenticationContext;
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
    public String getSSOCookieDomainName() {
        return this.ssoCookieDomainName;
    }

    @Override
    @Nonnull
    public AuthenticationMethod getAuthenticationMethod() {
        return this.authenticationMethod;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ClientPropertiesGeneric");
        sb.append("{applicationName='").append(this.applicationName).append('\'');
        sb.append(", applicationPassword='").append(this.applicationPassword).append('\'');
        sb.append(", applicationAuthenticationURL='").append(this.applicationAuthenticationURL).append('\'');
        sb.append(", cookieTokenKey='").append(this.getCookieTokenKey()).append('\'');
        sb.append(", sessionTokenKey='").append(this.sessionTokenKey).append('\'');
        sb.append(", sessionLastValidation='").append(this.sessionLastValidation).append('\'');
        sb.append(", sessionValidationInterval=").append(this.sessionValidationInterval);
        sb.append(", baseURL='").append(this.baseURL).append('\'');
        sb.append(", httpProxyPort='").append(this.httpProxyPort).append('\'');
        sb.append(", httpProxyHost='").append(this.httpProxyHost).append('\'');
        sb.append(", httpProxyUsername='").append(this.httpProxyUsername).append('\'');
        sb.append(", httpProxyPassword='").append(this.httpProxyPassword).append('\'');
        sb.append(", httpMaxConnections='").append(this.httpMaxConnections).append('\'');
        sb.append(", httpTimeout='").append(this.httpTimeout).append('\'');
        sb.append(", socketTimeout='").append(this.socketTimeout).append('\'');
        sb.append(", applicationAuthenticationContext=").append(this.applicationAuthenticationContext);
        sb.append(", authenticationMethod=").append(this.authenticationMethod.getKey());
        sb.append('}');
        return sb.toString();
    }
}

