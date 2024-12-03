/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.crowd.embedded.api;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.google.common.base.Function;
import java.util.Comparator;

public class UserComparator
implements Comparator<User> {
    public static final Comparator<User> USER_COMPARATOR = new UserComparator();
    public static Function<User, Key> KEY_MAKER = new Function<User, Key>(){

        public Key apply(User t) {
            return new Key(IdentifierUtils.toLowerCase(t.getName()), t.getDirectoryId());
        }
    };

    private UserComparator() {
    }

    public static boolean equal(User user1, User user2) {
        if (user1 == user2) {
            return true;
        }
        if (user1 == null || user2 == null) {
            return false;
        }
        if (user1.getDirectoryId() != user2.getDirectoryId()) {
            return false;
        }
        return IdentifierUtils.equalsInLowerCase(user1.getName(), user2.getName());
    }

    public static boolean equalsObject(User user, Object o) {
        if (user == o) {
            return true;
        }
        if (user == null) {
            return false;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User otherUser = (User)o;
        return UserComparator.equal(user, otherUser);
    }

    public static int hashCode(User user) {
        int result = (int)(user.getDirectoryId() ^ user.getDirectoryId() >>> 32);
        return 31 * IdentifierUtils.toLowerCase(user.getName()).hashCode() + result;
    }

    public static int compareTo(User user1, User user2) {
        long directoryId2;
        int nameCompare = IdentifierUtils.compareToInLowerCase(user1.getName(), user2.getName());
        if (nameCompare != 0) {
            return nameCompare;
        }
        long directoryId1 = user1.getDirectoryId();
        return directoryId1 < (directoryId2 = user2.getDirectoryId()) ? -1 : (directoryId1 == directoryId2 ? 0 : 1);
    }

    @Override
    public int compare(User user1, User user2) {
        return UserComparator.compareTo(user1, user2);
    }

    public static class Key
    implements Comparable<Key> {
        private final String name;
        private final Long directoryId;

        public Key(String name, long directoryId) {
            this.name = name;
            this.directoryId = directoryId;
        }

        @Override
        public int compareTo(Key o) {
            int nameComparison = this.name.compareTo(o.name);
            if (nameComparison != 0) {
                return nameComparison;
            }
            return this.directoryId.compareTo(o.directoryId);
        }
    }
}

