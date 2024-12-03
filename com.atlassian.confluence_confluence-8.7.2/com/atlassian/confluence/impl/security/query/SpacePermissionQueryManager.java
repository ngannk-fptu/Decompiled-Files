/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.fugue.Either
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.security.query;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.security.access.AccessDenied;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryBuilder;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Either;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Internal
public interface SpacePermissionQueryManager {
    public Either<AccessDenied, SpacePermissionQueryBuilder> createSpacePermissionQueryBuilder(@Nullable ConfluenceUser var1, @NonNull String var2);
}

