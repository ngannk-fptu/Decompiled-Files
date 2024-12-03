/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg;

import java.io.Serializable;
import java.util.Locale;
import org.hibernate.AssertionFailure;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.internal.util.StringHelper;

public class ImprovedNamingStrategy
implements NamingStrategy,
Serializable {
    public static final NamingStrategy INSTANCE = new ImprovedNamingStrategy();

    @Override
    public String classToTableName(String className) {
        return ImprovedNamingStrategy.addUnderscores(StringHelper.unqualify(className));
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        return ImprovedNamingStrategy.addUnderscores(StringHelper.unqualify(propertyName));
    }

    @Override
    public String tableName(String tableName) {
        return ImprovedNamingStrategy.addUnderscores(tableName);
    }

    @Override
    public String columnName(String columnName) {
        return ImprovedNamingStrategy.addUnderscores(columnName);
    }

    protected static String addUnderscores(String name) {
        StringBuilder buf = new StringBuilder(name.replace('.', '_'));
        for (int i = 1; i < buf.length() - 1; ++i) {
            if (!Character.isLowerCase(buf.charAt(i - 1)) || !Character.isUpperCase(buf.charAt(i)) || !Character.isLowerCase(buf.charAt(i + 1))) continue;
            buf.insert(i++, '_');
        }
        return buf.toString().toLowerCase(Locale.ROOT);
    }

    @Override
    public String collectionTableName(String ownerEntity, String ownerEntityTable, String associatedEntity, String associatedEntityTable, String propertyName) {
        return this.tableName(ownerEntityTable + '_' + this.propertyToColumnName(propertyName));
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
        return this.columnName(header);
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

