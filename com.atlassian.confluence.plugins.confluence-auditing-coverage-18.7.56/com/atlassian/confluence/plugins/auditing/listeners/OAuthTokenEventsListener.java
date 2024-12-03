/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.confluence.audit.AuditingContext
 *  com.atlassian.confluence.audit.StandardAuditResourceTypes
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.oauth.event.AccessTokenAddedEvent
 *  com.atlassian.oauth.event.AccessTokenRemovedEvent
 *  com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.auditing.listeners;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.plugins.auditing.listeners.AbstractEventListener;
import com.atlassian.confluence.plugins.auditing.utils.AuditCategories;
import com.atlassian.confluence.plugins.auditing.utils.MessageKeyBuilder;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.oauth.event.AccessTokenAddedEvent;
import com.atlassian.oauth.event.AccessTokenRemovedEvent;
import com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import java.util.Optional;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

@ConfluenceComponent(value="oauthTokenEventsListener")
public class OAuthTokenEventsListener
extends AbstractEventListener {
    static final String OATH_AUDIT_SUMMARY_TOKEN_ADDED = MessageKeyBuilder.buildSummaryTextKey("oauth.token.added");
    static final String OATH_AUDIT_SUMMARY_TOKEN_REMOVED = MessageKeyBuilder.buildSummaryTextKey("oauth.token.removed");
    static final String OATH_AUDIT_VALUE_CONSUMER_NAME = MessageKeyBuilder.buildChangedValueTextKey("oauth.consumer.name");
    private final ServiceProviderConsumerStore serviceProviderConsumerStore;
    private final StandardAuditResourceTypes standardAuditResourceTypes;
    private final UserAccessor userAccessor;

    @Autowired
    public OAuthTokenEventsListener(AuditService auditBroker, EventListenerRegistrar eventListenerRegistrar, @ComponentImport I18nResolver i18nResolver, @ComponentImport LocaleResolver localeResolver, @ComponentImport ServiceProviderConsumerStore serviceProviderConsumerStore, @ConfluenceImport StandardAuditResourceTypes standardAuditResourceTypes, @ConfluenceImport UserAccessor userAccessor, @ComponentImport AuditingContext auditingContext) {
        super(auditBroker, eventListenerRegistrar, i18nResolver, localeResolver, auditingContext);
        this.serviceProviderConsumerStore = serviceProviderConsumerStore;
        this.standardAuditResourceTypes = standardAuditResourceTypes;
        this.userAccessor = userAccessor;
    }

    @EventListener
    public void onTokenAddedEvent(AccessTokenAddedEvent event) {
        this.save(() -> {
            AuditEvent.Builder auditEventBuilder = AuditEvent.builder((AuditType)this.buildOAuthAuditType(OATH_AUDIT_SUMMARY_TOKEN_ADDED));
            this.buildConsumerChangedValue(event.getConsumerKey(), true).ifPresent(arg_0 -> ((AuditEvent.Builder)auditEventBuilder).changedValue(arg_0));
            this.buildAffectedUserResource(event.getUsername()).ifPresent(arg_0 -> ((AuditEvent.Builder)auditEventBuilder).affectedObject(arg_0));
            return auditEventBuilder.build();
        });
    }

    @EventListener
    public void onTokenRemovedEvent(AccessTokenRemovedEvent event) {
        this.save(() -> {
            AuditEvent.Builder auditEventBuilder = AuditEvent.builder((AuditType)this.buildOAuthAuditType(OATH_AUDIT_SUMMARY_TOKEN_REMOVED));
            this.buildConsumerChangedValue(event.getConsumerKey(), false).ifPresent(arg_0 -> ((AuditEvent.Builder)auditEventBuilder).changedValue(arg_0));
            this.buildAffectedUserResource(event.getUsername()).ifPresent(arg_0 -> ((AuditEvent.Builder)auditEventBuilder).affectedObject(arg_0));
            return auditEventBuilder.build();
        });
    }

    private AuditType buildOAuthAuditType(String key) {
        return AuditType.fromI18nKeys((CoverageArea)CoverageArea.SECURITY, (CoverageLevel)CoverageLevel.ADVANCED, (String)AuditCategories.AUTH_CATEGORY, (String)key).build();
    }

    private Optional<AuditResource> buildAffectedUserResource(@Nullable String username) {
        return Optional.ofNullable(username).map(arg_0 -> ((UserAccessor)this.userAccessor).getUserByName(arg_0)).map(user -> AuditResource.builder((String)user.getFullName(), (String)this.standardAuditResourceTypes.user()).id(user.getKey().getStringValue()).build());
    }

    private Optional<ChangedValue> buildConsumerChangedValue(@Nullable String consumerKey, boolean isAdded) {
        return Optional.ofNullable(consumerKey).map(arg_0 -> ((ServiceProviderConsumerStore)this.serviceProviderConsumerStore).get(arg_0)).map(consumer -> isAdded ? ChangedValue.fromI18nKeys((String)OATH_AUDIT_VALUE_CONSUMER_NAME).to(consumer.getName()).build() : ChangedValue.fromI18nKeys((String)OATH_AUDIT_VALUE_CONSUMER_NAME).from(consumer.getName()).build());
    }
}

