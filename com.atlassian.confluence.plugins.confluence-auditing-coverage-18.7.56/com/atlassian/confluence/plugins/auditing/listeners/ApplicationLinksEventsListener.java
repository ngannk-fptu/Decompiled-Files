/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.event.ApplicationLinkAddedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkDetailsChangedEvent
 *  com.atlassian.applinks.core.event.BeforeApplicationLinkDeletedEvent
 *  com.atlassian.applinks.spi.link.ApplicationLinkDetails
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.confluence.audit.AuditingContext
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.auditing.listeners;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.event.ApplicationLinkAddedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkDetailsChangedEvent;
import com.atlassian.applinks.core.event.BeforeApplicationLinkDeletedEvent;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.plugins.auditing.listeners.AbstractEventListener;
import com.atlassian.confluence.plugins.auditing.utils.AuditCategories;
import com.atlassian.confluence.plugins.auditing.utils.MessageKeyBuilder;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="applicationLinksEventsListener")
public class ApplicationLinksEventsListener
extends AbstractEventListener {
    static final String SUMMARY_APPLINK_ADDED = MessageKeyBuilder.buildSummaryTextKey("applink.added");
    static final String SUMMARY_APPLINK_DELETED = MessageKeyBuilder.buildSummaryTextKey("applink.deleted");
    static final String SUMMARY_APPLINK_EDITED = MessageKeyBuilder.buildSummaryTextKey("applink.edited");
    static final String VALUE_APPLINK_TYPE = MessageKeyBuilder.buildChangedValueTextKey("applink.type");
    static final String VALUE_APPLINK_NAME = MessageKeyBuilder.buildChangedValueTextKey("applink.name");
    static final String VALUE_APPLINK_DISPLAY_URL = MessageKeyBuilder.buildChangedValueTextKey("applink.url");
    static final String VALUE_APPLINK_PRIMARY = MessageKeyBuilder.buildChangedValueTextKey("applink.primary");

    @Autowired
    public ApplicationLinksEventsListener(@ComponentImport AuditService auditBroker, @ComponentImport(value="eventPublisher") EventListenerRegistrar eventListenerRegistrar, @ComponentImport I18nResolver i18nResolver, @ComponentImport LocaleResolver localeResolver, @ComponentImport AuditingContext auditingContext) {
        super(auditBroker, eventListenerRegistrar, i18nResolver, localeResolver, auditingContext);
    }

    @EventListener
    public void onApplicationLinkAddedEvent(ApplicationLinkAddedEvent event) {
        this.save(() -> {
            ApplicationLink applicationLink = event.getApplicationLink();
            AuditEvent.Builder auditEventBuilder = AuditEvent.builder((AuditType)this.buildApplicationLinksAuditType(SUMMARY_APPLINK_ADDED)).changedValue(ChangedValue.fromI18nKeys((String)VALUE_APPLINK_TYPE).to(this.translate(applicationLink.getType().getI18nKey())).build());
            this.changedValuesFromDetails(null, ApplicationLinkDetails.builder((ApplicationLink)applicationLink).build()).forEach(arg_0 -> ((AuditEvent.Builder)auditEventBuilder).addChangedValueIfDifferent(arg_0));
            return auditEventBuilder.build();
        });
    }

    @EventListener
    public void onBeforeApplicationLinkDeletedEvent(BeforeApplicationLinkDeletedEvent event) {
        this.save(() -> {
            ApplicationLink applicationLink = event.getApplicationLink();
            AuditEvent.Builder auditEventBuilder = AuditEvent.builder((AuditType)this.buildApplicationLinksAuditType(SUMMARY_APPLINK_DELETED)).changedValue(ChangedValue.fromI18nKeys((String)VALUE_APPLINK_TYPE).from(this.translate(applicationLink.getType().getI18nKey())).build());
            this.changedValuesFromDetails(ApplicationLinkDetails.builder((ApplicationLink)applicationLink).build(), null).forEach(arg_0 -> ((AuditEvent.Builder)auditEventBuilder).addChangedValueIfDifferent(arg_0));
            return auditEventBuilder.build();
        });
    }

    @EventListener
    public void onApplicationLinkDetailsChangedEvent(ApplicationLinkDetailsChangedEvent event) {
        this.save(() -> {
            ApplicationLink applicationLink = event.getApplicationLink();
            AuditEvent.Builder auditEventBuilder = AuditEvent.builder((AuditType)this.buildApplicationLinksAuditType(SUMMARY_APPLINK_EDITED));
            this.changedValuesFromDetails(null, ApplicationLinkDetails.builder((ApplicationLink)applicationLink).build()).forEach(arg_0 -> ((AuditEvent.Builder)auditEventBuilder).addChangedValueIfDifferent(arg_0));
            return auditEventBuilder.build();
        });
    }

    private AuditType buildApplicationLinksAuditType(String key) {
        return AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)AuditCategories.ADMIN_CATEGORY, (String)key).build();
    }

    private Collection<ChangedValue> changedValuesFromDetails(@Nullable ApplicationLinkDetails from, @Nullable ApplicationLinkDetails to) {
        return Arrays.asList(ChangedValue.fromI18nKeys((String)VALUE_APPLINK_NAME).from((String)Optional.ofNullable(from).map(ApplicationLinkDetails::getName).orElse(null)).to((String)Optional.ofNullable(to).map(ApplicationLinkDetails::getName).orElse(null)).build(), ChangedValue.fromI18nKeys((String)VALUE_APPLINK_DISPLAY_URL).from((String)Optional.ofNullable(from).map(ApplicationLinkDetails::getDisplayUrl).map(URI::toASCIIString).orElse(null)).to((String)Optional.ofNullable(to).map(ApplicationLinkDetails::getDisplayUrl).map(URI::toASCIIString).orElse(null)).build(), ChangedValue.fromI18nKeys((String)VALUE_APPLINK_PRIMARY).from((String)Optional.ofNullable(from).map(ApplicationLinkDetails::isPrimary).map(String::valueOf).orElse(null)).to((String)Optional.ofNullable(to).map(ApplicationLinkDetails::isPrimary).map(String::valueOf).orElse(null)).build());
    }
}

