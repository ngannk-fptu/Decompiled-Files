/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.userlister.model;

import com.atlassian.confluence.extra.userlister.model.ListedUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class UserList {
    public static final String ALL_GROUP_NAME = "*";
    private final String groupName;
    private final List<String> usernames;
    private final Set<String> loggedInUsernames;
    private final UserAccessor userAccessor;
    private final boolean useSpecificGroupName;
    private List<ListedUser> users;

    public UserList(String groupName, UserAccessor userAccessor, List<String> usernames, Set<String> loggedInUsernames) {
        this.groupName = groupName;
        this.userAccessor = userAccessor;
        this.usernames = usernames;
        this.loggedInUsernames = loggedInUsernames;
        this.useSpecificGroupName = !groupName.equals(ALL_GROUP_NAME);
    }

    public String getGroup() {
        return this.groupName;
    }

    public List<ListedUser> getUsers() {
        if (this.users == null) {
            this.loadUsers();
        }
        return Collections.unmodifiableList(this.users);
    }

    public boolean isUseSpecificGroupName() {
        return this.useSpecificGroupName;
    }

    private void loadUsers() {
        this.users = this.usernames.stream().map(arg_0 -> ((UserAccessor)this.userAccessor).getUserByName(arg_0)).filter(Objects::nonNull).filter(user -> !this.userAccessor.isDeactivated((User)user)).map(user -> new ListedUser((User)user, this.loggedInUsernames.contains(user.getName()))).collect(Collectors.toList());
        Collections.sort(this.users, Comparator.comparing(user -> StringUtils.isBlank((CharSequence)user.getFullName()) ? user.getName() : user.getFullName(), String.CASE_INSENSITIVE_ORDER));
    }
}

