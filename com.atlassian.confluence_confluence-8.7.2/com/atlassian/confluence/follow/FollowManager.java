/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.follow;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface FollowManager {
    @Transactional(readOnly=true)
    public boolean isUserFollowing(User var1, User var2);

    public void followUser(ConfluenceUser var1, ConfluenceUser var2);

    public void unfollowUser(User var1, User var2);

    public void removeAllConnectionsFor(User var1);
}

