/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.bonnie.Searchable
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.event.events.index;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.pages.Attachment;
import org.checkerframework.checker.nullness.qual.NonNull;

@EventName(value="confluence.index.indexDocumentBuild")
public class IndexDocumentBuildEvent {
    private final long durationMillis;
    private final String documentClass;
    private final String searchResultType;
    private String attachmentType;
    private long fileSize;

    public IndexDocumentBuildEvent(long startMillis, long endMillis, String documentType, @NonNull Searchable searchable) {
        this.durationMillis = endMillis - startMillis;
        this.searchResultType = documentType;
        this.documentClass = searchable.getClass().getSimpleName();
        if (searchable instanceof Attachment) {
            Attachment attachment = (Attachment)searchable;
            this.attachmentType = attachment.getNiceType();
            this.fileSize = attachment.getFileSize();
        }
        this.attachmentType = this.attachmentType == null ? "" : this.attachmentType;
    }

    public long getDurationMillis() {
        return this.durationMillis;
    }

    public String getDocumentClass() {
        return this.documentClass;
    }

    public String getSearchResultType() {
        return this.searchResultType;
    }

    public String getAttachmentType() {
        return this.attachmentType;
    }

    public long getFileSize() {
        return this.fileSize;
    }
}

