/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.RequestCacheThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.Group
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.wrapper;

import com.atlassian.confluence.extra.calendar3.wrapper.UserAccessorWrapper;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.Group;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="cachingUserAccessorWrapper")
public class CachingUserAccessorWrapper
implements UserAccessorWrapper {
    private static final String USER_GROUPS_CACHE = "user.groups.cache";
    private static final String VALID_GROUP_CACHE = "valid.group.cache";
    private static final String VALID_USER_CACHE = "valid.user.cache";
    private final UserAccessor userAccessor;

    @Autowired
    public CachingUserAccessorWrapper(@ComponentImport UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    @Override
    public List<String> getUserGroups(ConfluenceUser user) {
        Map requestCache = RequestCacheThreadLocal.getRequestCache();
        Map userGroupsThreadLocalCache = (Map)requestCache.computeIfAbsent(USER_GROUPS_CACHE, key -> new HashMap());
        return userGroupsThreadLocalCache.computeIfAbsent(user, arg_0 -> ((UserAccessor)this.userAccessor).getGroupNames(arg_0));
    }

    @Override
    public Group getGroup(String groupName) {
        Map requestCache = RequestCacheThreadLocal.getRequestCache();
        Map validGroupsThreadLocalCache = (Map)requestCache.computeIfAbsent(VALID_GROUP_CACHE, key -> new HashMap());
        return validGroupsThreadLocalCache.computeIfAbsent(groupName, arg_0 -> ((UserAccessor)this.userAccessor).getGroup(arg_0));
    }

    @Override
    public ConfluenceUser getUser(String userKey) {
        Map requestCache = RequestCacheThreadLocal.getRequestCache();
        Map validUsersThreadLocalCache = (Map)requestCache.computeIfAbsent(VALID_USER_CACHE, key -> new HashMap());
        UserKey userKeyObject = new UserKey(userKey);
        return validUsersThreadLocalCache.computeIfAbsent(userKeyObject, arg_0 -> ((UserAccessor)this.userAccessor).getUserByKey(arg_0));
    }
}

