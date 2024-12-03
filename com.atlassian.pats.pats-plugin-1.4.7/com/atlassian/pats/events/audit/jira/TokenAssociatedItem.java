/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.auditing.AssociatedItem
 *  com.atlassian.jira.auditing.AssociatedItem$Type
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.pats.events.audit.jira;

import com.atlassian.jira.auditing.AssociatedItem;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TokenAssociatedItem
implements AssociatedItem {
    private final String userKey;
    private final String username;

    public TokenAssociatedItem(String userKey, String username) {
        this.userKey = userKey;
        this.username = username;
    }

    @Nonnull
    public String getObjectName() {
        return this.username;
    }

    @Nullable
    public String getObjectId() {
        return this.userKey;
    }

    @Nonnull
    public AssociatedItem.Type getObjectType() {
        return AssociatedItem.Type.USER;
    }

    @Nullable
    public String getParentName() {
        return null;
    }

    @Nullable
    public String getParentId() {
        return null;
    }
}

