/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.sal.api.user.UserKey
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.user.persistence.dao;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.user.UserKey;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;

@ParametersAreNonnullByDefault
@Transactional(readOnly=true)
public interface ConfluenceUserDao {
    @Transactional
    public void create(ConfluenceUser var1);

    @Transactional
    public void update(ConfluenceUser var1);

    @Transactional
    public void remove(ConfluenceUser var1);

    @Transactional
    public @NonNull ConfluenceUser rename(String var1, String var2, boolean var3);

    @Transactional
    public @NonNull ConfluenceUser rename(ConfluenceUser var1, String var2, boolean var3);

    @Transactional
    public void deactivateUser(String var1);

    public @Nullable ConfluenceUser findByKey(@Nullable UserKey var1);

    public @Nullable ConfluenceUser findByUsername(@Nullable String var1);

    public @NonNull Set<ConfluenceUser> getAll();

    public Map<String, UserKey> findUserKeysByLowerNames(Iterable<String> var1);

    public Map<UserKey, String> findLowerNamesByKeys(Iterable<UserKey> var1);

    public boolean isDeletedUser(ConfluenceUser var1);

    public boolean isUnsyncedUser(ConfluenceUser var1);

    public List<ConfluenceUser> searchUnsyncedUsers(String var1);

    public int countUnsyncedUsers();

    public Map<UserKey, Optional<ConfluenceUser>> findByKeys(Set<UserKey> var1);

    public List<ConfluenceUser> findConfluenceUsersByLowerNames(Iterable<String> var1);

    public List<UserKey> getAllUserKeys();
}

