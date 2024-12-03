/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.application;

import com.atlassian.crowd.model.application.GroupMapping;
import java.util.Comparator;

@Deprecated
public class GroupMappingComparator
implements Comparator<GroupMapping> {
    @Override
    public int compare(GroupMapping o1, GroupMapping o2) {
        int directoryNameCompare = String.CASE_INSENSITIVE_ORDER.compare(o1.getDirectory().getName(), o2.getDirectory().getName());
        if (directoryNameCompare == 0) {
            return String.CASE_INSENSITIVE_ORDER.compare(o1.getGroupName(), o2.getGroupName());
        }
        return directoryNameCompare;
    }
}

