/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 */
package com.atlassian.crowd.model.group;

import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.group.Group;
import java.util.Comparator;

public class GroupComparator
implements Comparator<Group> {
    public static final Comparator<Group> GROUP_COMPARATOR = new GroupComparator();

    private GroupComparator() {
    }

    public static boolean equal(Group group1, Group group2) {
        if (group1 == group2) {
            return true;
        }
        if (group1 == null || group2 == null) {
            return false;
        }
        if (group1.getDirectoryId() != group2.getDirectoryId()) {
            return false;
        }
        return IdentifierUtils.equalsInLowerCase((String)group1.getName(), (String)group2.getName());
    }

    public static boolean equalsObject(Group group, Object o) {
        if (group == o) {
            return true;
        }
        if (group == null) {
            return false;
        }
        if (!(o instanceof Group)) {
            return false;
        }
        Group otherGroup = (Group)o;
        return GroupComparator.equal(group, otherGroup);
    }

    public static int hashCode(Group group) {
        int result = (int)(group.getDirectoryId() ^ group.getDirectoryId() >>> 32);
        return 31 * IdentifierUtils.toLowerCase((String)group.getName()).hashCode() + result;
    }

    public static int compareTo(Group group1, Group group2) {
        long directoryId2;
        int nameCompare = IdentifierUtils.compareToInLowerCase((String)group1.getName(), (String)group2.getName());
        if (nameCompare != 0) {
            return nameCompare;
        }
        long directoryId1 = group1.getDirectoryId();
        return directoryId1 < (directoryId2 = group2.getDirectoryId()) ? -1 : (directoryId1 == directoryId2 ? 0 : 1);
    }

    @Override
    public int compare(Group group1, Group group2) {
        return GroupComparator.compareTo(group1, group2);
    }
}

