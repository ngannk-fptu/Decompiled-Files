/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.confluence.core.persistence.schema.api;

import com.atlassian.confluence.core.persistence.schema.api.SchemaElementComparison;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public interface TableSchemaComparison {
    public String getTableName();

    public Iterable<? extends SchemaElementComparison.ColumnComparison> getColumns();

    public Iterable<? extends SchemaElementComparison.IndexComparison> getIndexes();
}

