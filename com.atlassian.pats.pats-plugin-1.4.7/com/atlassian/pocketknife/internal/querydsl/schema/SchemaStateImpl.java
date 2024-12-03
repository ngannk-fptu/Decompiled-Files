/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.pocketknife.internal.querydsl.schema;

import com.atlassian.pocketknife.api.querydsl.schema.SchemaState;
import com.google.common.base.MoreObjects;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SchemaStateImpl
implements SchemaState {
    private final RelationalPath relationalPath;
    private final SchemaState.Presence tablePresence;
    private final Map<Path, SchemaState.Presence> columnState;
    private final Set<String> addedColumns;

    SchemaStateImpl(RelationalPath relationalPath, SchemaState.Presence tablePresence, Map<Path, SchemaState.Presence> columnState, Set<String> addedColumns) {
        this.relationalPath = relationalPath;
        this.tablePresence = tablePresence;
        this.columnState = columnState;
        this.addedColumns = addedColumns;
    }

    @Override
    public SchemaState.Presence getColumnState(Path<?> column) {
        return Optional.ofNullable(this.columnState.get(column)).orElse(SchemaState.Presence.MISSING);
    }

    @Override
    public RelationalPath getRelationalPath() {
        return this.relationalPath;
    }

    @Override
    public SchemaState.Presence getTableState() {
        return this.tablePresence;
    }

    @Override
    public Set<String> getAddedColumns() {
        return this.addedColumns;
    }

    @Override
    public Set<Path<?>> getMissingColumns() {
        Stream<Path> pathStream = this.columnState.entrySet().stream().filter(e -> e.getValue() == SchemaState.Presence.MISSING).map(Map.Entry::getKey);
        return pathStream.collect(Collectors.toSet());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("relationalPath", (Object)this.relationalPath).add("tablePresence", (Object)this.tablePresence).add("columnState", this.columnState).add("addedColumns", this.addedColumns).toString();
    }
}

