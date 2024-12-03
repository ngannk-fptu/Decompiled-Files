/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.page;

import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.types.Copied;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.persistence.dao.bulk.copy.PageCopyOptions;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PageCopyEvent
extends PageEvent
implements Copied {
    private static final long serialVersionUID = -688431098921580811L;
    private final Page origin;
    private final Page destination;
    private final ConfluenceUser initiator;
    private final PageCopyOptions pageCopyOptions;

    public PageCopyEvent(Object source, Page origin, Page destination, @Nullable ConfluenceUser initiator, boolean suppressNotifications) {
        super(source, destination, suppressNotifications);
        this.origin = origin;
        this.destination = destination;
        this.initiator = initiator;
        this.pageCopyOptions = null;
    }

    public PageCopyEvent(Object source, Page origin, Page destination, boolean suppressNotifications, PageCopyOptions pageCopyOptions) {
        super(source, destination, suppressNotifications);
        this.origin = origin;
        this.destination = destination;
        this.initiator = pageCopyOptions.getUser();
        this.pageCopyOptions = pageCopyOptions;
    }

    public Page getOrigin() {
        return this.origin;
    }

    public Page getDestination() {
        return this.destination;
    }

    public @Nullable ConfluenceUser getInitiator() {
        return this.initiator;
    }

    public @Nullable PageCopyOptions getPageCopyOptions() {
        return this.pageCopyOptions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.origin, this.destination, this.initiator, this.pageCopyOptions);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof PageCopyEvent)) {
            return false;
        }
        PageCopyEvent that = (PageCopyEvent)obj;
        return this.origin.equals(that.origin) && this.destination.equals(that.destination) && Objects.equals(this.initiator, that.initiator) && Objects.equals(this.pageCopyOptions, that.pageCopyOptions);
    }
}

