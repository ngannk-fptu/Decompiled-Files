/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.diagnostics.internal.perflog.model;

import com.atlassian.diagnostics.internal.perflog.model.InstrumentQueryResults;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class IpdLogsResponse {
    @JsonProperty(value="logLines")
    List<InstrumentQueryResults> logLines;
    @JsonProperty(value="currentUserTimezone")
    String currentUserTimezone;

    public static IpdLogsResponseBuilder builder() {
        return new IpdLogsResponseBuilder();
    }

    public List<InstrumentQueryResults> getLogLines() {
        return this.logLines;
    }

    public String getCurrentUserTimezone() {
        return this.currentUserTimezone;
    }

    public void setLogLines(List<InstrumentQueryResults> logLines) {
        this.logLines = logLines;
    }

    public void setCurrentUserTimezone(String currentUserTimezone) {
        this.currentUserTimezone = currentUserTimezone;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IpdLogsResponse)) {
            return false;
        }
        IpdLogsResponse other = (IpdLogsResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        List<InstrumentQueryResults> this$logLines = this.getLogLines();
        List<InstrumentQueryResults> other$logLines = other.getLogLines();
        if (this$logLines == null ? other$logLines != null : !((Object)this$logLines).equals(other$logLines)) {
            return false;
        }
        String this$currentUserTimezone = this.getCurrentUserTimezone();
        String other$currentUserTimezone = other.getCurrentUserTimezone();
        return !(this$currentUserTimezone == null ? other$currentUserTimezone != null : !this$currentUserTimezone.equals(other$currentUserTimezone));
    }

    protected boolean canEqual(Object other) {
        return other instanceof IpdLogsResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List<InstrumentQueryResults> $logLines = this.getLogLines();
        result = result * 59 + ($logLines == null ? 43 : ((Object)$logLines).hashCode());
        String $currentUserTimezone = this.getCurrentUserTimezone();
        result = result * 59 + ($currentUserTimezone == null ? 43 : $currentUserTimezone.hashCode());
        return result;
    }

    public String toString() {
        return "IpdLogsResponse(logLines=" + this.getLogLines() + ", currentUserTimezone=" + this.getCurrentUserTimezone() + ")";
    }

    public IpdLogsResponse() {
    }

    public IpdLogsResponse(List<InstrumentQueryResults> logLines, String currentUserTimezone) {
        this.logLines = logLines;
        this.currentUserTimezone = currentUserTimezone;
    }

    public static class IpdLogsResponseBuilder {
        private List<InstrumentQueryResults> logLines;
        private String currentUserTimezone;

        IpdLogsResponseBuilder() {
        }

        public IpdLogsResponseBuilder logLines(List<InstrumentQueryResults> logLines) {
            this.logLines = logLines;
            return this;
        }

        public IpdLogsResponseBuilder currentUserTimezone(String currentUserTimezone) {
            this.currentUserTimezone = currentUserTimezone;
            return this;
        }

        public IpdLogsResponse build() {
            return new IpdLogsResponse(this.logLines, this.currentUserTimezone);
        }

        public String toString() {
            return "IpdLogsResponse.IpdLogsResponseBuilder(logLines=" + this.logLines + ", currentUserTimezone=" + this.currentUserTimezone + ")";
        }
    }
}

