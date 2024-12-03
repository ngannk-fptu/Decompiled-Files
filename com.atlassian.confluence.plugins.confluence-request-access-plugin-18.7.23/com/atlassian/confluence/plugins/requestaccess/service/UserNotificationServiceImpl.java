/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.VersionHistorySummary
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.requestaccess.service;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.requestaccess.service.UserNotificationService;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserNotificationServiceImpl
implements UserNotificationService {
    private static final Logger log = LoggerFactory.getLogger(UserNotificationServiceImpl.class);
    private static final int RECIPIENTS_LIMIT = 5;
    private static final String SPACE_ADMINS_REQUEST_LIMIT_OVERRIDE = "confluence.request.access.space.admin.limit";
    private static final int SPACE_ADMINS_REQUEST_LIMIT = 100;
    private static final int spaceAdminLimit = Integer.getInteger("confluence.request.access.space.admin.limit", 100);
    private static final SecureRandom random = new SecureRandom();
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final UserAccessor userAccessor;
    private final PageManager pageManager;

    @Autowired
    public UserNotificationServiceImpl(@ComponentImport PermissionManager permissionManager, @ComponentImport SpaceManager spaceManager, @ComponentImport UserAccessor userAccessor, @ComponentImport PageManager pageManager) {
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
        this.userAccessor = userAccessor;
        this.pageManager = pageManager;
    }

    @Override
    public LinkedHashSet<ConfluenceUser> findRequestAccessRecipient(AbstractPage page) {
        List historyLatestFirst = this.pageManager.getVersionHistorySummaries((ContentEntityObject)page).stream().sorted(Comparator.comparing(VersionHistorySummary::getLastModificationDate).reversed()).collect(Collectors.toList());
        LinkedHashSet<ConfluenceUser> contributorsWithAccess = new LinkedHashSet<ConfluenceUser>();
        for (VersionHistorySummary summary : historyLatestFirst) {
            summary.getContributorSet().stream().filter(u -> !contributorsWithAccess.contains(u) && this.hasPermissionsToGrantAccess((User)u, page)).limit(5 - contributorsWithAccess.size()).forEach(contributorsWithAccess::add);
        }
        if (contributorsWithAccess.size() == 5) {
            return contributorsWithAccess;
        }
        Set contributorNames = contributorsWithAccess.stream().map(Principal::getName).collect(Collectors.toSet());
        LinkedHashSet spaceAdminsWithAccess = this.spaceManager.getSpaceAdmins(page.getSpace(), spaceAdminLimit).stream().map(Principal::getName).filter(adminName -> !contributorNames.contains(adminName)).map(arg_0 -> ((UserAccessor)this.userAccessor).getUserByName(arg_0)).filter(u -> this.hasPermissionsToGrantAccess((User)u, page)).collect(Collectors.toCollection(LinkedHashSet::new));
        if (spaceAdminsWithAccess.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Unable to find a space admin that isn't already a contributor to page {}", (Object)page.getIdAsString());
            }
            return contributorsWithAccess;
        }
        ArrayList adminsList = new ArrayList(spaceAdminsWithAccess);
        if (log.isDebugEnabled()) {
            log.debug("Nr of Space Admins with Access: {}", (Object)spaceAdminsWithAccess.size());
        }
        for (int tries = spaceAdminLimit; contributorsWithAccess.size() < 5 && tries > 0; --tries) {
            ConfluenceUser confluenceUser = (ConfluenceUser)adminsList.get(random.nextInt(adminsList.size()));
            if (contributorNames.contains(confluenceUser.getName())) continue;
            contributorsWithAccess.add(confluenceUser);
            contributorNames.add(confluenceUser.getName());
        }
        return contributorsWithAccess;
    }

    private boolean hasPermissionsToGrantAccess(User user, AbstractPage page) {
        return this.permissionManager.hasPermission(user, Permission.VIEW, (Object)page) && this.permissionManager.hasPermission(user, Permission.SET_PERMISSIONS, (Object)page);
    }
}

