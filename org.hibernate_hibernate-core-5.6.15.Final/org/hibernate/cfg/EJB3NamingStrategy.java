/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg;

import java.io.Serializable;
import org.hibernate.AssertionFailure;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.internal.util.StringHelper;

public class EJB3NamingStrategy
implements NamingStrategy,
Serializable {
    public static final NamingStrategy INSTANCE = new EJB3NamingStrategy();

    @Override
    public String classToTableName(String className) {
        return StringHelper.unqualify(className);
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        return StringHelper.unqualify(propertyName);
    }

    @Override
    public String tableName(String tableName) {
        return tableName;
    }

    @Override
    public String columnName(String columnName) {
        return columnName;
    }

    @Override
    public String collectionTableName(String ownerEntity, String ownerEntityTable, String associatedEntity, String associatedEntityTable, String propertyName) {
        return this.tableName(ownerEntityTable + "_" + (associatedEntityTable != null ? associatedEntityTable : StringHelper.unqualify(propertyName)));
    }

    @Override
    public String joinKeyColumnName(String joinedColumn, String joinedTable) {
        return this.columnName(joinedColumn);
    }

    @Override
    public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {
        String header;
        String string = header = propertyName != null ? StringHelper.unqualify(propertyName) : propertyTableName;
        if (header == null) {
            throw new AssertionFailure("NamingStrategy not properly filled");
        }
        return this.columnName(header + "_" + referencedColumnName);
    }

    @Override
    public String logicalColumnName(String columnName, String propertyName) {
        return StringHelper.isNotEmpty(columnName) ? columnName : StringHelper.unqualify(propertyName);
    }

    @Override
    public String logicalCollectionTableName(String tableName, String ownerEntityTable, String associatedEntityTable, String propertyName) {
        if (tableName != null) {
            return tableName;
        }
        return ownerEntityTable + "_" + (associatedEntityTable != null ? associatedEntityTable : StringHelper.unqualify(propertyName));
    }

    @Override
    public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
        return StringHelper.isNotEmpty(columnName) ? columnName : StringHelper.unqualify(propertyName) + "_" + referencedColumn;
    }
}

