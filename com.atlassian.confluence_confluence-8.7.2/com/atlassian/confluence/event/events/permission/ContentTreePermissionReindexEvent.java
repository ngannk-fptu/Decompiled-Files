/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.event.events.permission;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ContentTreePermissionReindexEvent
extends ContentEvent {
    private static final long serialVersionUID = 1446820920149171326L;
    private final ContentEntityObject content;

    public ContentTreePermissionReindexEvent(Object src, ContentEntityObject content) {
        super(src, false);
        this.content = (ContentEntityObject)Preconditions.checkNotNull((Object)content);
    }

    @Override
    public @NonNull ContentEntityObject getContent() {
        return this.content;
    }
}

