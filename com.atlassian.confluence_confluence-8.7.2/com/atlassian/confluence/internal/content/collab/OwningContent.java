/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.content.collab;

import com.atlassian.confluence.internal.content.collab.IncludeOwnContentEnum;

public class OwningContent {
    private long contentId;
    private IncludeOwnContentEnum includeOwnContentEnum;

    public OwningContent(long contentId, IncludeOwnContentEnum includeOwnContentEnum) {
        this.contentId = contentId;
        this.includeOwnContentEnum = includeOwnContentEnum;
    }

    public long getContentId() {
        return this.contentId;
    }

    public boolean isShouldIncludeOwnContent() {
        return IncludeOwnContentEnum.INCLUDE.equals((Object)this.includeOwnContentEnum);
    }
}

