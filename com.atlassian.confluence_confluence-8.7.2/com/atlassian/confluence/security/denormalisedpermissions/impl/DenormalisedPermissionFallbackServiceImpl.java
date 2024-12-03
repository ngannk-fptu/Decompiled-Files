/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Either
 *  com.atlassian.user.User
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.annotation.Nullable
 *  org.apache.commons.collections4.CollectionUtils
 *  org.apache.commons.lang3.time.StopWatch
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl;

import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.impl.search.v2.lucene.filter.SpacePermissionsFilterDao;
import com.atlassian.confluence.impl.security.access.AccessDenied;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryBuilder;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryManager;
import com.atlassian.confluence.internal.ContentPermissionManagerInternal;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import com.atlassian.confluence.internal.spaces.SpaceManagerInternal;
import com.atlassian.confluence.internal.spaces.persistence.SpaceDaoInternal;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.security.denormalisedpermissions.BulkPermissionService;
import com.atlassian.confluence.security.denormalisedpermissions.impl.RegularEntitiesAndPermissionsHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.analytics.PermittedSpacesAnalyticsEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.analytics.SpaceKeysWithStatusesAnalyticsEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.analytics.VisibleChildPagesAnalyticsEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.analytics.VisiblePagesInSpaceAnalyticsEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.analytics.VisibleTopLevelPagesAnalyticsEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.SimpleContent;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Either;
import com.atlassian.user.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;

