/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;

public class ContentPermissionSummary {
    private final long id;
    private final Space space;
    private final ConfluenceUser creator;

    public ContentPermissionSummary(long id, Space space, ConfluenceUser creator) {
        this.id = id;
        this.space = space;
        this.creator = creator;
    }

    public long getId() {
        return this.id;
    }

    public Space getSpace() {
        return this.space;
    }

    public ConfluenceUser getCreator() {
        return this.creator;
    }
}

