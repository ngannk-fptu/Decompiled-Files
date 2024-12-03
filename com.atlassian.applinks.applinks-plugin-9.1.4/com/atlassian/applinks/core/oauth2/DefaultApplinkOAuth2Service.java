/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.OAuth2ConnectionType
 *  com.atlassian.applinks.api.oauth2.ApplinkOAuth2Service
 *  com.atlassian.applinks.spi.util.TypeAccessor
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity
 *  com.atlassian.oauth2.provider.api.client.Client
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core.oauth2;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.OAuth2ConnectionType;
import com.atlassian.applinks.api.oauth2.ApplinkOAuth2Service;
import com.atlassian.applinks.application.generic.GenericApplicationTypeImpl;
import com.atlassian.applinks.core.auth.ApplicationLinkRequestFactoryFactory;
import com.atlassian.applinks.core.link.DefaultApplicationLink;
import com.atlassian.applinks.core.oauth2.ClientConfigStorageServiceFactory;
import com.atlassian.applinks.core.oauth2.OAuth2ProviderServiceFactory;
import com.atlassian.applinks.core.property.ApplicationLinkProperties;
import com.atlassian.applinks.core.property.PropertyService;
import com.atlassian.applinks.spi.util.TypeAccessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity;
import com.atlassian.oauth2.provider.api.client.Client;
import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultApplinkOAuth2Service
implements ApplinkOAuth2Service {
    private final ClientConfigStorageServiceFactory clientConfigStorageServiceFactory;
    private final OAuth2ProviderServiceFactory oAuth2ProviderServiceFactory;
    private final TypeAccessor typeAccessor;
    private final PropertyService propertyService;
    private final ApplicationLinkRequestFactoryFactory requestFactoryFactory;
    private final EventPublisher eventPublisher;

    @Autowired
    public DefaultApplinkOAuth2Service(ClientConfigStorageServiceFactory clientConfigStorageServiceFactory, OAuth2ProviderServiceFactory oAuth2ProviderServiceFactory, TypeAccessor typeAccessor, PropertyService propertyService, ApplicationLinkRequestFactoryFactory requestFactoryFactory, EventPublisher eventPublisher) {
        this.clientConfigStorageServiceFactory = Objects.requireNonNull(clientConfigStorageServiceFactory);
        this.oAuth2ProviderServiceFactory = Objects.requireNonNull(oAuth2ProviderServiceFactory);
        this.typeAccessor = Objects.requireNonNull(typeAccessor);
        this.propertyService = Objects.requireNonNull(propertyService);
        this.requestFactoryFactory = Objects.requireNonNull(requestFactoryFactory);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public Iterable<ApplicationLink> getApplicationLinksForOAuth2Clients() {
        return this.clientConfigStorageServiceFactory.get().map(service -> service.list().stream()).orElse(Stream.empty()).map(this::createLinkFromClient).collect(Collectors.toList());
    }

    private ApplicationLink createLinkFromClient(ClientConfigurationEntity client) {
        ApplicationId id = new ApplicationId(client.getId());
        ApplicationType type = this.typeAccessor.loadApplicationType(GenericApplicationTypeImpl.TYPE_ID);
        ApplicationLinkProperties applicationLinkProperties = this.propertyService.getApplicationLinkProperties(Objects.requireNonNull(id));
        if (client.getAuthorizationEndpoint() != null) {
            URI uri = URI.create(client.getAuthorizationEndpoint());
            String schemaAndHost = String.format("%s://%s", uri.getScheme(), uri.getHost());
            applicationLinkProperties.setDisplayUrl(schemaAndHost);
            applicationLinkProperties.setRpcUrl(schemaAndHost);
        }
        applicationLinkProperties.setName(client.getName());
        applicationLinkProperties.setOAuth2ConnectionType(OAuth2ConnectionType.OAUTH2_CLIENT);
        return new DefaultApplicationLink(id, type, applicationLinkProperties, this.requestFactoryFactory, this.eventPublisher);
    }

    public Iterable<ApplicationLink> getApplicationLinksForOAuth2Provider() {
        return this.oAuth2ProviderServiceFactory.get().map(service -> service.listClients().stream()).orElse(Stream.empty()).map(this::createLinkFromProvider).collect(Collectors.toList());
    }

    private ApplicationLink createLinkFromProvider(Client client) {
        ApplicationId id = new ApplicationId(client.getId());
        ApplicationType type = this.typeAccessor.loadApplicationType(GenericApplicationTypeImpl.TYPE_ID);
        ApplicationLinkProperties applicationLinkProperties = this.propertyService.getApplicationLinkProperties(Objects.requireNonNull(id));
        if (client.getRedirects() != null || !client.getRedirects().isEmpty()) {
            URI uri = URI.create((String)client.getRedirects().get(0));
            String schemaAndHost = String.format("%s://%s", uri.getScheme(), uri.getHost());
            applicationLinkProperties.setDisplayUrl(schemaAndHost);
            applicationLinkProperties.setRpcUrl(schemaAndHost);
        }
        applicationLinkProperties.setName(client.getName());
        applicationLinkProperties.setOAuth2ConnectionType(OAuth2ConnectionType.OAUTH2_PROVIDER);
        return new DefaultApplicationLink(id, type, applicationLinkProperties, this.requestFactoryFactory, this.eventPublisher);
    }
}

