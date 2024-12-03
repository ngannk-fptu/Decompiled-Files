/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.checkerframework.checker.nullness.qual.EnsuresNonNullIf
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.page;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.event.events.content.Edited;
import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.types.ConfluenceEntityUpdated;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.google.common.base.Preconditions;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PageUpdateEvent
extends PageEvent
implements Edited,
ConfluenceEntityUpdated,
NotificationEnabledEvent {
    private static final long serialVersionUID = -1651577340299573312L;
    private final @Nullable AbstractPage originalPage;
    private final PageUpdateTrigger updateTrigger;

    @Deprecated
    public PageUpdateEvent(Object src, Page updatedPage, @Nullable AbstractPage originalPage, boolean suppressNotifications, PageUpdateTrigger updateTrigger) {
        super(src, updatedPage, suppressNotifications);
        this.originalPage = originalPage;
        this.updateTrigger = (PageUpdateTrigger)Preconditions.checkNotNull((Object)updateTrigger);
    }

    public PageUpdateEvent(Object source, Page updatedPage, @Nullable AbstractPage originalPage, @Nullable OperationContext<PageUpdateTrigger> operationContext) {
        super(source, updatedPage, operationContext);
        this.originalPage = originalPage;
        this.updateTrigger = operationContext != null ? operationContext.getUpdateTrigger() : PageUpdateTrigger.UNKNOWN;
    }

    public @Nullable AbstractPage getOriginalPage() {
        return this.originalPage;
    }

    @Override
    public boolean isMinorEdit() {
        return this.isSuppressNotifications();
    }

    public @NonNull PageUpdateTrigger getUpdateTrigger() {
        return this.updateTrigger;
    }

    @Override
    public @Nullable ConfluenceEntityObject getOld() {
        return this.originalPage;
    }

    @Override
    public @NonNull ConfluenceEntityObject getNew() {
        return this.getContent();
    }

    @EnsuresNonNullIf(expression={"getOriginalPage()"}, result=true)
    public boolean isTitleChanged() {
        if (this.originalPage == null) {
            return false;
        }
        return !this.originalPage.getTitle().equals(this.getContent().getTitle());
    }

    @Override
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
        if (!(obj instanceof PageUpdateEvent)) {
            return false;
        }
        PageUpdateEvent other = (PageUpdateEvent)obj;
        if (!Objects.equals(this.originalPage, other.originalPage)) {
            return false;
        }
        return this.updateTrigger == other.updateTrigger;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.originalPage, this.updateTrigger);
    }
}

