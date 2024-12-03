/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.google.common.base.Preconditions
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.confluence.core.persistence.schema.descriptor;

import com.atlassian.confluence.core.persistence.schema.api.SchemaElementComparison;
import com.atlassian.confluence.core.persistence.schema.descriptor.AbstractDescriptorComparison;
import com.atlassian.confluence.core.persistence.schema.descriptor.ColumnDescriptor;
import com.atlassian.fugue.Maybe;
import com.google.common.base.Preconditions;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class ComparedColumn
extends AbstractDescriptorComparison<ColumnDescriptor>
implements SchemaElementComparison.ColumnComparison {
    private String columnName;

    public ComparedColumn(String columnName, Maybe<ColumnDescriptor> expected, Maybe<ColumnDescriptor> actual) {
        super(expected, actual);
        this.columnName = ((String)Preconditions.checkNotNull((Object)columnName)).toLowerCase();
    }

    @Override
    public String getColumnName() {
        return this.columnName;
    }
}

