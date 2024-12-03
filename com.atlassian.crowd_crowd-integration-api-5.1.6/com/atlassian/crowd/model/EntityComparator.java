/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.crowd.embedded.api.GroupComparator
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserComparator
 */
package com.atlassian.crowd.model;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.UserComparator;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupComparator;
import java.util.Comparator;

public final class EntityComparator {
    private EntityComparator() {
    }

    public static <T> Comparator<T> of(Class<T> type) {
        if (String.class.isAssignableFrom(type)) {
            return String.CASE_INSENSITIVE_ORDER;
        }
        if (User.class.isAssignableFrom(type)) {
            return UserComparator.USER_COMPARATOR;
        }
        if (com.atlassian.crowd.embedded.api.Group.class.isAssignableFrom(type)) {
            return com.atlassian.crowd.embedded.api.GroupComparator.GROUP_COMPARATOR;
        }
        if (Group.class.isAssignableFrom(type)) {
            return GroupComparator.GROUP_COMPARATOR;
        }
        throw new IllegalStateException("Can't find comparator for type " + type);
    }
}

