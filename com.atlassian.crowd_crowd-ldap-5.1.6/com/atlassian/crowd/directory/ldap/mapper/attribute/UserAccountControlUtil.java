/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.ldap.mapper.attribute;

public class UserAccountControlUtil {
    private static long USER_ACCOUNT_DISABLED_BITMASK = 2L;

    public static boolean isUserEnabled(String userAccountControlValue) {
        long attributeValue = Long.parseLong(userAccountControlValue);
        return (attributeValue & USER_ACCOUNT_DISABLED_BITMASK) == 0L;
    }

    public static String enabledUser(String currentValue) {
        long attributeValue = Long.parseLong(currentValue);
        return Long.toString(attributeValue & (USER_ACCOUNT_DISABLED_BITMASK ^ 0xFFFFFFFFFFFFFFFFL));
    }

    public static String disabledUser(String currentValue) {
        long attributeValue = Long.parseLong(currentValue);
        return Long.toString(attributeValue | USER_ACCOUNT_DISABLED_BITMASK);
    }
}

