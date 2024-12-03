/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  io.atlassian.fugue.Either
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.spaces;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.impl.security.access.AccessDenied;
import com.atlassian.confluence.internal.spaces.SpacesQueryWithPermissionQueryBuilder;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpacesQuery;
import io.atlassian.fugue.Either;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SpaceManagerInternal
extends SpaceManager {
    @Transactional(readOnly=true)
    public @NonNull PageResponse<Space> getSpaces(SpacesQuery var1, LimitedRequest var2, Predicate<? super Space> ... var3);

    public @NonNull Either<AccessDenied, SpacesQueryWithPermissionQueryBuilder> toSpacesQueryWithPermissionQueryBuilder(SpacesQuery var1);
}

