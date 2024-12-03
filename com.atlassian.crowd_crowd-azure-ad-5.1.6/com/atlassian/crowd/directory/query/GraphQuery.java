/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.query;

import com.atlassian.crowd.directory.query.ODataFilter;
import com.atlassian.crowd.directory.query.ODataSelect;
import com.atlassian.crowd.directory.query.ODataTop;
import java.util.Objects;

public class GraphQuery {
    private final ODataFilter filter;
    private final ODataSelect select;
    private final ODataTop limit;
    private final int startIndex;

    public GraphQuery(ODataFilter filter, ODataSelect select, int startIndex, ODataTop limit) {
        this.filter = filter;
        this.select = select;
        this.startIndex = startIndex;
        this.limit = limit;
    }

    public ODataFilter getFilter() {
        return this.filter;
    }

    public ODataSelect getSelect() {
        return this.select;
    }

    public ODataTop getLimit() {
        return this.limit;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GraphQuery that = (GraphQuery)o;
        return Objects.equals(this.limit, that.limit) && this.startIndex == that.startIndex && Objects.equals(this.filter, that.filter) && Objects.equals(this.select, that.select);
    }

    public int hashCode() {
        return Objects.hash(this.filter, this.select, this.limit, this.startIndex);
    }
}

