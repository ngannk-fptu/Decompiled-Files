/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.upm.core.rest;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.upm.core.Sys;
import java.net.URI;
import java.util.Objects;
import javax.ws.rs.core.UriBuilder;

public abstract class CoreUriBuilder {
    private final ApplicationProperties applicationProperties;
    private final String baseUrl;

    protected CoreUriBuilder(ApplicationProperties applicationProperties, String baseUrl) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl");
    }

    public final URI makeAbsolute(URI uri) {
        return this.makeAbsolute(URI.create(this.applicationProperties.getBaseUrl(UrlMode.ABSOLUTE)), uri);
    }

    protected final URI makeAbsolute(URI base, URI path) {
        if (path.isAbsolute()) {
            return path;
        }
        return base.resolve(path).normalize();
    }

    protected UriBuilder newApplicationBaseUriBuilder() {
        URI base = URI.create(this.applicationProperties.getBaseUrl(UrlMode.ABSOLUTE)).normalize();
        return UriBuilder.fromPath((String)base.getPath());
    }

    protected UriBuilder newPluginBaseUriBuilder() {
        return this.newApplicationBaseUriBuilder().path(this.baseUrl);
    }

    public URI buildAtlassianIdLoginUri() {
        return URI.create(Sys.getAtlassianIdBaseUrl() + "/id/rest/login");
    }

    public URI buildShoppingCartUri() {
        return URI.create(Sys.getShoppingCartBaseUrl());
    }

    public URI buildGoAtlassianUri(String redirectPath) {
        return URI.create(Sys.getGoAtlassianBaseUrl() + redirectPath);
    }

    public URI buildMpacBaseurl() {
        return URI.create(Sys.getMpacBaseUrl());
    }

    public URI buildMacCreateAppEvalLicenseUri() {
        return URI.create(Sys.getMacBaseUrl() + "/license/evaluation");
    }

    public URI buildHamletCreateEvalLicenseUri() {
        return URI.create(Sys.getHamletBaseUrl() + "/1.0/public/license/createEvaluation");
    }

    public URI buildHamletCrossgradeLicenseUri() {
        return URI.create(Sys.getHamletBaseUrl() + "/1.0/public/addon/crossgrade");
    }

    public URI buildBillingProxyUri() {
        return URI.create(Sys.getMacBillingUrl() + "/proxy");
    }
}

