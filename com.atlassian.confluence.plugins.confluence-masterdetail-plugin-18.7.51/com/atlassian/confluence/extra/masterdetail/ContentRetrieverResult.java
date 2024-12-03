/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.extra.masterdetail;

import com.atlassian.confluence.core.ContentEntityObject;
import java.util.List;

public class ContentRetrieverResult {
    private final List<ContentEntityObject> rows;
    private final boolean limited;

    public ContentRetrieverResult(List<ContentEntityObject> rows, Boolean limited) {
        this.rows = rows;
        this.limited = limited;
    }

    public List<ContentEntityObject> getRows() {
        return this.rows;
    }

    public boolean isLimited() {
        return this.limited;
    }
}

