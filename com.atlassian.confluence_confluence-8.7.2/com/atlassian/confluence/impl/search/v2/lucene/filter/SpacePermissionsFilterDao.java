/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.impl.search.v2.lucene.filter;

import com.atlassian.confluence.impl.security.query.SpacePermissionQueryBuilder;
import com.atlassian.user.User;
import java.util.List;

public interface SpacePermissionsFilterDao {
    @Deprecated
    public List<String> getPermittedSpaceKeysForUser(User var1);

    public List<String> getPermittedSpaceKeys(SpacePermissionQueryBuilder var1);

    public List<String> getUnPermittedSpaceKeys(SpacePermissionQueryBuilder var1);
}

