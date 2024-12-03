/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.user.persistence.dao.compatibility;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class FindUserHelper {
    static final String CONFLUENCE_USER_DAO_COMPONENT_NAME = "confluenceUserDao";

    public static @Nullable ConfluenceUser getUserByUsername(@Nullable String username) {
        if (StringUtils.isEmpty((CharSequence)username)) {
            return null;
        }
        ConfluenceUser user = FindUserHelper.getConfluenceUserDao().findByUsername(username);
        return user;
    }

    public static ConfluenceUser getUserByUserKey(UserKey userKey) {
        if (userKey == null) {
            return null;
        }
        return FindUserHelper.getConfluenceUserDao().findByKey(userKey);
    }

    public static ConfluenceUser getUser(@Nullable User user) {
        if (user == null || user instanceof ConfluenceUser) {
            return (ConfluenceUser)user;
        }
        return FindUserHelper.getConfluenceUserDao().findByUsername(user.getName());
    }

    private static ConfluenceUserDao getConfluenceUserDao() {
        return (ConfluenceUserDao)ContainerManager.getComponent((String)CONFLUENCE_USER_DAO_COMPONENT_NAME, ConfluenceUserDao.class);
    }
}

