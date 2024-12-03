/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.query;

import com.atlassian.crowd.directory.query.MicrosoftGraphQueryParam;
import java.util.Objects;

public class ODataFilter
implements MicrosoftGraphQueryParam {
    public static final String QUERY_PARAM_NAME = "$filter";
    private final String value;
    public static final ODataFilter EMPTY = new ODataFilter("");

    public ODataFilter(String value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return QUERY_PARAM_NAME;
    }

    @Override
    public String asRawValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ODataFilter that = (ODataFilter)o;
        return Objects.equals(this.value, that.value);
    }

    public int hashCode() {
        return Objects.hash(this.value);
    }
}

