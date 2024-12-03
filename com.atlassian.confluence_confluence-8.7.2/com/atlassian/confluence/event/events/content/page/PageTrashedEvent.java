/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.page;

import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.types.Trashed;
import com.atlassian.confluence.event.events.types.UserDriven;
import com.atlassian.confluence.pages.Page;
import com.atlassian.user.User;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PageTrashedEvent
extends PageEvent
implements Trashed,
UserDriven {
    private static final long serialVersionUID = 84846921729504988L;
    private final @Nullable User trasher;

    @Deprecated
    public PageTrashedEvent(Object source, Page trashedPage, @Nullable User trasher) {
        this(source, trashedPage, trasher, false);
    }

    public PageTrashedEvent(Object source, Page trashedPage, @Nullable User trasher, boolean suppressNotifications) {
        super(source, trashedPage, suppressNotifications);
        this.trasher = trasher;
    }

    @Override
    public @Nullable User getOriginatingUser() {
        return this.trasher;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof PageTrashedEvent)) {
            return false;
        }
        PageTrashedEvent other = (PageTrashedEvent)obj;
        return Objects.equals(this.trasher, other.trasher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.trasher);
    }
}

