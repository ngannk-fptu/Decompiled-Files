/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.security.SpacePermission;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SpacePermissionSaver {
    @Deprecated
    public void savePermission(SpacePermission var1);
}

