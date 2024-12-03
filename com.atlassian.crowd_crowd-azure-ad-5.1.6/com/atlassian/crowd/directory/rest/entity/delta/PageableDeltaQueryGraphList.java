/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.directory.rest.entity.delta;

import com.atlassian.crowd.directory.rest.entity.PageableGraphList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public abstract class PageableDeltaQueryGraphList<T>
extends PageableGraphList<T> {
    @JsonProperty(value="@odata.deltaLink")
    private final String deltaLink;

    protected PageableDeltaQueryGraphList() {
        this.deltaLink = null;
    }

    protected PageableDeltaQueryGraphList(String nextLink, List<T> entries) {
        super(nextLink, entries);
        this.deltaLink = null;
    }

    public PageableDeltaQueryGraphList(String nextLink, List<T> entries, String deltaLink) {
        super(nextLink, entries);
        this.deltaLink = deltaLink;
    }

    public String getDeltaLink() {
        return this.deltaLink;
    }
}

