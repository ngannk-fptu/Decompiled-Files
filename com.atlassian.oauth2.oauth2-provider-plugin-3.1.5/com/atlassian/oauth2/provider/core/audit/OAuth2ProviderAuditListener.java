/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.oauth2.provider.api.authorization.Authorization
 *  com.atlassian.oauth2.provider.api.client.Client
 *  com.atlassian.oauth2.provider.api.event.authorization.ClientAuthorizationEvent
 *  com.atlassian.oauth2.provider.api.event.client.ClientConfigurationCreatedEvent
 *  com.atlassian.oauth2.provider.api.event.client.ClientConfigurationDeletedEvent
 *  com.atlassian.oauth2.provider.api.event.client.ClientConfigurationEvent
 *  com.atlassian.oauth2.provider.api.event.client.ClientConfigurationUpdatedEvent
 *  com.atlassian.oauth2.provider.api.event.client.ClientSecretRefreshEvent
 *  com.atlassian.oauth2.provider.api.event.token.TokenCreatedEvent
 *  com.atlassian.oauth2.provider.api.event.token.TokenEvent
 *  com.atlassian.oauth2.provider.api.event.token.TokenRevokedEvent
 *  com.atlassian.sal.api.user.UserKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.oauth2.provider.core.audit;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.oauth2.provider.api.authorization.Authorization;
