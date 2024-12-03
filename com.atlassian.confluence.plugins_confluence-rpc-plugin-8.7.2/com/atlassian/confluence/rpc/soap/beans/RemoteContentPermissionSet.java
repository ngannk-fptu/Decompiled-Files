/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.ContentPermission
 *  com.atlassian.confluence.security.ContentPermissionSet
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.rpc.soap.beans.RemoteContentPermission;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.ContentPermissionSet;

public class RemoteContentPermissionSet {
    private String type;
    private RemoteContentPermission[] contentPermissions;
    public static final String __PARANAMER_DATA = "<init> java.lang.String type \n<init> com.atlassian.confluence.security.ContentPermissionSet permissionSet \n";

    public RemoteContentPermissionSet(String type) {
        this.type = type;
        this.contentPermissions = new RemoteContentPermission[0];
    }

    public RemoteContentPermissionSet(ContentPermissionSet permissionSet) {
        this.type = permissionSet.getType();
        this.contentPermissions = new RemoteContentPermission[permissionSet.size()];
        int i = 0;
        for (ContentPermission contentPermission : permissionSet) {
            this.contentPermissions[i++] = new RemoteContentPermission(contentPermission);
        }
    }

    public String getType() {
        return this.type;
    }

    public RemoteContentPermission[] getContentPermissions() {
        return this.contentPermissions;
    }
}

