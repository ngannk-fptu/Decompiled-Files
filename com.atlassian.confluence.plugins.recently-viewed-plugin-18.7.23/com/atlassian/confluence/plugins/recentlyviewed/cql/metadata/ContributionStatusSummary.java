/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.plugins.recentlyviewed.cql.metadata;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ContributionStatusSummary {
    @JsonProperty
    private final String status;
    @JsonProperty
    private final DateTime when;

    @JsonCreator
    public ContributionStatusSummary(@JsonProperty(value="status") String status, @JsonProperty(value="when") DateTime when) {
        this.status = status;
        this.when = when;
    }

    public String getStatus() {
        return this.status;
    }

    public DateTime getWhen() {
        return this.when;
    }
}

