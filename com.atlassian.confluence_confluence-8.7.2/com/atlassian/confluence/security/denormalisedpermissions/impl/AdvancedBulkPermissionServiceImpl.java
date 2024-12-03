/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Nullable
 *  org.hibernate.dialect.Dialect
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl;

import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.security.denormalisedpermissions.AdvancedBulkPermissionService;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionStateManager;
import com.atlassian.confluence.security.denormalisedpermissions.impl.analytics.DenormalisedPermissionFailAnalyticsEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.DenormalisedContentChangeLogListener;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.DenormalisedSpaceChangeLogListener;
import com.atlassian.confluence.security.denormalisedpermissions.impl.user.DenormalisedSidManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import org.hibernate.dialect.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AdvancedBulkPermissionServiceImpl
implements AdvancedBulkPermissionService {
    private static final Logger log = LoggerFactory.getLogger(AdvancedBulkPermissionServiceImpl.class);
    private final DenormalisedPermissionStateManager denormalisedPermissionStateManager;
    private final ConfluenceAccessManager confluenceAccessManager;
    private final EventPublisher eventPublisher;
    private final DenormalisedSidManager denormalisedSidManager;
    private DenormalisedContentChangeLogListener denormalisedContentChangeLogListener;
    private DenormalisedSpaceChangeLogListener denormalisedSpaceChangeLogListener;
    private static final ReentrantLock flushLock = new ReentrantLock();

    @Autowired
    public AdvancedBulkPermissionServiceImpl(DenormalisedPermissionStateManager denormalisedPermissionStateManager, DenormalisedSidManager denormalisedSidManager, ConfluenceAccessManager confluenceAccessManager, EventPublisher eventPublisher, DenormalisedContentChangeLogListener denormalisedContentChangeLogListener, DenormalisedSpaceChangeLogListener denormalisedSpaceChangeLogListener) {
        this.denormalisedPermissionStateManager = denormalisedPermissionStateManager;
        this.denormalisedSidManager = denormalisedSidManager;
        this.confluenceAccessManager = confluenceAccessManager;
        this.eventPublisher = eventPublisher;
        this.denormalisedContentChangeLogListener = denormalisedContentChangeLogListener;
        this.denormalisedSpaceChangeLogListener = denormalisedSpaceChangeLogListener;
    }

    private boolean hasUserAccessToConfluence(ConfluenceUser confluenceUser) {
        AccessStatus accessStatus = this.confluenceAccessManager.getUserAccessStatusNoExemptions(confluenceUser);
        return accessStatus.canUseConfluence();
    }

    @Override
    public Set<Long> getAllUserSids(@Nullable ConfluenceUser confluenceUser) {
        if (!this.hasUserAccessToConfluence(confluenceUser)) {
            return Collections.emptySet();
        }
        if (!this.isContentApiUpAndRunning() && !this.isSpaceApiUpAndRunning()) {
            throw new IllegalStateException("Fast permissions service is not ready. This method should be called only if fast permissions are up and running.");
        }
        try {
            return this.denormalisedSidManager.getAllUserSids(confluenceUser);
        }
        catch (Exception e) {
            this.eventPublisher.publish((Object)new DenormalisedPermissionFailAnalyticsEvent(DenormalisedPermissionFailAnalyticsEvent.Action.GET_ALL_USER_SIDS));
            throw e;
        }
    }

    @Override
    public boolean isUserSuperAdmin(Set<Long> userSids) {
        return userSids.contains(-3L);
    }

    @Override
    public String getDatabaseDialect() {
        Dialect dialect = DataAccessUtils.getDialect();
        return dialect != null ? dialect.toString() : null;
    }

    @Override
    public boolean isApiUpAndRunning() {
        return this.denormalisedPermissionStateManager.isApiReady();
    }

    @Override
    public boolean isSpaceApiUpAndRunning() {
        return this.denormalisedPermissionStateManager.isSpaceApiReady();
    }

    @Override
    public boolean isContentApiUpAndRunning() {
        return this.denormalisedPermissionStateManager.isContentApiReady();
    }

    @Override
    public void flushPermissionsQueue() {
        if (!flushLock.tryLock()) {
            log.error("flushPermissionsQueue is already being processed");
            return;
        }
        try {
            this.denormalisedContentChangeLogListener.processLogRecords();
            this.denormalisedSpaceChangeLogListener.processLogRecords();
        }
        finally {
            flushLock.unlock();
        }
    }
}

