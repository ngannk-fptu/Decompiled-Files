/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.johnson.event;

import com.atlassian.johnson.event.EventLevel;
import javax.annotation.Nullable;

public final class EventLevels {
    private EventLevels() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " should not be instantiated");
    }

    @Nullable
    public static EventLevel error() {
        return EventLevel.get("error");
    }

    @Nullable
    public static EventLevel fatal() {
        return EventLevel.get("fatal");
    }

    @Nullable
    public static EventLevel warning() {
        return EventLevel.get("warning");
    }
}

