/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.user;

public class SearchRequest {
    private final boolean showUnlicensedUsers;

    protected SearchRequest(boolean showUnlicensedUsers) {
        this.showUnlicensedUsers = showUnlicensedUsers;
    }

    public boolean isShowUnlicensedUsers() {
        return this.showUnlicensedUsers;
    }
}

