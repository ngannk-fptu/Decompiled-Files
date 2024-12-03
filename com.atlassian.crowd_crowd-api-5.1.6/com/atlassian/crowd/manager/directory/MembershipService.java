/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.FeatureInaccessibleException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.user.User
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.FeatureInaccessibleException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.directory.DirectoryPermissionException;
import com.atlassian.crowd.manager.permission.DirectoryGroup;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.util.BatchResult;
import com.atlassian.crowd.util.BatchResultEntityWithCause;
import java.util.Collection;
import java.util.List;

public interface MembershipService {
    public BatchResult<BatchResultEntityWithCause> addUsersToGroup(long var1, List<String> var3, String var4) throws GroupNotFoundException, DirectoryNotFoundException, DirectoryPermissionException, OperationFailedException, FeatureInaccessibleException;

    public BatchResult<BatchResultEntityWithCause> addUserToGroups(long var1, String var3, List<String> var4) throws UserNotFoundException, DirectoryNotFoundException, OperationFailedException, FeatureInaccessibleException;

    public BatchResult<BatchResultEntityWithCause> removeUsersFromGroup(long var1, List<String> var3, String var4) throws GroupNotFoundException, DirectoryNotFoundException, OperationFailedException, FeatureInaccessibleException;

    public BatchResult<BatchResultEntityWithCause> removeUserFromGroups(long var1, String var3, List<String> var4) throws UserNotFoundException, DirectoryNotFoundException, OperationFailedException, FeatureInaccessibleException;

    public DirectoryGroup getGroupDetails(long var1, String var3) throws DirectoryNotFoundException, GroupNotFoundException, OperationFailedException, FeatureInaccessibleException;

    public Collection<User> getDirectUsersFromGroup(long var1, String var3, int var4, int var5) throws DirectoryNotFoundException, OperationFailedException, GroupNotFoundException, FeatureInaccessibleException;
}

