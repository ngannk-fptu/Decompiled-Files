/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.permission;

import com.atlassian.crowd.manager.permission.DirectoryGroup;
import com.atlassian.crowd.model.permission.UserPermission;

public interface PermittedGroup
extends DirectoryGroup {
    public UserPermission getPermission();
}

