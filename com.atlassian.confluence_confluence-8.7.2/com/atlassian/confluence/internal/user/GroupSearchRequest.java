/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.user;

import com.atlassian.confluence.internal.user.GroupSearchRequestBuilder;
import com.atlassian.confluence.internal.user.SearchRequest;

public class GroupSearchRequest
extends SearchRequest {
    private final String groupTerm;

    GroupSearchRequest(String groupTerm, boolean showUnlicensedUsers) {
        super(showUnlicensedUsers);
        this.groupTerm = groupTerm;
    }

    public String getGroupTerm() {
        return this.groupTerm;
    }

    public static GroupSearchRequestBuilder builder() {
        return new GroupSearchRequestBuilder();
    }
}

