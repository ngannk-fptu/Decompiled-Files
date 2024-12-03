/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.security.ThreadLocalPermissionsCacheInternal
 *  com.atlassian.confluence.pages.ChildPositionComparator
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.security.denormalisedpermissions.AdvancedBulkPermissionService
 *  com.atlassian.confluence.security.denormalisedpermissions.BulkPermissionService
 *  com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.SimpleContent
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.internal.security.ThreadLocalPermissionsCacheInternal;
import com.atlassian.confluence.pages.ChildPositionComparator;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.macros.advanced.xhtml.ChildrenMacro;
import com.atlassian.confluence.security.denormalisedpermissions.AdvancedBulkPermissionService;
import com.atlassian.confluence.security.denormalisedpermissions.BulkPermissionService;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.SimpleContent;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.user.User;
import java.sql.Date;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BulkPermissionHelper {
    private static final String FORCE_USE_BULK_PERMISSIONS_GET_PARAMETER_NAME = "force-use-bulk-permissions";
    private static final Comparator<Page> CHILD_PAGE_COMPARATOR = new ChildPositionComparator();
    private static final Logger log = LoggerFactory.getLogger(ChildrenMacro.class);
    public static final int UNLIMITED_DEPTH = 0;
    private static final String BULK_PERMISSIONS_ENABLED_DARK_FEATURE_NAME = "confluence.denormalisedpermissions";
    private final DarkFeatureManager darkFeatureManager;
    private final AdvancedBulkPermissionService advancedBulkPermissionService;
    private BulkPermissionService bulkPermissionService;

    public BulkPermissionHelper(DarkFeatureManager darkFeatureManager, AdvancedBulkPermissionService advancedBulkPermissionService, BulkPermissionService bulkPermissionService) {
        this.darkFeatureManager = darkFeatureManager;
        this.advancedBulkPermissionService = advancedBulkPermissionService;
        this.bulkPermissionService = bulkPermissionService;
    }

    public boolean shouldCallBulkPermissionsAPI() {
        return this.getForceUseVariableState().orElse(this.isDarkFeatureEnabled() && this.isBulkPermissionsUpAndRunning());
    }

    public boolean shouldPrintDebugInformation() {
        HttpServletRequest request = ServletContextThreadLocal.getRequest();
        return request != null && StringUtils.isNotEmpty((CharSequence)request.getParameter(FORCE_USE_BULK_PERMISSIONS_GET_PARAMETER_NAME));
    }

    public boolean isDarkFeatureEnabled() {
        return this.darkFeatureManager != null && this.darkFeatureManager.isEnabledForAllUsers(BULK_PERMISSIONS_ENABLED_DARK_FEATURE_NAME).orElse(false) != false;
    }

    public boolean isBulkPermissionsUpAndRunning() {
        return this.advancedBulkPermissionService != null && this.advancedBulkPermissionService.isApiUpAndRunning();
    }

    public static List<Page> fromSimpleContentList(List<SimpleContent> simpleContent, Space space) {
        return simpleContent.stream().map(s -> BulkPermissionHelper.fromSimpleContent(s, space)).sorted(CHILD_PAGE_COMPARATOR).collect(Collectors.toList());
    }

    public static Page fromSimpleContent(SimpleContent simpleContent) {
        Page page = new Page();
        page.setId(simpleContent.getId());
        page.setTitle(simpleContent.getTitle());
        page.setCreationDate(Date.from(simpleContent.getCreationDate()));
        page.setLastModificationDate(Date.from(simpleContent.getLastModificationDate()));
        page.setContentStatus(simpleContent.getStatus().name());
        page.setPosition(simpleContent.getPosition());
        return page;
    }

    public static boolean isPermissionExempt(UserAccessor userAccessor, CrowdService crowdService, User user) {
        if (user == null) {
            return false;
        }
        return BulkPermissionHelper.isExemptViaAdminGroupMembership(userAccessor, crowdService, user);
    }

    public Map<Long, List<Page>> getAllDescendants(Space space, Long rootPageId, int depth) {
        StopWatch watch = StopWatch.createStarted();
        HashMap<Long, List<Page>> pageToChildrenMap = new HashMap<Long, List<Page>>();
        int level = 0;
        boolean noDepthLimit = depth == 0;
        long spaceId = space.getId();
        log.debug("Got a request with space id = {} and root page id = {}", (Object)spaceId, (Object)rootPageId);
        Set<Long> parentPageIdSetToProcess = Collections.singleton(rootPageId);
        if (rootPageId == null) {
            List topLevelPages = this.bulkPermissionService.getVisibleTopLevelPages(AuthenticatedUserThreadLocal.get(), spaceId).stream().map(BulkPermissionHelper::fromSimpleContent).sorted(CHILD_PAGE_COMPARATOR).collect(Collectors.toList());
            log.trace("Got {} top level pages for space id = {}", (Object)topLevelPages.size(), (Object)spaceId);
            pageToChildrenMap.put(null, topLevelPages);
            parentPageIdSetToProcess = topLevelPages.stream().map(EntityObject::getId).collect(Collectors.toSet());
        }
        while (noDepthLimit || level < depth) {
            Map<Long, List> allChildrenOnTheCurrentLevel = this.bulkPermissionService.getVisibleChildPages(AuthenticatedUserThreadLocal.get(), parentPageIdSetToProcess, false).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> BulkPermissionHelper.fromSimpleContentList((List)e.getValue(), space)));
            if (allChildrenOnTheCurrentLevel.size() == 0) {
                log.debug("There are no pages anymore, retrieved {} pages with children in {} ms. Current level is {}", new Object[]{pageToChildrenMap.size(), watch.getTime(), level});
                return pageToChildrenMap;
            }
            pageToChildrenMap.putAll(allChildrenOnTheCurrentLevel);
            parentPageIdSetToProcess = allChildrenOnTheCurrentLevel.values().stream().flatMap(Collection::stream).map(EntityObject::getId).collect(Collectors.toSet());
            log.trace("Got {} pages with {} children on level {}", new Object[]{allChildrenOnTheCurrentLevel.size(), parentPageIdSetToProcess.size(), ++level});
        }
        log.debug("Depth limit of {} has been reached, retrieved {} pages with children in {} ms.", new Object[]{depth, pageToChildrenMap.size(), watch.getTime()});
        return pageToChildrenMap;
    }

    private static Page fromSimpleContent(SimpleContent simpleContent, Space space) {
        Page page = BulkPermissionHelper.fromSimpleContent(simpleContent);
        page.setSpace(space);
        return page;
    }

    private static boolean isExemptViaAdminGroupMembership(UserAccessor userAccessor, CrowdService crowdService, User user) {
        Boolean isExempt = ThreadLocalPermissionsCacheInternal.hasPermissionExemption((User)user);
        if (isExempt == null) {
            isExempt = !userAccessor.isDeactivated(user) && BulkPermissionHelper.isMemberOfAdministratorsGroup(crowdService, user);
            ThreadLocalPermissionsCacheInternal.cachePermissionExemption((User)user, (boolean)isExempt);
        }
        return isExempt;
    }

    private static boolean isMemberOfAdministratorsGroup(CrowdService crowdService, User user) {
        return crowdService.isUserMemberOfGroup(user.getName(), "confluence-administrators");
    }

    private Optional<Boolean> getForceUseVariableState() {
        return this.getForceUseVariableValue().filter(s -> "true".equalsIgnoreCase((String)s) || "false".equalsIgnoreCase((String)s)).map(Boolean::valueOf);
    }

    private Optional<String> getForceUseVariableValue() {
        return Optional.ofNullable(ServletContextThreadLocal.getRequest()).map(r -> r.getParameter(FORCE_USE_BULK_PERMISSIONS_GET_PARAMETER_NAME)).map(String::toLowerCase);
    }
}

