/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.user;

import com.atlassian.confluence.internal.user.AbstractSearchRequestBuilder;
import com.atlassian.confluence.internal.user.GroupSearchRequest;

public class GroupSearchRequestBuilder
extends AbstractSearchRequestBuilder<GroupSearchRequest> {
    private String groupTerm;

    public GroupSearchRequestBuilder groupTerm(String groupTerm) {
        this.groupTerm = groupTerm;
        return this;
    }

    @Override
    public GroupSearchRequest build() {
        return new GroupSearchRequest(this.groupTerm, this.showUnlicensedUsers);
    }
}

