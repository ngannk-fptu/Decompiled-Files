/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.api;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import java.util.Comparator;

public class GroupComparator
implements Comparator<Group> {
    public static final Comparator<Group> GROUP_COMPARATOR = new GroupComparator();

    private GroupComparator() {
    }

    @Override
    public int compare(Group group1, Group group2) {
        return GroupComparator.compareTo(group1, group2);
    }

    public static boolean equal(Group group1, Group group2) {
        if (group1 == group2) {
            return true;
        }
        if (group1 == null || group2 == null) {
            return false;
        }
        return IdentifierUtils.equalsInLowerCase(group1.getName(), group2.getName());
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
        return IdentifierUtils.toLowerCase(group.getName()).hashCode();
    }

    public static int compareTo(Group group1, Group group2) {
        return IdentifierUtils.compareToInLowerCase(group1.getName(), group2.getName());
    }
}

