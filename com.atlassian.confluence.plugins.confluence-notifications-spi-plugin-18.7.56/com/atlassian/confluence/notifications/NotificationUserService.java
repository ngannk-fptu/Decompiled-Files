/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.notifications;

import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.fugue.Maybe;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.Optional;

public interface NotificationUserService {
    public User findUserForKey(UserKey var1);

    public User findUserForPerson(User var1, Person var2);

    public User findUserForKey(User var1, Maybe<UserKey> var2);

    public User findUserByKey(User var1, Optional<UserKey> var2);

    public User findUserForName(User var1, Maybe<String> var2);

    public User getAnonymousUser(User var1);
}

