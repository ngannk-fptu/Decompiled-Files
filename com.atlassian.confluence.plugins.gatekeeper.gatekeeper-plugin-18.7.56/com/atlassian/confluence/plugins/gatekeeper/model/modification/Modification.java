/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.modification;

import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyAnonymous;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyGroup;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyUser;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.PermissionSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Modification {
    private Map<TinyOwner, PermissionSet> permissionMap = new HashMap<TinyOwner, PermissionSet>();
    private Set<String> spaces = new LinkedHashSet<String>();

    public Map<TinyOwner, PermissionSet> getPermissions() {
        return this.permissionMap;
    }

    public void setAnonymousPermissions(int permissions) {
        this.permissionMap.put(TinyAnonymous.ANONYMOUS, new PermissionSet(permissions));
    }

    public void setGroupPermissions(String group, int permissions) {
        this.permissionMap.put(new TinyGroup(group), new PermissionSet(permissions));
    }

    public void setUserPermissions(String username, int permissions) {
        this.permissionMap.put(new TinyUser(username, "", false), new PermissionSet(permissions));
    }

    public Set<String> getSpaces() {
        return this.spaces;
    }

    public void setSpaces(String spaceKeys) {
        String[] keys = spaceKeys.split(",");
        this.spaces.addAll(Arrays.asList(keys));
    }
}

