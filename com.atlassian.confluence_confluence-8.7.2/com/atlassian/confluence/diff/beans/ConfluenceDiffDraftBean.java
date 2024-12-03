/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.diff.beans;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.diff.DiffException;
import com.atlassian.confluence.diff.Differ;
import com.atlassian.confluence.pages.Draft;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class ConfluenceDiffDraftBean {
    private List<String> errors = null;
    private String htmlDiff = null;
    private final String title;
    private final long draftId;

    public ConfluenceDiffDraftBean(ContentEntityObject original, Draft latest, Differ differ) {
        this.title = original.getTitle();
        this.draftId = latest == null ? 0L : latest.getId();
        try {
            this.htmlDiff = differ.diff(original, latest);
        }
        catch (DiffException e) {
            this.errors = ImmutableList.of((Object)e.getMessage());
        }
    }

    public String getHtmlDiff() {
        return this.htmlDiff;
    }

    public String getTitle() {
        return this.title;
    }

    public long getDraftId() {
        return this.draftId;
    }

    public List<String> getErrors() {
        return this.errors;
    }
}

