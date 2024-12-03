/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.page;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.pages.Page;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class PageEvent
extends ContentEvent {
    private final Page page;

    @Deprecated
    public PageEvent(Object source, Page page) {
        super(source, false);
        this.page = Objects.requireNonNull(page);
    }

    @Deprecated
    public PageEvent(Object source, Page page, boolean suppressNotifications) {
        super(source, suppressNotifications);
        this.page = Objects.requireNonNull(page);
    }

    public PageEvent(Object source, Page page, @Nullable OperationContext<?> operationContext) {
        super(source, operationContext);
        this.page = Objects.requireNonNull(page);
    }

    public @NonNull Page getPage() {
        return this.page;
    }

    @Override
    public @NonNull ContentEntityObject getContent() {
        return this.getPage();
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof PageEvent)) {
            return false;
        }
        PageEvent other = (PageEvent)obj;
        return this.page.equals(other.page);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.page);
    }
}

