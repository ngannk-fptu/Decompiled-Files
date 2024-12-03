/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.cql.v2search.query;

import com.atlassian.confluence.plugins.cql.v2search.query.AbstractUserFieldQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class WatcherQuery
extends AbstractUserFieldQuery {
    public static final String WATCHER_FIELD = "watchers";

    public WatcherQuery(UserKey ... userKeys) {
        super(Arrays.asList(userKeys).stream().map(UserKey::getStringValue).collect(Collectors.toList()));
    }

    public WatcherQuery(UserAccessor userAccessor, String ... usernames) {
        this(WatcherQuery.getUserKeysFromUsernames(userAccessor, usernames));
    }

    private static UserKey[] getUserKeysFromUsernames(UserAccessor userAccessor, String ... usernames) {
        ArrayList<UserKey> userkeys = new ArrayList<UserKey>();
        for (String username : usernames) {
            ConfluenceUser user = userAccessor.getUserByName(username);
            if (user == null) continue;
            userkeys.add(user.getKey());
        }
        return userkeys.toArray(new UserKey[userkeys.size()]);
    }

    @Override
    public String getFieldName() {
        return WATCHER_FIELD;
    }
}

