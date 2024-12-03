/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.group.Group
 */
package com.atlassian.crowd.manager.directory.nestedgroups;

import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.model.group.Group;
import java.util.Collection;
import java.util.List;

public interface NestedGroupsProvider {
    public List<Group> getDirectlyRelatedGroups(Collection<String> var1) throws OperationFailedException;

    public String getIdentifierForSubGroupsQuery(Group var1);

    public String normalizeIdentifier(String var1);

    public int getMaxBatchSize();
}

