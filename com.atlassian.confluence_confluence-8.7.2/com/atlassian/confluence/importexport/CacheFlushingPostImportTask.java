/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.event.migration.XMLRestoreFinishedEvent
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.PostImportTask;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.crowd.event.migration.XMLRestoreFinishedEvent;
import com.atlassian.event.api.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public final class CacheFlushingPostImportTask
implements PostImportTask {
    private static final Logger log = LoggerFactory.getLogger(CacheFlushingPostImportTask.class);
    private final SpacePermissionManager spacePermissionManager;
    private final EventPublisher eventPublisher;

    public CacheFlushingPostImportTask(SpacePermissionManager spacePermissionManager, EventPublisher eventPublisher) {
        this.spacePermissionManager = spacePermissionManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(ImportContext context) {
        log.info("Flushing space permission caches");
        this.spacePermissionManager.flushCaches();
        log.info("Publishing restore-finished event to Embedded Crowd components");
        this.eventPublisher.publish((Object)new XMLRestoreFinishedEvent((Object)this, context.getWorkingFile()));
    }
}

