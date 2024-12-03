/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.DirectoryCacheChangeOperations$GroupsToAddUpdateReplace
 *  com.atlassian.crowd.directory.synchronisation.cache.AbstractGroupActionStrategy
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  com.atlassian.crowd.util.EqualityUtil
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.synchronisation.cache;

import com.atlassian.crowd.directory.DirectoryCacheChangeOperations;
import com.atlassian.crowd.directory.synchronisation.cache.AbstractGroupActionStrategy;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.atlassian.crowd.util.EqualityUtil;
import java.util.Date;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultGroupActionStrategy
extends AbstractGroupActionStrategy {
    private static final Logger logger = LoggerFactory.getLogger(DefaultGroupActionStrategy.class);

    public DirectoryCacheChangeOperations.GroupsToAddUpdateReplace decide(@Nullable InternalDirectoryGroup groupMatchedByName, @Nullable InternalDirectoryGroup groupMatchedByExternalId, Group remoteGroup, Date syncStartDate, long directoryId) throws OperationFailedException {
        if (groupMatchedByName == null) {
            logger.debug("group '{}' not found, adding", (Object)remoteGroup.getName());
            return this.addGroup(remoteGroup);
        }
        if (!remoteGroup.getName().equals(groupMatchedByName.getName())) {
            logger.warn("remote group name [ {} ] casing differs from local group name [ {} ]. Group details will be kept updated, but the group name cannot be updated", (Object)remoteGroup.getName(), (Object)groupMatchedByName.getName());
        }
        if (this.wasGroupUpdatedAfterSearchStart(remoteGroup, groupMatchedByName, syncStartDate, directoryId)) {
            return NO_OP;
        }
        if (groupMatchedByName.isLocal()) {
            logger.info("group '{}' in directory [ {} ] matches local group of same name, skipping", (Object)remoteGroup.getName(), (Object)directoryId);
            return NO_OP;
        }
        if (remoteGroup.getType() == GroupType.LEGACY_ROLE && groupMatchedByName.getType() == GroupType.GROUP) {
            logger.debug("role [ {} ] in directory [ {} ] matches local group of same name, skipping", (Object)remoteGroup.getName(), (Object)directoryId);
            return NO_OP;
        }
        if (remoteGroup.getType() == GroupType.GROUP && groupMatchedByName.getType() == GroupType.LEGACY_ROLE) {
            logger.debug("role [ {} ] in directory [ {} ] matches legacy role of same name, replacing", (Object)groupMatchedByName.getName(), (Object)directoryId);
            return this.replaceGroup(groupMatchedByName, remoteGroup);
        }
        if (DefaultGroupActionStrategy.hasChanged(remoteGroup, (Group)groupMatchedByName)) {
            return this.updateGroup(groupMatchedByName, remoteGroup);
        }
        logger.trace("group '{}' unmodified, skipping", (Object)remoteGroup.getName());
        return NO_OP;
    }

    private static boolean hasChanged(Group remoteGroup, Group internalGroup) {
        return EqualityUtil.different((String)remoteGroup.getDescription(), (String)internalGroup.getDescription()) || EqualityUtil.different((String)remoteGroup.getExternalId(), (String)internalGroup.getExternalId());
    }
}

