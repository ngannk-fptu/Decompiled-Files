/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Either
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl;

import com.atlassian.confluence.core.persistence.schema.api.SchemaInformationService;
import com.atlassian.confluence.impl.security.access.AccessDenied;
import com.atlassian.confluence.impl.security.access.SpacePermissionAccessMapper;
import com.atlassian.confluence.impl.security.access.SpacePermissionSubjectType;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.security.denormalisedpermissions.BulkPermissionService;
import com.atlassian.confluence.security.denormalisedpermissions.impl.RegularEntitiesAndPermissionsHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.TooManySidsException;
import com.atlassian.confluence.security.denormalisedpermissions.impl.analytics.PermittedSpaceIdsAnalyticsEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.analytics.PermittedSpacesAnalyticsEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.analytics.SpaceKeysWithStatusesAnalyticsEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.analytics.VisibleChildPagesAnalyticsEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.analytics.VisiblePagesInSpaceAnalyticsEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.analytics.VisibleTopLevelPagesAnalyticsEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.dao.DenormalisedContentViewPermissionDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.SimpleContent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.dao.DenormalisedSpaceChangeLogDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.dao.DenormalisedSpacePermissionDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.dao.SpaceKeyWithPermission;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain.SpacePermissionType;
import com.atlassian.confluence.security.denormalisedpermissions.impl.user.DenormalisedSidManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Either;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DenormalisedPermissionServiceImpl
implements BulkPermissionService {
    private static final Logger log = LoggerFactory.getLogger(DenormalisedPermissionServiceImpl.class);
    private static final int IN_BATCH_SIZE = 1000;
    private final DenormalisedSpacePermissionDao denormalisedSpacePermissionDao;
    private final ConfluenceAccessManager confluenceAccessManager;
    private final SpacePermissionAccessMapper spacePermissionAccessMapper;
    private final DenormalisedContentViewPermissionDao denormalisedContentViewPermissionDao;
    private final EventPublisher eventPublisher;
    private final DenormalisedSidManager denormalisedSidManager;
    private final RegularEntitiesAndPermissionsHelper regularEntitiesAndPermissionsHelper;
    private final DenormalisedSpaceChangeLogDao denormalisedSpaceChangeLogDao;
    private final SchemaInformationService schemaInformationService;

    @Autowired
    public DenormalisedPermissionServiceImpl(DenormalisedSpacePermissionDao denormalisedSpacePermissionDao, DenormalisedSidManager denormalisedSidManager, ConfluenceAccessManager confluenceAccessManager, SpacePermissionAccessMapper spacePermissionAccessMapper, DenormalisedContentViewPermissionDao denormalisedContentViewPermissionDao, RegularEntitiesAndPermissionsHelper regularEntitiesAndPermissionsHelper, DenormalisedSpaceChangeLogDao denormalisedSpaceChangeLogDao, EventPublisher eventPublisher, SchemaInformationService schemaInformationService) {
        this.denormalisedSpacePermissionDao = denormalisedSpacePermissionDao;
        this.denormalisedSidManager = denormalisedSidManager;
        this.confluenceAccessManager = confluenceAccessManager;
        this.spacePermissionAccessMapper = spacePermissionAccessMapper;
        this.denormalisedContentViewPermissionDao = denormalisedContentViewPermissionDao;
        this.regularEntitiesAndPermissionsHelper = regularEntitiesAndPermissionsHelper;
        this.denormalisedSpaceChangeLogDao = denormalisedSpaceChangeLogDao;
        this.eventPublisher = eventPublisher;
        this.schemaInformationService = schemaInformationService;
    }

    @Override
    public Map<String, Boolean> getAllSpaceKeysWithPermissionStatuses(ConfluenceUser confluenceUser, String spacePermissionType) {
        Map<String, Boolean> spaceKeysWithPermissionStatus;
        StopWatch stopWatch = StopWatch.createStarted();
        Set<Long> sids = this.getFullDenormalisedSidListForUser(confluenceUser, spacePermissionType);
        boolean permissionExempt = sids.contains(-3L);
        int permittedSpaceCount = 0;
        int nonPermittedSpaceCount = 0;
        if (permissionExempt) {
            List<String> spaceKeys = this.regularEntitiesAndPermissionsHelper.findAllSpaceKeys();
            spaceKeysWithPermissionStatus = spaceKeys.stream().collect(Collectors.toMap(s -> s, s -> true));
            permittedSpaceCount = spaceKeysWithPermissionStatus.size();
        } else {
            List<SpaceKeyWithPermission> spaceListWithPermissions = this.denormalisedSpacePermissionDao.getAllSpacesKeysWithPermissionInfo(sids, this.getInternalSpacePermissionType(spacePermissionType));
            spaceKeysWithPermissionStatus = new HashMap<String, Boolean>(spaceListWithPermissions.size());
            for (SpaceKeyWithPermission spaceWithPermission : spaceListWithPermissions) {
                if (spaceWithPermission.isHasPermission()) {
                    ++permittedSpaceCount;
                } else {
                    ++nonPermittedSpaceCount;
                }
                spaceKeysWithPermissionStatus.put(spaceWithPermission.getSpaceKey(), spaceWithPermission.isHasPermission());
            }
        }
        this.eventPublisher.publish((Object)new SpaceKeysWithStatusesAnalyticsEvent(permittedSpaceCount, nonPermittedSpaceCount, false, false, stopWatch.getTime(), 0L, permissionExempt, sids.size()));
        return spaceKeysWithPermissionStatus;
    }

    private boolean hasUserAccessToConfluence(ConfluenceUser confluenceUser, String spacePermissionType) {
        AccessStatus accessStatus = this.confluenceAccessManager.getUserAccessStatus(confluenceUser);
        Either<AccessDenied, Set<SpacePermissionSubjectType>> spacePermissionSubjectTypes = this.spacePermissionAccessMapper.getPermissionCheckSubjectTypes(accessStatus, spacePermissionType);
        return spacePermissionSubjectTypes.isRight();
    }

    private boolean hasUserAccessToConfluence(ConfluenceUser confluenceUser) {
        AccessStatus accessStatus = this.confluenceAccessManager.getUserAccessStatus(confluenceUser);
        return accessStatus.canUseConfluence();
    }

    @Override
    public List<Space> getPermittedSpaces(SpacesQuery spaceQuery, int offset, int limit) {
        StopWatch stopWatch = StopWatch.createStarted();
        String spacePermissionType = spaceQuery.getPermissionType();
        Set<Long> sids = this.getFullDenormalisedSidListForUser((ConfluenceUser)spaceQuery.getUser(), spacePermissionType);
        boolean permissionExempt = sids.contains(-3L);
        List<Space> spaces = this.denormalisedSpacePermissionDao.getSpaces(sids, this.getInternalSpacePermissionType(spacePermissionType), spaceQuery, offset, limit, permissionExempt);
        this.eventPublisher.publish((Object)new PermittedSpacesAnalyticsEvent(spaces.size(), limit, false, false, stopWatch.getTime(), permissionExempt, sids.size()));
        return spaces;
    }

    @Override
    public Set<Long> getPermittedSpaceIds(@Nullable ConfluenceUser confluenceUser, Set<Long> spaceIds, String spacePermissionType) {
        StopWatch stopWatch = StopWatch.createStarted();
        Set<Long> sids = this.getFullDenormalisedSidListForUser(confluenceUser, spacePermissionType);
        boolean permissionExempt = sids.contains(-3L);
        if (permissionExempt) {
            this.sendAnalyticsEventForGettingPermittedSpaceIds(stopWatch, sids.size(), permissionExempt, spaceIds.size(), spaceIds.size(), spaceIds.size(), 0);
            return spaceIds;
        }
        Set<Long> recentlyChangedSpaces = this.getRecentlyChangedSpaces();
        Set<Long> permittedSpaceIds = this.denormalisedSpacePermissionDao.findPermittedSpaceIds(sids, spaceIds, this.getInternalSpacePermissionType(spacePermissionType));
        int numberOfPermittedSpacesBeforePermissionsRechecking = permittedSpaceIds.size();
        permittedSpaceIds = permittedSpaceIds.stream().filter(spaceId -> !recentlyChangedSpaces.contains(spaceId) || this.isSpacePermittedViaRegularPermissions(confluenceUser, (long)spaceId, spacePermissionType)).collect(Collectors.toSet());
        this.sendAnalyticsEventForGettingPermittedSpaceIds(stopWatch, sids.size(), permissionExempt, spaceIds.size(), permittedSpaceIds.size(), numberOfPermittedSpacesBeforePermissionsRechecking, recentlyChangedSpaces.size());
        return permittedSpaceIds;
    }

    private void sendAnalyticsEventForGettingPermittedSpaceIds(StopWatch stopWatch, int numberOfSids, boolean permissionExempt, int inputNumberOfSpaces, int visibleNumberOfSpaces, int visibleNumberOfSpacesBeforeRecheckingPermissions, int numberOfRecentlyChangedSpaces) {
        long duration = stopWatch.getTime();
        log.debug("getPermittedSpaceIds was called. Execution time: {} ms, permission exempt: {}, number of sids: {}, input number of spaces: {}, visible number of spaces: {}, number of spaces before rechecking permission: {}, number of recently changed spaces: {}", new Object[]{duration, permissionExempt, numberOfSids, inputNumberOfSpaces, visibleNumberOfSpaces, visibleNumberOfSpacesBeforeRecheckingPermissions, numberOfRecentlyChangedSpaces});
        this.eventPublisher.publish((Object)new PermittedSpaceIdsAnalyticsEvent(duration, numberOfSids, permissionExempt, inputNumberOfSpaces, visibleNumberOfSpaces, visibleNumberOfSpacesBeforeRecheckingPermissions, numberOfRecentlyChangedSpaces));
    }

    private boolean isSpacePermittedViaRegularPermissions(@Nullable ConfluenceUser confluenceUser, long spaceId, String spacePermissionType) {
        return this.regularEntitiesAndPermissionsHelper.isSpacePermitted(confluenceUser, spaceId, spacePermissionType);
    }

    private Set<Long> getRecentlyChangedSpaces() {
        return new HashSet<Long>(this.denormalisedSpaceChangeLogDao.getAllChangedSpaceIds());
    }

    @Override
    public Map<Long, List<SimpleContent>> getVisibleChildPages(@Nullable ConfluenceUser confluenceUser, Set<Long> parentPageIdSet, boolean checkInheritedPermissions) {
        if (!checkInheritedPermissions) {
            StopWatch stopWatch = StopWatch.createStarted();
            Set<Long> sids = this.getFullDenormalisedSidListForUser(confluenceUser);
            boolean permissionExempt = sids.contains(-3L);
            Iterable partitions = Iterables.partition(parentPageIdSet, (int)1000);
            HashMap<Long, List<SimpleContent>> fullResultMap = new HashMap<Long, List<SimpleContent>>();
            int partitionsProcessed = 0;
            for (List partition : partitions) {
                Map<Long, List<SimpleContent>> resultMap = this.denormalisedContentViewPermissionDao.getAllVisibleChildren(partition, sids, permissionExempt);
                StopWatch localStopWatch = StopWatch.createStarted();
                log.trace("getVisibleChildPages (partition N {}) for {} page ids and {} sids took {} ms. Returned results for {} pages.", new Object[]{partitionsProcessed++, partition.size(), sids.size(), localStopWatch.getTime(), resultMap.size()});
                fullResultMap.putAll(resultMap);
            }
            log.debug("getVisibleChildPages for {} page ids and {} sids took {} ms. Returned results for {} pages. Input list was splitted to {} partitions", new Object[]{parentPageIdSet.size(), sids.size(), stopWatch.getTime(), fullResultMap.size(), partitionsProcessed});
            long childPagesCount = fullResultMap.values().stream().mapToInt(List::size).sum();
            this.eventPublisher.publish((Object)new VisibleChildPagesAnalyticsEvent(parentPageIdSet.size(), checkInheritedPermissions, childPagesCount, false, stopWatch.getTime(), permissionExempt, sids.size()));
            return fullResultMap;
        }
        throw new IllegalStateException("checkInheritedPermissions parameter is not supported for getVisibleChildPages");
    }

    @Override
    public List<SimpleContent> getVisibleTopLevelPages(@Nullable ConfluenceUser confluenceUser, long spaceId) {
        List<Object> simplePages = Collections.emptyList();
        Integer amountOfSids = null;
        StopWatch stopWatch = StopWatch.createStarted();
        boolean permissionExempt = false;
        if (this.regularEntitiesAndPermissionsHelper.isSpacePermitted(confluenceUser, spaceId, "VIEWSPACE")) {
            Set<Long> sids = this.getFullDenormalisedSidListForUser(confluenceUser);
            permissionExempt = sids.contains(-3L);
            simplePages = this.denormalisedContentViewPermissionDao.getAllVisibleTopLevelPages(spaceId, sids, permissionExempt);
        }
        long duration = stopWatch.getTime();
        log.debug("getVisibleTopLevelPages took {} ms. Returned {} pages", (Object)duration, (Object)simplePages.size());
        this.eventPublisher.publish((Object)new VisibleTopLevelPagesAnalyticsEvent(simplePages.size(), false, duration, permissionExempt, amountOfSids));
        return simplePages;
    }

    @Override
    public List<SimpleContent> getAllVisiblePagesInSpace(@Nullable ConfluenceUser confluenceUser, long spaceId) {
        List sidIdList = Collections.emptyList();
        List<SimpleContent> permittedPages = Collections.emptyList();
        List<Object> visiblePages = Collections.emptyList();
        StopWatch stopWatch = StopWatch.createStarted();
        boolean permissionExempt = false;
        if (this.regularEntitiesAndPermissionsHelper.isSpacePermitted(confluenceUser, spaceId, "VIEWSPACE")) {
            Set<Long> sids = this.getFullDenormalisedSidListForUser(confluenceUser);
            permissionExempt = sids.contains(-3L);
            permittedPages = this.denormalisedContentViewPermissionDao.getVisiblePagesFromSpace(spaceId, sids, permissionExempt);
            visiblePages = this.getPagesWithVisibleAncestors(permittedPages);
        }
        long duration = stopWatch.getTime();
        log.debug("getAllVisiblePagesInSpace took {} ms. Returned {} visible pages. Processed {} permitted pages", new Object[]{duration, visiblePages.size(), permittedPages.size()});
        this.eventPublisher.publish((Object)new VisiblePagesInSpaceAnalyticsEvent(visiblePages.size(), false, duration, permissionExempt, sidIdList.size()));
        return visiblePages;
    }

    @Override
    public Set<Long> getVisiblePageIds(@Nullable ConfluenceUser confluenceUser, Set<Long> pageIds, boolean checkSpacePermissions) {
        Set<Long> sids = this.getFullDenormalisedSidListForUser(confluenceUser);
        boolean permissionExempt = sids.contains(-3L);
        if (permissionExempt) {
            return pageIds;
        }
        if (checkSpacePermissions) {
            if (!this.hasUserAccessToConfluence(confluenceUser)) {
                return Collections.emptySet();
            }
            List<SimpleContent> contentList = this.denormalisedContentViewPermissionDao.getDenormalisedContentList(pageIds);
            Set<Long> spaceIds = contentList.stream().map(SimpleContent::getSpaceId).collect(Collectors.toSet());
            Set<Long> visibleSpaceIds = this.denormalisedSpacePermissionDao.findPermittedSpaceIds(sids, spaceIds, this.getInternalSpacePermissionType("VIEWSPACE"));
            pageIds = contentList.stream().filter(page -> visibleSpaceIds.contains(page.getSpaceId())).map(SimpleContent::getId).collect(Collectors.toSet());
        }
        return this.denormalisedContentViewPermissionDao.getVisiblePages(sids, pageIds);
    }

    private List<SimpleContent> getPagesWithVisibleAncestors(List<SimpleContent> allPermittedPagesFromSpace) {
        ArrayList<SimpleContent> finalListOfVisiblePages = new ArrayList<SimpleContent>(allPermittedPagesFromSpace.size());
        Stack<Long> pendingPagesToProcess = new Stack<Long>();
        HashMap parentIdToChildrenMap = new HashMap();
        for (SimpleContent page : allPermittedPagesFromSpace) {
            if (page.getParentId() == null) {
                finalListOfVisiblePages.add(page);
                pendingPagesToProcess.add(page.getId());
                continue;
            }
            parentIdToChildrenMap.computeIfAbsent(page.getParentId(), v -> new ArrayList()).add(page);
        }
        while (pendingPagesToProcess.size() > 0) {
            long pageId = (Long)pendingPagesToProcess.pop();
            List visibleChildren = parentIdToChildrenMap.getOrDefault(pageId, Collections.emptyList());
            finalListOfVisiblePages.addAll(visibleChildren);
            pendingPagesToProcess.addAll(visibleChildren.stream().map(SimpleContent::getId).collect(Collectors.toList()));
        }
        return finalListOfVisiblePages;
    }

    private SpacePermissionType getInternalSpacePermissionType(String spacePermissionType) {
        Preconditions.checkArgument((spacePermissionType != null ? 1 : 0) != 0, (Object)"Missing required space permission type");
        try {
            return SpacePermissionType.valueOf(spacePermissionType);
        }
        catch (IllegalArgumentException e) {
            String allowedPermissionTypes = Stream.of(SpacePermissionType.values()).map(Enum::name).collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Denormalised space permissions do not support this space permission type: " + spacePermissionType + ". Only " + allowedPermissionTypes + " are allowed");
        }
    }

    private Set<Long> getFullDenormalisedSidListForUser(ConfluenceUser confluenceUser, String spacePermissionType) {
        if (!this.hasUserAccessToConfluence(confluenceUser, spacePermissionType)) {
            return Collections.emptySet();
        }
        return this.getFullDenormalisedSidListForUser(confluenceUser);
    }

    private Set<Long> getFullDenormalisedSidListForUser(ConfluenceUser confluenceUser) {
        Set<Long> userSids = this.denormalisedSidManager.getAllUserSids(confluenceUser);
        int inExpressionCountLimit = this.schemaInformationService.getDialect().getInExpressionCountLimit();
        if (inExpressionCountLimit > 0 && userSids.size() > inExpressionCountLimit) {
            throw new TooManySidsException("User " + AuthenticatedUserThreadLocal.getUsername() + " has too many groups (" + userSids.size() + ") having content / space permissions. Fast permissions do not support more than " + inExpressionCountLimit + " groups (for current database). The request will be processed by legacy permissions.", userSids.size());
        }
        return userSids;
    }
}

