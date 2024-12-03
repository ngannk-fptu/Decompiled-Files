/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.synchronisation.cache;

import com.atlassian.crowd.directory.DirectoryCacheChangeOperations;
import com.atlassian.crowd.directory.synchronisation.cache.GroupActionStrategy;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractGroupActionStrategy
implements GroupActionStrategy {
    private Logger logger = LoggerFactory.getLogger(AbstractGroupActionStrategy.class);
    protected static final DirectoryCacheChangeOperations.GroupsToAddUpdateReplace NO_OP = new DirectoryCacheChangeOperations.GroupsToAddUpdateReplace((Set<GroupTemplate>)ImmutableSet.of(), (Set<GroupTemplate>)ImmutableSet.of(), (Map<String, GroupTemplate>)ImmutableMap.of());

    protected DirectoryCacheChangeOperations.GroupsToAddUpdateReplace replaceGroup(InternalDirectoryGroup internalGroup, Group remoteGroup) {
        return new DirectoryCacheChangeOperations.GroupsToAddUpdateReplace((Set<GroupTemplate>)ImmutableSet.of(), (Set<GroupTemplate>)ImmutableSet.of(), (Map<String, GroupTemplate>)ImmutableMap.of((Object)internalGroup.getName(), (Object)AbstractGroupActionStrategy.makeGroupTemplate(remoteGroup)));
    }

    protected DirectoryCacheChangeOperations.GroupsToAddUpdateReplace updateGroup(InternalDirectoryGroup internalGroup, Group remoteGroup) {
        GroupTemplate groupToUpdate = AbstractGroupActionStrategy.makeGroupTemplate(remoteGroup);
        groupToUpdate.setName(internalGroup.getName());
        return new DirectoryCacheChangeOperations.GroupsToAddUpdateReplace((Set<GroupTemplate>)ImmutableSet.of(), (Set<GroupTemplate>)ImmutableSet.of((Object)groupToUpdate), (Map<String, GroupTemplate>)ImmutableMap.of());
    }

    protected DirectoryCacheChangeOperations.GroupsToAddUpdateReplace addGroup(Group remoteGroup) {
        return new DirectoryCacheChangeOperations.GroupsToAddUpdateReplace((Set<GroupTemplate>)ImmutableSet.of((Object)AbstractGroupActionStrategy.makeGroupTemplate(remoteGroup)), (Set<GroupTemplate>)ImmutableSet.of(), (Map<String, GroupTemplate>)ImmutableMap.of());
    }

    private static GroupTemplate makeGroupTemplate(Group group) {
        GroupTemplate template = new GroupTemplate(group);
        template.setDescription(group.getDescription());
        return template;
    }

    protected boolean wasGroupUpdatedAfterSearchStart(Group remoteGroup, InternalDirectoryGroup internalGroup, Date syncStartDate, long directoryId) {
        if (internalGroup.getUpdatedDate() == null) {
            this.logger.warn("group '{}' in directory [ {} ] has no updated date", (Object)remoteGroup.getName(), (Object)directoryId);
        } else if (syncStartDate != null && internalGroup.getUpdatedDate().getTime() > syncStartDate.getTime()) {
            this.logger.debug("group '{}' in directory [ {} ] modified after synchronisation start, skipping", (Object)remoteGroup.getName(), (Object)directoryId);
            return true;
        }
        return false;
    }
}

