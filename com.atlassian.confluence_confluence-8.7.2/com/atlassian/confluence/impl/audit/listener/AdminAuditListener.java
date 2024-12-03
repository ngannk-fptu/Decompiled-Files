/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.event.events.admin.LicenceUpdatedEvent;
import com.atlassian.confluence.event.events.admin.MailErrorQueueDeletedEvent;
import com.atlassian.confluence.event.events.admin.MailErrorQueueResentEvent;
import com.atlassian.confluence.event.events.admin.MailQueueFlushedEvent;
import com.atlassian.confluence.event.events.admin.MailServerCreateEvent;
import com.atlassian.confluence.event.events.admin.MailServerDeleteEvent;
import com.atlassian.confluence.event.events.admin.MailServerEditEvent;
import com.atlassian.confluence.event.events.admin.MaxCacheSizeChangedEvent;
import com.atlassian.confluence.event.events.analytics.MaintenanceReadOnlyEvent;
import com.atlassian.confluence.event.events.cluster.ClusterMaintenanceBannerEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditAction;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractAuditListener;
import com.atlassian.event.api.EventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminAuditListener
extends AbstractAuditListener {
    public static final String MAIL_SERVER_CREATED_SUMMARY = AuditHelper.buildSummaryTextKey("mail.server.created");
    public static final String MAIL_SERVER_DELETED_SUMMARY = AuditHelper.buildSummaryTextKey("mail.server.deleted");
    public static final String MAIL_SERVER_EDITED_SUMMARY = AuditHelper.buildSummaryTextKey("mail.server.edited");
    public static final String MAX_CACHE_SIZE_CHANGED_SUMMARY = AuditHelper.buildSummaryTextKey("max.cache.size.changed");
    public static final String LICENCE_UPDATED_SUMMARY = AuditHelper.buildSummaryTextKey("licence.updated");
    public static final String MAINTENANCE_MODE_SUMMARY = AuditHelper.buildSummaryTextKey("maintenance.mode");
    public static final String MAINTENANCE_MODE_BANNER_CHANGED_SUMMARY = AuditHelper.buildSummaryTextKey("maintenance.mode.banner");
    public static final String MAIL_QUEUE_FLUSHED_SUMMARY = AuditHelper.buildSummaryTextKey("mail.queue.flushed");
    public static final String MAIL_ERROR_QUEUE_RESENT_SUMMARY = AuditHelper.buildSummaryTextKey("mail.queue.error.resent");
    public static final String MAIL_ERROR_QUEUE_DELETED_SUMMARY = AuditHelper.buildSummaryTextKey("mail.queue.error.deleted");
    public static final String KEY_MAINTENANCE_MODE_BANNER = AuditHelper.buildChangedValueTextKey("maintenance.mode.banner");
    public static final String KEY_MAINTENANCE_MODE_BANNER_ENABLED = AuditHelper.buildChangedValueTextKey("maintenance.mode.banner.enabled");
    private static final String KEY_MAX_CACHE_SIZE = "max.cache.size";
    private static final String KEY_AFFECTED_OBJECT_LICENSE = AuditHelper.buildTextKey("affected.object.license");
    private static final String KEY_READ_ONLY_MODE = AuditHelper.buildChangedValueTextKey("read.only.mode");

    public AdminAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, auditingContext);
    }

    @EventListener
    public void mailServerCreateEvent(MailServerCreateEvent event) {
        this.save(() -> AuditEvent.fromI18nKeys((String)AuditCategories.ADMIN, (String)MAIL_SERVER_CREATED_SUMMARY, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).affectedObject(this.buildResourceWithoutId(event.getServer().getName(), this.resourceTypes.mailServer())).changedValues(this.getAuditHandlerService().handle(event.getServer(), AuditAction.ADD)).build());
    }

    @EventListener
    public void mailServerEditEvent(MailServerEditEvent event) {
        this.save(() -> AuditEvent.fromI18nKeys((String)AuditCategories.ADMIN, (String)MAIL_SERVER_EDITED_SUMMARY, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).affectedObject(this.buildResourceWithoutId(event.getServer().getName(), this.resourceTypes.mailServer())).changedValues(this.getAuditHandlerService().handle(event.getServer(), AuditAction.ADD)).build());
    }

    @EventListener
    public void mailServerDeleteEvent(MailServerDeleteEvent event) {
        this.save(() -> AuditEvent.fromI18nKeys((String)AuditCategories.ADMIN, (String)MAIL_SERVER_DELETED_SUMMARY, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).affectedObject(this.buildResourceWithoutId(event.getServer().getName(), this.resourceTypes.mailServer())).changedValues(this.getAuditHandlerService().handle(event.getServer(), AuditAction.REMOVE)).build());
    }

    @EventListener
    public void maxCacheSizeChangedEvent(MaxCacheSizeChangedEvent event) {
        this.save(() -> AuditEvent.fromI18nKeys((String)AuditCategories.ADMIN, (String)MAX_CACHE_SIZE_CHANGED_SUMMARY, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).affectedObject(this.buildResourceWithoutId(event.getCacheName(), this.resourceTypes.cache())).changedValue(this.newChangedValue(KEY_MAX_CACHE_SIZE, event.getPreviousMaxCacheSize(), event.getMaxCacheSize())).build());
    }

    @EventListener
    public void licenceUpdatedEvent(LicenceUpdatedEvent event) {
        this.save(() -> AuditEvent.fromI18nKeys((String)AuditCategories.ADMIN, (String)LICENCE_UPDATED_SUMMARY, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).affectedObject(this.buildResourceWithoutId(this.auditHelper.translate(KEY_AFFECTED_OBJECT_LICENSE), this.resourceTypes.license())).build());
    }

    @EventListener
    public void onReadOnlyModeEvent(MaintenanceReadOnlyEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)AuditCategories.ADMIN, (String)MAINTENANCE_MODE_SUMMARY).build()).changedValue(ChangedValue.fromI18nKeys((String)KEY_READ_ONLY_MODE).to(this.getOnOff(event.isEnabled())).build()).build());
    }

    @EventListener
    public void onMaintenanceBannerChanged(ClusterMaintenanceBannerEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)AuditCategories.ADMIN, (String)MAINTENANCE_MODE_BANNER_CHANGED_SUMMARY).build()).changedValues(this.getChangedValuesForMaintenanceBannerEvent(event)).build());
    }

    private List<ChangedValue> getChangedValuesForMaintenanceBannerEvent(ClusterMaintenanceBannerEvent event) {
        ArrayList<ChangedValue> changedValues = new ArrayList<ChangedValue>();
        if (event.isEnabled() ^ event.wasEnabled()) {
            changedValues.add(ChangedValue.fromI18nKeys((String)KEY_MAINTENANCE_MODE_BANNER_ENABLED).to(this.getOnOff(event.isEnabled())).build());
        }
        if (!Objects.equals(event.getMessage(), event.getPreviousMessage())) {
            changedValues.add(ChangedValue.fromI18nKeys((String)KEY_MAINTENANCE_MODE_BANNER).from(event.getPreviousMessage()).to(event.getMessage()).build());
        }
        return changedValues;
    }

    @EventListener
    public void onMailQueueFlushedListener(MailQueueFlushedEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)AuditCategories.ADMIN, (String)MAIL_QUEUE_FLUSHED_SUMMARY).build()).build());
    }

    @EventListener
    public void onMailQueueFlushedListener(MailErrorQueueResentEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)AuditCategories.ADMIN, (String)MAIL_ERROR_QUEUE_RESENT_SUMMARY).build()).build());
    }

    @EventListener
    public void onMailQueueFlushedListener(MailErrorQueueDeletedEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)AuditCategories.ADMIN, (String)MAIL_ERROR_QUEUE_DELETED_SUMMARY).build()).build());
    }
}

