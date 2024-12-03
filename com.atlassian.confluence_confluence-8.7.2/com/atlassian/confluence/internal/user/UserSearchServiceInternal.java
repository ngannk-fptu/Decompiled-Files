/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.fugue.Pair
 */
package com.atlassian.confluence.internal.user;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.internal.user.GroupSearchRequest;
import com.atlassian.confluence.internal.user.UserSearchRequest;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Pair;
import java.util.List;

public interface UserSearchServiceInternal {
    public PageResponse<ConfluenceUser> doUserSearch(PageRequest var1, UserSearchRequest var2) throws ServiceException;

    public Pair<List<String>, PageResponse<ConfluenceUser>> doMemberOfGroupsSearch(PageRequest var1, GroupSearchRequest var2) throws ServiceException;

    public boolean isSupportsSimpleSearch();
}

