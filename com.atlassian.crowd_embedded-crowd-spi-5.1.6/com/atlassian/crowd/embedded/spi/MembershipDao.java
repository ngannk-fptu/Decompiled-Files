/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.MembershipAlreadyExistsException
 *  com.atlassian.crowd.exception.MembershipNotFoundException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.util.BatchResult
 *  com.atlassian.crowd.util.BoundedCount
 *  com.google.common.collect.ListMultimap
 */
package com.atlassian.crowd.embedded.spi;

import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.MembershipNotFoundException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.util.BatchResult;
import com.atlassian.crowd.util.BoundedCount;
import com.google.common.collect.ListMultimap;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface MembershipDao {
    public boolean isUserDirectMember(long var1, String var3, String var4);

    public boolean isGroupDirectMember(long var1, String var3, String var4);

    public void addUserToGroup(long var1, String var3, String var4) throws UserNotFoundException, GroupNotFoundException, MembershipAlreadyExistsException;

    public BatchResult<String> addUserToGroups(long var1, String var3, Set<String> var4) throws UserNotFoundException;

    public BatchResult<String> addAllUsersToGroup(long var1, Collection<String> var3, String var4) throws GroupNotFoundException;

    public void addGroupToGroup(long var1, String var3, String var4) throws GroupNotFoundException, MembershipAlreadyExistsException;

    public BatchResult<String> addAllGroupsToGroup(long var1, Collection<String> var3, String var4) throws GroupNotFoundException;

    public void removeUserFromGroup(long var1, String var3, String var4) throws UserNotFoundException, GroupNotFoundException, MembershipNotFoundException;

    public BatchResult<String> removeUsersFromGroup(long var1, Collection<String> var3, String var4) throws GroupNotFoundException;

    public void removeGroupFromGroup(long var1, String var3, String var4) throws GroupNotFoundException, MembershipNotFoundException;

    public BatchResult<String> removeGroupsFromGroup(long var1, Collection<String> var3, String var4) throws GroupNotFoundException;

    public <T> List<T> search(long var1, MembershipQuery<T> var3);

    public <T> ListMultimap<String, T> searchGroupedByName(long var1, MembershipQuery<T> var3);

    public BoundedCount countDirectMembersOfGroup(long var1, String var3, int var4);
}

