/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter;

import com.atlassian.dbexporter.Column;
import com.atlassian.dbexporter.ForeignKey;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class Table {
    private final String name;
    private final List<Column> columns;
    private final Collection<ForeignKey> foreignKeys;

    public Table(String name, List<Column> columns, Collection<ForeignKey> foreignKeys) {
        this.name = Objects.requireNonNull(name);
        this.columns = new LinkedList<Column>((Collection)Objects.requireNonNull(columns));
        this.foreignKeys = new LinkedList<ForeignKey>(Objects.requireNonNull(foreignKeys));
    }

    public String getName() {
        return this.name;
    }

    public List<Column> getColumns() {
        return Collections.unmodifiableList(this.columns);
    }

    public Collection<ForeignKey> getForeignKeys() {
        return Collections.unmodifiableCollection(this.foreignKeys);
    }
}

