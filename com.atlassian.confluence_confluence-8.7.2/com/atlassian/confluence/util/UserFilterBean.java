/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class UserFilterBean {
    private List users;
    private String emailPattern;
    private String group;
    private UserAccessor userAccessor;

    public List getFilteredUsers() throws Exception {
        List filteredUsers = this.getUsers();
        if (StringUtils.isNotEmpty((CharSequence)this.getGroup())) {
            filteredUsers = this.getFilteredByGroup(filteredUsers, this.getGroup());
        }
        if (StringUtils.isNotEmpty((CharSequence)this.getEmailPattern())) {
            filteredUsers = this.getFilteredByEmailPattern(filteredUsers, this.getEmailPattern());
        }
        return filteredUsers;
    }

    private List getFilteredByGroup(List userList, String groupName) {
        ArrayList<User> result = new ArrayList<User>();
        for (User user : userList) {
            if (!this.getUserAccessor().hasMembership(groupName, user.getName())) continue;
            result.add(user);
        }
        return result;
    }

    private List getFilteredByEmailPattern(List userList, String emailPattern) {
        if (!StringUtils.isNotEmpty((CharSequence)emailPattern)) {
            return userList;
        }
        emailPattern = emailPattern.toLowerCase();
        ArrayList<User> result = new ArrayList<User>();
        for (User user : userList) {
            if (user == null || !StringUtils.isNotEmpty((CharSequence)user.getEmail()) || user.getEmail().toLowerCase().indexOf(emailPattern) == -1) continue;
            result.add(user);
        }
        return result;
    }

    public String getEmailPattern() {
        return this.emailPattern;
    }

    public void setEmailPattern(String emailPattern) {
        this.emailPattern = emailPattern;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List getUsers() {
        return this.users;
    }

    public void setUsers(List users) {
        this.users = users;
    }

    public UserAccessor getUserAccessor() {
        if (this.userAccessor == null) {
            this.userAccessor = (UserAccessor)ContainerManager.getComponent((String)"userAccessor");
        }
        return this.userAccessor;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }
}

