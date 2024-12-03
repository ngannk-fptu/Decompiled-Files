/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.plugins.recentlyviewed.cql.metadata;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RecentlyViewedSummary {
    @JsonProperty
    private final DateTime lastSeen;
    @JsonProperty
    private final String friendlyLastSeen;

    public RecentlyViewedSummary(@JsonProperty(value="lastSeen") DateTime lastSeen, @JsonProperty(value="friendlyLastSeen") String friendlyLastSeen) {
        this.lastSeen = lastSeen;
        this.friendlyLastSeen = friendlyLastSeen;
    }

    public DateTime getLastSeen() {
        return this.lastSeen;
    }

    public String getFriendlyLastSeen() {
        return this.friendlyLastSeen;
    }
}

