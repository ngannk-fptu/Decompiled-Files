/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.AuthorisationURIGenerator
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.auth.Anonymous
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.google.common.collect.Maps
 *  com.google.common.primitives.Ints
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.applinks.internal.rest.client;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.AuthorisationURIGenerator;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.Anonymous;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.internal.rest.RestUrlBuilder;
import com.atlassian.applinks.internal.rest.client.AuthorisationUriAwareRequest;
import com.atlassian.applinks.internal.rest.client.DefaultAuthorisationUriAwareRequest;
import com.atlassian.sal.api.net.Request;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public final class RestRequestBuilder {
    private static final long DEFAULT_TIMEOUT_SECONDS = 15L;
    private final ApplicationLink link;
    private final Map<String, String> headers = Maps.newHashMap();
    private RestUrlBuilder url = new RestUrlBuilder();
    private Request.MethodType methodType = Request.MethodType.GET;
    private String accept = "application/json";
    private Class<? extends AuthenticationProvider> authentication = Anonymous.class;
    private Object body = null;
    private String contentType = "application/json";
    private int connectionTimeoutMillis = (int)TimeUnit.SECONDS.toMillis(15L);
    private int socketTimeoutMillis = (int)TimeUnit.SECONDS.toMillis(15L);
    private boolean followRedirects = true;

    @Nonnull
    public static AuthorisationUriAwareRequest createAnonymousRequest(@Nonnull ApplicationLink link, @Nonnull RestUrlBuilder url, @Nonnull Request.MethodType methodType) {
        return new RestRequestBuilder(link).methodType(methodType).url(url).buildAnonymous();
    }

    @Nonnull
    public static AuthorisationUriAwareRequest createAnonymousRequest(@Nonnull ApplicationLink link, @Nonnull RestUrlBuilder url) {
        return new RestRequestBuilder(link).url(url).buildAnonymous();
    }

    public RestRequestBuilder(@Nonnull ApplicationLink link) {
        this.link = Objects.requireNonNull(link, "link");
    }

    @Nonnull
    public RestRequestBuilder url(@Nonnull RestUrlBuilder url) {
        this.url = Objects.requireNonNull(url, "url");
        return this;
    }

    @Nonnull
    public RestRequestBuilder anonymous() {
        return this.authentication(Anonymous.class);
    }

    @Nonnull
    public RestRequestBuilder authentication(@Nonnull Class<? extends AuthenticationProvider> authentication) {
        this.authentication = Objects.requireNonNull(authentication, "authentication");
        return this;
    }

    @Nonnull
    public RestRequestBuilder body(@Nullable Object body) {
        this.body = body;
        return this;
    }

    @Nonnull
    public RestRequestBuilder contentType(@Nonnull String contentType) {
        this.contentType = Objects.requireNonNull(contentType, "contentType");
        return this;
    }

    @Nonnull
    public RestRequestBuilder accept(@Nonnull String mediaType) {
        this.accept = Objects.requireNonNull(mediaType, "mimeType");
        return this;
    }

    @Nonnull
    public RestRequestBuilder methodType(@Nonnull Request.MethodType methodType) {
        this.methodType = Objects.requireNonNull(methodType, "methodType");
        return this;
    }

    @Nonnull
    public RestRequestBuilder header(@Nonnull String name, @Nonnull String value) {
        this.headers.put(Objects.requireNonNull(name, "name"), Objects.requireNonNull(value, "value"));
        return this;
    }

    @Nonnull
    public RestRequestBuilder connectionTimeout(long value, @Nonnull TimeUnit unit) {
        Validate.isTrue((value > 0L ? 1 : 0) != 0, (String)"timeout value must be >0", (Object[])new Object[0]);
        this.connectionTimeoutMillis = Ints.saturatedCast((long)unit.toMillis(value));
        return this;
    }

    @Nonnull
    public RestRequestBuilder socketTimeout(long value, @Nonnull TimeUnit unit) {
        Validate.isTrue((value > 0L ? 1 : 0) != 0, (String)"timeout value must be >0", (Object[])new Object[0]);
        this.socketTimeoutMillis = Ints.saturatedCast((long)unit.toMillis(value));
        return this;
    }

    @Nonnull
    public RestRequestBuilder followRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }

    @Nonnull
    public AuthorisationUriAwareRequest build() throws CredentialsRequiredException {
        return this.buildRequest(this.link.createAuthenticatedRequestFactory(this.authentication));
    }

    @Nonnull
    public Optional<AuthorisationUriAwareRequest> buildOptional() throws CredentialsRequiredException {
        ApplicationLinkRequestFactory requestFactory = this.link.createAuthenticatedRequestFactory(this.authentication);
        if (requestFactory == null) {
            return Optional.empty();
        }
        return Optional.of(this.buildRequest(requestFactory));
    }

    private AuthorisationUriAwareRequest buildRequest(@Nonnull ApplicationLinkRequestFactory requestFactory) throws CredentialsRequiredException {
        ApplicationLinkRequest request = (ApplicationLinkRequest)((ApplicationLinkRequest)((ApplicationLinkRequest)((ApplicationLinkRequest)((ApplicationLinkRequest)requestFactory.createRequest(this.methodType, this.url.toString()).setHeader("Accept", this.accept)).addHeader("X-Atlassian-Token", "no-check")).setConnectionTimeout(this.connectionTimeoutMillis)).setSoTimeout(this.socketTimeoutMillis)).setFollowRedirects(this.followRedirects);
        if (this.body != null && this.isMutatingMethodType()) {
            request.setEntity(this.body);
            request.setHeader("Content-Type", this.contentType);
        }
        for (Map.Entry<String, String> header : this.headers.entrySet()) {
            request.setHeader(header.getKey(), header.getValue());
        }
        return new DefaultAuthorisationUriAwareRequest(request, (AuthorisationURIGenerator)requestFactory);
    }

    @Nonnull
    public AuthorisationUriAwareRequest buildAnonymous() {
        try {
            return this.build();
        }
        catch (CredentialsRequiredException e) {
            throw new IllegalStateException("Unexpected credentials required", e);
        }
    }

    private boolean isMutatingMethodType() {
        return this.methodType == Request.MethodType.POST || this.methodType == Request.MethodType.PUT;
    }
}

