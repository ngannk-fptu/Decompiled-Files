/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.page.PageCapabilities
 *  com.atlassian.sal.api.page.PageCapability
 *  com.atlassian.sal.api.user.UserRole
 *  com.atlassian.seraph.config.SecurityConfigFactory
 */
package com.atlassian.sal.core.auth;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.page.PageCapabilities;
import com.atlassian.sal.api.page.PageCapability;
import com.atlassian.sal.api.user.UserRole;
import com.atlassian.seraph.config.SecurityConfigFactory;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.EnumSet;

public class SeraphLoginUriProvider
implements LoginUriProvider {
    private final ApplicationProperties applicationProperties;

    public SeraphLoginUriProvider(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public URI getLoginUri(URI returnUri) {
        return this.getLoginUri(returnUri, PageCapabilities.empty());
    }

    public URI getLoginUri(URI returnUri, EnumSet<PageCapability> pageCaps) {
        return this.getLoginUriForRole(returnUri, UserRole.USER, pageCaps);
    }

    public URI getLoginUriForRole(URI returnUri, UserRole role) {
        return this.getLoginUriForRole(returnUri, role, PageCapabilities.empty());
    }

    public URI getLoginUriForRole(URI returnUri, UserRole role, EnumSet<PageCapability> pageCaps) {
        String loginURL = SecurityConfigFactory.getInstance().getLoginURL(true, true);
        try {
            String newUrl = loginURL.replace("${originalurl}", URLEncoder.encode(returnUri.toString(), "UTF-8")).replace("${userRole}", role.toString()).replace("${pageCaps}", PageCapabilities.toString(pageCaps));
            return new URI(this.applicationProperties.getBaseUrl(UrlMode.AUTO) + newUrl);
        }
        catch (UnsupportedEncodingException | URISyntaxException e) {
            throw new RuntimeException("Error getting login uri. LoginUrl = " + loginURL + ", ReturnUri = " + returnUri, e);
        }
    }
}

