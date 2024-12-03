/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.johnson.Johnson
 *  com.atlassian.johnson.JohnsonEventContainer
 *  com.atlassian.johnson.event.Event
 *  com.atlassian.johnson.event.EventLevel
 *  com.atlassian.johnson.event.EventPredicates
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.setup.johnson;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.johnson.Johnson;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.event.Event;
import com.atlassian.johnson.event.EventLevel;
import com.atlassian.johnson.event.EventPredicates;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public class JohnsonUtils {
    public static Event raiseJohnsonEvent(JohnsonEventType eventType, String eventDescription, @Nullable String exception, JohnsonEventLevel eventLevel) {
        Event johnsonEvent = new Event(eventType.eventType(), eventDescription, exception, eventLevel.level());
        Johnson.getEventContainer().addEvent(johnsonEvent);
        return johnsonEvent;
    }

    public static Event raiseJohnsonEventRequiringTranslation(JohnsonEventType eventType, String i18nKey, @Nullable String exception, JohnsonEventLevel eventLevel) {
        Event johnsonEvent = new Event(eventType.eventType(), i18nKey, exception, eventLevel.level());
        johnsonEvent.addAttribute((Object)"i18nKey", (Object)i18nKey);
        Johnson.getEventContainer().addEvent(johnsonEvent);
        return johnsonEvent;
    }

    public static void removeEvent(Event johnsonEvent) {
        Johnson.getEventContainer().removeEvent(johnsonEvent);
    }

    public static Optional<JohnsonEventLevel> findHighestEventLevel() {
        return JohnsonUtils.findHighestEventLevel(Johnson.getEventContainer());
    }

    public static Optional<JohnsonEventLevel> findHighestEventLevel(JohnsonEventContainer eventContainer) {
        Stream eventStream = eventContainer.getEvents().stream();
        return eventStream.map(Event::getLevel).filter(Objects::nonNull).map(EventLevel::getLevel).map(JohnsonEventLevel::withName).map(Enum::ordinal).min(Integer::compare).map(minOrdinal -> JohnsonEventLevel.values()[minOrdinal]);
    }

    public static boolean eventExists(Predicate<Event> predicate) {
        return JohnsonUtils.eventExists(Johnson.getEventContainer(), predicate);
    }

    public static boolean eventExists(JohnsonEventContainer container, Predicate<Event> predicate) {
        Collection events = container.getEvents();
        return events.stream().anyMatch(predicate);
    }

    public static void dismissEvents() {
        JohnsonEventContainer johnsonEventContainer = Johnson.getEventContainer();
        johnsonEventContainer.stream().filter(EventPredicates.attributeEquals((String)"dismissible", (Object)true)).forEach(arg_0 -> ((JohnsonEventContainer)johnsonEventContainer).removeEvent(arg_0));
    }

    public static boolean allEventsDismissible() {
        return Johnson.getEventContainer().stream().allMatch(EventPredicates.attributeEquals((String)"dismissible", (Object)true));
    }
}

