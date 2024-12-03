/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.comparator;

import com.atlassian.confluence.plugins.gatekeeper.model.comparator.OwnerComparator;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;

public class UserComparator
implements OwnerComparator<TinyOwner> {
    public static final UserComparator USER_USERNAME_COMPARATOR = new UserComparator();

    @Override
    public int compare(TinyOwner user1, TinyOwner user2) {
        if (user1.isAnonymous() && user2.isAnonymous()) {
            return 0;
        }
        if (user1.isAnonymous()) {
            return -1;
        }
        if (user2.isAnonymous()) {
            return 1;
        }
        String username1 = user1.getName();
        String username2 = user2.getName();
        return username1.compareToIgnoreCase(username2);
    }
}

