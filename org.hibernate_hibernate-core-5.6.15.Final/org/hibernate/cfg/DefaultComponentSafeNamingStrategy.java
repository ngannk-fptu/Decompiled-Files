/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg;

import java.util.Locale;
import org.hibernate.AssertionFailure;
import org.hibernate.cfg.EJB3NamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.internal.util.StringHelper;

public class DefaultComponentSafeNamingStrategy
extends EJB3NamingStrategy {
    public static final NamingStrategy INSTANCE = new DefaultComponentSafeNamingStrategy();

    protected static String addUnderscores(String name) {
        return name.replace('.', '_').toLowerCase(Locale.ROOT);
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        return DefaultComponentSafeNamingStrategy.addUnderscores(propertyName);
    }

    @Override
    public String collectionTableName(String ownerEntity, String ownerEntityTable, String associatedEntity, String associatedEntityTable, String propertyName) {
        return this.tableName(ownerEntityTable + "_" + (associatedEntityTable != null ? associatedEntityTable : DefaultComponentSafeNamingStrategy.addUnderscores(propertyName)));
    }

    @Override
    public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {
        String header;
        String string = header = propertyName != null ? DefaultComponentSafeNamingStrategy.addUnderscores(propertyName) : propertyTableName;
        if (header == null) {
            throw new AssertionFailure("NamingStrategy not properly filled");
        }
        return this.columnName(header + "_" + referencedColumnName);
    }

    @Override
    public String logicalColumnName(String columnName, String propertyName) {
        return StringHelper.isNotEmpty(columnName) ? columnName : propertyName;
    }

    @Override
    public String logicalCollectionTableName(String tableName, String ownerEntityTable, String associatedEntityTable, String propertyName) {
        if (tableName != null) {
            return tableName;
        }
        return ownerEntityTable + "_" + (associatedEntityTable != null ? associatedEntityTable : propertyName);
    }

    @Override
    public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
        return StringHelper.isNotEmpty(columnName) ? columnName : propertyName + "_" + referencedColumn;
    }
}

