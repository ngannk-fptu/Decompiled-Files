/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.sal.api.user.UserKey
 *  javax.persistence.EntityManager
 *  javax.persistence.Query
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator.cache;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.SubCache;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyGlobalPermissionEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyGroupEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyUserEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.global.GlobalPermissions;
import com.atlassian.sal.api.user.UserKey;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GlobalPermissionCache
implements SubCache {
    private static final Logger logger = LoggerFactory.getLogger(GlobalPermissionCache.class);
    private static final String GLOBAL_PERMISSIONS_QUERY = "SELECT sp.group, u.key, u.lowerName FROM SpacePermission sp LEFT JOIN sp.userSubject u WHERE sp.space.id IS null AND sp.type = 'USECONFLUENCE'";
    private GlobalPermissions globalPermissions;
    private GlobalPermissions newGlobalPermissions;

    GlobalPermissionCache(GlobalPermissionCache globalPermissionCache) {
        this.globalPermissions = globalPermissionCache.globalPermissions;
    }

    GlobalPermissionCache(EntityManager entityManager) {
        logger.debug("Reading global permissions. Globals: all");
        this.globalPermissions = new GlobalPermissions();
        Query query = entityManager.createQuery(GLOBAL_PERMISSIONS_QUERY);
        List queryResults = query.getResultList();
        int counter = 0;
        for (Object[] row : queryResults) {
            String groupName = (String)row[0];
            UserKey userKey = (UserKey)row[1];
            String username = (String)row[2];
            GlobalPermissionCache.setPermission(groupName, userKey, username, this.globalPermissions);
            logger.trace("Reading global permissions: Current count = {}", (Object)(++counter));
        }
        logger.trace("Reading global permissions completed. Total processed = {}", (Object)counter);
    }

    @VisibleForTesting
    static void setPermission(String groupName, UserKey userKey, String username, GlobalPermissions globalPermissions) {
        if (StringUtils.isNotEmpty((CharSequence)groupName)) {
            globalPermissions.setGroupCanUse(groupName);
            logger.trace("Reading global permissions: processing group {}", (Object)groupName);
        } else if (userKey != null) {
            if (StringUtils.isNotEmpty((CharSequence)username)) {
                globalPermissions.setUserCanUse(username);
                logger.trace("Reading global permissions: processing user {}", (Object)username);
            } else {
                logger.debug("Ignoring global permissions for unknown user {}", (Object)userKey);
            }
        } else {
            globalPermissions.setAnonymousCanUse();
            logger.trace("Reading global permissions: processing anonymous permission", (Object)username);
        }
    }

    @Override
    public void update(TinyEvent event) {
        if (this.newGlobalPermissions == null) {
            this.newGlobalPermissions = this.globalPermissions.copy();
        }
        switch (event.getEventType()) {
            case GLOBAL_PERMISSION_ADDED: {
                this.setCanUse(((TinyGlobalPermissionEvent)event).getPermission());
                break;
            }
            case GLOBAL_PERMISSION_DELETED: {
                this.unsetCanUse(((TinyGlobalPermissionEvent)event).getPermission());
                break;
            }
            case GROUP_DELETED: {
                this.newGlobalPermissions.unsetGroupCanUse(((TinyGroupEvent)event).getGroupName());
                break;
            }
            case USER_DELETED: {
                this.newGlobalPermissions.unsetUserCanUse(((TinyUserEvent)event).getUsername());
                break;
            }
            case USER_RENAMED: {
                this.newGlobalPermissions.renameUser(((TinyUserEvent)event).getOldUsername(), ((TinyUserEvent)event).getUsername());
            }
        }
    }

    private void setCanUse(String permission) {
        if (permission.startsWith("g")) {
            this.newGlobalPermissions.setGroupCanUse(permission.substring(2));
        } else if (permission.startsWith("u")) {
            this.newGlobalPermissions.setUserCanUse(permission.substring(2));
        } else if (permission.startsWith("a")) {
            this.newGlobalPermissions.setAnonymousCanUse();
        }
    }

    private void unsetCanUse(String permission) {
        if (permission.startsWith("g")) {
            this.newGlobalPermissions.unsetGroupCanUse(permission.substring(2));
        } else if (permission.startsWith("u")) {
            this.newGlobalPermissions.unsetUserCanUse(permission.substring(2));
        } else if (permission.startsWith("a")) {
            this.newGlobalPermissions.unsetAnonymousCanUse();
        }
    }

    public void finish() {
        if (this.newGlobalPermissions != null) {
            this.globalPermissions = this.newGlobalPermissions;
            this.newGlobalPermissions = null;
        }
    }

    public Set<String> getCanUseGroups() {
        return this.globalPermissions.getGroupsCanUse();
    }

    public boolean hasUserCanUse(String username) {
        return this.globalPermissions.getUsersCanUse().contains(username);
    }

    public boolean hasGroupCanUse(String groupName) {
        return this.globalPermissions.getGroupsCanUse().contains(groupName);
    }

    public boolean isGlobalAnonymousAccessEnabled() {
        return this.globalPermissions.getAnonymousCanUse();
    }
}

