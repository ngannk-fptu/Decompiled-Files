/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.user.BestNameComparator2
 *  com.atlassian.core.util.FilterUtils
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.user.User
 *  com.atlassian.util.concurrent.Supplier
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.user.BestNameComparator2;
import com.atlassian.core.util.FilterUtils;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.user.User;
import com.atlassian.util.concurrent.Supplier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class UserPickerFilter {
    private static final BestNameComparator2 COMPARATOR = new BestNameComparator2();
    public String nameFilter = null;
    public String emailFilter = null;
    public String group = null;
    private final Supplier<UserAccessor> userAccessor = new LazyComponentReference("userAccessor");

    public String getNameFilter() {
        return this.nameFilter;
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = FilterUtils.verifyString((String)nameFilter);
    }

    public String getEmailFilter() {
        return this.emailFilter;
    }

    public void setEmailFilter(String emailFilter) {
        this.emailFilter = FilterUtils.verifyString((String)emailFilter);
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = FilterUtils.verifyString((String)group);
    }

    public List getFilteredUsers(Collection users) {
        ArrayList<User> filteredUsers = new ArrayList<User>();
        for (User user : users) {
            boolean shouldAllow = true;
            if (StringUtils.isNotEmpty((CharSequence)this.nameFilter)) {
                if (user.getFullName() == null) {
                    shouldAllow = false;
                } else if (user.getFullName().toLowerCase().indexOf(this.nameFilter.toLowerCase()) < 0) {
                    shouldAllow = false;
                }
            }
            if (StringUtils.isNotEmpty((CharSequence)this.emailFilter)) {
                if (user.getEmail() == null) {
                    shouldAllow = false;
                } else if (user.getEmail().toLowerCase().indexOf(this.emailFilter.toLowerCase()) < 0) {
                    shouldAllow = false;
                }
            }
            if (StringUtils.isNotEmpty((CharSequence)this.group) && !this.getUserAccessor().hasMembership(this.group, user.getName())) {
                shouldAllow = false;
            }
            if (!shouldAllow) continue;
            filteredUsers.add(user);
        }
        Collections.sort(filteredUsers, COMPARATOR);
        return filteredUsers;
    }

    public UserAccessor getUserAccessor() {
        return (UserAccessor)this.userAccessor.get();
    }
}

