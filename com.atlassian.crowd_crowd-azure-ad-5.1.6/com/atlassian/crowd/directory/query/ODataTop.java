/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.query;

import com.atlassian.crowd.directory.query.MicrosoftGraphQueryParam;
import java.util.Objects;

public class ODataTop
implements MicrosoftGraphQueryParam {
    public static final int MS_GRAPH_MAX_PAGE_SIZE = 999;
    public static final String QUERY_PARAM_NAME = "$top";
    public static final ODataTop FULL_PAGE = new ODataTop(999);
    public static final ODataTop SINGLE_RESULT = new ODataTop(1);
    private final int value;

    public ODataTop(int value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return QUERY_PARAM_NAME;
    }

    @Override
    public String asRawValue() {
        return String.valueOf(this.value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ODataTop oDataTop = (ODataTop)o;
        return this.value == oDataTop.value;
    }

    public static ODataTop forSize(int size) {
        return size >= 999 ? FULL_PAGE : new ODataTop(size);
    }

    public int hashCode() {
        return Objects.hash(this.value);
    }
}

