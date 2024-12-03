/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.page;

import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.pages.Page;
import com.google.common.base.Preconditions;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PageCreateFromTemplateEvent
extends PageEvent
implements Created {
    private static final long serialVersionUID = 3153484701068925805L;
    private final String templateId;

    public PageCreateFromTemplateEvent(Object src, Page page, String templateId) {
        super(src, page, false);
        this.templateId = (String)Preconditions.checkNotNull((Object)templateId);
    }

    public @NonNull String getTemplateId() {
        return this.templateId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof PageCreateFromTemplateEvent)) {
            return false;
        }
        PageCreateFromTemplateEvent other = (PageCreateFromTemplateEvent)obj;
        return this.templateId.equals(other.templateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.templateId);
    }
}

