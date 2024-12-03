/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.page;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PageCreateEvent
extends PageEvent
implements Created,
NotificationEnabledEvent {
    private static final long serialVersionUID = 5416410257042854701L;
    private final ImmutableMap<String, Serializable> context;
    private final PageUpdateTrigger updateTrigger;

    @Deprecated
    public PageCreateEvent(Object source, Page page) {
        this(source, page, Collections.emptyMap(), false, PageUpdateTrigger.UNKNOWN);
    }

    @Deprecated
    public PageCreateEvent(Object source, Page page, Map<String, Serializable> context) {
        this(source, page, context, false, PageUpdateTrigger.UNKNOWN);
    }

    @Deprecated
    public PageCreateEvent(Object source, Page page, Map<String, Serializable> context, boolean suppressNotifications) {
        this(source, page, context, suppressNotifications, PageUpdateTrigger.UNKNOWN);
    }

    @Deprecated
    public PageCreateEvent(Object source, Page page, Map<String, Serializable> context, boolean suppressNotifications, PageUpdateTrigger updateTrigger) {
        super(source, page, suppressNotifications);
        this.context = ImmutableMap.copyOf(context);
        this.updateTrigger = updateTrigger;
    }

    public PageCreateEvent(Object source, Page page, Map<String, Serializable> context, @Nullable OperationContext<PageUpdateTrigger> operationContext) {
        super(source, page, operationContext);
        this.context = ImmutableMap.copyOf(context);
        this.updateTrigger = operationContext != null ? operationContext.getUpdateTrigger() : PageUpdateTrigger.UNKNOWN;
    }

    @Deprecated
    public ImmutableMap<String, Serializable> getContext() {
        return this.context;
    }

    public Map<String, Serializable> getContextMap() {
        return this.getContext();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof PageCreateEvent)) {
            return false;
        }
        PageCreateEvent that = (PageCreateEvent)obj;
        return this.context.equals(that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.context);
    }

    public @NonNull PageUpdateTrigger getUpdateTrigger() {
        return this.updateTrigger;
    }
}

