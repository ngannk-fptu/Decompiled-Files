/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;

public class ConfluenceGroupCache {
    public static final String GROUP_CACHE_KEY = "atlassian.core.util.groups.cache.key";

    public static Collection getGroups(HttpServletRequest request, UserAccessor userAccessor) {
        if (request == null) {
            return Collections.EMPTY_LIST;
        }
        ArrayList<String> groups = (ArrayList<String>)request.getAttribute(GROUP_CACHE_KEY);
        if (groups != null) {
            return groups;
        }
        User remoteUser = (User)SecurityConfigFactory.getInstance().getAuthenticator().getUser(request);
        if (remoteUser == null) {
            return Collections.EMPTY_LIST;
        }
        groups = new ArrayList<String>();
        for (Group group : userAccessor.getGroups(remoteUser)) {
            groups.add(group.getName());
        }
        request.setAttribute(GROUP_CACHE_KEY, groups);
        return groups;
    }
}

