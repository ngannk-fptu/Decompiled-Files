/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.user.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.api.impl.pagination.PaginationQueryImpl;
import com.atlassian.confluence.api.impl.pagination.PagingIterator;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.confluence.user.actions.UserAware;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.Lists;
import java.security.Principal;
import java.util.List;

public class ViewFollowersAction
extends AbstractUserProfileAction
implements UserAware {
    private PaginationSupport<String> paginationSupport;
    private int startIndex = 0;
    private String username;
    private static final int DEFAULT_PAGE_SIZE = 40;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        this.paginationSupport = new PaginationSupport(40);
        PagingIterator<String> usernames = this.followManager.getFollowers(this.getUser(), PaginationQueryImpl.createNewQuery(Principal::getName)).pagingIterator();
        this.paginationSupport.setItems((List)Lists.newArrayList(usernames));
        this.paginationSupport.setStartIndex(this.startIndex);
        return super.execute();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public ConfluenceUser getUser() {
        return this.userAccessor.getUserByName(this.username);
    }

    public PaginationSupport getPaginationSupport() {
        return this.paginationSupport;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }
}

