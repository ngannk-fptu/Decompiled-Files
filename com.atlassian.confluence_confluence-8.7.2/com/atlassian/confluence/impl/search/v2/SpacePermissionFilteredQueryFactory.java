/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.search.v2;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.search.v2.SpacePermissionQuery;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SpacePermissionQueryFactory;
import com.atlassian.confluence.user.ConfluenceUser;
import javax.annotation.Nullable;

@Deprecated
@Internal
public class SpacePermissionFilteredQueryFactory
implements SpacePermissionQueryFactory {
    @Override
    public SearchQuery create(@Nullable ConfluenceUser user) {
        return new SpacePermissionQuery(user);
    }
}

