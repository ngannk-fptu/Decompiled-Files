/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.user.UserKey;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface ConfluenceUserResolver {
    @Nullable
    public ConfluenceUser getUserByKey(UserKey var1);

    @Nullable
    public ConfluenceUser getUserByName(String var1);

    @Nonnull
    public List<ConfluenceUser> getUsersByUserKeys(List<UserKey> var1);

    @Nonnull
    public PageResponse<ConfluenceUser> getUsers(LimitedRequest var1);

    @Nullable
    public ConfluenceUser getExistingUserByKey(UserKey var1);

    @Nullable
    public ConfluenceUser getExistingUserByPerson(Person var1);

    @Nonnull
    public Optional<ConfluenceUser> getExistingByApiUser(User var1);
}

