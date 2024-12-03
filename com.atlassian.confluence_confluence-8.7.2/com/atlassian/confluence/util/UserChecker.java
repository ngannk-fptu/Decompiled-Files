/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

public interface UserChecker {
    public static final String NUMBER_OF_REGISTERED_USERS = "too.many.users.result";
    @Deprecated
    public static final int UNLIMITED_USERS = -2;

    public int getNumberOfRegisteredUsers();

    public boolean hasTooManyUsers();

    public boolean isLicensedToAddMoreUsers();

    public void incrementRegisteredUserCount();

    public void decrementRegisteredUserCount();

    public void resetResult();

    public boolean isUnlimitedUserLicense();
}

