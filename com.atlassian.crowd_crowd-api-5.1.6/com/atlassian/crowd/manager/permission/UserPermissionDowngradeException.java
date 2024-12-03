/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.CrowdException
 */
package com.atlassian.crowd.manager.permission;

import com.atlassian.crowd.exception.CrowdException;

public class UserPermissionDowngradeException
extends CrowdException {
    final String groupName;

    public UserPermissionDowngradeException(String groupName, String s) {
        super(s);
        this.groupName = groupName;
    }

    public String getGroupName() {
        return this.groupName;
    }
}

