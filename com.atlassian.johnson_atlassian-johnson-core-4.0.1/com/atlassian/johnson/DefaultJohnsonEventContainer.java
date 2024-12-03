/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.johnson;

import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.event.Event;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DefaultJohnsonEventContainer
implements JohnsonEventContainer {
    private final List<Event> events = new CopyOnWriteArrayList<Event>();

    @Override
    public void addEvent(Event event) {
        this.events.add((Event)Preconditions.checkNotNull((Object)event, (Object)"event"));
    }

    @Override
    public void clear() {
        this.events.clear();
    }

    @Override
    @Nonnull
    public Optional<Event> firstEvent(Predicate<? super Event> predicate) {
        return this.stream().filter(predicate).findFirst();
    }

    @Nonnull
    public List<Event> getEvents() {
        return Collections.unmodifiableList(this.events);
    }

    @Override
    @Nonnull
    public Collection<Event> getEvents(Predicate<? super Event> predicate) {
        return this.stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public boolean hasEvent(Predicate<? super Event> predicate) {
        return !this.getEvents(predicate).isEmpty();
    }

    @Override
    public boolean hasEvents() {
        return !this.events.isEmpty();
    }

    @Override
    public void removeEvent(Event event) {
        this.events.remove(Preconditions.checkNotNull((Object)event, (Object)"event"));
    }

    @Override
    @Nonnull
    public Stream<Event> stream() {
        return this.events.stream();
    }
}

