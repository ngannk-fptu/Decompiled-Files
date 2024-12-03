/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.ChangedValue$Builder
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
import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractAuditListener;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.event.api.EventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class GlobalSettingsAuditListener
extends AbstractAuditListener {
    public static final String GLOBAL_SETTINGS_CHANGED_SUMMARY = AuditHelper.buildSummaryTextKey("global.settings.edited");
    public static final String SECURITY_SETTINGS_CHANGED_SUMMARY = AuditHelper.buildSummaryTextKey("global.security.settings.edited");
    static final String SHOW_APPLICATION_TITLE_CHANGED_VALUE = AuditHelper.buildChangedValueTextKey("showApplicationTitle.title");
    static final String SHOW_APPLICATION_TITLE_CHANGED_VALUE_ON = AuditHelper.buildChangedValueTextKey("showApplicationTitle.on");
    static final String SHOW_APPLICATION_TITLE_CHANGED_VALUE_OFF = AuditHelper.buildChangedValueTextKey("showApplicationTitle.off");

    public GlobalSettingsAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, auditingContext);
    }

    @EventListener
    public void globalSettingsChangedEvent(GlobalSettingsChangedEvent event) {
        Supplier<AuditEvent> auditEventSupplier = GlobalSettingsChangedEvent.Type.SECURITY.equals((Object)event.getType()) ? () -> ((AuditEvent.Builder)AuditEvent.fromI18nKeys((String)AuditCategories.ADMIN, (String)SECURITY_SETTINGS_CHANGED_SUMMARY, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).changedValues(this.getChangedValues(event.getOldSettings(), event.getNewSettings()))).build() : () -> ((AuditEvent.Builder)AuditEvent.fromI18nKeys((String)AuditCategories.ADMIN, (String)GLOBAL_SETTINGS_CHANGED_SUMMARY, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).changedValues(this.getChangedValues(event.getOldSettings(), event.getNewSettings()))).build();
        this.save(auditEventSupplier);
    }

    private List<ChangedValue> getChangedValues(Settings oldSettings, Settings newSettings) {
        ArrayList<ChangedValue> changedValues = new ArrayList<ChangedValue>();
        if (oldSettings.showApplicationTitle() ^ newSettings.showApplicationTitle()) {
            ChangedValue.Builder showApplicationTitleBuilder = ChangedValue.fromI18nKeys((String)SHOW_APPLICATION_TITLE_CHANGED_VALUE);
            if (newSettings.showApplicationTitle()) {
                showApplicationTitleBuilder.to(this.auditHelper.translate(SHOW_APPLICATION_TITLE_CHANGED_VALUE_ON));
            } else {
                showApplicationTitleBuilder.to(this.auditHelper.translate(SHOW_APPLICATION_TITLE_CHANGED_VALUE_OFF));
            }
            changedValues.add(showApplicationTitleBuilder.build());
        }
        changedValues.addAll(this.getAuditHandlerService().handle(Optional.ofNullable(oldSettings), Optional.ofNullable(newSettings)));
        return changedValues;
    }
}

