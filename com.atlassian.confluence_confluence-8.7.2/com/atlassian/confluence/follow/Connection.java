/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.follow;

import com.atlassian.confluence.user.ConfluenceUser;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Connection {
    long id;
    private ConfluenceUser follower;
    private ConfluenceUser followee;

    public Connection() {
    }

    public Connection(ConfluenceUser follower, ConfluenceUser followee) {
        this.follower = follower;
        this.followee = followee;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ConfluenceUser getFollowerUser() {
        return this.follower;
    }

    public ConfluenceUser getFolloweeUser() {
        return this.followee;
    }

    public int hashCode() {
        return new HashCodeBuilder().append(this.id).append((Object)this.follower).append((Object)this.followee).toHashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Connection)) {
            return false;
        }
        Connection that = (Connection)obj;
        return new EqualsBuilder().append(this.id, that.id).append((Object)this.follower, (Object)that.follower).append((Object)this.followee, (Object)that.followee).isEquals();
    }
}

