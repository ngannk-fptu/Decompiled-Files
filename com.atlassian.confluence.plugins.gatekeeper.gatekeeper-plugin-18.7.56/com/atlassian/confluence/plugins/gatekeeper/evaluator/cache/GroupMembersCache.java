/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.user.Group
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator.cache;

import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.SubCache;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyGroupEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyUserEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.GroupMembers;
import com.atlassian.confluence.plugins.gatekeeper.util.CopyOnceMap;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.Group;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GroupMembersCache
implements SubCache {
    private static final Logger logger = LoggerFactory.getLogger(GroupMembersCache.class);
    private final UserAccessor userAccessor;
    private Map<String, GroupMembers> groupMembersMap;
    private CopyOnceMap<GroupMembers> updateMap;

    GroupMembersCache(UserAccessor userAccessor, Map<String, GroupMembers> groupMembersMap) {
        this.userAccessor = userAccessor;
        this.groupMembersMap = groupMembersMap;
        this.updateMap = new CopyOnceMap<GroupMembers>(groupMembersMap);
    }

    GroupMembersCache(GroupMembersCache groupMembersCache, UserAccessor userAccessor) {
        this(userAccessor, groupMembersCache.groupMembersMap);
    }

    GroupMembersCache(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
        this.groupMembersMap = Collections.synchronizedMap(new Object2ObjectOpenHashMap());
        this.updateMap = new CopyOnceMap<GroupMembers>(this.groupMembersMap);
    }

    @Override
    public void update(TinyEvent event) {
        switch (event.getEventType()) {
            case USER_MEMBERSHIP_ADDED: 
            case USER_MEMBERSHIP_DELETED: 
            case GROUP_MEMBERSHIP_ADDED: 
            case GROUP_MEMBERSHIP_DELETED: {
                this.updateMap.setAsModified();
                this.updateMap.getUnderlyingMap().clear();
                break;
            }
            case GROUP_ADDED: 
            case GROUP_DELETED: {
                this.updateMap.remove(((TinyGroupEvent)event).getGroupName());
                break;
            }
            case USER_RENAMED: {
                this.invalidateMembershipsForUserRename(((TinyUserEvent)event).getOldUsername(), ((TinyUserEvent)event).getUsername());
            }
        }
    }

    private void invalidateMembershipsForUserRename(String oldUsername, String newUsername) {
        for (Map.Entry<String, GroupMembers> entry : this.updateMap.getUnderlyingMap().entrySet()) {
            GroupMembers groupMembers = entry.getValue();
            if (!groupMembers.contains(oldUsername) && !groupMembers.contains(newUsername)) continue;
            this.updateMap.remove(entry.getKey());
        }
    }

    private GroupMembers getGroupMembersFromApi(String groupName) {
        StopWatch stopWatch = StopWatch.createStarted();
        Group group = this.userAccessor.getGroup(groupName);
        if (group == null) {
            logger.warn("Group not found for group name: {}", (Object)groupName);
            return new GroupMembers();
        }
        List<String> memberNamesAsList = this.userAccessor.getMemberNamesAsList(group).stream().map(String::toLowerCase).collect(Collectors.toList());
        logger.debug("Fetched {} members for {} in {} ms", new Object[]{memberNamesAsList.size(), groupName, stopWatch.getTime()});
        return new GroupMembers(memberNamesAsList);
    }

    public void finish() {
        logger.debug("Finishing GroupMembersCache with updateMap status {}", (Object)this.updateMap.isModified());
        if (this.updateMap.isModified()) {
            this.groupMembersMap = Collections.synchronizedMap(this.updateMap.getUnderlyingMap());
            logger.debug("GroupMembersCache finished with new size {}", (Object)this.groupMembersMap.size());
        }
    }

    public GroupMembers get(String groupName) {
        return this.groupMembersMap.computeIfAbsent(groupName, this::getGroupMembersFromApi);
    }
}

