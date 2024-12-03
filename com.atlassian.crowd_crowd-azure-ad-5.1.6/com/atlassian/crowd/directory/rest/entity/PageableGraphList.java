/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.directory.rest.entity;

import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class PageableGraphList<T> {
    @JsonProperty(value="@odata.nextLink")
    private final String nextLink;
    @JsonProperty(value="value")
    private final List<T> entries;

    protected PageableGraphList() {
        this.nextLink = null;
        this.entries = null;
    }

    protected PageableGraphList(String nextLink, List<T> entries) {
        this.nextLink = nextLink;
        this.entries = entries;
    }

    public String getNextLink() {
        return this.nextLink;
    }

    public List<T> getEntries() {
        return this.entries;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PageableGraphList [nextLink=");
        builder.append(this.nextLink);
        builder.append(", entries=");
        builder.append(this.entries);
        builder.append("]");
        return builder.toString();
    }
}

