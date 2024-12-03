/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.event.events.content.page;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public class PageMoveCompletedEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 6474260036768743227L;
    private final List<Page> movedPageList;
    private final Space oldSpace;

    public PageMoveCompletedEvent(Page sourcePage, List<Page> movedPageList, Space oldSpace) {
        super(sourcePage);
        this.movedPageList = ImmutableList.copyOf(movedPageList);
        this.oldSpace = (Space)Preconditions.checkNotNull((Object)oldSpace);
    }

    public @NonNull List<Page> getMovedPageList() {
        return this.movedPageList;
    }

    public @NonNull Space getOldSpace() {
        return this.oldSpace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PageMoveCompletedEvent)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PageMoveCompletedEvent that = (PageMoveCompletedEvent)o;
        if (!this.movedPageList.equals(that.movedPageList)) {
            return false;
        }
        return this.oldSpace.equals(that.oldSpace);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.movedPageList.hashCode();
        result = 31 * result + this.oldSpace.hashCode();
        return result;
    }
}

