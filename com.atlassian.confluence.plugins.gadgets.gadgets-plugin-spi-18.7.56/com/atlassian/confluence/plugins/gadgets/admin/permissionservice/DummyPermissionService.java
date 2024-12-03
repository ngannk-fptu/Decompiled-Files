/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.directory.spi.DirectoryPermissionService
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.gadgets.admin.permissionservice;

import com.atlassian.gadgets.directory.spi.DirectoryPermissionService;
import javax.annotation.Nullable;

public class DummyPermissionService
implements DirectoryPermissionService {
    public boolean canConfigureDirectory(@Nullable String username) {
        return false;
    }
}

