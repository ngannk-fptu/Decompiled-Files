/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.johnson.event.Event
 *  com.atlassian.johnson.event.EventLevel
 *  com.atlassian.johnson.event.EventPredicates
 *  com.atlassian.johnson.event.EventType
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.internal.health;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.johnson.event.Event;
import com.atlassian.johnson.event.EventLevel;
import com.atlassian.johnson.event.EventPredicates;
import com.atlassian.johnson.event.EventType;
import java.util.Optional;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public final class JohnsonEventPredicates {
    public static @NonNull Predicate<Event> blocksStartup() {
        return event -> Optional.ofNullable(event).filter(EventPredicates.attributeEquals((String)"dismissible", (Object)true).negate()).map(Event::getLevel).map(EventLevel::getLevel).map(JohnsonEventLevel::withName).filter(level -> level.isAtLeast(JohnsonEventLevel.ERROR)).isPresent();
    }

    public static @NonNull Predicate<Event> blocksStartupButNotLicenseEvents() {
        return event -> Optional.ofNullable(event).filter(EventPredicates.attributeEquals((String)"dismissible", (Object)true).negate()).filter(EventPredicates.type((EventType)JohnsonEventType.LICENSE_INCONSISTENCY.eventType()).negate()).filter(EventPredicates.type((EventType)JohnsonEventType.LICENSE_INCOMPATIBLE.eventType()).negate()).map(Event::getLevel).map(EventLevel::getLevel).map(JohnsonEventLevel::withName).filter(level -> level.isAtLeast(JohnsonEventLevel.ERROR)).isPresent();
    }

    public static @NonNull Predicate<Event> hasLevel(JohnsonEventLevel eventLevel) {
        return event -> Optional.ofNullable(event).map(Event::getLevel).map(EventLevel::getLevel).map(JohnsonEventLevel::withName).filter(eventLevel::equals).isPresent();
    }

    public static @NonNull Predicate<Event> hasType(JohnsonEventType eventType) {
        return event -> Optional.ofNullable(event).map(Event::getKey).map(EventType::getType).map(JohnsonEventType::withName).filter(eventType::equals).isPresent();
    }

    private JohnsonEventPredicates() {
    }
}

