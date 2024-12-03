/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.AuditResource$Builder
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.confluence.audit.AuditingContext
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.custom_apps.api.CustomApp
 *  com.atlassian.plugins.custom_apps.api.events.NavigationLinkAddedEvent
 *  com.atlassian.plugins.custom_apps.api.events.NavigationLinkRemovedEvent
 *  com.atlassian.plugins.custom_apps.api.events.NavigationLinkUpdatedEvent
 *  com.atlassian.plugins.navlink.producer.navigation.NavigationLink
 *  com.atlassian.plugins.navlink.producer.navigation.links.NavigationLinkBase
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
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
import com.atlassian.confluence.plugins.auditing.listeners.AbstractEventListener;
import com.atlassian.confluence.plugins.auditing.utils.AuditCategories;
import com.atlassian.confluence.plugins.auditing.utils.ChangedValuesCalculator;
import com.atlassian.confluence.plugins.auditing.utils.MessageKeyBuilder;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.custom_apps.api.CustomApp;
import com.atlassian.plugins.custom_apps.api.events.NavigationLinkAddedEvent;
import com.atlassian.plugins.custom_apps.api.events.NavigationLinkRemovedEvent;
import com.atlassian.plugins.custom_apps.api.events.NavigationLinkUpdatedEvent;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.atlassian.plugins.navlink.producer.navigation.links.NavigationLinkBase;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="navigationLinksAuditListener")
public class NavigationLinksAuditListener
extends AbstractEventListener {
    private static final String NAVIGATION_LINK_AUDIT_PREFIX = "nav.link.";

    @Autowired
    public NavigationLinksAuditListener(AuditService auditBroker, EventListenerRegistrar eventListenerRegistrar, @ComponentImport I18nResolver i18nResolver, @ComponentImport LocaleResolver localeResolver, @ComponentImport AuditingContext auditingContext) {
        super(auditBroker, eventListenerRegistrar, i18nResolver, localeResolver, auditingContext);
    }

    @EventListener
    public void onNavLinkAddedEvent(NavigationLinkAddedEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)this.buildNavLinkAuditType("created")).affectedObject(this.buildAuditResource(event.getAffectedApp()).build()).changedValues(this.compareApps(null, event.getAffectedApp())).build());
    }

    @EventListener
    public void onNavLinkUpdatedEvent(NavigationLinkUpdatedEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)this.buildNavLinkAuditType("updated")).affectedObject(this.buildAuditResource(event.getAffectedApp()).build()).changedValues(this.compareApps(event.getOldValue(), event.getAffectedApp())).build());
    }

    @EventListener
    public void onNavigationLinkRemovedEvent(NavigationLinkRemovedEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)this.buildNavLinkAuditType("removed")).affectedObject(this.buildAuditResource(event.getAffectedApp()).build()).changedValues(this.compareApps(event.getAffectedApp(), null)).build());
    }

    private AuditType buildNavLinkAuditType(String i18NSuffix) {
        return AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)AuditCategories.ADMIN_CATEGORY, (String)MessageKeyBuilder.buildSummaryTextKey(NAVIGATION_LINK_AUDIT_PREFIX + i18NSuffix.toLowerCase())).build();
    }

    private AuditResource.Builder buildAuditResource(CustomApp app) {
        String appName = app.getUrl();
        String appLinkObjectName = this.translate(MessageKeyBuilder.buildSummaryTextKey("nav.link.nav.link"));
        return AuditResource.builder((String)appName, (String)appLinkObjectName).id(app.getId()).uri(app.getUrl());
    }

    private List<ChangedValue> compareApps(CustomApp app, CustomApp otherApp) {
        ChangedValuesCalculator<CustomApp> customAppCalc = new ChangedValuesCalculator<CustomApp>(app, otherApp);
        ChangedValuesCalculator<NavigationLink> navLinkCalc = new ChangedValuesCalculator<NavigationLink>(app != null ? app.getNavigationLink() : null, otherApp != null ? otherApp.getNavigationLink() : null);
        return Stream.of(customAppCalc.getDifference(MessageKeyBuilder.buildChangedValueTextKey("allowed.groups"), CustomApp::getAllowedGroups), customAppCalc.getDifference(MessageKeyBuilder.buildChangedValueTextKey("display.name"), CustomApp::getDisplayName), customAppCalc.getDifference(MessageKeyBuilder.buildChangedValueTextKey("hidden"), CustomApp::getHide), customAppCalc.getDifference(MessageKeyBuilder.buildChangedValueTextKey("source.application.type"), CustomApp::getSourceApplicationType), navLinkCalc.getDifference(MessageKeyBuilder.buildChangedValueTextKey("navigation.link.href"), NavigationLinkBase::getHref)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }
}

