/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import javax.annotation.Nullable;

public interface SpacePermissionQueryFactory {
    public SearchQuery create(@Nullable ConfluenceUser var1);
}

