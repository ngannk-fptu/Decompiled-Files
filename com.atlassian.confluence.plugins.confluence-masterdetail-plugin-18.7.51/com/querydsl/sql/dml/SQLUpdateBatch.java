/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.dml;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import java.util.Map;

public class SQLUpdateBatch {
    private final QueryMetadata metadata;
    private final Map<Path<?>, Expression<?>> updates;

    public SQLUpdateBatch(QueryMetadata metadata, Map<Path<?>, Expression<?>> updates) {
        this.metadata = metadata;
        this.updates = updates;
    }

    public QueryMetadata getMetadata() {
        return this.metadata;
    }

    public Map<Path<?>, Expression<?>> getUpdates() {
        return this.updates;
    }
}

