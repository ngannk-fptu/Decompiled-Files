/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.google.common.base.Joiner
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.applinks.internal.rest;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.rest.RestUrl;
import com.atlassian.applinks.internal.rest.RestVersion;
import com.google.common.base.Joiner;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.Validate;

public class RestUrlBuilder {
    public static final RestUrl REST_CONTEXT = RestUrl.forPath("rest");
    public static final RestUrl APPLINKS_REST_MODULE = RestUrl.forPath("applinks");
    private URI baseUrl;
    private RestUrl module = APPLINKS_REST_MODULE;
    private RestVersion version = RestVersion.DEFAULT;
    private RestUrl path = RestUrl.EMPTY;
    private final List<String> queryParams = new ArrayList<String>();

    public RestUrlBuilder() {
    }

    public RestUrlBuilder(@Nonnull URI baseUrl) {
        this.baseUrl = RestUrlBuilder.validateBaseUrl(baseUrl);
    }

    public RestUrlBuilder(@Nonnull String baseUrl) {
        this(URI.create(baseUrl));
    }

    public RestUrlBuilder(@Nonnull RestUrlBuilder other) {
        this(Objects.requireNonNull(other, (String)"other").baseUrl);
        this.module = other.module;
        this.version = other.version;
        this.path = other.path;
    }

    @Nonnull
    public RestUrlBuilder baseUrl(@Nonnull URI baseUrl) {
        this.baseUrl = RestUrlBuilder.validateBaseUrl(baseUrl);
        return this;
    }

    @Nonnull
    public RestUrlBuilder to(@Nonnull ApplicationLink applicationLink) {
        return this.baseUrl(applicationLink.getRpcUrl());
    }

    @Nonnull
    public RestUrlBuilder baseUrl(@Nonnull String baseUrl) {
        return this.baseUrl(URI.create(baseUrl));
    }

    @Nonnull
    public RestUrlBuilder module(@Nonnull RestUrl module) {
        this.module = Objects.requireNonNull(module, "module");
        return this;
    }

    @Nonnull
    public RestUrlBuilder module(@Nonnull String modulePath) {
        return this.module(RestUrl.forPath(modulePath));
    }

    @Nonnull
    public RestUrlBuilder version(@Nonnull RestVersion version) {
        this.version = Objects.requireNonNull(version, "version");
        return this;
    }

    @Nonnull
    public RestUrlBuilder addPath(@Nonnull RestUrl path) {
        this.path = this.path.add(path);
        return this;
    }

    @Nonnull
    public RestUrlBuilder addPath(@Nonnull String path) {
        this.path = this.path.add(path);
        return this;
    }

    @Nonnull
    public RestUrlBuilder addApplicationId(@Nonnull ApplicationId applicationId) {
        return this.addPath(Objects.requireNonNull(applicationId, "applicationId").toString());
    }

    @Nonnull
    public RestUrlBuilder addApplink(@Nonnull ApplicationLink applicationLink) {
        return this.addApplicationId(Objects.requireNonNull(applicationLink, "applicationLink").getId());
    }

    @Nonnull
    public RestUrlBuilder queryParam(@Nonnull String name, @Nonnull String value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");
        this.queryParams.add(name + "=" + value);
        return this;
    }

    @Nonnull
    public URI build() {
        String url = this.getBaseUrl().add(REST_CONTEXT).add(this.module).add(this.version.getPath()).add(this.path).toString();
        if (!this.isAbsolute()) {
            url = "/" + url;
        }
        if (!this.queryParams.isEmpty()) {
            url = url + "?" + Joiner.on((String)"&").join(this.queryParams);
        }
        return URI.create(url);
    }

    public String toString() {
        return this.build().toString();
    }

    private static URI validateBaseUrl(URI baseUrl) {
        Objects.requireNonNull(baseUrl, "baseUrl");
        Validate.isTrue((boolean)baseUrl.isAbsolute(), (String)("Base URL was not an absolute URI: " + baseUrl), (Object[])new Object[0]);
        return baseUrl;
    }

    private boolean isAbsolute() {
        return this.baseUrl != null;
    }

    private RestUrl getBaseUrl() {
        return this.baseUrl != null ? RestUrl.forPath(this.baseUrl.toASCIIString()) : RestUrl.EMPTY;
    }
}

