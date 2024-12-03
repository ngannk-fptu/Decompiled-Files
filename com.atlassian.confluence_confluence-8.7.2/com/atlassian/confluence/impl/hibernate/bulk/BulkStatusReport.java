/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.google.common.annotations.VisibleForTesting
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.impl.hibernate.bulk;

import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.impl.hibernate.bulk.BulkSimpleMessage;
import com.atlassian.confluence.impl.hibernate.bulk.BulkSimpleMessageTypes;
import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class BulkStatusReport {
    @JsonProperty
    private SimpleMessage status;
    @JsonProperty
    private String destinationUrl;
    @JsonProperty
    private int totalPageNeedToCopy;
    @JsonProperty
    private List<BulkSimpleMessage> errors;

    @VisibleForTesting
    public BulkStatusReport() {
    }

    protected BulkStatusReport(String destinationUrl, SimpleMessage status, List<BulkSimpleMessage> errors) {
        this.destinationUrl = destinationUrl;
        this.status = status;
        this.errors = errors == null ? new ArrayList() : errors;
    }

    public int getTotalPageNeedToCopy() {
        return this.totalPageNeedToCopy;
    }

    void setTotalPageNeedToCopy(int totalPageNeedToCopy) {
        this.totalPageNeedToCopy = totalPageNeedToCopy;
    }

    public String getDestinationUrl() {
        return this.destinationUrl;
    }

    public SimpleMessage getStatus() {
        return this.status;
    }

    public List<BulkSimpleMessage> getErrors() {
        return this.errors;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private SimpleMessage status;
        private String destinationUrl;
        private int totalPageNeedToCopy;
        private List<BulkSimpleMessage> errors = new ArrayList<BulkSimpleMessage>();

        public Builder withTotalPageNeedToCopy(int totalPageNeedToCopy) {
            this.totalPageNeedToCopy = totalPageNeedToCopy;
            return this;
        }

        public Builder withDestinationUrl(String destinationUrl) {
            this.destinationUrl = destinationUrl;
            return this;
        }

        public Builder withMessageKey(String messageKey, String ... args) {
            this.status = SimpleMessage.withKeyAndArgs((String)messageKey, (Object[])args);
            return this;
        }

        public Builder addErrorMessage(String messageKey, String ... args) {
            this.errors.add(BulkSimpleMessage.withKeyAndArgs(BulkSimpleMessageTypes.ERROR, messageKey, (Object[])args));
            return this;
        }

        public Builder addWarnMessage(String messageKey, String ... args) {
            this.errors.add(BulkSimpleMessage.withKeyAndArgs(BulkSimpleMessageTypes.WARN, messageKey, (Object[])args));
            return this;
        }

        public BulkStatusReport build() {
            BulkStatusReport bulkStatusReport = new BulkStatusReport(this.destinationUrl, this.status, this.errors);
            if (this.totalPageNeedToCopy > 0) {
                bulkStatusReport.setTotalPageNeedToCopy(this.totalPageNeedToCopy);
            }
            return bulkStatusReport;
        }
    }
}

