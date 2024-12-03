/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.comparator;

import com.atlassian.confluence.plugins.gatekeeper.model.comparator.OwnerComparator;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;

public class GroupComparator
implements OwnerComparator<TinyOwner> {
    public static final GroupComparator GROUP_NAME_COMPARATOR = new GroupComparator();

    @Override
    public int compare(TinyOwner group1, TinyOwner group2) {
        String name1 = group1.getName();
        String name2 = group2.getName();
        return name1.compareToIgnoreCase(name2);
    }
}

