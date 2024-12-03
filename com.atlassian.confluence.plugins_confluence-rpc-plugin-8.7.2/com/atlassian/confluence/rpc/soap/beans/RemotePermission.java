/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.ContentPermission
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.security.ContentPermission;

@Deprecated
public class RemotePermission {
    private String lockType;
    private String lockedBy;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.security.ContentPermission permission \nsetLockType java.lang.String lockType \nsetLockedBy java.lang.String lockedBy \n";

    public RemotePermission() {
    }

    public RemotePermission(ContentPermission permission) {
        this.lockType = permission.getType();
        this.lockedBy = permission.isGroupPermission() ? permission.getGroupName() : permission.getUserName();
    }

    public String getLockType() {
        return this.lockType;
    }

    public void setLockType(String lockType) {
        this.lockType = lockType;
    }

    public String getLockedBy() {
        return this.lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }
}

