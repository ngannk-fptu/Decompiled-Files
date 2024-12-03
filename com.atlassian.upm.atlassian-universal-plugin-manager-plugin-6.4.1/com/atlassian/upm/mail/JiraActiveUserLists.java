/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.user.ApplicationUser
 *  com.atlassian.jira.user.util.UserUtil
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.upm.mail;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.mail.ProductUserLists;
import java.security.Principal;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JiraActiveUserLists
implements ProductUserLists {
    private final UserUtil userUtil;
    private final Function<Principal, UserKey> toUserKeys;

    public JiraActiveUserLists(UserUtil userUtil, UserManager userManager) {
        this.userUtil = Objects.requireNonNull(userUtil, "userUtil");
        this.toUserKeys = user -> userManager.getUserProfile(user.getName()).getUserKey();
    }

    @Override
    public Set<UserKey> getSystemAdmins() {
        return Collections.unmodifiableSet(this.userUtil.getJiraSystemAdministrators().stream().filter(ApplicationUser::isActive).map(this.toUserKeys).collect(Collectors.toSet()));
    }

    @Override
    public Set<UserKey> getAdminsAndSystemAdmins() {
        return Collections.unmodifiableSet(this.userUtil.getJiraAdministrators().stream().filter(ApplicationUser::isActive).map(this.toUserKeys).collect(Collectors.toSet()));
    }
}

