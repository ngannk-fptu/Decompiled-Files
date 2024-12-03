/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.pocketknife.api.querydsl.schema;

import com.atlassian.annotations.PublicApi;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import java.util.Set;

@PublicApi
public interface SchemaState {
    public RelationalPath getRelationalPath();

    public Presence getTableState();

    public Presence getColumnState(Path<?> var1);

    public Set<Path<?>> getMissingColumns();

    public Set<String> getAddedColumns();

    public static enum Presence {
        SAME,
        DIFFERENT,
        MISSING;

    }
}

