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

public class UIAnalyticsEventDto
extends AnalyticsEventDto {
    @JsonProperty
    private final String source;
    @JsonProperty
    private final String actionSubjectId;
    @JsonProperty
    private final String containerType;
    @JsonProperty
    private final String containerId;
    @JsonProperty
    private final String actionSubject;

    @JsonCreator
    private UIAnalyticsEventDto(@JsonProperty(value="timestamp") long timestamp, @JsonProperty(value="actionSubject") String actionSubject, @JsonProperty(value="actionSubjectId") String actionSubjectId, @JsonProperty(value="containerType") String containerType, @JsonProperty(value="containerId") String containerId, @JsonProperty(value="action") String action, @JsonProperty(value="source") String source, @JsonProperty(value="attributes") Map<String, Object> attributes) {
        super(timestamp, action, attributes);
        this.source = source;
        this.actionSubjectId = actionSubjectId;
        this.containerType = containerType;
        this.containerId = containerId;
        this.actionSubject = actionSubject;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSource() {
        return this.source;
    }

    public String getActionSubjectId() {
        return this.actionSubjectId;
    }

    public String getContainerType() {
        return this.containerType;
    }

    public String getContainerId() {
        return this.containerId;
    }

    public String getActionSubject() {
        return this.actionSubject;
    }

    public static class Builder {
        private long timestamp;
        private String source;
        private String actionSubjectId;
        private String containerType;
        private String containerId;
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

        public Builder containerType(String containerType) {
            this.containerType = containerType;
            return this;
        }

        public Builder containerId(String containerId) {
            this.containerId = containerId;
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

        public UIAnalyticsEventDto build() {
            return new UIAnalyticsEventDto(this.timestamp, this.actionSubject, this.actionSubjectId, this.containerType, this.containerId, this.action, this.source, this.attributes);
        }
    }
}

