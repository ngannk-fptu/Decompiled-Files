/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.johnson;

import com.atlassian.johnson.event.Event;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface JohnsonEventContainer {
    public void addEvent(Event var1);

    public void clear();

    @Nonnull
    public Optional<Event> firstEvent(Predicate<? super Event> var1);

    @Nonnull
    public Collection<Event> getEvents();

    @Nonnull
    public Collection<Event> getEvents(Predicate<? super Event> var1);

    public boolean hasEvent(Predicate<? super Event> var1);

    public boolean hasEvents();

    public void removeEvent(Event var1);

    @Nonnull
    public Stream<Event> stream();
}

