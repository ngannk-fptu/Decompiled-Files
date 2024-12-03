/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.base.Joiner
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.api.impl.pagination.PaginationQueryImpl;
import com.atlassian.confluence.core.actions.RssDescriptor;
import com.atlassian.confluence.event.events.profile.ViewNetworkEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.confluence.user.actions.UserAware;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.base.Joiner;
import java.security.Principal;
import org.apache.commons.lang3.StringUtils;

public class ViewFollowAction
extends AbstractUserProfileAction
implements UserAware {
    private String username;
    private String followersList;
    private String undoUnfollow;
    private EventPublisher eventPublisher;

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        ConfluenceUser user = this.getUser();
        if (user != null) {
            ViewNetworkEvent event = new ViewNetworkEvent(this);
            this.eventPublisher.publish((Object)event);
            this.followersList = this.calculateFollowersList(user);
            return "success";
        }
        return "error";
    }

    @Override
    public String getUsername() {
        if (StringUtils.isEmpty((CharSequence)this.username) && this.getAuthenticatedUser() != null) {
            this.username = this.getAuthenticatedUser().getName();
        }
        return this.username;
    }

    public void setUsername(String username) {
        if (HtmlUtil.shouldUrlDecode(username)) {
            username = HtmlUtil.urlDecode(username);
        }
        this.username = username;
    }

    private String calculateFollowersList(ConfluenceUser user) {
        return Joiner.on((String)",").join(this.followManager.getFollowing(user, PaginationQueryImpl.createNewQuery(Principal::getName)).pagingIterator());
    }

    public String getFollowersList() {
        return this.followersList;
    }

    public RssDescriptor getRssDescriptor() {
        return new RssDescriptor("/feeds/network.action?username=" + HtmlUtil.urlEncode(this.username) + "&max=40", null, this.getAuthenticatedUser() != null);
    }

    public User getUndoUnfollowUser() {
        return this.getUserByName(this.undoUnfollow);
    }

    public String getUndoUnfollow() {
        return this.undoUnfollow;
    }

    public void setUndoUnfollow(String undoUnfollow) {
        this.undoUnfollow = undoUnfollow;
    }

    public boolean isUndoUnfollowNeeded() {
        User undoUnfollowUser = this.getUndoUnfollowUser();
        return undoUnfollowUser != null && !this.followManager.isUserFollowing(this.getUser(), undoUnfollowUser);
    }
}

