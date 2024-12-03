/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.diagnostics.internal.perflog.model;

import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class InstrumentQueryResults {
    @JsonProperty(value="timestamp")
    private String timestamp;
    @JsonProperty(value="label")
    private String label;
    @JsonProperty(value="ObjectName")
    private String ObjectName;
    @JsonProperty(value="attributes")
    private Map<String, Object> attributes;

    public static InstrumentQueryResultsBuilder builder() {
        return new InstrumentQueryResultsBuilder();
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getLabel() {
        return this.label;
    }

    public String getObjectName() {
        return this.ObjectName;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setObjectName(String ObjectName) {
        this.ObjectName = ObjectName;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InstrumentQueryResults)) {
            return false;
        }
        InstrumentQueryResults other = (InstrumentQueryResults)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$timestamp = this.getTimestamp();
        String other$timestamp = other.getTimestamp();
        if (this$timestamp == null ? other$timestamp != null : !this$timestamp.equals(other$timestamp)) {
            return false;
        }
        String this$label = this.getLabel();
        String other$label = other.getLabel();
        if (this$label == null ? other$label != null : !this$label.equals(other$label)) {
            return false;
        }
        String this$ObjectName = this.getObjectName();
        String other$ObjectName = other.getObjectName();
        if (this$ObjectName == null ? other$ObjectName != null : !this$ObjectName.equals(other$ObjectName)) {
            return false;
        }
        Map<String, Object> this$attributes = this.getAttributes();
        Map<String, Object> other$attributes = other.getAttributes();
        return !(this$attributes == null ? other$attributes != null : !((Object)this$attributes).equals(other$attributes));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InstrumentQueryResults;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $timestamp = this.getTimestamp();
        result = result * 59 + ($timestamp == null ? 43 : $timestamp.hashCode());
        String $label = this.getLabel();
        result = result * 59 + ($label == null ? 43 : $label.hashCode());
        String $ObjectName = this.getObjectName();
        result = result * 59 + ($ObjectName == null ? 43 : $ObjectName.hashCode());
        Map<String, Object> $attributes = this.getAttributes();
        result = result * 59 + ($attributes == null ? 43 : ((Object)$attributes).hashCode());
        return result;
    }

    public String toString() {
        return "InstrumentQueryResults(timestamp=" + this.getTimestamp() + ", label=" + this.getLabel() + ", ObjectName=" + this.getObjectName() + ", attributes=" + this.getAttributes() + ")";
    }

    public InstrumentQueryResults() {
    }

    public InstrumentQueryResults(String timestamp, String label, String ObjectName, Map<String, Object> attributes) {
        this.timestamp = timestamp;
        this.label = label;
        this.ObjectName = ObjectName;
        this.attributes = attributes;
    }

    public static class InstrumentQueryResultsBuilder {
        private String timestamp;
        private String label;
        private String ObjectName;
        private Map<String, Object> attributes;

        InstrumentQueryResultsBuilder() {
        }

        public InstrumentQueryResultsBuilder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public InstrumentQueryResultsBuilder label(String label) {
            this.label = label;
            return this;
        }

        public InstrumentQueryResultsBuilder ObjectName(String ObjectName) {
            this.ObjectName = ObjectName;
            return this;
        }

        public InstrumentQueryResultsBuilder attributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        public InstrumentQueryResults build() {
            return new InstrumentQueryResults(this.timestamp, this.label, this.ObjectName, this.attributes);
        }

        public String toString() {
            return "InstrumentQueryResults.InstrumentQueryResultsBuilder(timestamp=" + this.timestamp + ", label=" + this.label + ", ObjectName=" + this.ObjectName + ", attributes=" + this.attributes + ")";
        }
    }
}

