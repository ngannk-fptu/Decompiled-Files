/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.ObjectDao
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.spaces.persistence.dao;

import bucket.core.persistence.ObjectDao;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.internal.spaces.SpacesQueryWithPermissionQueryBuilder;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public interface SpaceDao
extends ObjectDao {
    public @Nullable Space getById(long var1);

    public @Nullable Space getSpace(@Nullable String var1);

    public @NonNull List<Space> getSpacesCreatedByUser(@Nullable String var1);

    public @Nullable Space getPersonalSpace(@Nullable ConfluenceUser var1);

    public @NonNull List<Space> getSpacesContainingPagesEditedByUser(@Nullable String var1);

    public @NonNull List<Space> getSpacesContainingCommentsByUser(@Nullable String var1);

    public @NonNull List<Space> getSpacesCreatedOrUpdatedSinceDate(Date var1);

    public int findPageTotal(Space var1);

    public int getNumberOfBlogPosts(Space var1);

    public @NonNull List<Space> getSpacesCreatedAfter(Date var1);

    public @NonNull List<Space> getSpaces(SpacesQueryWithPermissionQueryBuilder var1, int var2, int var3);

    public @NonNull List<Space> getSpaces(SpacesQueryWithPermissionQueryBuilder var1);

    public int countSpaces(SpacesQueryWithPermissionQueryBuilder var1);

    public @Nullable Space getSpaceByContentId(long var1);

    public @NonNull Collection<String> findSpaceKeysWithStatus(String var1);

    public @NonNull String findUniqueVersionOfSpaceKey(String var1);

    public void performOnAll(Consumer<Space> var1);
}

