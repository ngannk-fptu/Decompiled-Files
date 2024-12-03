/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.OAuth2ConnectionType
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider
 *  com.atlassian.applinks.api.auth.NonImpersonatingAuthenticationProvider
 *  com.atlassian.applinks.api.event.ApplicationLinkDetailsChangedEvent
 *  com.atlassian.applinks.spi.application.StaticUrlApplicationType
 *  com.atlassian.applinks.spi.link.ApplicationLinkDetails
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.link;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.OAuth2ConnectionType;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider;
import com.atlassian.applinks.api.auth.NonImpersonatingAuthenticationProvider;
import com.atlassian.applinks.api.event.ApplicationLinkDetailsChangedEvent;
import com.atlassian.applinks.core.ImmutableApplicationLink;
import com.atlassian.applinks.core.auth.ApplicationLinkRequestFactoryFactory;
import com.atlassian.applinks.core.link.InternalApplicationLink;
import com.atlassian.applinks.core.property.ApplicationLinkProperties;
import com.atlassian.applinks.spi.application.StaticUrlApplicationType;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import com.atlassian.event.api.EventPublisher;
import java.net.URI;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultApplicationLink
implements InternalApplicationLink {
    private final ApplicationId id;
    private final ApplicationType type;
    private final ApplicationLinkProperties applicationLinkProperties;
    private final ApplicationLinkRequestFactoryFactory requestFactoryFactory;
    private final EventPublisher eventPublisher;
    private static final Logger LOG = LoggerFactory.getLogger((String)DefaultApplicationLink.class.getName());

    public DefaultApplicationLink(ApplicationId serverId, ApplicationType type, ApplicationLinkProperties applicationLinkProperties, ApplicationLinkRequestFactoryFactory requestFactoryFactory, EventPublisher eventPublisher) {
        this.id = Objects.requireNonNull(serverId, "serverId can't be null");
        this.applicationLinkProperties = Objects.requireNonNull(applicationLinkProperties, "applicationLinkProperties can't be null");
        this.requestFactoryFactory = Objects.requireNonNull(requestFactoryFactory, "requestFactoryFactory can't be null");
        this.type = Objects.requireNonNull(type, "type can't be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher can't be null");
    }

    public void update(ApplicationLinkDetails details) {
        ImmutableApplicationLink originalApplink = new ImmutableApplicationLink((ApplicationLink)this, this.requestFactoryFactory);
        this.applicationLinkProperties.setName(details.getName());
        this.applicationLinkProperties.setDisplayUrl(details.getDisplayUrl());
        this.applicationLinkProperties.setRpcUrl(details.getRpcUrl());
        this.eventPublisher.publish((Object)new ApplicationLinkDetailsChangedEvent((ApplicationLink)this, (ApplicationLink)originalApplink));
    }

    @Override
    public void setPrimaryFlag(boolean isPrimary) {
        this.applicationLinkProperties.setIsPrimary(isPrimary);
    }

    @Override
    public void setSystem(boolean isSystem) {
        this.applicationLinkProperties.setSystem(isSystem);
    }

    public ApplicationId getId() {
        return this.id;
    }

    public ApplicationType getType() {
        return this.type;
    }

    public String getName() {
        return this.applicationLinkProperties.getName();
    }

    public URI getDisplayUrl() {
        if (this.type instanceof StaticUrlApplicationType) {
            return ((StaticUrlApplicationType)this.type).getStaticUrl();
        }
        return this.applicationLinkProperties.getDisplayUrl();
    }

    public URI getRpcUrl() {
        if (this.type instanceof StaticUrlApplicationType) {
            return ((StaticUrlApplicationType)this.type).getStaticUrl();
        }
        return this.applicationLinkProperties.getRpcUrl();
    }

    public boolean isPrimary() {
        return this.applicationLinkProperties.isPrimary();
    }

    public boolean isSystem() {
        return this.applicationLinkProperties.isSystem();
    }

    public OAuth2ConnectionType getOAuth2ConnectionType() {
        return this.applicationLinkProperties.getOAuth2ConnectionType();
    }

    public ApplicationLinkRequestFactory createAuthenticatedRequestFactory() {
        return this.requestFactoryFactory.getApplicationLinkRequestFactory((ApplicationLink)this);
    }

    public ApplicationLinkRequestFactory createAuthenticatedRequestFactory(Class<? extends AuthenticationProvider> providerClass) {
        return this.requestFactoryFactory.getApplicationLinkRequestFactory((ApplicationLink)this, providerClass);
    }

    public ApplicationLinkRequestFactory createImpersonatingAuthenticatedRequestFactory() {
        return this.requestFactoryFactory.getApplicationLinkRequestFactory((ApplicationLink)this, ImpersonatingAuthenticationProvider.class);
    }

    public ApplicationLinkRequestFactory createNonImpersonatingAuthenticatedRequestFactory() {
        return this.requestFactoryFactory.getApplicationLinkRequestFactory((ApplicationLink)this, NonImpersonatingAuthenticationProvider.class);
    }

    public Object getProperty(String key) {
        return this.applicationLinkProperties.getProperty(key);
    }

    public Object putProperty(String key, Object value) {
        if (LOG.isDebugEnabled()) {
            String message = String.format("Putting property [%s] as [%s] for application link [%s/%s]", key, value, this.getId() != null ? this.getId().get() : null, this.getRpcUrl() != null ? this.getRpcUrl().toString() : null);
            LOG.debug(message);
        }
        return this.applicationLinkProperties.putProperty(key, value);
    }

    public Object removeProperty(String key) {
        if (LOG.isDebugEnabled()) {
            String message = String.format("Removing property [%s] was [%s] for application link [%s/%s]", key, this.applicationLinkProperties.getProperty(key), this.getId() != null ? this.getId().get() : null, this.getRpcUrl() != null ? this.getRpcUrl().toString() : null);
            LOG.debug(message);
        }
        return this.applicationLinkProperties.removeProperty(key);
    }

    public String toString() {
        return String.format("%s (%s) %s %s", this.getName(), this.id, this.getRpcUrl(), this.getType());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultApplicationLink that = (DefaultApplicationLink)o;
        return !(this.id != null ? !this.id.equals((Object)that.id) : that.id != null);
    }

    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }
}