public class DenormalisedPermissionFallbackServiceImpl
implements BulkPermissionService {
    private final SpaceManagerInternal spaceManager;
    private final SpaceDaoInternal spaceDaoInternal;
    private final SpacePermissionsFilterDao spacePermissionsFilterDao;
    private final SpacePermissionQueryManager spacePermissionQueryManager;
    private final EventPublisher eventPublisher;
    private final ContentPermissionManagerInternal contentPermissionManager;
    private final PageManagerInternal pageManager;
    private final PermissionManager permissionManager;
    private final PermissionCheckExemptions permissionCheckExemptions;
    private final ConfluenceAccessManager confluenceAccessManager;
    private final SpacePermissionManager spacePermissionManager;
    private final RegularEntitiesAndPermissionsHelper regularEntitiesAndPermissionsHelper;

    public DenormalisedPermissionFallbackServiceImpl(SpaceManagerInternal spaceManager, SpaceDaoInternal spaceDaoInternal, SpacePermissionsFilterDao spacePermissionsFilterDao, SpacePermissionQueryManager spacePermissionQueryManager, EventPublisher eventPublisher, ContentPermissionManagerInternal contentPermissionManager, PageManagerInternal pageManager, PermissionManager permissionManager, PermissionCheckExemptions permissionCheckExemptions, ConfluenceAccessManager confluenceAccessManager, SpacePermissionManager spacePermissionManager, RegularEntitiesAndPermissionsHelper regularEntitiesAndPermissionsHelper) {
        this.spaceManager = spaceManager;
        this.spaceDaoInternal = spaceDaoInternal;
        this.spacePermissionsFilterDao = spacePermissionsFilterDao;
        this.spacePermissionQueryManager = spacePermissionQueryManager;
        this.eventPublisher = eventPublisher;
        this.contentPermissionManager = contentPermissionManager;
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.permissionCheckExemptions = permissionCheckExemptions;
        this.confluenceAccessManager = confluenceAccessManager;
        this.spacePermissionManager = spacePermissionManager;
        this.regularEntitiesAndPermissionsHelper = regularEntitiesAndPermissionsHelper;
    }

    @Override
    public Map<String, Boolean> getAllSpaceKeysWithPermissionStatuses(ConfluenceUser confluenceUser, String spacePermissionType) {
        Map<String, Boolean> resultMap;
        boolean isExempt = this.permissionCheckExemptions.isExempt(confluenceUser);
        StopWatch stopWatch = StopWatch.createStarted();
        List<String> allSpaceKeys = this.spaceDaoInternal.findAllSpaceKeys();
        Either<AccessDenied, SpacePermissionQueryBuilder> permissionQueryBuilderEither = this.spacePermissionQueryManager.createSpacePermissionQueryBuilder(confluenceUser, spacePermissionType);
        int permittedSpacesCount = 0;
        int nonPermittedSpacesCount = 0;
        if (permissionQueryBuilderEither.isLeft()) {
            resultMap = allSpaceKeys.stream().collect(Collectors.toMap(s -> s, s -> false));
            nonPermittedSpacesCount = resultMap.size();
        } else if (this.permissionCheckExemptions.isExempt(confluenceUser)) {
            resultMap = allSpaceKeys.stream().collect(Collectors.toMap(s -> s, s -> true));
            permittedSpacesCount = resultMap.size();
        } else {
            SpacePermissionQueryBuilder queryBuilder = (SpacePermissionQueryBuilder)permissionQueryBuilderEither.right().get();
            resultMap = new HashMap<String, Boolean>(allSpaceKeys.size());
            List<String> permittedSpaces = this.spacePermissionsFilterDao.getPermittedSpaceKeys(queryBuilder);
            permittedSpacesCount = permittedSpaces.size();
            resultMap.putAll(permittedSpaces.stream().collect(Collectors.toMap(s -> s, s -> true)));
            for (String spaceKey : allSpaceKeys) {
                if (resultMap.containsKey(spaceKey)) continue;
                resultMap.put(spaceKey, false);
                ++nonPermittedSpacesCount;
            }
        }
        this.eventPublisher.publish((Object)new SpaceKeysWithStatusesAnalyticsEvent(permittedSpacesCount, nonPermittedSpacesCount, true, false, stopWatch.getTime(), 0L, isExempt, null));
        return resultMap;
    }

    @Override
    public List<Space> getPermittedSpaces(SpacesQuery spaceQuery, int offset, int limit) {
        boolean isExempt = this.permissionCheckExemptions.isExempt(spaceQuery.getUser());
        StopWatch stopWatch = StopWatch.createStarted();
        LimitedRequest request = LimitedRequestImpl.create((int)offset, (int)limit, (int)limit, (boolean)false);
        List spaces = this.spaceManager.getSpaces(spaceQuery, request, x -> true).getResults();
        this.eventPublisher.publish((Object)new PermittedSpacesAnalyticsEvent(spaces.size(), limit, true, false, stopWatch.getTime(), isExempt, null));
        return new ArrayList<Space>(spaces);
    }

    @Override
    public Set<Long> getPermittedSpaceIds(@Nullable ConfluenceUser confluenceUser, Set<Long> spaceIds, String spacePermissionType) {
        if (!this.hasUserAccessToConfluence(confluenceUser)) {
            return Collections.emptySet();
        }
        boolean isExempt = this.permissionCheckExemptions.isExempt(confluenceUser);
        if (isExempt) {
            return spaceIds;
        }
        HashSet<Long> permittedSpaces = new HashSet<Long>();
        for (Long spaceId : spaceIds) {
            Space space = this.spaceManager.getSpace(spaceId);
            if (space == null || !this.spacePermissionManager.hasPermission(spacePermissionType, space, confluenceUser)) continue;
            permittedSpaces.add(space.getId());
        }
        return permittedSpaces;
    }

    @Override
    public Map<Long, List<SimpleContent>> getVisibleChildPages(@Nullable ConfluenceUser confluenceUser, Set<Long> parentPageIdSet, boolean checkInheritedPermissions) {
        HashMap<Long, List<SimpleContent>> results = new HashMap<Long, List<SimpleContent>>();
        boolean isExempt = this.permissionCheckExemptions.isExempt(confluenceUser);
        long childPagesCount = 0L;
        StopWatch stopWatch = StopWatch.createStarted();
        for (Long parentPageId : parentPageIdSet) {
            List<SimpleContent> visibleChildPages = this.getVisibleChildPages(confluenceUser, parentPageId, isExempt, checkInheritedPermissions);
            childPagesCount += (long)visibleChildPages.size();
            results.put(parentPageId, visibleChildPages);
        }
        this.eventPublisher.publish((Object)new VisibleChildPagesAnalyticsEvent(parentPageIdSet.size(), checkInheritedPermissions, childPagesCount, true, stopWatch.getTime(), isExempt));
        return results;
    }

    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="SpotBugs thinks that NPE is possible but it is not the case")
    private List<SimpleContent> getVisibleChildPages(@Nullable ConfluenceUser confluenceUser, Long parentPageId, boolean isExempt, boolean checkInheritedPermissions) {
        Page parentPage = Objects.requireNonNull(this.pageManager.getPage(parentPageId));
        if (isExempt) {
            PageResponse<Page> allChildren = this.pageManager.getChildren(parentPage, LimitedRequestImpl.create((int)0x7FFFFFFE), Depth.ROOT);
            return allChildren.getResults().stream().map(SimpleContent::from).collect(Collectors.toList());
        }
        List<Page> visibleChildren = checkInheritedPermissions ? this.contentPermissionManager.getPermittedChildren(parentPage, confluenceUser) : this.contentPermissionManager.getPermittedChildrenIgnoreInheritedPermissions(parentPage, confluenceUser);
        return visibleChildren.stream().map(SimpleContent::from).collect(Collectors.toList());
    }

    @Override
    public List<SimpleContent> getVisibleTopLevelPages(@Nullable ConfluenceUser confluenceUser, long spaceId) {
        List<SimpleContent> simplePages;
        boolean isExempt = this.permissionCheckExemptions.isExempt(confluenceUser);
        StopWatch stopWatch = StopWatch.createStarted();
        Space space = this.spaceManager.getSpace(spaceId);
        if (space == null || !this.permissionManager.hasPermission((User)confluenceUser, Permission.VIEW, space)) {
            simplePages = new ArrayList<SimpleContent>();
        } else {
            List topLevelPages = this.pageManager.getTopLevelPages(space);
            if (CollectionUtils.isEmpty((Collection)topLevelPages)) {
                simplePages = new ArrayList();
            } else if (isExempt) {
                simplePages = topLevelPages.stream().map(SimpleContent::from).collect(Collectors.toList());
            } else {
                List<Page> visibleTopLevelPages = this.contentPermissionManager.getPermittedPagesIgnoreInheritedPermissions(topLevelPages, confluenceUser, "View");
                simplePages = visibleTopLevelPages.stream().map(SimpleContent::from).collect(Collectors.toList());
            }
        }
        this.eventPublisher.publish((Object)new VisibleTopLevelPagesAnalyticsEvent(simplePages.size(), true, stopWatch.getTime(), isExempt));
        return simplePages;
    }

    @Override
    public List<SimpleContent> getAllVisiblePagesInSpace(@Nullable ConfluenceUser confluenceUser, long spaceId) {
        List<Object> visiblePages;
        boolean isExempt = this.permissionCheckExemptions.isExempt(confluenceUser);
        StopWatch stopWatch = StopWatch.createStarted();
        Space space = this.spaceManager.getSpace(spaceId);
        if (isExempt) {
            PageResponse<Page> allPages = this.pageManager.getFilteredPages(space, LimitedRequestImpl.create((int)0x7FFFFFFE), new Predicate[0]);
            visiblePages = allPages.getResults().stream().map(SimpleContent::from).collect(Collectors.toList());
        } else if (space == null || !this.permissionManager.hasPermission((User)confluenceUser, Permission.VIEW, space)) {
            visiblePages = new ArrayList();
        } else {
            PageResponse<Page> pages = this.pageManager.getFilteredPages(space, LimitedRequestImpl.create((int)0x7FFFFFFE), page -> this.contentPermissionManager.hasContentLevelPermission((User)confluenceUser, "View", (ContentEntityObject)page));
            visiblePages = pages.getResults().stream().map(SimpleContent::from).collect(Collectors.toList());
        }
        this.eventPublisher.publish((Object)new VisiblePagesInSpaceAnalyticsEvent(visiblePages.size(), true, stopWatch.getTime(), isExempt));
        return visiblePages;
    }

    @Override
    public Set<Long> getVisiblePageIds(@Nullable ConfluenceUser confluenceUser, Set<Long> pageIds, boolean checkSpacePermissions) {
        if (this.permissionCheckExemptions.isExempt(confluenceUser)) {
            return pageIds;
        }
        if (checkSpacePermissions) {
            pageIds = this.getPageIdsWithPermittedSpaces(confluenceUser, pageIds);
        }
        Map<Long, ValidationResult> longValidationResultMap = this.contentPermissionManager.hasContentLevelPermission(confluenceUser, "View", pageIds);
        return longValidationResultMap.entrySet().stream().filter(item -> ((ValidationResult)item.getValue()).isAuthorized()).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    private boolean hasUserAccessToConfluence(ConfluenceUser confluenceUser) {
        AccessStatus accessStatus = this.confluenceAccessManager.getUserAccessStatusNoExemptions(confluenceUser);
        return accessStatus.canUseConfluence();
    }

    private Set<Long> getPageIdsWithPermittedSpaces(ConfluenceUser confluenceUser, Set<Long> pageIds) {
        if (!this.hasUserAccessToConfluence(confluenceUser)) {
            return Collections.emptySet();
        }
        List<Page> pages = this.regularEntitiesAndPermissionsHelper.getPagesByIds(pageIds);
        Set uniqueSpaceSet = pages.stream().map(SpaceContentEntityObject::getSpace).collect(Collectors.toSet());
        Set permittedSpaceIds = uniqueSpaceSet.stream().filter(space -> this.spacePermissionManager.hasPermission("VIEWSPACE", (Space)space, confluenceUser)).map(EntityObject::getId).collect(Collectors.toSet());
        if (permittedSpaceIds.isEmpty()) {
            return Collections.emptySet();
        }
        return pages.stream().filter(page -> permittedSpaceIds.contains(page.getSpace().getId())).map(EntityObject::getId).collect(Collectors.toSet());
    }
}

