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

public class MentionQuery
extends AbstractUserFieldQuery {
    public static final String MENTION_FIELD = "mentions";

    public MentionQuery(String ... username) {
        super(Arrays.asList(username));
    }

    public MentionQuery(UserAccessor userAccessor, UserKey ... userKeys) {
        this(MentionQuery.getUsernameFromKey(userAccessor, userKeys));
    }

    private static String[] getUsernameFromKey(UserAccessor userAccessor, UserKey ... userKeys) {
        ArrayList<String> usernames = new ArrayList<String>();
        for (UserKey userKey : userKeys) {
            ConfluenceUser user = userAccessor.getUserByKey(userKey);
            if (user == null) continue;
            usernames.add(user.getName());
        }
        return usernames.toArray(new String[usernames.size()]);
    }

    @Override
    public String getFieldName() {
        return MENTION_FIELD;
    }
}

