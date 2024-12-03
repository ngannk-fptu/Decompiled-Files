/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.sal.usercompatibility;

import com.atlassian.sal.usercompatibility.CompatibilityUserUtilAccessException;
import com.atlassian.sal.usercompatibility.IdentifierUtils;
import com.atlassian.sal.usercompatibility.UserKey;
import com.google.common.base.Function;

public class UserKeys {
    private static Boolean userKeyImplemented;

    public static boolean isUserKeyImplemented() {
        if (userKeyImplemented == null) {
            try {
                UserKeys.getSalUserKeyClass();
                userKeyImplemented = true;
            }
            catch (CompatibilityUserUtilAccessException e) {
                userKeyImplemented = false;
            }
        }
        return userKeyImplemented;
    }

    public static Class<?> getSalUserKeyClass() {
        try {
            return Class.forName("com.atlassian.sal.api.user.UserKey");
        }
        catch (ClassNotFoundException e) {
            throw new CompatibilityUserUtilAccessException(e);
        }
    }

    public static UserKey getUserKeyFrom(String username) {
        if (username != null) {
            return new UserKey(IdentifierUtils.toLowerCase(username));
        }
        return null;
    }

    public static Object getSalUserKeyOrStringFrom(String userKey) {
        if (userKey == null) {
            return null;
        }
        if (UserKeys.isUserKeyImplemented()) {
            Class<?> userKeyClass = UserKeys.getSalUserKeyClass();
            try {
                return userKeyClass.getConstructor(String.class).newInstance(userKey);
            }
            catch (Exception e) {
                throw new CompatibilityUserUtilAccessException(e);
            }
        }
        return userKey;
    }

    public static Function<String, UserKey> toUserKeys() {
        return new Function<String, UserKey>(){

            public UserKey apply(String username) {
                return UserKeys.getUserKeyFrom(username);
            }
        };
    }
}

