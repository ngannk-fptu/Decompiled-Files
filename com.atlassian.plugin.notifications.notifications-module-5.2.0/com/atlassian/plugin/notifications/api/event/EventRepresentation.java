/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugin.notifications.api.event;

import com.atlassian.sal.api.message.I18nResolver;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class EventRepresentation
implements Comparable<EventRepresentation> {
    public static final String ALL_EVENTS_ID = "all_events";
    @JsonProperty
    private final int id;
    @JsonProperty
    private final String eventKey;
    @JsonProperty
    private final String name;

    @JsonCreator
    public EventRepresentation(@JsonProperty(value="id") int id, @JsonProperty(value="eventKey") String eventKey, @JsonProperty(value="name") String name) {
        this.id = id;
        this.eventKey = eventKey;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getEventKey() {
        return this.eventKey;
    }

    public String getName() {
        return this.name;
    }

    public static EventRepresentation allEventsRepresentation(I18nResolver i18n) {
        return new EventRepresentation(0, ALL_EVENTS_ID, i18n.getText("notifications.plugin.name.all.events"));
    }

    @Override
    public int compareTo(EventRepresentation o) {
        return this.name.compareTo(o.getName());
    }
}

