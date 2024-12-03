/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.google.common.base.Supplier
 *  org.joda.time.format.ISODateTimeFormat
 */
package com.atlassian.confluence.plugins.like.graphql;

import com.atlassian.confluence.api.model.people.User;
import com.atlassian.graphql.annotations.GraphQLName;
import com.google.common.base.Supplier;
import java.util.Date;
import java.util.Set;
import org.joda.time.format.ISODateTimeFormat;

public class LikeEntity {
    @GraphQLName
    private User user;
    @GraphQLName
    private String creationDate;
    private Supplier<Set<String>> followeesUsernames;

    public LikeEntity(User user, Date creationDate, Supplier<Set<String>> followeesUsernames) {
        this.user = user;
        this.creationDate = ISODateTimeFormat.dateTime().print(creationDate.getTime());
        this.followeesUsernames = followeesUsernames;
    }

    public User getUser() {
        return this.user;
    }

    @GraphQLName
    public boolean currentUserIsFollowing() {
        return ((Set)this.followeesUsernames.get()).contains(this.user.getUsername());
    }
}

