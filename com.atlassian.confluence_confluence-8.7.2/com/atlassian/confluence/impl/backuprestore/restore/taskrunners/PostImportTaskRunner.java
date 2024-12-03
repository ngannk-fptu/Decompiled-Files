/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.event.migration.XMLRestoreFinishedEvent
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.taskrunners;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupProperties;
import com.atlassian.confluence.impl.backuprestore.restore.taskrunners.PostSiteImportUpgrader;
import com.atlassian.confluence.internal.index.status.ReIndexJobPersister;
import com.atlassian.confluence.internal.pages.TrashManagerInternal;
import com.atlassian.confluence.internal.spaces.persistence.SpaceDaoInternal;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.trust.KeyPairInitialiser;
import com.atlassian.crowd.event.migration.XMLRestoreFinishedEvent;
import com.atlassian.event.api.EventPublisher;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.Instant;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostImportTaskRunner {
    private static final Logger log = LoggerFactory.getLogger(PostImportTaskRunner.class);
    private final SpacePermissionManager spacePermissionManager;
    private final EventPublisher eventPublisher;
    private final ReIndexJobPersister reIndexJobPersister;
    private final TrashManagerInternal trashManagerInternal;
    private final KeyPairInitialiser keyPairInitialiser;
    private final PostSiteImportUpgrader postSiteImportUpgrader;
    private final SpaceDaoInternal spaceDaoInternal;

    public PostImportTaskRunner(SpacePermissionManager spacePermissionManager, EventPublisher eventPublisher, ReIndexJobPersister reIndexJobPersister, TrashManagerInternal trashManagerInternal, KeyPairInitialiser keyPairInitialiser, PostSiteImportUpgrader postSiteImportUpgrader, SpaceDaoInternal spaceDaoInternal) {
        this.spacePermissionManager = spacePermissionManager;
        this.eventPublisher = eventPublisher;
        this.reIndexJobPersister = reIndexJobPersister;
        this.trashManagerInternal = trashManagerInternal;
        this.keyPairInitialiser = keyPairInitialiser;
        this.postSiteImportUpgrader = postSiteImportUpgrader;
        this.spaceDaoInternal = spaceDaoInternal;
    }

    public void runUpgradeTasks(BackupProperties backupProperties) throws BackupRestoreException {
        this.postSiteImportUpgrader.runUpgradeTasks(backupProperties);
    }

    public void runCacheFlushingPostImportTask(String workingFile) {
        log.debug("Flushing space permission caches");
        this.spacePermissionManager.flushCaches();
        log.debug("Publishing restore-finished event to Embedded Crowd components");
        this.eventPublisher.publish((Object)new XMLRestoreFinishedEvent((Object)this, workingFile));
    }

    public void runClearReIndexJobPostImportTask() {
        log.debug("Deleting re-index history of previous instance");
        this.reIndexJobPersister.clear();
    }

    public void evictSpacesFromCache(Collection<String> spaceKeys) {
        spaceKeys.forEach(this.spaceDaoInternal::removeSpaceFromCache);
    }

    public void runTrashDatePostImportTaskForSpaces(Collection<String> spaceKeys) {
        Instant importTime = Instant.now();
        spaceKeys.forEach(spaceKey -> {
            log.debug("Setting {} as default trash date for space {}", (Object)importTime, spaceKey);
            this.trashManagerInternal.migrateTrashDate((String)spaceKey, importTime);
        });
    }

    public void runTrashDatePostImportTaskForSite() {
        Instant importTime = Instant.now();
        log.debug("Setting {} as default trash date", (Object)importTime);
        this.trashManagerInternal.migrateTrashDate(importTime);
    }

    public void runKeyInitPostImportTask() throws BackupRestoreException {
        try {
            log.debug("Init Confluence key after a site import");
            this.keyPairInitialiser.initConfluenceKey();
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            log.error("Problem when init Confluence key after a site import");
            throw new BackupRestoreException(e);
        }
    }
}

