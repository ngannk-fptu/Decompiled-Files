/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.user.search.page.Pager
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.user;

import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.search.page.Pager;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface UserAccessorInternal
extends UserAccessor {
    @Override
    public @Nullable ConfluenceUser getExistingUserByPerson(Person var1);

    @Override
    public Optional<ConfluenceUser> getExistingByApiUser(User var1);

    public boolean isDeletedUser(ConfluenceUser var1);

    public boolean isUnsyncedUser(ConfluenceUser var1);

    public boolean isCrowdManaged(ConfluenceUser var1);

    public Pager<ConfluenceUser> searchUnsyncedUsers(String var1);
}

