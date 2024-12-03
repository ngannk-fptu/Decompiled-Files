/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.persistence.dao;

import com.atlassian.confluence.core.persistence.VersionedObjectDao;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.persistence.dao.hibernate.SpacePermissionDTOLight;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface SpacePermissionDao
extends VersionedObjectDao<SpacePermission> {
    public SpacePermission getById(long var1);

    public boolean hasPermission(SpacePermission var1);

    public List<SpacePermission> findAllGlobalPermissions();

    public List<SpacePermission> findAllGlobalPermissionsForType(String var1);

    public List<SpacePermission> findPermissionsForUser(ConfluenceUser var1);

    public List<SpacePermission> findPermissionsForGroup(String var1);

    public List<SpacePermission> findPermissionsForSpace(Space var1);

    public List<SpacePermissionDTOLight> findPermissionsForSpacesAndTypes(Set<Long> var1, Collection<String> var2);

    public void removePermissionsForUser(String var1);

    public void removePermissionsForGroup(String var1);

    public void removePermissionsForSpace(Space var1);

    public List findPermissionTypes(SpacePermission var1);

    public Collection<SpacePermission> findGroupPermissionsForSpace(Space var1, String var2);

    public Collection<SpacePermission> findGlobalGroupPermissions(String var1);
}

