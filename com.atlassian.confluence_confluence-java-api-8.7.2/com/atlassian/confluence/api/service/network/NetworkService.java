/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.api.service.network;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.sal.api.user.UserKey;

@ExperimentalApi
public interface NetworkService {
    public PageResponse<User> getFollowers(UserKey var1, PageRequest var2) throws NotFoundException;

    public PageResponse<User> getFollowing(UserKey var1, PageRequest var2) throws NotFoundException;
}