import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.provider.api.event.authorization.ClientAuthorizationEvent;
import com.atlassian.oauth2.provider.api.event.client.ClientConfigurationCreatedEvent;
import com.atlassian.oauth2.provider.api.event.client.ClientConfigurationDeletedEvent;
import com.atlassian.oauth2.provider.api.event.client.ClientConfigurationEvent;
import com.atlassian.oauth2.provider.api.event.client.ClientConfigurationUpdatedEvent;
import com.atlassian.oauth2.provider.api.event.client.ClientSecretRefreshEvent;
import com.atlassian.oauth2.provider.api.event.token.TokenCreatedEvent;
import com.atlassian.oauth2.provider.api.event.token.TokenEvent;
import com.atlassian.oauth2.provider.api.event.token.TokenRevokedEvent;
import com.atlassian.oauth2.provider.core.user.ProductUserProvider;
import com.atlassian.sal.api.user.UserKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class OAuth2ProviderAuditListener
implements InitializingBean,
DisposableBean {
    private final Logger logger = LoggerFactory.getLogger(OAuth2ProviderAuditListener.class);
    private final AuditService auditService;
    private final EventPublisher eventPublisher;
    private final ProductUserProvider productUserProvider;

    public OAuth2ProviderAuditListener(AuditService auditService, EventPublisher eventPublisher, ProductUserProvider productUserProvider) {
        this.auditService = auditService;
        this.eventPublisher = eventPublisher;
        this.productUserProvider = productUserProvider;
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onClientAuthorization(ClientAuthorizationEvent event) {
        this.logger.debug("Received ClientAuthorizationEvent: {}", (Object)event);
        this.auditService.audit(this.buildClientAuthorizationAuditEvent(this.getAttributesForAuthorization(event.getAuthorization()), event.getAuthorization().getUserKey()));
    }

    @EventListener
    public void onClientConfigurationCreated(ClientConfigurationCreatedEvent event) {
        this.logger.debug("Received ClientConfigurationCreatedEvent: {}", (Object)event);
        this.auditService.audit(this.buildClientConfigurationAuditEvent("audit.logging.summary.client.configuration.created", this.getChangedValuesForClient((ClientConfigurationEvent)event), this.getAttributesForClientEvent(event.getNewClient().getClientId(), event.getNewClient().getUserKey())));
    }

    @EventListener
    public void onClientConfigurationUpdated(ClientConfigurationUpdatedEvent event) {
        this.logger.debug("Received ClientConfigurationUpdatedEvent: {}", (Object)event);
        List<ChangedValue> changedValues = this.getChangedValuesForClient((ClientConfigurationEvent)event);
        if (!changedValues.isEmpty()) {
            this.auditService.audit(this.buildClientConfigurationAuditEvent("audit.logging.summary.client.configuration.updated", changedValues, this.getAttributesForClientEvent(event.getNewClient().getClientId(), event.getNewClient().getUserKey())));
        }
    }

    @EventListener
    public void onClientConfigurationDeleted(ClientConfigurationDeletedEvent event) {
        this.logger.debug("Received ClientConfigurationDeletedEvent: {}", (Object)event);
        this.auditService.audit(this.buildClientConfigurationAuditEvent("audit.logging.summary.client.configuration.deleted", this.getChangedValuesForClient((ClientConfigurationEvent)event), this.getAttributesForClientEvent(event.getOldClient().getClientId(), event.getOldClient().getUserKey())));
    }

    @EventListener
    public void onClientSecretRefreshed(ClientSecretRefreshEvent event) {
        this.logger.debug("Received ClientSecretRefreshEvent: {}", (Object)event);
        this.auditService.audit(this.buildClientConfigurationAuditEvent("audit.logging.summary.client.secret.refreshed", Collections.emptyList(), this.getAttributesForClientSecretRefreshEvent(event)));
    }

    @EventListener
    public void onTokenCreated(TokenCreatedEvent event) {
        this.logger.debug("Received TokenCreatedEvent: {}", (Object)event);
        this.auditService.audit(this.buildTokenAuditEvent("audit.logging.summary.token.created", this.getAttributesForTokenEvent((TokenEvent)event), event.getUserKey()));
    }

    @EventListener
    public void onTokenRevoked(TokenRevokedEvent event) {
        this.logger.debug("Received TokenCreatedEvent: {}", (Object)event);
        this.auditService.audit(this.buildTokenAuditEvent("audit.logging.summary.token.revoked", this.getAttributesForTokenEvent((TokenEvent)event), event.getUserKey()));
    }

    private AuditEvent buildClientAuthorizationAuditEvent(List<AuditAttribute> extraAttributes, String userKey) {
        return this.getBaseAuditEventBuilder("audit.logging.category.security", "audit.logging.summary.client.authorization.successful", Collections.emptyList(), extraAttributes, userKey).build();
    }

    private AuditEvent buildClientConfigurationAuditEvent(String summary, List<ChangedValue> changedValues, List<AuditAttribute> extraAttributes) {
        return this.getBaseAuditEventBuilder("audit.logging.category.general.configuration", summary, changedValues, extraAttributes).build();
    }

    private AuditEvent buildTokenAuditEvent(String summary, List<AuditAttribute> extraAttributes, String userKey) {
        return this.getBaseAuditEventBuilder("audit.logging.category.security", summary, Collections.emptyList(), extraAttributes, userKey).build();
    }

    private AuditEvent.Builder getBaseAuditEventBuilder(String key, String summary, List<ChangedValue> changedValues, List<AuditAttribute> extraAttributes, String userKey) {
        AuditEvent.Builder auditEventBuilder = this.getBaseAuditEventBuilder(key, summary, changedValues, extraAttributes);
        this.productUserProvider.getUsernameForKey(new UserKey(userKey)).ifPresent(username -> auditEventBuilder.affectedObject(AuditResource.builder((String)username, (String)"USER").id(userKey).build()));
        return auditEventBuilder;
    }

    private AuditEvent.Builder getBaseAuditEventBuilder(String key, String summary, List<ChangedValue> changedValues, List<AuditAttribute> extraAttributes) {
        return AuditEvent.fromI18nKeys((String)key, (String)summary, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).changedValues(changedValues).extraAttributes(extraAttributes);
    }

    private List<ChangedValue> getChangedValuesForClient(ClientConfigurationEvent event) {
        ArrayList<ChangedValue> changedValues = new ArrayList<ChangedValue>();
        Client oldClient = event.getOldClient();
        Client newClient = event.getNewClient();
        if (event instanceof ClientConfigurationUpdatedEvent) {
            if (!oldClient.getName().equals(newClient.getName())) {
                changedValues.add(this.getChangedValue("audit.logging.summary.client.name", oldClient.getName(), newClient.getName()));
            }
            if (!oldClient.getScope().getScopeAndInheritedScopes().equals(newClient.getScope().getScopeAndInheritedScopes())) {
                changedValues.add(this.getChangedValue("audit.logging.summary.scopes", oldClient.getScope().toString(), newClient.getScope().toString()));
            }
            if (!new HashSet(oldClient.getRedirects()).equals(new HashSet(newClient.getRedirects()))) {
                changedValues.add(this.getChangedValue("audit.logging.summary.redirect.uri", this.formatRedirects(oldClient.getRedirects()), this.formatRedirects(newClient.getRedirects())));
            }
        } else if (event instanceof ClientConfigurationCreatedEvent) {
            changedValues.add(this.getChangedValue("audit.logging.summary.client.name", "", newClient.getName()));
            changedValues.add(this.getChangedValue("audit.logging.summary.scopes", "", newClient.getScope().toString()));
            changedValues.add(this.getChangedValue("audit.logging.summary.redirect.uri", "", this.formatRedirects(newClient.getRedirects())));
        } else if (event instanceof ClientConfigurationDeletedEvent) {
            changedValues.add(this.getChangedValue("audit.logging.summary.client.name", oldClient.getName(), ""));
            changedValues.add(this.getChangedValue("audit.logging.summary.scopes", oldClient.getScope().toString(), ""));
            changedValues.add(this.getChangedValue("audit.logging.summary.redirect.uri", this.formatRedirects(oldClient.getRedirects()), ""));
        }
        return changedValues;
    }

    private ChangedValue getChangedValue(String key, String oldValue, String newValue) {
        return ChangedValue.fromI18nKeys((String)key).from(oldValue).to(newValue).build();
    }

    private List<AuditAttribute> getAttributesForAuthorization(Authorization authorization) {
        ArrayList<AuditAttribute> attributes = new ArrayList<AuditAttribute>();
        attributes.add(this.getAuditAttribute("audit.logging.summary.user.key", authorization.getUserKey()));
        attributes.add(this.getAuditAttribute("audit.logging.summary.client.id", authorization.getClientId()));
        attributes.add(this.getAuditAttribute("audit.logging.summary.redirect.uri", authorization.getRedirectUri()));
        return attributes;
    }

    private List<AuditAttribute> getAttributesForClientSecretRefreshEvent(ClientSecretRefreshEvent event) {
        List<AuditAttribute> attributes = this.getAttributesForClientEvent(event.getClient().getClientId(), event.getClient().getUserKey());
        attributes.add(this.getAuditAttribute("audit.logging.summary.client.name", event.getClient().getName()));
        return attributes;
    }

    private List<AuditAttribute> getAttributesForClientEvent(String clientId, String userKey) {
        ArrayList<AuditAttribute> attributes = new ArrayList<AuditAttribute>();
        attributes.add(this.getAuditAttribute("audit.logging.summary.client.id", clientId));
        attributes.add(this.getAuditAttribute("audit.logging.summary.user.key", userKey));
        return attributes;
    }

    private List<AuditAttribute> getAttributesForTokenEvent(TokenEvent event) {
        ArrayList<AuditAttribute> attributes = new ArrayList<AuditAttribute>();
        attributes.add(this.getAuditAttribute("audit.logging.summary.client.id", event.getClientId()));
        if (event instanceof TokenCreatedEvent) {
            TokenCreatedEvent tokenCreatedEvent = (TokenCreatedEvent)event;
            attributes.add(this.getAuditAttribute("audit.logging.summary.scopes", tokenCreatedEvent.getScope()));
            attributes.add(this.getAuditAttribute("audit.logging.summary.token.refresh.count", String.valueOf(tokenCreatedEvent.getRefreshCount())));
        }
        attributes.add(this.getAuditAttribute("audit.logging.summary.user.key", event.getUserKey()));
        return attributes;
    }

    private AuditAttribute getAuditAttribute(String key, String value) {
        return AuditAttribute.fromI18nKeys((String)key, (String)value).build();
    }

    private String formatRedirects(List<String> redirects) {
        StringBuilder redirectStringBuilder = new StringBuilder();
        for (String redirect : redirects) {
            if (redirects.indexOf(redirect) == redirects.size() - 1) {
                redirectStringBuilder.append(redirect);
                continue;
            }
            redirectStringBuilder.append(redirect).append("\n");
        }
        return redirectStringBuilder.toString();
    }

    public static interface Keys {
        public static final String SECURITY_KEY = "audit.logging.category.security";
        public static final String GENERAL_CONFIGURATION_KEY = "audit.logging.category.general.configuration";
        public static final String CLIENT_AUTHORIZATION_SUCCESSFUL = "audit.logging.summary.client.authorization.successful";
        public static final String CLIENT_CONFIGURATION_CREATED = "audit.logging.summary.client.configuration.created";
        public static final String CLIENT_CONFIGURATION_UPDATED = "audit.logging.summary.client.configuration.updated";
        public static final String CLIENT_CONFIGURATION_DELETED = "audit.logging.summary.client.configuration.deleted";
        public static final String CLIENT_SECRET_REFRESHED = "audit.logging.summary.client.secret.refreshed";
        public static final String TOKEN_CREATED = "audit.logging.summary.token.created";
        public static final String TOKEN_REVOKED = "audit.logging.summary.token.revoked";
        public static final String REFRESH_COUNT = "audit.logging.summary.token.refresh.count";
        public static final String USER_KEY = "audit.logging.summary.user.key";
        public static final String CLIENT_ID = "audit.logging.summary.client.id";
        public static final String CLIENT_NAME = "audit.logging.summary.client.name";
        public static final String REDIRECT_URI = "audit.logging.summary.redirect.uri";
        public static final String SCOPES = "audit.logging.summary.scopes";
    }
}

