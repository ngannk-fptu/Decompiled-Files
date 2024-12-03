/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.johnson.JohnsonEventContainer
 *  com.atlassian.johnson.event.Event
 */
package com.atlassian.confluence.impl.health.analytics;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.event.Event;
import java.util.Collection;
import java.util.Optional;

@ParametersAreNonnullByDefault
final class HealthCheckJohnsonEvents {
    static Optional<Event> findEventById(JohnsonEventContainer johnsonContainer, String eventId) {
        Collection events = johnsonContainer.getEvents();
        return events.stream().filter(event -> eventId.equals(event.getAttribute((Object)"eventKey"))).findFirst();
    }

    private HealthCheckJohnsonEvents() {
    }
}

