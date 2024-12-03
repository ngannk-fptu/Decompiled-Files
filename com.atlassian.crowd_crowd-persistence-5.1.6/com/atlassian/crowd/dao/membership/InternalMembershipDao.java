/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.crowd.embedded.spi.MembershipDao
 *  com.atlassian.crowd.util.BatchResult
 */
package com.atlassian.crowd.dao.membership;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.crowd.embedded.spi.MembershipDao;
import com.atlassian.crowd.model.membership.InternalMembership;
import com.atlassian.crowd.util.BatchResult;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface InternalMembershipDao
extends MembershipDao {
    public void removeGroupMembers(long var1, String var3);

    public void removeGroupMemberships(long var1, String var3);

    public void removeUserMemberships(long var1, String var3);

    public void removeAllRelationships(long var1);

    public void removeAllUserRelationships(long var1);

    public void renameUserRelationships(long var1, String var3, String var4);

    public void renameGroupRelationships(long var1, String var3, String var4);

    public BatchResult<InternalMembership> addAll(Set<InternalMembership> var1);

    @ExperimentalSpi
    public List<InternalMembership> getMembershipsCreatedAfter(long var1, Date var3, int var4);
}

