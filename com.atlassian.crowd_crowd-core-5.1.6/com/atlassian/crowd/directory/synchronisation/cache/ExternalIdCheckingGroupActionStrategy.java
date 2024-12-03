/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.DirectoryCacheChangeOperations$GroupsToAddUpdateReplace
 *  com.atlassian.crowd.directory.synchronisation.cache.AbstractGroupActionStrategy
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  com.atlassian.crowd.util.EqualityUtil
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.directory.synchronisation.cache;

import com.atlassian.crowd.directory.DirectoryCacheChangeOperations;
import com.atlassian.crowd.directory.synchronisation.cache.AbstractGroupActionStrategy;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.atlassian.crowd.util.EqualityUtil;
import java.util.Date;
import javax.annotation.Nullable;

public class ExternalIdCheckingGroupActionStrategy
extends AbstractGroupActionStrategy {
    public DirectoryCacheChangeOperations.GroupsToAddUpdateReplace decide(@Nullable InternalDirectoryGroup internalGroup, @Nullable InternalDirectoryGroup groupMatchedByExternalId, Group remoteGroup, Date syncStartDate, long directoryId) throws OperationFailedException {
        if (internalGroup != null && internalGroup.isLocal()) {
            return NO_OP;
        }
        if (groupMatchedByExternalId == null) {
            return this.addGroup(remoteGroup);
        }
        if (this.wasGroupUpdatedAfterSearchStart(remoteGroup, groupMatchedByExternalId, syncStartDate, directoryId)) {
            return NO_OP;
        }
        if (EqualityUtil.different((String)groupMatchedByExternalId.getName(), (String)remoteGroup.getName())) {
            return this.replaceGroup(groupMatchedByExternalId, remoteGroup);
        }
        if (EqualityUtil.different((String)groupMatchedByExternalId.getDescription(), (String)remoteGroup.getDescription())) {
            return this.updateGroup(groupMatchedByExternalId, remoteGroup);
        }
        return NO_OP;
    }
}

