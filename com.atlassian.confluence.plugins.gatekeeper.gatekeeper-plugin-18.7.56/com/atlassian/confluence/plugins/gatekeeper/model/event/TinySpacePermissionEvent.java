/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.plugins.gatekeeper.model.event;

import com.atlassian.confluence.plugins.gatekeeper.model.event.EventType;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permission;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.PermissionSet;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permissions;
import com.atlassian.confluence.plugins.gatekeeper.model.space.SpacePermissions;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.spaces.Space;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TinySpacePermissionEvent
extends TinyEvent {
    private static final long serialVersionUID = 5139622357480692827L;
    private Map<String, List<String>> permissions;

    public TinySpacePermissionEvent(EventType eventType) {
        super(eventType);
    }

    public static TinyEvent added(Iterable<SpacePermission> permissions) {
        TinySpacePermissionEvent e = new TinySpacePermissionEvent(EventType.SPACE_PERMISSION_ADDED);
        e.permissions = e.convertSpacePermissions(permissions);
        return e;
    }

    public static TinyEvent deleted(String spaceKey, Iterable<SpacePermission> permissions) {
        TinySpacePermissionEvent e = new TinySpacePermissionEvent(EventType.SPACE_PERMISSION_DELETED);
        e.permissions = e.convertSpacePermissions(spaceKey, permissions);
        return e;
    }

    public Map<String, List<String>> getPermissions() {
        return this.permissions;
    }

    private Map<String, List<String>> convertSpacePermissions(Iterable<SpacePermission> spacePermissionCollection) {
        return this.convertSpacePermissions(null, spacePermissionCollection);
    }

    private Map<String, List<String>> convertSpacePermissions(String forcedSpaceKey, Iterable<SpacePermission> spacePermissionCollection) {
        HashMap<String, SpacePermissions> result = new HashMap<String, SpacePermissions>();
        for (SpacePermission sp : spacePermissionCollection) {
            Permission permission;
            SpacePermissions spacePermissions;
            Space space = sp.getSpace();
            String spaceKey = forcedSpaceKey;
            if (space != null) {
                spaceKey = space.getKey();
            }
            if ((spacePermissions = (SpacePermissions)result.get(spaceKey)) == null) {
                spacePermissions = new SpacePermissions();
                result.put(spaceKey, spacePermissions);
            }
            if ((permission = Permissions.getPermissionByType(sp.getType())) == null) continue;
            if (sp.isGroupPermission()) {
                spacePermissions.setGroupPermission(sp.getGroup(), permission);
                continue;
            }
            if (sp.isUserPermission()) {
                spacePermissions.setUserPermission(sp.getUserSubject().getName().toLowerCase(), permission);
                continue;
            }
            if (!sp.isAnonymousPermission()) continue;
            spacePermissions.setAnonymousPermission(permission);
        }
        return this.convertToInternalFormat(result);
    }

    private Map<String, List<String>> convertToInternalFormat(Map<String, SpacePermissions> spacePermissionsMap) {
        HashMap<String, List<String>> result = new HashMap<String, List<String>>();
        for (Map.Entry<String, SpacePermissions> entry : spacePermissionsMap.entrySet()) {
            PermissionSet anonymousPermissions;
            String spaceKey = entry.getKey();
            SpacePermissions spacePermissions = entry.getValue();
            ArrayList<CallSite> internalPermissionList = (ArrayList<CallSite>)result.get(spaceKey);
            if (internalPermissionList == null) {
                internalPermissionList = new ArrayList<CallSite>();
                result.put(spaceKey, internalPermissionList);
            }
            if (!(anonymousPermissions = spacePermissions.getAnonymousPermissions()).isEmpty()) {
                internalPermissionList.add((CallSite)((Object)("a:" + anonymousPermissions.toTransferFormat())));
            }
            Map<String, PermissionSet> userPermissions = spacePermissions.getUserPermissionMap();
            for (Map.Entry<String, PermissionSet> userEntry : userPermissions.entrySet()) {
                String username = userEntry.getKey();
                PermissionSet permissionSet = userEntry.getValue();
                internalPermissionList.add((CallSite)((Object)("u:" + permissionSet.toTransferFormat() + ":" + username)));
            }
            Map<String, PermissionSet> groupPermissions = spacePermissions.getGroupPermissionMap();
            for (Map.Entry<String, PermissionSet> groupEntry : groupPermissions.entrySet()) {
                String groupName = groupEntry.getKey();
                PermissionSet permissionSet = groupEntry.getValue();
                internalPermissionList.add((CallSite)((Object)("g:" + permissionSet.toTransferFormat() + ":" + groupName)));
            }
        }
        return result;
    }

    public String toString() {
        return this.eventType + "{permissions=" + this.permissions + "}";
    }
}

