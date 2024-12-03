/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.event.events.content.page;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.types.Viewed;
import com.atlassian.confluence.spaces.Space;
import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;

public class PageListViewEvent
extends SpaceEvent
implements Viewed {
    private static final long serialVersionUID = -8822815204267137687L;
    private final String viewType;

    public PageListViewEvent(Object src, Space space, String viewType) {
        super(src, space);
        this.viewType = (String)Preconditions.checkNotNull((Object)viewType);
    }

    public @NonNull String getViewType() {
        return this.viewType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PageListViewEvent)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PageListViewEvent that = (PageListViewEvent)o;
        return this.viewType.equals(that.viewType);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.viewType.hashCode();
        return result;
    }
}

