/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.core.PersistentDecorator;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractAuditListener;
import com.atlassian.confluence.themes.events.ColourSchemeChangedEvent;
import com.atlassian.confluence.themes.events.DecoratorChangedEvent;
import com.atlassian.confluence.themes.events.FaviconChangedEvent;
import com.atlassian.confluence.themes.events.LookAndFeelEvent;
import com.atlassian.confluence.themes.events.SiteLogoChangedEvent;
import com.atlassian.confluence.themes.events.StylesheetChangedEvent;
import com.atlassian.confluence.themes.events.ThemeChangedEvent;
import com.atlassian.event.api.EventListener;
import java.util.List;
import java.util.Optional;

public class LookAndFeelAuditListener
extends AbstractAuditListener {
    public static final String COLOUR_SCHEME_EDITED_SUMMARY = AuditHelper.buildSummaryTextKey("color.scheme.modified");
    public static final String COLOUR_SCHEME_CHANGED_SUMMARY = AuditHelper.buildSummaryTextKey("color.scheme.type.changed");
    public static final String STYLESHEET_ADDED_SUMMARY = AuditHelper.buildSummaryTextKey("stylesheet.added");
    public static final String STYLESHEET_REMOVED_SUMMARY = AuditHelper.buildSummaryTextKey("stylesheet.removed");
    public static final String THEME_CHANGED_SUMMARY = AuditHelper.buildSummaryTextKey("theme.changed");
    public static final String DECORATOR_CHANGED_SUMMARY = AuditHelper.buildSummaryTextKey("decorator.modified");
    public static final String SITE_LOGO_CHANGED_SUMMARY = AuditHelper.buildSummaryTextKey("site.logo.changed");
    public static final String FAVICON_UPLOADED_SUMARY = AuditHelper.buildSummaryTextKey("favicon.changed");
    public static final String FAVICON_RESET_SUMARY = AuditHelper.buildSummaryTextKey("favicon.reset");

    public LookAndFeelAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, auditingContext);
    }

    @EventListener
    public void colourSchemeChangedEvent(ColourSchemeChangedEvent event) {
        this.save(() -> {
            if (!event.isCustomSchemeEdit()) {
                return this.getRecordBuilderForLookAndFeel(COLOUR_SCHEME_CHANGED_SUMMARY, event).changedValue(this.newChangedValue("color.scheme.type", event.getOldColourSchemeType(), event.getNewColourSchemeType())).build();
            }
            return this.getRecordBuilderForLookAndFeel(COLOUR_SCHEME_EDITED_SUMMARY, event).build();
        });
    }

    @EventListener
    public void styleSheetChangedEvent(StylesheetChangedEvent event) {
        String action = event.getChangeType() == StylesheetChangedEvent.StylesheetChangeType.ADDED ? STYLESHEET_ADDED_SUMMARY : STYLESHEET_REMOVED_SUMMARY;
        this.save(() -> this.getRecordBuilderForLookAndFeel(action, event).build());
    }

    @EventListener
    public void themeChangedEvent(ThemeChangedEvent event) {
        this.save(() -> this.getRecordBuilderForLookAndFeel(THEME_CHANGED_SUMMARY, event).changedValue(this.newChangedValue("theme.key", event.getOldThemeKey(), event.getNewThemeKey())).build());
    }

    @EventListener
    public void decoratorChangedEvent(DecoratorChangedEvent event) {
        this.save(() -> this.getRecordBuilderForLookAndFeel(DECORATOR_CHANGED_SUMMARY, event).changedValues(this.getChangedValues(event.getOldDecorator(), event.getNewDecorator())).build());
    }

    @EventListener
    public void siteLogoChangedEvent(SiteLogoChangedEvent event) {
        this.save(() -> this.getRecordBuilderForLookAndFeel(SITE_LOGO_CHANGED_SUMMARY, event).build());
    }

    @EventListener
    public void faviconChangedEvent(FaviconChangedEvent event) {
        this.saveIfPresent(() -> {
            if (event.getAction().equals((Object)FaviconChangedEvent.Action.RESET)) {
                return Optional.of(this.getRecordBuilderForLookAndFeel(FAVICON_RESET_SUMARY, event).build());
            }
            if (event.getAction().equals((Object)FaviconChangedEvent.Action.UPLOADED)) {
                return Optional.of(this.getRecordBuilderForLookAndFeel(FAVICON_UPLOADED_SUMARY, event).build());
            }
            return Optional.empty();
        });
    }

    private List<ChangedValue> getChangedValues(PersistentDecorator oldDecorator, PersistentDecorator newDecorator) {
        return this.getAuditHandlerService().handle(Optional.ofNullable(oldDecorator), Optional.ofNullable(newDecorator));
    }

    private AuditEvent.Builder getRecordBuilderForLookAndFeel(String action, LookAndFeelEvent event) {
        if (event.isGlobal()) {
            return AuditEvent.fromI18nKeys((String)AuditCategories.ADMIN, (String)action, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION);
        }
        return AuditEvent.fromI18nKeys((String)AuditCategories.SPACES, (String)action, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).affectedObject(this.buildResource(this.auditHelper.fetchSpaceDisplayName(event.getSpaceKey()), this.resourceTypes.space(), this.auditHelper.fetchSpaceId(event.getSpaceKey())));
    }
}

