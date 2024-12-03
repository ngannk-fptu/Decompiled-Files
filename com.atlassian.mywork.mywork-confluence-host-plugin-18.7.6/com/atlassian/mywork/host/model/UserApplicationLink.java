/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.host.model;

public class UserApplicationLink {
    private final long id;
    private final String applicationLinkId;
    private final boolean authVerified;
    private final long created;
    private final long updated;

    public UserApplicationLink(long id, String applicationLinkId, boolean authVerified, long created, long updated) {
        this.id = id;
        this.applicationLinkId = applicationLinkId;
        this.authVerified = authVerified;
        this.created = created;
        this.updated = updated;
    }

    public long getId() {
        return this.id;
    }

    public String getApplicationLinkId() {
        return this.applicationLinkId;
    }

    public boolean isAuthVerified() {
        return this.authVerified;
    }

    public long getCreated() {
        return this.created;
    }

    public long getUpdated() {
        return this.updated;
    }
}

