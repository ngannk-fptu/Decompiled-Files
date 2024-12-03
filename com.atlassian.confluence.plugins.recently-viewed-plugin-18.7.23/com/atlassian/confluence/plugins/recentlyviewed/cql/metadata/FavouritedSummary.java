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
public class FavouritedSummary {
    @JsonProperty
    final boolean isFavourite;
    @JsonProperty
    final DateTime favouritedDate;

    public FavouritedSummary(@JsonProperty(value="isFavourite") boolean isFavourite, @JsonProperty(value="favouritedDate") DateTime favouritedDate) {
        this.isFavourite = isFavourite;
        this.favouritedDate = favouritedDate;
    }

    public boolean getIsFavourite() {
        return this.isFavourite;
    }

    public DateTime getFavouritedDate() {
        return this.favouritedDate;
    }
}

