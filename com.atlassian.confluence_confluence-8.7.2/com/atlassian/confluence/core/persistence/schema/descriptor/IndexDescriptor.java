/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.confluence.core.persistence.schema.descriptor;

import com.atlassian.confluence.core.persistence.schema.descriptor.DescriptorComparator;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class IndexDescriptor
implements DescriptorComparator<IndexDescriptor> {
    private final String tableName;
    private final String indexName;
    private final boolean nonUnique;
    private final List<String> columnNames;

    public IndexDescriptor(String tableName, String indexName, boolean nonUnique, Iterable<String> columnNames) {
        this.tableName = ((String)Preconditions.checkNotNull((Object)tableName)).toLowerCase();
        this.indexName = ((String)Preconditions.checkNotNull((Object)indexName)).toLowerCase();
        this.nonUnique = nonUnique;
        this.columnNames = Lists.newArrayList((Iterable)Iterables.transform((Iterable)((Iterable)Preconditions.checkNotNull(columnNames)), input -> ((String)Preconditions.checkNotNull((Object)input)).toLowerCase()));
    }

    @Override
    public boolean matches(IndexDescriptor that) {
        return Objects.equals(this.tableName, that.tableName) && Objects.equals(this.indexName, that.indexName) && this.nonUnique == that.nonUnique && Objects.equals(this.columnNames, that.columnNames);
    }

    public String toString() {
        return String.format("Index %s.%s on %s %s", this.tableName, this.indexName, this.columnNames, this.nonUnique ? "non-unique" : "unique");
    }
}

