/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.collect.Sets;
import java.util.Set;

public class PageChangesBean {
    private final int numberOfChanges;
    private final Set<ConfluenceUser> usersWhoMadeChanges;

    public PageChangesBean(int numberOfChanges, Iterable<ConfluenceUser> usersWhoMadeChanges) {
        this.numberOfChanges = numberOfChanges;
        this.usersWhoMadeChanges = Sets.newLinkedHashSet(usersWhoMadeChanges);
    }

    public int getNumberOfChanges() {
        return this.numberOfChanges;
    }

    public Set<ConfluenceUser> getUsers() {
        return this.usersWhoMadeChanges;
    }
}

