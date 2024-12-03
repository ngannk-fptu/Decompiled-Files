/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.event.events.follow;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.types.UserDriven;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class FollowEvent
extends ConfluenceEvent
implements UserDriven,
NotificationEnabledEvent {
    private static final long serialVersionUID = 8119576408521660684L;
    private final ConfluenceUser follower;
    private final ConfluenceUser followee;

    public FollowEvent(Object src, ConfluenceUser followee, ConfluenceUser follower) {
        super(src);
        this.followee = followee;
        this.follower = follower;
    }

    public ConfluenceUser getFollowerUser() {
        return this.follower;
    }

    public ConfluenceUser getFolloweeUser() {
        return this.followee;
    }

    @Override
    public User getOriginatingUser() {
        return this.follower;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        FollowEvent rhs = (FollowEvent)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append((Object)this.follower, (Object)rhs.follower).append((Object)this.followee, (Object)rhs.followee).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(101, 103).appendSuper(super.hashCode()).append((Object)this.follower).append((Object)this.followee).toHashCode();
    }

    public String toString() {
        ToStringBuilder builder = new ToStringBuilder((Object)this);
        return builder.append("subject", (Object)this.followee).append("follower", (Object)this.follower).append("source", this.getSource()).toString();
    }

    public final boolean isSuppressNotifications() {
        return false;
    }
}

