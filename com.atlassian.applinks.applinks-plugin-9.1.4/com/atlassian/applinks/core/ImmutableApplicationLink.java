/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.OAuth2ConnectionType
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider
 *  com.atlassian.applinks.api.auth.NonImpersonatingAuthenticationProvider
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.applinks.core;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.OAuth2ConnectionType;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider;
import com.atlassian.applinks.api.auth.NonImpersonatingAuthenticationProvider;
import com.atlassian.applinks.core.auth.ApplicationLinkRequestFactoryFactory;
import java.net.URI;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class ImmutableApplicationLink
implements ReadOnlyApplicationLink,
ApplicationLink {
    private final ApplicationId applicationId;
    private final ApplicationType applicationType;
    private final String name;
    private final URI displayUrl;
    private final URI rpcUrl;
    private final boolean isPrimary;
    private final boolean isSystem;
    private final OAuth2ConnectionType oAuth2ConnectionType;
    private final ApplicationLinkRequestFactoryFactory requestFactoryFactory;

    public ImmutableApplicationLink(ApplicationLink that, ApplicationLinkRequestFactoryFactory requestFactoryFactory) {
        Objects.requireNonNull(that, "that");
        Objects.requireNonNull(requestFactoryFactory, "requestFactoryFactory");
        this.applicationId = that.getId();
        this.applicationType = that.getType();
        this.name = that.getName();
        this.displayUrl = that.getDisplayUrl();
        this.rpcUrl = that.getRpcUrl();
        this.isPrimary = that.isPrimary();
        this.isSystem = that.isSystem();
        this.oAuth2ConnectionType = that.getOAuth2ConnectionType();
        this.requestFactoryFactory = requestFactoryFactory;
    }

    public ApplicationId getId() {
        return this.applicationId;
    }

    public ApplicationType getType() {
        return this.applicationType;
    }

    public String getName() {
        return this.name;
    }

    public URI getDisplayUrl() {
        return this.displayUrl;
    }

    public URI getRpcUrl() {
        return this.rpcUrl;
    }

    public boolean isPrimary() {
        return this.isPrimary;
    }

    public boolean isSystem() {
        return this.isSystem;
    }

    public OAuth2ConnectionType getOAuth2ConnectionType() {
        return this.oAuth2ConnectionType;
    }

    public ApplicationLinkRequestFactory createAuthenticatedRequestFactory() {
        return this.requestFactoryFactory.getApplicationLinkRequestFactory(this);
    }

    public ApplicationLinkRequestFactory createAuthenticatedRequestFactory(Class<? extends AuthenticationProvider> providerClass) {
        return this.requestFactoryFactory.getApplicationLinkRequestFactory(this, providerClass);
    }

    public ApplicationLinkRequestFactory createImpersonatingAuthenticatedRequestFactory() {
        return this.requestFactoryFactory.getApplicationLinkRequestFactory(this, ImpersonatingAuthenticationProvider.class);
    }

    public ApplicationLinkRequestFactory createNonImpersonatingAuthenticatedRequestFactory() {
        return this.requestFactoryFactory.getApplicationLinkRequestFactory(this, NonImpersonatingAuthenticationProvider.class);
    }

    public Object getProperty(String key) {
        return null;
    }

    public Object putProperty(String key, Object value) {
        throw new UnsupportedOperationException("putProperty not allowed on immutable applink");
    }

    public Object removeProperty(String key) {
        throw new UnsupportedOperationException("removeProperty not allowed on immutable applink");
    }

    public String toString() {
        return String.format("%s (%s) %s %s", this.name, this.applicationId, this.rpcUrl, this.applicationType);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableApplicationLink that = (ImmutableApplicationLink)o;
        return this.applicationId != null ? this.applicationId.equals((Object)that.applicationId) : that.applicationId == null;
    }

    public int hashCode() {
        return this.applicationId != null ? this.applicationId.hashCode() : 0;
    }
}

