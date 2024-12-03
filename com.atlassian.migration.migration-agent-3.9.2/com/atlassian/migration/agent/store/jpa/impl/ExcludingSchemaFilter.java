/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.hibernate.boot.model.relational.Namespace
 *  org.hibernate.boot.model.relational.Sequence
 *  org.hibernate.mapping.Table
 *  org.hibernate.tool.schema.spi.SchemaFilter
 */
package com.atlassian.migration.agent.store.jpa.impl;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.spi.SchemaFilter;

public class ExcludingSchemaFilter
implements SchemaFilter {
    private final Set<String> excludedTables;

    ExcludingSchemaFilter(Iterable<String> excludedTables) {
        this.excludedTables = ImmutableSet.copyOf(excludedTables);
    }

    public boolean includeNamespace(Namespace namespace) {
        return true;
    }

    public boolean includeTable(Table table) {
        return !this.excludedTables.contains(table.getName());
    }

    public boolean includeSequence(Sequence sequence) {
        return true;
    }
}

