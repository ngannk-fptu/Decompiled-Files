/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.confluence.core.persistence.schema.descriptor;

import com.atlassian.confluence.core.persistence.schema.descriptor.DescriptorComparator;
import com.google.common.base.Preconditions;
import java.util.Objects;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class ColumnDescriptor
implements DescriptorComparator<ColumnDescriptor> {
    private final String tableName;
    private final String columnName;
    private final String typeString;
    private final boolean nullable;

    public ColumnDescriptor(String tableName, String columnName, String typeString, boolean nullable) {
        this.tableName = ((String)Preconditions.checkNotNull((Object)tableName)).toLowerCase();
        this.columnName = ((String)Preconditions.checkNotNull((Object)columnName)).toLowerCase();
        this.typeString = ((String)Preconditions.checkNotNull((Object)typeString)).toLowerCase();
        this.nullable = nullable;
    }

    @Override
    public boolean matches(ColumnDescriptor that) {
        return Objects.equals(this.tableName, that.tableName) && Objects.equals(this.columnName, that.columnName) && this.nullable == that.nullable && (Objects.equals(this.typeString, that.typeString) || this.typeString.startsWith(this.typeString) || that.typeString.startsWith(this.typeString));
    }

    public String toString() {
        return String.format("Column %s.%s %s %s", this.tableName, this.columnName, this.typeString, this.nullable ? "nullable" : "non-nullable");
    }
}

