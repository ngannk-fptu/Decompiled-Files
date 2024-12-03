/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.search.v2;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

@Deprecated
@Internal
public class SpacePermissionQuery
implements SearchQuery {
    public static final String KEY = "spacePermission";
    private final ConfluenceUser user;

    public SpacePermissionQuery(@Nullable ConfluenceUser user) {
        this.user = user;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Collections.emptyList();
    }

    public ConfluenceUser getUser() {
        return this.user;
    }
}

