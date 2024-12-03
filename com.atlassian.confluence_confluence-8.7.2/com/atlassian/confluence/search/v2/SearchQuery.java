/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.Expandable;
import com.atlassian.confluence.search.v2.SubClause;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public interface SearchQuery
extends Expandable<SearchQuery> {
    public static final float DEFAULT_BOOST = 1.0f;

    public String getKey();

    default public List getParameters() {
        return Collections.emptyList();
    }

    @Override
    default public SearchQuery expand() {
        return this;
    }

    default public float getBoost() {
        return 1.0f;
    }

    default public Stream<SubClause<SearchQuery>> getSubClauses() {
        return Stream.empty();
    }
}

