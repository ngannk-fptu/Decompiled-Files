/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.ConstantScoreQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HasPersonalSpaceQuery
implements SearchQuery {
    public static final String KEY = "hasPersonalSpace";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Collections.emptyList();
    }

    @Override
    public SearchQuery expand() {
        return new ConstantScoreQuery(new TermQuery(KEY, String.valueOf(Boolean.TRUE)));
    }

    public int hashCode() {
        return Objects.hash(KEY);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HasPersonalSpaceQuery)) {
            return false;
        }
        return Objects.equals(this.getKey(), ((HasPersonalSpaceQuery)obj).getKey());
    }
}

