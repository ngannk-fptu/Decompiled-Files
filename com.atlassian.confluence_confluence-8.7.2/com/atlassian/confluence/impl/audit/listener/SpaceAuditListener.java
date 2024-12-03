/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.event.events.space.SpaceArchivedEvent;
import com.atlassian.confluence.event.events.space.SpaceCreateEvent;
import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.space.SpaceLogoUpdateEvent;
import com.atlassian.confluence.event.events.space.SpaceRemoveEvent;
import com.atlassian.confluence.event.events.space.SpaceTrashEmptyEvent;
import com.atlassian.confluence.event.events.space.SpaceUnArchivedEvent;
import com.atlassian.confluence.event.events.space.SpaceUpdateEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractAuditListener;
import com.atlassian.event.api.EventListener;
import com.google.common.collect.ImmutableMap;

public class SpaceAuditListener
extends AbstractAuditListener {
    private static final String SPACE_CREATE_SUMMARY = AuditHelper.buildSummaryTextKey("space.created");
    private static final String SPACE_REMOVE_SUMMARY = AuditHelper.buildSummaryTextKey("space.removed");
    private static final String SPACE_ARCHIVED_SUMMARY = AuditHelper.buildSummaryTextKey("space.archived");
    private static final String SPACE_UNARCHIVED_SUMMARY = AuditHelper.buildSummaryTextKey("space.unarchived");
    private static final String SPACE_TRASH_EMPTY_SUMMARY = AuditHelper.buildSummaryTextKey("space.trash.emptied");
    private static final String SPACE_LOGO_UPLOADED_SUMMARY = AuditHelper.buildSummaryTextKey("space.logo.uploaded");
    private static final String SPACE_LOGO_DELETED_SUMMARY = AuditHelper.buildSummaryTextKey("space.logo.deleted");
    private static final String SPACE_LOGO_ENABLED_SUMMARY = AuditHelper.buildSummaryTextKey("space.logo.enabled");
    private static final String SPACE_LOGO_DISABLED_SUMMARY = AuditHelper.buildSummaryTextKey("space.logo.disabled");
    private static final String SPACE_LOGO_UNKNOWN_SUMMARY = AuditHelper.buildSummaryTextKey("space.logo.unknown");
    private static final String SPACE_CONFIGURATION_UPDATED = AuditHelper.buildSummaryTextKey("space.config.updated");
    private static final ImmutableMap<SpaceLogoUpdateEvent.SpaceLogoActions, String> SUMMARY_BY_LOGO_ACTION = ImmutableMap.of((Object)((Object)SpaceLogoUpdateEvent.SpaceLogoActions.UPLOAD), (Object)SPACE_LOGO_UPLOADED_SUMMARY, (Object)((Object)SpaceLogoUpdateEvent.SpaceLogoActions.DELETE), (Object)SPACE_LOGO_DELETED_SUMMARY, (Object)((Object)SpaceLogoUpdateEvent.SpaceLogoActions.ENABLE), (Object)SPACE_LOGO_ENABLED_SUMMARY, (Object)((Object)SpaceLogoUpdateEvent.SpaceLogoActions.DISABLE), (Object)SPACE_LOGO_DISABLED_SUMMARY);

    public SpaceAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, auditingContext);
    }

    @EventListener
    public void onSpaceCreateEvent(SpaceCreateEvent event) {
        this.save(() -> this.spaceEntityBuilder(event, SPACE_CREATE_SUMMARY).changedValues(this.calculateChangedValues(null, event.getSpace())).build());
    }

    @EventListener
    public void spaceRemoveEvent(SpaceRemoveEvent event) {
        this.save(() -> this.spaceEntityBuilder(event, SPACE_REMOVE_SUMMARY).build());
    }

    @EventListener
    public void spaceUpdateEvent(SpaceUpdateEvent event) {
        this.save(() -> this.spaceEntityBuilder(event, SPACE_CONFIGURATION_UPDATED).changedValues(this.calculateChangedValues(event.getOriginalSpace(), event.getSpace())).build());
    }

    @EventListener
    public void spaceArchivedEvent(SpaceArchivedEvent event) {
        this.save(() -> this.spaceEntityBuilder(event, SPACE_ARCHIVED_SUMMARY).build());
    }

    @EventListener
    public void spaceUnArchivedEvent(SpaceUnArchivedEvent event) {
        this.save(() -> this.spaceEntityBuilder(event, SPACE_UNARCHIVED_SUMMARY).build());
    }

    @EventListener
    public void spaceTrashEmptyEvent(SpaceTrashEmptyEvent event) {
        this.save(() -> this.spaceEntityBuilder(event, SPACE_TRASH_EMPTY_SUMMARY).build());
    }

    @EventListener
    public void spaceLogoUpdateEvent(SpaceLogoUpdateEvent event) {
        String action = (String)SUMMARY_BY_LOGO_ACTION.getOrDefault((Object)event.getEventTypeEnum(), (Object)SPACE_LOGO_UNKNOWN_SUMMARY);
        this.save(() -> this.spaceEntityBuilder(event, action).build());
    }

    private AuditEvent.Builder spaceEntityBuilder(SpaceEvent event, String action) {
        return AuditEvent.fromI18nKeys((String)AuditCategories.SPACES, (String)action, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.LOCAL_CONFIG_AND_ADMINISTRATION).affectedObject(this.buildResource(event.getSpace().getName(), this.resourceTypes.space(), event.getSpace().getId()));
    }
}

