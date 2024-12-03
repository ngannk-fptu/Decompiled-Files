/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto.analytics;

import com.atlassian.migration.agent.dto.analytics.AnalyticsEventDto;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class TrackAnalyticsEventDto
extends AnalyticsEventDto {
    @JsonProperty
    private final String actionSubjectId;
    @JsonProperty
    private final String source;
    @JsonProperty
    private final String actionSubject;

    @JsonCreator
    public TrackAnalyticsEventDto(@JsonProperty(value="timestamp") long timestamp, @JsonProperty(value="action") String action, @JsonProperty(value="actionSubject") String actionSubject, @JsonProperty(value="actionSubjectId") String actionSubjectId, @JsonProperty(value="source") String source, @JsonProperty(value="attributes") Map<String, Object> attributes) {
        super(timestamp, action, attributes);
        this.actionSubjectId = actionSubjectId;
        this.source = source;
        this.actionSubject = actionSubject;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getActionSubjectId() {
        return this.actionSubjectId;
    }

    public String getSource() {
        return this.source;
    }

    public String getActionSubject() {
        return this.actionSubject;
    }

    public static class Builder {
        private long timestamp;
        private String source;
        private String actionSubjectId;
        private String action;
        private String actionSubject;
        private Map<String, Object> attributes;

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder actionSubject(String actionSubject) {
            this.actionSubject = actionSubject;
            return this;
        }

        public Builder actionSubjectId(String actionSubjectId) {
            this.actionSubjectId = actionSubjectId;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder attributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        public TrackAnalyticsEventDto build() {
            return new TrackAnalyticsEventDto(this.timestamp, this.action, this.actionSubject, this.actionSubjectId, this.source, this.attributes);
        }
    }
}

