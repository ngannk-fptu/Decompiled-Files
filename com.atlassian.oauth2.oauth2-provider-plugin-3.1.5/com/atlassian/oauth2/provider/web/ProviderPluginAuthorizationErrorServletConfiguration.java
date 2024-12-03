/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 */
package com.atlassian.oauth2.provider.web;

import com.atlassian.oauth2.provider.core.web.servlet.AuthorizationErrorServletConfiguration;
import com.atlassian.oauth2.provider.web.ServletConfiguration;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;

public class ProviderPluginAuthorizationErrorServletConfiguration
extends ServletConfiguration
implements AuthorizationErrorServletConfiguration {
    private static final String CONSENT_PAGE_PATH = "/plugins/servlet/oauth2/error";

    public ProviderPluginAuthorizationErrorServletConfiguration(ApplicationProperties applicationProperties) {
        super(applicationProperties);
    }

    @Override
    public String moduleKey() {
        return "com.atlassian.oauth2.oauth2-provider-plugin:oauth2-provider-consent";
    }

    @Override
    public String templateName() {
        return "OAuth.Authorization.Error.display";
    }

    @Override
    public String errorServletUri() {
        return this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + CONSENT_PAGE_PATH;
    }
}

