/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.security.denormalisedpermissions.BulkPermissionService;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionStateManager;
import com.atlassian.confluence.security.denormalisedpermissions.impl.RequestCannotBeProcessedByFastPermissionsException;
import com.atlassian.confluence.security.denormalisedpermissions.impl.TooManySidsException;
import com.atlassian.confluence.security.denormalisedpermissions.impl.analytics.DenormalisedPermissionFailAnalyticsEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.SimpleContent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain.SpacePermissionType;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DenormalisedPermissionRouter
implements BulkPermissionService {
    private static final Logger log = LoggerFactory.getLogger(DenormalisedPermissionRouter.class);
    private final BulkPermissionService realDenormalisedPermissionService;
    private final BulkPermissionService fallbackDenormalisedPermissionService;
    private final DenormalisedPermissionStateManager denormalisedPermissionStateManager;
    private final EventPublisher eventPublisher;

    public DenormalisedPermissionRouter(BulkPermissionService realDenormalisedPermissionService, BulkPermissionService fallbackDenormalisedPermissionService, DenormalisedPermissionStateManager denormalisedPermissionStateManager, EventPublisher eventPublisher) {
        this.realDenormalisedPermissionService = realDenormalisedPermissionService;
        this.fallbackDenormalisedPermissionService = fallbackDenormalisedPermissionService;
        this.denormalisedPermissionStateManager = denormalisedPermissionStateManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Map<String, Boolean> getAllSpaceKeysWithPermissionStatuses(@Nullable ConfluenceUser confluenceUser, String spacePermissionType) {
        try {
            if (this.isSpacePermissionTypeSupported(spacePermissionType) && this.isSpaceApiUpAndRunning()) {
                return this.realDenormalisedPermissionService.getAllSpaceKeysWithPermissionStatuses(confluenceUser, spacePermissionType);
            }
        }
        catch (RequestCannotBeProcessedByFastPermissionsException e) {
            log.warn(e.getMessage());
            this.sendAnalyticsEventAboutFailure(DenormalisedPermissionFailAnalyticsEvent.Action.GET_ALL_SPACE_KEYS_WITH_PERMISSION_STATUSES, e);
        }
        catch (Exception e) {
            log.error("Denormalised permission execution failed for user " + confluenceUser + " and permission type " + spacePermissionType, (Throwable)e);
            this.sendAnalyticsEventAboutFailure(DenormalisedPermissionFailAnalyticsEvent.Action.GET_ALL_SPACE_KEYS_WITH_PERMISSION_STATUSES);
        }
        return this.fallbackDenormalisedPermissionService.getAllSpaceKeysWithPermissionStatuses(confluenceUser, spacePermissionType);
    }

    @Override
    public List<Space> getPermittedSpaces(SpacesQuery spaceQuery, int offset, int limit) {
        try {
            if (this.isSpaceQuerySuitableForDenormalisedSpacePermissions(spaceQuery) && this.isSpaceApiUpAndRunning()) {
                return this.realDenormalisedPermissionService.getPermittedSpaces(spaceQuery, offset, limit);
            }
        }
        catch (RequestCannotBeProcessedByFastPermissionsException e) {
            log.warn(e.getMessage());
            this.sendAnalyticsEventAboutFailure(DenormalisedPermissionFailAnalyticsEvent.Action.GET_PERMITTED_SPACES, e);
        }
        catch (Exception e) {
            log.error("Denormalised permission execution failed for space query " + spaceQuery.toString() + ". Exception: " + e.getMessage(), (Throwable)e);
            this.sendAnalyticsEventAboutFailure(DenormalisedPermissionFailAnalyticsEvent.Action.GET_PERMITTED_SPACES);
        }
        return this.fallbackDenormalisedPermissionService.getPermittedSpaces(spaceQuery, offset, limit);
    }

    @Override
    public Set<Long> getPermittedSpaceIds(@Nullable ConfluenceUser confluenceUser, Set<Long> spaceIds, String spacePermissionType) {
        try {
            if (this.isSpaceApiUpAndRunning()) {
                return this.realDenormalisedPermissionService.getPermittedSpaceIds(confluenceUser, spaceIds, spacePermissionType);
            }
        }
        catch (RequestCannotBeProcessedByFastPermissionsException e) {
            log.warn(e.getMessage());
            this.sendAnalyticsEventAboutFailure(DenormalisedPermissionFailAnalyticsEvent.Action.GET_PERMITTED_SPACE_IDS, e);
        }
        catch (Exception e) {
            log.error("Denormalised permission execution failed for user " + confluenceUser + " and permission type " + spacePermissionType, (Throwable)e);
            this.sendAnalyticsEventAboutFailure(DenormalisedPermissionFailAnalyticsEvent.Action.GET_PERMITTED_SPACE_IDS);
        }
        return this.fallbackDenormalisedPermissionService.getPermittedSpaceIds(confluenceUser, spaceIds, spacePermissionType);
    }

    @Override
    public Map<Long, List<SimpleContent>> getVisibleChildPages(@Nullable ConfluenceUser confluenceUser, Set<Long> parentPageIdSet, boolean checkInheritedPermissions) {
        Preconditions.checkNotNull(parentPageIdSet, (Object)"parentPageIdSet must not be null");
        if (parentPageIdSet.size() == 0) {
            return Collections.emptyMap();
        }
        try {
            if (this.isContentApiUpAndRunning()) {
                return this.realDenormalisedPermissionService.getVisibleChildPages(confluenceUser, parentPageIdSet, checkInheritedPermissions);
            }
        }
        catch (RequestCannotBeProcessedByFastPermissionsException e) {
            log.warn(e.getMessage());
            this.sendAnalyticsEventAboutFailure(DenormalisedPermissionFailAnalyticsEvent.Action.GET_VISIBLE_CHILD_PAGES, e);
        }
        catch (Exception e) {
            log.error("Denormalised permission execution failed, parentPageIdSet size = " + parentPageIdSet.size() + ". Exception: " + e.getMessage(), (Throwable)e);
            this.sendAnalyticsEventAboutFailure(DenormalisedPermissionFailAnalyticsEvent.Action.GET_VISIBLE_CHILD_PAGES);
        }
        return this.fallbackDenormalisedPermissionService.getVisibleChildPages(confluenceUser, parentPageIdSet, checkInheritedPermissions);
    }

    @Override
    public List<SimpleContent> getVisibleTopLevelPages(@Nullable ConfluenceUser confluenceUser, long spaceId) {
        try {
            if (this.isContentApiUpAndRunning()) {
                return this.realDenormalisedPermissionService.getVisibleTopLevelPages(confluenceUser, spaceId);
            }
        }
        catch (RequestCannotBeProcessedByFastPermissionsException e) {
            log.warn(e.getMessage());
            this.sendAnalyticsEventAboutFailure(DenormalisedPermissionFailAnalyticsEvent.Action.GET_VISIBLE_TOP_LEVEL_PAGES, e);
        }
        catch (Exception e) {
            log.error("Denormalised permission execution failed, spaceId = " + spaceId + ". Exception: " + e.getMessage(), (Throwable)e);
            this.sendAnalyticsEventAboutFailure(DenormalisedPermissionFailAnalyticsEvent.Action.GET_VISIBLE_TOP_LEVEL_PAGES);
        }
        return this.fallbackDenormalisedPermissionService.getVisibleTopLevelPages(confluenceUser, spaceId);
    }

    @Override
    public List<SimpleContent> getAllVisiblePagesInSpace(@Nullable ConfluenceUser confluenceUser, long spaceId) {
        try {
            if (this.isContentApiUpAndRunning()) {
                return this.realDenormalisedPermissionService.getAllVisiblePagesInSpace(confluenceUser, spaceId);
            }
        }
        catch (RequestCannotBeProcessedByFastPermissionsException e) {
            log.warn(e.getMessage());
            this.sendAnalyticsEventAboutFailure(DenormalisedPermissionFailAnalyticsEvent.Action.GET_ALL_VISIBLE_PAGES_IN_SPACE, e);
        }
        catch (Exception e) {
            log.error("Denormalised permission execution failed, spaceId = " + spaceId + ". Exception: " + e.getMessage(), (Throwable)e);
            this.sendAnalyticsEventAboutFailure(DenormalisedPermissionFailAnalyticsEvent.Action.GET_ALL_VISIBLE_PAGES_IN_SPACE);
        }
        return this.fallbackDenormalisedPermissionService.getAllVisiblePagesInSpace(confluenceUser, spaceId);
    }

    @Override
    public Set<Long> getVisiblePageIds(@Nullable ConfluenceUser confluenceUser, Set<Long> pageIds, boolean checkSpacePermissions) {
        try {
            if (this.isContentApiUpAndRunning() && (!checkSpacePermissions || this.isSpaceApiUpAndRunning())) {
                return this.realDenormalisedPermissionService.getVisiblePageIds(confluenceUser, pageIds, checkSpacePermissions);
            }
        }
        catch (RequestCannotBeProcessedByFastPermissionsException e) {
            log.warn(e.getMessage());
            this.sendAnalyticsEventAboutFailure(DenormalisedPermissionFailAnalyticsEvent.Action.GET_PERMITTED_PAGE_IDS, e);
        }
        catch (Exception e) {
            log.error("Denormalised permission execution failed, page ids set size: " + pageIds.size() + ". Exception: " + e.getMessage(), (Throwable)e);
            this.sendAnalyticsEventAboutFailure(DenormalisedPermissionFailAnalyticsEvent.Action.GET_PERMITTED_PAGE_IDS);
        }
        return this.fallbackDenormalisedPermissionService.getVisiblePageIds(confluenceUser, pageIds, checkSpacePermissions);
    }

    private boolean isSpacePermissionTypeSupported(String spacePermissionType) {
        return Arrays.stream(SpacePermissionType.values()).anyMatch(t -> t.name().equals(spacePermissionType));
    }

    private boolean isSpaceQuerySuitableForDenormalisedSpacePermissions(SpacesQuery spacesQuery) {
        return spacesQuery.getCreationDate() == null && !spacesQuery.getFavourite().isPresent() && (spacesQuery.getLabels() == null || spacesQuery.getLabels().isEmpty()) && spacesQuery.getSpaceType() == null && this.isSpacePermissionTypeSupported(spacesQuery.getPermissionType());
    }

    private boolean isSpaceApiUpAndRunning() {
        return this.denormalisedPermissionStateManager.isSpaceApiReady();
    }

    private boolean isContentApiUpAndRunning() {
        return this.denormalisedPermissionStateManager.isContentApiReady();
    }

    @Internal
    @VisibleForTesting
    public void sendAnalyticsEventAboutFailure(DenormalisedPermissionFailAnalyticsEvent.Action action) {
        this.eventPublisher.publish((Object)new DenormalisedPermissionFailAnalyticsEvent(action));
    }

    private void sendAnalyticsEventAboutFailure(DenormalisedPermissionFailAnalyticsEvent.Action action, RequestCannotBeProcessedByFastPermissionsException e) {
        if (e instanceof TooManySidsException) {
            int numberOfSids = ((TooManySidsException)e).getNumberOfSids();
            this.eventPublisher.publish((Object)new DenormalisedPermissionFailAnalyticsEvent(action, DenormalisedPermissionFailAnalyticsEvent.ErrorType.TOO_MANY_SIDS, numberOfSids));
        } else {
            this.eventPublisher.publish((Object)new DenormalisedPermissionFailAnalyticsEvent(action, DenormalisedPermissionFailAnalyticsEvent.ErrorType.REJECTED_BY_FAST_PERMISSIONS));
        }
    }
}

