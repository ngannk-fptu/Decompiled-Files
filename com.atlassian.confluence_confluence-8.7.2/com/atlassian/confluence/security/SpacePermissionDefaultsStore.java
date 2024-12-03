/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.spaces.Space;
import java.util.Set;

public interface SpacePermissionDefaultsStore {
    public void save();

    public Space getTemplateSpace();

    public Set<SpacePermission> createPermissionsForSpace(Space var1);

    public Set<String> getGroups();

    public void reset();
}

