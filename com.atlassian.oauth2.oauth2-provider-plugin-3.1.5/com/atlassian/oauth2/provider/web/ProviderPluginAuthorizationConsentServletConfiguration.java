/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.oauth2.provider.web;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.oauth2.provider.core.web.servlet.AuthorizationConsentServletConfiguration;
import com.atlassian.oauth2.provider.web.ServletConfiguration;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import org.apache.commons.lang3.StringUtils;

public class ProviderPluginAuthorizationConsentServletConfiguration
extends ServletConfiguration
implements AuthorizationConsentServletConfiguration {
    @VisibleForTesting
    static final String CONSENT_PAGE_PATH = "/plugins/servlet/oauth2/consent";

    public ProviderPluginAuthorizationConsentServletConfiguration(ApplicationProperties applicationProperties) {
        super(applicationProperties);
    }

    @Override
    public String moduleKey() {
        return "com.atlassian.oauth2.oauth2-provider-plugin:oauth2-provider-consent";
    }

    @Override
    public String templateName() {
        return "OAuth.Authorization.Consent.display";
    }

    @Override
    public String consentServletUri() {
        return this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + CONSENT_PAGE_PATH;
    }

    @Override
    public String productName() {
        return StringUtils.capitalize((String)this.applicationProperties.getDisplayName().toLowerCase());
    }
}

