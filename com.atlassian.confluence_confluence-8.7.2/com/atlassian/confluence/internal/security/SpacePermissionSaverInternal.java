/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.security;

import com.atlassian.confluence.internal.security.SpacePermissionContext;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionSaver;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SpacePermissionSaverInternal
extends SpacePermissionSaver {
    public void savePermission(SpacePermission var1, SpacePermissionContext var2);
}

