/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermission
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.rpc.soap.beans.RemoteContentPermission;
import com.atlassian.confluence.security.SpacePermission;
import java.util.Collection;

public class RemoteSpacePermissionSet {
    private String type;
    private RemoteContentPermission[] contentPermissions;
    public static final String __PARANAMER_DATA = "<init> java.lang.String type \n<init> java.lang.String,java.util.Collection type,permissionSet \n";

    public RemoteSpacePermissionSet(String type) {
        this.type = type;
        this.contentPermissions = new RemoteContentPermission[0];
    }

    public RemoteSpacePermissionSet(String type, Collection<SpacePermission> permissionSet) {
        this.type = type;
        this.contentPermissions = new RemoteContentPermission[permissionSet.size()];
        int i = 0;
        for (SpacePermission permission : permissionSet) {
            this.contentPermissions[i++] = new RemoteContentPermission(permission);
        }
    }

    public String getType() {
        return this.type;
    }

    public RemoteContentPermission[] getSpacePermissions() {
        return this.contentPermissions;
    }
}

