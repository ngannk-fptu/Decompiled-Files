/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.confluence.core.persistence.schema.descriptor;

import com.atlassian.confluence.core.persistence.schema.api.SchemaElementComparison;
import com.atlassian.confluence.core.persistence.schema.api.TableSchemaComparison;
import com.google.common.base.Preconditions;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class ComparedTable
implements TableSchemaComparison {
    private final String tableName;
    private final Iterable<? extends SchemaElementComparison.IndexComparison> indexComparisons;
    private final Iterable<? extends SchemaElementComparison.ColumnComparison> columnComparisons;

    public ComparedTable(String tableName, Iterable<? extends SchemaElementComparison.IndexComparison> indexComparisons, Iterable<? extends SchemaElementComparison.ColumnComparison> columnComparisons) {
        this.tableName = ((String)Preconditions.checkNotNull((Object)tableName)).toLowerCase();
        this.indexComparisons = (Iterable)Preconditions.checkNotNull(indexComparisons);
        this.columnComparisons = (Iterable)Preconditions.checkNotNull(columnComparisons);
    }

    @Override
    public String getTableName() {
        return this.tableName;
    }

    @Override
    public Iterable<? extends SchemaElementComparison.IndexComparison> getIndexes() {
        return this.indexComparisons;
    }

    @Override
    public Iterable<? extends SchemaElementComparison.ColumnComparison> getColumns() {
        return this.columnComparisons;
    }
}

