/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.sal.api.user.UserKey
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  javax.persistence.EntityManager
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator.cache;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.SubCache;
import com.atlassian.confluence.plugins.gatekeeper.model.event.EventType;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyGroupEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinySpaceEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinySpacePermissionEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyUserEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permission;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.PermissionSet;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permissions;
import com.atlassian.confluence.plugins.gatekeeper.model.space.SpacePermissions;
import com.atlassian.confluence.plugins.gatekeeper.util.CopyOnceMap;
import com.atlassian.confluence.plugins.gatekeeper.util.QueryByIdBatcher;
import com.atlassian.sal.api.user.UserKey;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SpacePermissionCache
implements SubCache {
    private static final Logger logger = LoggerFactory.getLogger(SpacePermissionCache.class);
    private static final SpacePermissions EMPTY_SPACE_PERMISSIONS = new SpacePermissions();
    private static final String SPACE_PERMISSIONS_QUERY_INITIAL = "SELECT sp.id, sp.space.key, sp.type, sp.group, u.key, u.lowerName FROM SpacePermission sp LEFT JOIN sp.userSubject u WHERE sp.space.id IS NOT null ORDER BY sp.id";
    private static final String SPACE_PERMISSIONS_QUERY_BATCHED = "SELECT sp.id, sp.space.key, sp.type, sp.group, u.key, u.lowerName FROM SpacePermission sp LEFT JOIN sp.userSubject u WHERE sp.space.id IS NOT null AND sp.id > :id ORDER BY sp.id";
    private Map<String, SpacePermissions> spacePermissionsMap;
    private CopyOnceMap<SpacePermissions> updateMap;

    SpacePermissionCache(SpacePermissionCache spacePermissionCache) {
        this.spacePermissionsMap = spacePermissionCache.spacePermissionsMap;
        this.updateMap = new CopyOnceMap<SpacePermissions>(this.spacePermissionsMap);
    }

    SpacePermissionCache(EntityManager entityManager) {
        List<Object[]> queryResults;
        logger.trace("Reading space permissions. Spaces: all");
        this.spacePermissionsMap = new Object2ObjectOpenHashMap();
        int counter = 0;
        QueryByIdBatcher queryByIdBatcher = new QueryByIdBatcher(entityManager, SPACE_PERMISSIONS_QUERY_INITIAL, SPACE_PERMISSIONS_QUERY_BATCHED);
        while (!(queryResults = queryByIdBatcher.getBatch()).isEmpty()) {
            for (Object[] row : queryResults) {
                Permission permission;
                String spaceKey = ((String)row[1]).intern();
                String permissionType = (String)row[2];
                String groupName = (String)row[3];
                UserKey userKey = (UserKey)row[4];
                String username = (String)row[5];
                SpacePermissions spacePermissions = this.spacePermissionsMap.get(spaceKey);
                if (spacePermissions == null) {
                    spacePermissions = new SpacePermissions();
                    this.spacePermissionsMap.put(spaceKey, spacePermissions);
                    logger.trace("Reading space permissions: creating space permission cache for space {}", (Object)spaceKey);
                }
                if ((permission = Permissions.getPermissionByType(permissionType)) != null) {
                    SpacePermissionCache.setPermission(spaceKey, groupName, userKey, username, permission, spacePermissions);
                    ++counter;
                    continue;
                }
                logger.trace("Reading space permissions: found unknown permission type [{}] in space {}", (Object)permissionType, (Object)spaceKey);
            }
            logger.trace("Finished reading one batch - Reading space permissions: finished processing {} space permissions", (Object)counter);
            if (queryResults.size() >= queryByIdBatcher.getBatchSize()) continue;
        }
        this.updateMap = new CopyOnceMap<SpacePermissions>(this.spacePermissionsMap);
        logger.trace("Reading space permissions: finished processing {} space permissions", (Object)counter);
    }

    @VisibleForTesting
    static void setPermission(String spaceKey, String groupName, UserKey userKey, String username, Permission permission, SpacePermissions spacePermissions) {
        if (StringUtils.isNotEmpty((CharSequence)groupName)) {
            spacePermissions.setGroupPermission(groupName, permission);
            logger.trace("Reading space permissions: processing space permission for group {} in space {}", (Object)groupName, (Object)spaceKey);
        } else if (userKey != null) {
            SpacePermissionCache.setUserPermission(userKey, username, permission, spacePermissions);
        } else {
            spacePermissions.setAnonymousPermission(permission);
            logger.trace("Reading space permissions: processing space permission for anonymous in space {}", (Object)spaceKey);
        }
    }

    @VisibleForTesting
    static void setUserPermission(UserKey userKey, String username, Permission permission, SpacePermissions spacePermissions) {
        if (StringUtils.isEmpty((CharSequence)username)) {
            logger.debug("no username found for {}, skipping invalid permission", (Object)userKey);
            return;
        }
        spacePermissions.setUserPermission(username, permission);
        logger.trace("Reading space permissions: processing space permission for user {}", (Object)userKey);
    }

    @Override
    public void update(TinyEvent event) {
        EventType eventType = event.getEventType();
        switch (eventType) {
            case SPACE_PERMISSION_ADDED: {
                this.setPermissions(((TinySpacePermissionEvent)event).getPermissions());
                break;
            }
            case SPACE_PERMISSION_DELETED: {
                this.unsetPermissions(((TinySpacePermissionEvent)event).getPermissions());
                break;
            }
            case SPACE_DELETED: {
                this.updateMap.remove(((TinySpaceEvent)event).getKey());
                break;
            }
            case SPACE_ADDED: {
                this.updateMap.put(((TinySpaceEvent)event).getKey(), new SpacePermissions());
                break;
            }
            case GROUP_DELETED: {
                this.removeGroup(((TinyGroupEvent)event).getGroupName());
                break;
            }
            case USER_DELETED: {
                this.removeUser(((TinyUserEvent)event).getUsername());
                break;
            }
            case USER_RENAMED: {
                this.renameUser(((TinyUserEvent)event).getOldUsername(), ((TinyUserEvent)event).getUsername());
            }
        }
    }

    private void setPermissions(Map<String, List<String>> permissionMap) {
        for (Map.Entry<String, List<String>> entry : permissionMap.entrySet()) {
            String spaceKey = entry.getKey();
            SpacePermissions spacePermissions = this.updateMap.getOrCopy(spaceKey);
            if (spacePermissions != null) {
                List<String> permissions = entry.getValue();
                for (String permission : permissions) {
                    PermissionSet permissionSet = this.extractPermissionSet(permission);
                    if (permission.startsWith("g")) {
                        spacePermissions.setGroupPermissions(this.extractName(permission), permissionSet);
                    }
                    if (permission.startsWith("u")) {
                        spacePermissions.setUserPermissions(this.extractName(permission), permissionSet);
                    }
                    if (!permission.startsWith("a")) continue;
                    spacePermissions.setAnonymousPermissions(permissionSet);
                }
                continue;
            }
            logger.debug("Can't set permissions cache, space '{}' is missing from cache!", (Object)spaceKey);
        }
    }

    private void unsetPermissions(Map<String, List<String>> permissionMap) {
        for (Map.Entry<String, List<String>> entry : permissionMap.entrySet()) {
            String spaceKey = entry.getKey();
            SpacePermissions spacePermissions = this.updateMap.getOrCopy(spaceKey);
            if (spacePermissions != null) {
                List<String> permissions = entry.getValue();
                for (String permission : permissions) {
                    PermissionSet permissionSet = this.extractPermissionSet(permission);
                    if (permission.startsWith("g")) {
                        spacePermissions.unsetGroupPermissions(this.extractName(permission), permissionSet);
                    }
                    if (permission.startsWith("u")) {
                        spacePermissions.unsetUserPermissions(this.extractName(permission), permissionSet);
                    }
                    if (!permission.startsWith("a")) continue;
                    spacePermissions.unsetAnonymousPermissions(permissionSet);
                }
                continue;
            }
            logger.debug("Can't unset permissions cache, space '{}' is missing from cache!", (Object)spaceKey);
        }
    }

    private PermissionSet extractPermissionSet(String permission) {
        int value = 0;
        try {
            int p = permission.indexOf(58, 2);
            String s = p < 0 ? permission.substring(2) : permission.substring(2, p);
            value = Integer.parseInt(s);
        }
        catch (Exception e) {
            logger.error("Can't convert permission string [{}]", (Object)permission);
            logger.debug("", (Throwable)e);
        }
        return new PermissionSet(value);
    }

    private String extractName(String permission) {
        int p = permission.indexOf(58, 2);
        return permission.substring(p + 1);
    }

    private void renameUser(String oldUsername, String newUsername) {
        Set<Map.Entry<String, SpacePermissions>> entries = this.updateMap.getUnderlyingMap().entrySet();
        for (Map.Entry<String, SpacePermissions> entry : entries) {
            String spaceKey = entry.getKey();
            SpacePermissions spacePermissions = entry.getValue();
            if (!spacePermissions.containsUser(oldUsername)) continue;
            this.updateMap.getOrCopy(spaceKey).renameUser(oldUsername, newUsername);
        }
    }

    private void removeUser(String username) {
        Set<Map.Entry<String, SpacePermissions>> entries = this.updateMap.getUnderlyingMap().entrySet();
        for (Map.Entry<String, SpacePermissions> entry : entries) {
            String spaceKey = entry.getKey();
            SpacePermissions spacePermissions = entry.getValue();
            if (!spacePermissions.containsUser(username)) continue;
            this.updateMap.getOrCopy(spaceKey).removeUser(username);
        }
    }

    private void removeGroup(String groupName) {
        Set<Map.Entry<String, SpacePermissions>> entries = this.updateMap.getUnderlyingMap().entrySet();
        for (Map.Entry<String, SpacePermissions> entry : entries) {
            String spaceKey = entry.getKey();
            SpacePermissions spacePermissions = entry.getValue();
            if (!spacePermissions.containsGroup(groupName)) continue;
            this.updateMap.getOrCopy(spaceKey).removeGroup(groupName);
        }
    }

    public void finish() {
        if (this.updateMap.isModified()) {
            this.spacePermissionsMap = this.updateMap.getUnderlyingMap();
        }
    }

    public SpacePermissions get(String spaceKey) {
        SpacePermissions result = this.spacePermissionsMap.get(spaceKey);
        return result != null ? result : EMPTY_SPACE_PERMISSIONS;
    }
}

