/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.GroupManager
 *  com.atlassian.user.search.page.Pager
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.GroupMembershipAccessor;
import com.atlassian.confluence.user.GroupResolver;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.search.page.Pager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GroupManagerGroupResolver
implements GroupResolver,
GroupMembershipAccessor {
    private static final Logger log = LoggerFactory.getLogger(GroupManagerGroupResolver.class);
    private final GroupManager groupManager;

    GroupManagerGroupResolver(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    @Override
    public Group getGroup(String name) {
        if (StringUtils.isEmpty((CharSequence)name)) {
            return null;
        }
        Group group = null;
        try {
            group = this.groupManager.getGroup(name);
        }
        catch (EntityException e) {
            log.error(e.getMessage());
        }
        return group;
    }

    @Override
    public Pager<String> getMemberNames(Group group) {
        Pager pager = null;
        if (group == null) {
            throw new IllegalArgumentException("There are no members in a null group");
        }
        try {
            pager = this.groupManager.getMemberNames(group);
        }
        catch (EntityException e) {
            log.error(e.toString(), (Throwable)e);
        }
        return pager;
    }
}

