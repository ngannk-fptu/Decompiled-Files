/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.config.lifecycle.LifecycleContext
 *  com.atlassian.config.lifecycle.LifecycleItem
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.config.lifecycle.LifecycleContext;
import com.atlassian.config.lifecycle.LifecycleItem;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ZduManager;
import com.atlassian.confluence.cluster.ZduStatus;
import com.atlassian.confluence.event.events.cluster.ZduFinalizationRequestEvent;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataStorageLocationResolver;
import com.atlassian.confluence.upgrade.AttachmentMigratorToV4;
import com.atlassian.confluence.upgrade.V4AttachmentDisabledAnalyticsEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.features.DarkFeatureManager;
import java.io.File;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentV3toV4MigrationAction
implements LifecycleItem {
    private static final Logger log = LoggerFactory.getLogger(AttachmentV3toV4MigrationAction.class);
    public static final String V_3_TO_V_4_MIGRATION_LOCK = "v3-to-v4-migration.lock";
    private AttachmentDataStorageLocationResolver attachmentDirectoryLocationResolverV3;
    private AttachmentMigratorToV4 attachmentMigratorToV4;
    private DarkFeatureManager salDarkFeatureManager;
    private ExecutorService attachmentMigrationExecutorService;
    private ClusterLockService clusterLockService;
    private ZduManager zduManager;
    private EventPublisher eventPublisher;

    public void startup(LifecycleContext lifecycleContext) throws InterruptedException {
        this.eventPublisher.register((Object)this);
        this.startMigration();
    }

    private void startMigration() {
        if (this.salDarkFeatureManager.isEnabledForAllUsers("confluence.disable-attachments-ver004").orElse(false).booleanValue()) {
            this.eventPublisher.publish((Object)new V4AttachmentDisabledAnalyticsEvent());
            log.debug("Attachments migration from version 3 to version 4 is disabled by a feature flag: confluence.disable-attachments-ver004");
            return;
        }
        if (this.zduManager.getUpgradeStatus().getState().equals((Object)ZduStatus.State.ENABLED)) {
            log.debug("Attachments migration postponed due to ZDU upgrade in progress.");
            return;
        }
        File ver003Dir = this.attachmentDirectoryLocationResolverV3.getFileLocation().asJavaFile();
        if (!ver003Dir.exists()) {
            log.debug("Attachments migration from version 3 to version 4 not needed.");
            return;
        }
        this.attachmentMigrationExecutorService.submit(() -> {
            ClusterLock clusterMigrationLock = this.clusterLockService.getLockForName(V_3_TO_V_4_MIGRATION_LOCK);
            if (clusterMigrationLock != null && !clusterMigrationLock.tryLock()) {
                log.info("V3 to V4 attachment migration is locked and it is probably running on another node.");
                return;
            }
            log.info("Attachments migration from version 3 to version 4 has started.");
            try {
                this.attachmentMigratorToV4.run();
            }
            catch (Exception e) {
                log.error("Attachments migration from version 3 to version 4 failed unexpectedly. Error: {}", (Object)e.getMessage());
            }
            finally {
                this.clearMigrationLock(clusterMigrationLock);
            }
        });
    }

    @EventListener
    public void onRequested(ZduFinalizationRequestEvent e) throws InterruptedException {
        log.debug("Attachments migration check triggered by completed ZDU upgrade.");
        this.startMigration();
    }

    public void shutdown(LifecycleContext lifecycleContext) throws Exception {
        this.attachmentMigratorToV4.stopMigration();
        this.eventPublisher.unregister((Object)this);
    }

    private void clearMigrationLock(ClusterLock lock) {
        if (lock != null) {
            lock.unlock();
        }
    }

    public void setAttachmentDirectoryLocationResolverV003(AttachmentDataStorageLocationResolver attachmentDirectoryLocationResolverV003) {
        this.attachmentDirectoryLocationResolverV3 = attachmentDirectoryLocationResolverV003;
    }

    public void setAttachmentMigratorToV4(AttachmentMigratorToV4 attachmentMigratorToV4) {
        this.attachmentMigratorToV4 = attachmentMigratorToV4;
    }

    public void setSalDarkFeatureManager(DarkFeatureManager salDarkFeatureManager) {
        this.salDarkFeatureManager = salDarkFeatureManager;
    }

    public void setAttachmentMigrationExecutorService(ExecutorService attachmentMigrationExecutorService) {
        this.attachmentMigrationExecutorService = attachmentMigrationExecutorService;
    }

    public void setClusterLockService(ClusterLockService clusterLockService) {
        this.clusterLockService = clusterLockService;
    }

    public void setClusterManager(ClusterManager clusterManager) {
        this.clusterLockService = (ClusterLockService)clusterManager;
    }

    public void setZduManager(ZduManager zduManager) {
        this.zduManager = zduManager;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}

