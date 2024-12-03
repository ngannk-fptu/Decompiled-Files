/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.FeatureInaccessibleException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 */
package com.atlassian.crowd.service;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.FeatureInaccessibleException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.util.List;

@ExperimentalApi
public interface DirectoryEntitySearchService {
    public List<User> searchUsers(EntityQuery<User> var1, List<Directory> var2) throws DirectoryNotFoundException, OperationFailedException, IllegalAccessException, UserNotFoundException, FeatureInaccessibleException;

    public List<User> searchPotentialMembers(EntityQuery<User> var1, long var2, String var4) throws DirectoryNotFoundException, GroupNotFoundException, OperationFailedException, UserNotFoundException, FeatureInaccessibleException;

    public List<Group> searchGroups(EntityQuery<Group> var1, List<Directory> var2) throws DirectoryNotFoundException, OperationFailedException, IllegalAccessException, UserNotFoundException, FeatureInaccessibleException;
}

