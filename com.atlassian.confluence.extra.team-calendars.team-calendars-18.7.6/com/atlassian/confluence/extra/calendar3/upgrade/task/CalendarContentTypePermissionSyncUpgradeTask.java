/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
 *  com.atlassian.activeobjects.external.ModelVersion
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.search.ConfluenceIndexer
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor$Propagation
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.extra.calendar3.contenttype.CalendarContentTypeManager;
import com.atlassian.confluence.extra.calendar3.upgrade.task.CalendarModelVersion;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="calendarContentTypePermissionSyncUpgradeTask")
public class CalendarContentTypePermissionSyncUpgradeTask
implements ActiveObjectsUpgradeTask {
    private static final Logger LOG = LoggerFactory.getLogger(CalendarContentTypePermissionSyncUpgradeTask.class);
    private final ConfluenceIndexer indexer;
    private final CalendarContentTypeManager calendarContentTypeManager;
    private final TransactionalHostContextAccessor hostContextAccessor;

    @Autowired
    public CalendarContentTypePermissionSyncUpgradeTask(ConfluenceIndexer indexer, CalendarContentTypeManager calendarContentTypeManager, @ComponentImport TransactionalHostContextAccessor hostContextAccessor) {
        this.indexer = indexer;
        this.calendarContentTypeManager = calendarContentTypeManager;
        this.hostContextAccessor = hostContextAccessor;
    }

    public ModelVersion getModelVersion() {
        return ModelVersion.valueOf((String)CalendarModelVersion.CALENDAR_MODEL_VERSION_20);
    }

    public void upgrade(ModelVersion currentVersion, ActiveObjects ao) {
        LOG.info("====================Start Upgrade Task To Sync Calendar Permissions with Calendar Content Type==============================");
        LOG.info("====================Begin reindexing of all Calendars==============================");
        try {
            this.reindexAllSubCalendars();
        }
        catch (Exception ex) {
            LOG.error("Exception during subcalendar reindex of CalendarContentTypePermissionSyncUpgradeTask", (Throwable)ex);
        }
        LOG.info("====================Begin reindexing of all Space Calendars==============================");
        try {
            this.reindexAllSpaceCalendars();
        }
        catch (Exception ex) {
            LOG.error("Exception during Space Calendar reindex of CalendarContentTypePermissionSyncUpgradeTask", (Throwable)ex);
        }
        LOG.info("====================End Upgrade Task To Sync Calendar Permissions with Calendar Content Type==============================");
    }

    private void reindexAllSubCalendars() {
        int batchSize = 100;
        int start = 0;
        boolean hasMore = true;
        while (hasMore) {
            SimplePageRequest request = new SimplePageRequest(start, batchSize);
            hasMore = (Boolean)this.hostContextAccessor.doInTransaction(TransactionalHostContextAccessor.Propagation.REQUIRES_NEW, () -> this.lambda$reindexAllSubCalendars$0((PageRequest)request));
            start += batchSize;
        }
    }

    private void reindexAllSpaceCalendars() {
        int batchSize = 100;
        int start = 0;
        boolean hasMore = true;
        while (hasMore) {
            SimplePageRequest request = new SimplePageRequest(start, batchSize);
            hasMore = (Boolean)this.hostContextAccessor.doInTransaction(TransactionalHostContextAccessor.Propagation.REQUIRES_NEW, () -> this.lambda$reindexAllSpaceCalendars$1((PageRequest)request));
            start += batchSize;
        }
    }

    private /* synthetic */ Boolean lambda$reindexAllSpaceCalendars$1(PageRequest request) {
        PageResponse<CustomContentEntityObject> spaceCalendars = this.calendarContentTypeManager.getAllCalendarContent(request);
        spaceCalendars.forEach(arg_0 -> ((ConfluenceIndexer)this.indexer).reIndex(arg_0));
        return spaceCalendars.hasMore();
    }

    private /* synthetic */ Boolean lambda$reindexAllSubCalendars$0(PageRequest request) {
        PageResponse<CustomContentEntityObject> spaceCalendars = this.calendarContentTypeManager.getAllSubCalendarContent(request);
        spaceCalendars.forEach(arg_0 -> ((ConfluenceIndexer)this.indexer).reIndex(arg_0));
        return spaceCalendars.hasMore();
    }
}

