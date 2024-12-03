/*
 * Decompiled with CFR 0.152.
 */
package com.timgroup.statsd;

import java.util.Date;

public class Event {
    private String title;
    private String text;
    private long millisSinceEpoch = -1L;
    private String hostname;
    private String aggregationKey;
    private String priority;
    private String sourceTypeName;
    private String alertType;

    public String getTitle() {
        return this.title;
    }

    public String getText() {
        return this.text;
    }

    public long getMillisSinceEpoch() {
        return this.millisSinceEpoch;
    }

    public String getHostname() {
        return this.hostname;
    }

    public String getAggregationKey() {
        return this.aggregationKey;
    }

    public String getPriority() {
        return this.priority;
    }

    public String getSourceTypeName() {
        return this.sourceTypeName;
    }

    public String getAlertType() {
        return this.alertType;
    }

    public static Builder builder() {
        return new Builder();
    }

    private Event() {
    }

    public static class Builder {
        private final Event event = new Event();

        private Builder() {
        }

        public Event build() {
            if (this.event.title == null || this.event.title.isEmpty()) {
                throw new IllegalStateException("event title must be set");
            }
            if (this.event.text == null || this.event.text.isEmpty()) {
                throw new IllegalStateException("event text must be set");
            }
            return this.event;
        }

        public Builder withTitle(String title) {
            this.event.title = title;
            return this;
        }

        public Builder withText(String text) {
            this.event.text = text;
            return this;
        }

        public Builder withDate(Date date) {
            this.event.millisSinceEpoch = date.getTime();
            return this;
        }

        public Builder withDate(long millisSinceEpoch) {
            this.event.millisSinceEpoch = millisSinceEpoch;
            return this;
        }

        public Builder withHostname(String hostname) {
            this.event.hostname = hostname;
            return this;
        }

        public Builder withAggregationKey(String aggregationKey) {
            this.event.aggregationKey = aggregationKey;
            return this;
        }

        public Builder withPriority(Priority priority) {
            this.event.priority = priority.name().toLowerCase();
            return this;
        }

        public Builder withSourceTypeName(String sourceTypeName) {
            this.event.sourceTypeName = sourceTypeName;
            return this;
        }

        public Builder withAlertType(AlertType alertType) {
            this.event.alertType = alertType.name().toLowerCase();
            return this;
        }
    }

    public static enum AlertType {
        ERROR,
        WARNING,
        INFO,
        SUCCESS;

    }

    public static enum Priority {
        LOW,
        NORMAL;

    }
}

