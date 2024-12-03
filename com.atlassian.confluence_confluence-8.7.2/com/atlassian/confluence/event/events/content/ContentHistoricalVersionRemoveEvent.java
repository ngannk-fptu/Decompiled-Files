/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.event.events.content;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.types.Removed;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ContentHistoricalVersionRemoveEvent
extends ContentEvent
implements Removed {
    private static final long serialVersionUID = 3122756062368352293L;
    private final ContentEntityObject historicalVersion;

    public ContentHistoricalVersionRemoveEvent(Object src, ContentEntityObject historicalVersion) {
        super(src, DefaultSaveContext.SUPPRESS_NOTIFICATIONS);
        this.historicalVersion = historicalVersion;
    }

    @Override
    public @NonNull ContentEntityObject getContent() {
        return this.historicalVersion;
    }
}

