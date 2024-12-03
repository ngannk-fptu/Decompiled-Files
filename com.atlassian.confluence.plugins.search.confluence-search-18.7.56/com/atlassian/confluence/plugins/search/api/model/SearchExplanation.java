/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.search.api.model;

import com.atlassian.confluence.core.ContentEntityObject;

public class SearchExplanation {
    private final ContentEntityObject content;
    private final String explanationString;

    public SearchExplanation(String explanation, ContentEntityObject content) {
        this.explanationString = explanation;
        this.content = content;
    }

    public ContentEntityObject getContent() {
        return this.content;
    }

    public String toString() {
        return this.explanationString;
    }
}

