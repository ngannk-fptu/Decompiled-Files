/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.admin.criteria;

import com.atlassian.confluence.admin.criteria.AdminConfigurationCriteria;
import com.atlassian.confluence.util.UserChecker;

public class MoreThanOneUserCriteria
implements AdminConfigurationCriteria {
    private static final int REQUIRED_USERS = 2;
    private UserChecker userChecker;

    public MoreThanOneUserCriteria(UserChecker userChecker) {
        this.userChecker = userChecker;
    }

    @Override
    public boolean isMet() {
        return this.runQuery() >= 2;
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public String getValue() {
        int value = this.runQuery();
        return value < 0 ? "Not available" : String.valueOf(value);
    }

    @Override
    public boolean getIgnored() {
        return false;
    }

    @Override
    public void setIgnored(boolean ignored) {
    }

    @Override
    public boolean hasLiveValue() {
        return true;
    }

    private int runQuery() {
        return this.userChecker.getNumberOfRegisteredUsers();
    }
}

