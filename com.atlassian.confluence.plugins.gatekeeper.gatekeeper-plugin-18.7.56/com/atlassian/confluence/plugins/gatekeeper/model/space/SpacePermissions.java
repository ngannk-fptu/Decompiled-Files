/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 */
package com.atlassian.confluence.plugins.gatekeeper.model.space;

import com.atlassian.confluence.plugins.gatekeeper.model.Copiable;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permission;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.PermissionSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Map;

public class SpacePermissions
implements Copiable<SpacePermissions> {
    private PermissionSet anonymousPermissions;
    private Map<String, PermissionSet> groupPermissionMap;
    private Map<String, PermissionSet> userPermissionMap;

    public PermissionSet getAnonymousPermissions() {
        return this.anonymousPermissions != null ? this.anonymousPermissions : PermissionSet.EMPTY_PERMISSION_SET;
    }

    public void setAnonymousPermissions(PermissionSet permissions) {
        if (this.anonymousPermissions == null) {
            this.anonymousPermissions = new PermissionSet(0);
        }
        this.anonymousPermissions.setPermissions(permissions);
    }

    public void setAnonymousPermission(Permission permission) {
        if (this.anonymousPermissions == null) {
            this.anonymousPermissions = new PermissionSet(0);
        }
        this.anonymousPermissions.setPermission(permission);
    }

    public void unsetAnonymousPermission(Permission permission) {
        if (this.anonymousPermissions != null) {
            this.anonymousPermissions.unsetPermission(permission);
            if (this.anonymousPermissions.isEmpty()) {
                this.anonymousPermissions = null;
            }
        }
    }

    public void unsetAnonymousPermissions(PermissionSet permissions) {
        if (this.anonymousPermissions != null) {
            this.anonymousPermissions.unsetPermissions(permissions);
            if (this.anonymousPermissions.isEmpty()) {
                this.anonymousPermissions = null;
            }
        }
    }

    public Map<String, PermissionSet> getGroupPermissionMap() {
        return this.groupPermissionMap != null ? this.groupPermissionMap : Collections.emptyMap();
    }

    public boolean containsGroup(String groupName) {
        return this.groupPermissionMap != null && this.groupPermissionMap.containsKey(groupName);
    }

    public void setGroupPermission(String groupName, Permission permission) {
        PermissionSet groupPermissions;
        groupName = groupName.intern();
        if (this.groupPermissionMap == null) {
            this.groupPermissionMap = new Object2ObjectOpenHashMap(1);
        }
        if ((groupPermissions = this.groupPermissionMap.get(groupName)) == null) {
            groupPermissions = new PermissionSet(0);
            this.groupPermissionMap.put(groupName, groupPermissions);
        }
        groupPermissions.setPermission(permission);
    }

    public void setGroupPermissions(String groupName, PermissionSet permissionSet) {
        PermissionSet groupPermissions;
        groupName = groupName.intern();
        if (this.groupPermissionMap == null) {
            this.groupPermissionMap = new Object2ObjectOpenHashMap(1);
        }
        if ((groupPermissions = this.groupPermissionMap.get(groupName)) == null) {
            groupPermissions = new PermissionSet(0);
            this.groupPermissionMap.put(groupName, groupPermissions);
        }
        groupPermissions.setPermissions(permissionSet);
    }

    public void unsetGroupPermission(String groupName, Permission permission) {
        PermissionSet groupPermissions;
        if (this.groupPermissionMap != null && (groupPermissions = this.groupPermissionMap.get(groupName)) != null) {
            groupPermissions.unsetPermission(permission);
            if (groupPermissions.isEmpty()) {
                this.groupPermissionMap.remove(groupName);
            }
        }
    }

    public void unsetGroupPermissions(String groupName, PermissionSet permissionSet) {
        PermissionSet groupPermissions;
        if (this.groupPermissionMap != null && (groupPermissions = this.groupPermissionMap.get(groupName)) != null) {
            groupPermissions.unsetPermissions(permissionSet);
            if (groupPermissions.isEmpty()) {
                this.groupPermissionMap.remove(groupName);
            }
        }
    }

    public Map<String, PermissionSet> getUserPermissionMap() {
        return this.userPermissionMap != null ? this.userPermissionMap : Collections.emptyMap();
    }

    public boolean containsUser(String username) {
        return this.userPermissionMap != null && this.userPermissionMap.containsKey(username);
    }

    public void setUserPermission(String username, Permission permission) {
        PermissionSet userPermissions;
        username = username.intern();
        if (this.userPermissionMap == null) {
            this.userPermissionMap = new Object2ObjectOpenHashMap(1);
        }
        if ((userPermissions = this.userPermissionMap.get(username)) == null) {
            userPermissions = new PermissionSet(0);
            this.userPermissionMap.put(username, userPermissions);
        }
        userPermissions.setPermission(permission);
    }

    public void setUserPermissions(String username, PermissionSet permissionSet) {
        PermissionSet userPermissions;
        username = username.intern();
        if (this.userPermissionMap == null) {
            this.userPermissionMap = new Object2ObjectOpenHashMap(1);
        }
        if ((userPermissions = this.userPermissionMap.get(username)) == null) {
            userPermissions = new PermissionSet(0);
            this.userPermissionMap.put(username, userPermissions);
        }
        userPermissions.setPermissions(permissionSet);
    }

    public void unsetUserPermission(String username, Permission permission) {
        PermissionSet userPermissions;
        if (this.userPermissionMap != null && (userPermissions = this.userPermissionMap.get(username)) != null) {
            userPermissions.unsetPermission(permission);
            if (userPermissions.isEmpty()) {
                this.userPermissionMap.remove(username);
            }
        }
    }

    public void unsetUserPermissions(String username, PermissionSet permissionSet) {
        PermissionSet userPermissions;
        if (this.userPermissionMap != null && (userPermissions = this.userPermissionMap.get(username)) != null) {
            userPermissions.unsetPermissions(permissionSet);
            if (userPermissions.isEmpty()) {
                this.userPermissionMap.remove(username);
            }
        }
    }

    public void renameUser(String oldUsername, String newUsername) {
        PermissionSet userPermissions;
        newUsername = newUsername.intern();
        if (this.userPermissionMap != null && (userPermissions = this.userPermissionMap.remove(oldUsername)) != null) {
            this.userPermissionMap.put(newUsername, userPermissions);
        }
    }

    @Override
    public SpacePermissions copy() {
        SpacePermissions result = new SpacePermissions();
        if (this.anonymousPermissions != null) {
            result.anonymousPermissions = new PermissionSet(this.anonymousPermissions);
        }
        if (this.groupPermissionMap != null) {
            result.groupPermissionMap = new Object2ObjectOpenHashMap(this.groupPermissionMap.size());
            for (Map.Entry<String, PermissionSet> entry : this.groupPermissionMap.entrySet()) {
                result.groupPermissionMap.put(entry.getKey(), new PermissionSet(entry.getValue()));
            }
        }
        if (this.userPermissionMap != null) {
            result.userPermissionMap = new Object2ObjectOpenHashMap(this.userPermissionMap.size());
            for (Map.Entry<String, PermissionSet> entry : this.userPermissionMap.entrySet()) {
                result.userPermissionMap.put(entry.getKey(), new PermissionSet(entry.getValue()));
            }
        }
        return result;
    }

    public void removeUser(String username) {
        if (this.userPermissionMap != null) {
            this.userPermissionMap.remove(username);
        }
    }

    public void removeGroup(String groupName) {
        if (this.groupPermissionMap != null) {
            this.groupPermissionMap.remove(groupName);
        }
    }
}

