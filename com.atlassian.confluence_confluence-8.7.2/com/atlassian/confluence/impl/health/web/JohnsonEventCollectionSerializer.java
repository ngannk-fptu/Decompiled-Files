/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.johnson.event.Event
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.health.web;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.impl.health.web.JohnsonEventSerializer;
import com.atlassian.confluence.impl.health.web.JohnsonEventSerializerFactory;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonArray;
import com.atlassian.johnson.event.Event;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public class JohnsonEventCollectionSerializer {
    private final JohnsonEventSerializerFactory johnsonEventSerializerFactory;

    public JohnsonEventCollectionSerializer(JohnsonEventSerializerFactory johnsonEventSerializerFactory) {
        this.johnsonEventSerializerFactory = Objects.requireNonNull(johnsonEventSerializerFactory);
    }

    public @NonNull JsonArray toJson(Iterable<Event> events) {
        return new JsonArray(StreamSupport.stream(events.spliterator(), false).map(this::serializeEvent).collect(Collectors.toList()));
    }

    private Json serializeEvent(Event event) {
        JohnsonEventSerializer johnsonEventSerializer = this.johnsonEventSerializerFactory.forEvent(event);
        return johnsonEventSerializer.toJson(event);
    }
}

