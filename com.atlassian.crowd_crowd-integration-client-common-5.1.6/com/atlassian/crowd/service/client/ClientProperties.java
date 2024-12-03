/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.service.client;

import com.atlassian.crowd.model.authentication.ApplicationAuthenticationContext;
import com.atlassian.crowd.service.client.AuthenticationMethod;
import java.util.Properties;
import javax.annotation.Nonnull;

public interface ClientProperties {
    public void updateProperties(Properties var1);

    public String getApplicationName();

    public String getApplicationPassword();

    public String getApplicationAuthenticationURL();

    public String getCookieTokenKey();

    public String getCookieTokenKey(String var1);

    @Deprecated
    public String getSessionTokenKey();

    public String getSessionLastValidation();

    public long getSessionValidationInterval();

    public ApplicationAuthenticationContext getApplicationAuthenticationContext();

    public String getHttpProxyPort();

    public String getHttpProxyHost();

    public String getHttpProxyUsername();

    public String getHttpProxyPassword();

    public String getHttpMaxConnections();

    public String getHttpTimeout();

    public String getSocketTimeout();

    public String getBaseURL();

    public String getSSOCookieDomainName();

    @Nonnull
    public AuthenticationMethod getAuthenticationMethod();
}

