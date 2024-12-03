/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.johnson.event;

import com.atlassian.johnson.event.Event;
import com.atlassian.johnson.event.EventLevel;
import com.atlassian.johnson.event.EventType;
import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class EventPredicates {
    private EventPredicates() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " should not be instantiated");
    }

    @Nonnull
    public static Predicate<Event> attributeEquals(String name, @Nullable Object value) {
        Objects.requireNonNull(name, "name");
        return event -> Objects.equals(value, event.getAttribute(name));
    }

    @Nonnull
    public static Predicate<Event> level(EventLevel ... levels) {
        Objects.requireNonNull(levels, "levels");
        if (levels.length == 0) {
            return event -> false;
        }
        ImmutableSet acceptedLevels = ImmutableSet.copyOf((Object[])levels);
        return arg_0 -> EventPredicates.lambda$level$2((Set)acceptedLevels, arg_0);
    }

    @Nonnull
    public static Predicate<Event> type(EventType type) {
        Objects.requireNonNull(type, "type");
        return event -> type.equals(event.getKey());
    }

    private static /* synthetic */ boolean lambda$level$2(Set acceptedLevels, Event event) {
        return acceptedLevels.contains(event.getLevel());
    }
}

