/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.johnson.event;

import com.atlassian.johnson.event.Event;
import com.google.common.base.Preconditions;
import java.util.EventObject;
import javax.annotation.Nonnull;

public class AddEvent
extends EventObject {
    private final Event event;

    public AddEvent(@Nonnull Object o, @Nonnull Event event) {
        super(o);
        this.event = (Event)Preconditions.checkNotNull((Object)event, (Object)"event");
    }

    @Nonnull
    public Event getEvent() {
        return this.event;
    }
}

