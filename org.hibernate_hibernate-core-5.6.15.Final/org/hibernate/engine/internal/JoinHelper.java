/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.internal;

import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.type.AssociationType;

public final class JoinHelper {
    public static String[] getAliasedLHSColumnNames(AssociationType type, String alias, int property, OuterJoinLoadable lhsPersister, Mapping mapping) {
        return JoinHelper.getAliasedLHSColumnNames(type, alias, property, 0, lhsPersister, mapping);
    }

    public static String[] getLHSColumnNames(AssociationType type, int property, OuterJoinLoadable lhsPersister, Mapping mapping) {
        return JoinHelper.getLHSColumnNames(type, property, 0, lhsPersister, mapping);
    }

    public static String[] getAliasedLHSColumnNames(AssociationType associationType, String columnQualifier, int propertyIndex, int begin, OuterJoinLoadable lhsPersister, Mapping mapping) {
        if (associationType.useLHSPrimaryKey()) {
            return StringHelper.qualify(columnQualifier, lhsPersister.getIdentifierColumnNames());
        }
        String propertyName = associationType.getLHSPropertyName();
        if (propertyName == null) {
            return ArrayHelper.slice(JoinHelper.toColumns(lhsPersister, columnQualifier, propertyIndex), begin, associationType.getColumnSpan(mapping));
        }
        return ((PropertyMapping)((Object)lhsPersister)).toColumns(columnQualifier, propertyName);
    }

    private static String[] toColumns(OuterJoinLoadable persister, String columnQualifier, int propertyIndex) {
        if (propertyIndex >= 0) {
            return persister.toColumns(columnQualifier, propertyIndex);
        }
        String[] cols = persister.getIdentifierColumnNames();
        String[] result = new String[cols.length];
        for (int j = 0; j < cols.length; ++j) {
            result[j] = StringHelper.qualify(columnQualifier, cols[j]);
        }
        return result;
    }

    public static String[] getLHSColumnNames(AssociationType type, int property, int begin, OuterJoinLoadable lhsPersister, Mapping mapping) {
        if (type.useLHSPrimaryKey()) {
            return lhsPersister.getIdentifierColumnNames();
        }
        String propertyName = type.getLHSPropertyName();
        if (propertyName == null) {
            return ArrayHelper.slice(property < 0 ? lhsPersister.getIdentifierColumnNames() : lhsPersister.getSubclassPropertyColumnNames(property), begin, type.getColumnSpan(mapping));
        }
        return lhsPersister.getPropertyColumnNames(propertyName);
    }

    public static String getLHSTableName(AssociationType type, int propertyIndex, OuterJoinLoadable lhsPersister) {
        if (type.useLHSPrimaryKey() || propertyIndex < 0) {
            return lhsPersister.getTableName();
        }
        String propertyName = type.getLHSPropertyName();
        if (propertyName == null) {
            return lhsPersister.getSubclassPropertyTableName(propertyIndex);
        }
        String propertyRefTable = lhsPersister.getPropertyTableName(propertyName);
        if (propertyRefTable == null) {
            propertyRefTable = lhsPersister.getSubclassPropertyTableName(propertyIndex);
        }
        return propertyRefTable;
    }

    public static String[] getRHSColumnNames(AssociationType type, SessionFactoryImplementor factory) {
        String uniqueKeyPropertyName = type.getRHSUniqueKeyPropertyName();
        Joinable joinable = type.getAssociatedJoinable(factory);
        if (uniqueKeyPropertyName == null) {
            return joinable.getKeyColumnNames();
        }
        return ((OuterJoinLoadable)joinable).getPropertyColumnNames(uniqueKeyPropertyName);
    }

    private JoinHelper() {
    }
}

