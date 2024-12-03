/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.johnson.event;

import com.atlassian.johnson.Johnson;
import java.util.Objects;

public class EventType {
    private final String description;
    private final String type;

    public EventType(String type, String description) {
        this.description = description;
        this.type = type;
    }

    public static EventType get(String type) {
        return Johnson.getConfig().getEventType(type);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof EventType) {
            EventType e = (EventType)o;
            return Objects.equals(this.getDescription(), e.getDescription()) && Objects.equals(this.getType(), e.getType());
        }
        return false;
    }

    public String getDescription() {
        return this.description;
    }

    public String getType() {
        return this.type;
    }

    public int hashCode() {
        return Objects.hash(this.getType(), this.getDescription());
    }

    public String toString() {
        return "(EventType: " + this.type + ")";
    }
}

