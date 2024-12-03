/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.query.Query
 */
package com.atlassian.confluence.impl.security.query;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.user.ConfluenceUser;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.query.Query;

@Internal
public interface SpacePermissionQueryBuilder {
    public String getHqlPermissionFilterString(String var1);

    public void substituteHqlQueryParameters(Query var1);

    public @Nullable ConfluenceUser getUser();

    default public String getPermissionType() {
        throw new UnsupportedOperationException("Not supported by implementation");
    }
}

