/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.core.rest.model.ApplicationLinkEntity;
import com.atlassian.applinks.core.rest.model.WebItemEntity;
import com.atlassian.applinks.core.rest.model.WebPanelEntity;
import com.atlassian.applinks.core.rest.util.EntityUtil;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="linkAndAuthProviderEntity")
public class LinkAndAuthProviderEntity {
    private ApplicationLinkEntity application;
    private Set<String> configuredOutboundAuthenticators;
    private Set<String> configuredInboundAuthenticators;
    private boolean hasIncomingAuthenticationProviders;
    private boolean hasOutgoingAuthenticationProviders;
    private List<WebItemEntity> webItems;
    private List<WebPanelEntity> webPanels;
    private boolean isSystem;
    private Set<String> entityTypeIdStrings;

    public LinkAndAuthProviderEntity() {
    }

    public LinkAndAuthProviderEntity(ApplicationLinkEntity applicationLinkEntity, Set<Class<? extends AuthenticationProvider>> configuredOutboundAuthenticators, Set<Class<? extends AuthenticationProvider>> configuredInboundAuthenticators, boolean hasOutgoingAuthenticationProviders, boolean hasIncomingAuthenticationProviders, List<WebItemEntity> webItems, List<WebPanelEntity> webPanels, Set<String> entityTypeIdStrings, boolean isSystem) {
        this.hasOutgoingAuthenticationProviders = hasOutgoingAuthenticationProviders;
        this.hasIncomingAuthenticationProviders = hasIncomingAuthenticationProviders;
        this.webItems = webItems;
        this.webPanels = webPanels;
        this.application = applicationLinkEntity;
        this.configuredOutboundAuthenticators = EntityUtil.getClassNames(configuredOutboundAuthenticators);
        this.configuredInboundAuthenticators = EntityUtil.getClassNames(configuredInboundAuthenticators);
        this.entityTypeIdStrings = entityTypeIdStrings;
        this.isSystem = isSystem;
    }

    public Set<String> getConfiguredOutboundAuthenticators() {
        return this.configuredOutboundAuthenticators;
    }

    public ApplicationLinkEntity getApplication() {
        return this.application;
    }

    public boolean hasIncomingAuthenticationProviders() {
        return this.hasIncomingAuthenticationProviders;
    }

    public boolean hasOutgoingAuthenticationProviders() {
        return this.hasOutgoingAuthenticationProviders;
    }

    public List<WebItemEntity> getWebItems() {
        return this.webItems;
    }

    public List<WebPanelEntity> getWebPanels() {
        return this.webPanels;
    }

    public Set<String> getEntityTypeIdStrings() {
        return this.entityTypeIdStrings;
    }

    public Set<String> getConfiguredInboundAuthenticators() {
        return this.configuredInboundAuthenticators;
    }

    public boolean isSystem() {
        return this.isSystem;
    }
}

