/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.Expandable;

public interface SearchSort
extends Expandable<SearchSort> {
    public String getKey();

    public Order getOrder();

    @Override
    default public SearchSort expand() {
        return this;
    }

    public static enum Type {
        STRING,
        FLOAT,
        DOUBLE,
        INTEGER,
        LONG;

    }

    public static enum Order {
        ASCENDING,
        DESCENDING;

    }
}

