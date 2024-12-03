/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.unique;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;

public interface UniqueDelegate {
    @Deprecated
    default public String getColumnDefinitionUniquenessFragment(Column column) {
        throw new IllegalStateException("getColumnDefinitionUniquenessFragment(...) was not implemented!");
    }

    default public String getColumnDefinitionUniquenessFragment(Column column, SqlStringGenerationContext context) {
        return this.getColumnDefinitionUniquenessFragment(column);
    }

    @Deprecated
    default public String getTableCreationUniqueConstraintsFragment(Table table) {
        throw new IllegalStateException("getTableCreationUniqueConstraintsFragment(...) was not implemented!");
    }

    default public String getTableCreationUniqueConstraintsFragment(Table table, SqlStringGenerationContext context) {
        return this.getTableCreationUniqueConstraintsFragment(table);
    }

    @Deprecated
    default public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata) {
        throw new IllegalStateException("getAlterTableToAddUniqueKeyCommand(...) was not implemented!");
    }

    default public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata, SqlStringGenerationContext context) {
        return this.getAlterTableToAddUniqueKeyCommand(uniqueKey, metadata);
    }

    @Deprecated
    default public String getAlterTableToDropUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata) {
        throw new IllegalStateException("getAlterTableToDropUniqueKeyCommand(...) was not implemented!");
    }

    default public String getAlterTableToDropUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata, SqlStringGenerationContext context) {
        return this.getAlterTableToDropUniqueKeyCommand(uniqueKey, metadata);
    }
}

