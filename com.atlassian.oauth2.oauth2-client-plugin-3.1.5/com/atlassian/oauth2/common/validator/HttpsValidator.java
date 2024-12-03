/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.oauth2.common.validator;

import com.atlassian.oauth2.common.properties.BooleanSystemProperty;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;

public class HttpsValidator {
    private final ApplicationProperties applicationProperties;
    private final BooleanSystemProperty isDevModeRequired;
    private final BooleanSystemProperty isBaseUrlHttpsSkipped;

    public HttpsValidator(ApplicationProperties applicationProperties, BooleanSystemProperty isDevModeRequired, BooleanSystemProperty isBaseUrlHttpsSkipped) {
        this.applicationProperties = applicationProperties;
        this.isDevModeRequired = isDevModeRequired;
        this.isBaseUrlHttpsSkipped = isBaseUrlHttpsSkipped;
    }

    public boolean isHttps(String uri) {
        return StringUtils.isNotBlank((CharSequence)uri) && this.isHttps(URI.create(uri));
    }

    public boolean isHttps(URI uri) {
        return this.isHttpsProtocol(uri.getScheme());
    }

    public boolean isHttps(URL url) {
        return this.isHttpsProtocol(url.getProtocol());
    }

    public boolean isBaseUrlHttpsRequired() {
        return this.isDevModeRequired.getValue() == false && this.isBaseUrlHttpsSkipped.getValue() == false;
    }

    public boolean isBaseUrlHttps() {
        return this.isHttps(this.getBaseUrl());
    }

    private boolean isHttpsProtocol(String protocol) {
        return "https".equals(protocol);
    }

    public void enforceSecureBaseUrl(URI baseUrl) {
        Preconditions.checkState((!this.isBaseUrlHttpsRequired() || this.isHttps(baseUrl) ? 1 : 0) != 0, (Object)"Base url protocol needs to be https");
    }

    public String getBaseUrl() {
        return this.applicationProperties.getBaseUrl(UrlMode.CANONICAL);
    }
}

