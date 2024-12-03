/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content;

import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.Timestamped;
import com.atlassian.confluence.event.events.content.Contented;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class ContentEvent
extends ConfluenceEvent
implements Contented,
Timestamped {
    private boolean suppressNotifications;

    @Deprecated
    public ContentEvent(Object src) {
        this(src, false);
    }

    @Deprecated
    public ContentEvent(Object src, boolean suppressNotifications) {
        super(src);
        this.suppressNotifications = suppressNotifications;
    }

    public ContentEvent(Object source, @Nullable OperationContext<?> context) {
        super(source);
        this.suppressNotifications = context != null && context.isSuppressNotifications();
    }

    public boolean isSuppressNotifications() {
        return this.suppressNotifications;
    }

    @Deprecated
    public void setSuppressNotifications(boolean suppressNotifications) {
        this.suppressNotifications = suppressNotifications;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof ContentEvent)) {
            return false;
        }
        return this.suppressNotifications == ((ContentEvent)o).suppressNotifications;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.suppressNotifications);
    }
}

