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
import com.atlassian.confluence.impl.health.web.DefaultJohnsonEventSerializer;
import com.atlassian.confluence.impl.health.web.JohnsonEventSerializer;
import com.atlassian.confluence.impl.health.web.LegacyJohnsonEventSerializer;
import com.atlassian.johnson.event.Event;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public class JohnsonEventSerializerFactory {
    private final DefaultJohnsonEventSerializer defaultJohnsonEventSerializer;
    private final LegacyJohnsonEventSerializer legacyJohnsonEventSerializer;

    public JohnsonEventSerializerFactory(DefaultJohnsonEventSerializer defaultJohnsonEventSerializer, LegacyJohnsonEventSerializer legacyJohnsonEventSerializer) {
        this.defaultJohnsonEventSerializer = Objects.requireNonNull(defaultJohnsonEventSerializer);
        this.legacyJohnsonEventSerializer = Objects.requireNonNull(legacyJohnsonEventSerializer);
    }

    @NonNull JohnsonEventSerializer forEvent(Event event) {
        if (this.isEventForModernJohnsonPage(event)) {
            return this.defaultJohnsonEventSerializer;
        }
        return this.legacyJohnsonEventSerializer;
    }

    private boolean isEventForModernJohnsonPage(Event event) {
        return "CONFSRVDEV-2798".equals(event.getAttribute((Object)"uiVersion"));
    }
}

