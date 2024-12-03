/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.ClientConfiguration
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.oauth2.client.util;

import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.properties.SystemProperty;
import com.atlassian.oauth2.common.validator.HttpsValidator;
import com.atlassian.sal.api.ApplicationProperties;
import com.google.common.base.Preconditions;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;

public class ClientHttpsValidator
extends HttpsValidator {
    public ClientHttpsValidator(ApplicationProperties applicationProperties) {
        super(applicationProperties, SystemProperty.DEV_MODE, SystemProperty.SKIP_BASE_URL_HTTPS_REQUIREMENT);
    }

    public boolean isSecure(String uri) {
        try {
            return StringUtils.isNotBlank((CharSequence)uri) && this.isSecure(new URL(uri));
        }
        catch (MalformedURLException e) {
            return false;
        }
    }

    public boolean isSecure(URI uri) {
        return !this.isOAuthProviderUrlHttpsRequired() || this.isHttps(uri);
    }

    public boolean isSecure(URL url) {
        return !this.isOAuthProviderUrlHttpsRequired() || this.isHttps(url);
    }

    public boolean isOAuthProviderUrlHttpsRequired() {
        return SystemProperty.DEV_MODE.getValue() == false && SystemProperty.SKIP_PROVIDER_HTTPS_REQUIREMENT.getValue() == false;
    }

    public void assertSecure(ClientConfiguration clientConfiguration) {
        Preconditions.checkArgument((boolean)this.isSecure(clientConfiguration.getAuthorizationEndpoint()), (Object)"Authorization endpoint not https");
        Preconditions.checkArgument((boolean)this.isSecure(clientConfiguration.getTokenEndpoint()), (Object)"Token endpoint not https");
    }
}

