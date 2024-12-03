/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.core.user;

import com.atlassian.user.User;
import java.util.Comparator;

public class BestNameComparator2
implements Comparator {
    public int compare(Object o1, Object o2) {
        User u1 = (User)o1;
        User u2 = (User)o2;
        if (u1 == null && u2 == null) {
            return 0;
        }
        if (u2 == null) {
            return -1;
        }
        if (u1 == null) {
            return 1;
        }
        String name1 = u1.getFullName();
        String name2 = u2.getFullName();
        if (name1 == null) {
            name1 = u1.getName();
        }
        if (name2 == null) {
            name2 = u2.getName();
        }
        if (name1 == null || name2 == null) {
            throw new RuntimeException("Null user name");
        }
        int fullNameComparison = name1.toLowerCase().compareTo(name2.toLowerCase());
        if (fullNameComparison == 0) {
            return u1.getName().compareTo(u2.getName());
        }
        return fullNameComparison;
    }
}

