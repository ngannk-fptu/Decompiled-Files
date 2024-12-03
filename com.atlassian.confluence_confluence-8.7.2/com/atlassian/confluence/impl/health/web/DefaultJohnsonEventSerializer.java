/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.johnson.event.Event
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.health.web;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.impl.health.web.JohnsonEventSerializer;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.johnson.event.Event;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public class DefaultJohnsonEventSerializer
implements JohnsonEventSerializer {
    @Override
    public @NonNull Json toJson(Event johnsonEvent) {
        String eventId;
        JsonObject eventJSON = new JsonObject();
        eventJSON.setProperty("title", this.getTitle(johnsonEvent)).setProperty("description", this.getDescription(johnsonEvent)).setProperty("level", this.getLevel(johnsonEvent)).setProperty("dismissible", this.isDismissible(johnsonEvent));
        String helpLink = this.getHelpUrl(johnsonEvent);
        if (helpLink != null) {
            eventJSON.setProperty("helpLink", helpLink);
        }
        if ((eventId = this.getEventId(johnsonEvent)) != null) {
            eventJSON.setProperty("eventId", eventId);
        }
        return eventJSON;
    }

    private String getTitle(Event event) {
        String category = event.getKey().getDescription();
        String title = event.getDesc();
        return String.format("%s: %s", category, title);
    }

    private String getDescription(Event event) {
        return event.getException();
    }

    private Boolean isDismissible(Event event) {
        return Boolean.TRUE.equals(event.getAttribute((Object)"dismissible"));
    }

    private String getLevel(Event event) {
        return event.getLevel().getLevel();
    }

    private String getHelpUrl(Event event) {
        return Optional.ofNullable(event.getAttribute((Object)"helpUrl")).map(Object::toString).map(StringUtils::trimToNull).orElse(null);
    }

    private String getEventId(Event event) {
        return (String)event.getAttribute((Object)"eventKey");
    }
}

