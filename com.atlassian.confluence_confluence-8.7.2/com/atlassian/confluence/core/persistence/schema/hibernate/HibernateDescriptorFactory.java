/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.hibernate.HibernateException
 *  org.hibernate.boot.model.naming.Identifier
 *  org.hibernate.dialect.Dialect
 *  org.hibernate.engine.spi.Mapping
 *  org.hibernate.mapping.Column
 *  org.hibernate.mapping.Index
 *  org.hibernate.mapping.Table
 *  org.hibernate.tool.schema.extract.spi.ColumnInformation
 *  org.hibernate.tool.schema.extract.spi.IndexInformation
 *  org.hibernate.tool.schema.extract.spi.TableInformation
 */
package com.atlassian.confluence.core.persistence.schema.hibernate;

import com.atlassian.confluence.core.persistence.schema.descriptor.ColumnDescriptor;
import com.atlassian.confluence.core.persistence.schema.descriptor.IndexDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.HibernateException;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;
import org.hibernate.tool.schema.extract.spi.IndexInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;

class HibernateDescriptorFactory {
    private final Mapping hibernateMapping;
    private final Dialect dialect;

    HibernateDescriptorFactory(Mapping hibernateMapping, Dialect dialect) {
        this.hibernateMapping = (Mapping)Preconditions.checkNotNull((Object)hibernateMapping);
        this.dialect = (Dialect)Preconditions.checkNotNull((Object)dialect);
    }

    ColumnDescriptor describe(Table table, Column column) throws HibernateException {
        return new ColumnDescriptor(table.getName(), column.getName(), column.getSqlType(this.dialect, this.hibernateMapping), column.isNullable());
    }

    IndexDescriptor describe(Table table, Index index) throws HibernateException {
        ArrayList columns = Lists.newArrayList((Iterator)index.getColumnIterator());
        return new IndexDescriptor(table.getName(), index.getName(), true, Iterables.transform((Iterable)columns, Column::getName));
    }

    IndexDescriptor describe(TableInformation table, IndexInformation index) throws HibernateException {
        List columns = index.getIndexedColumns().stream().map(ColumnInformation::getColumnIdentifier).collect(Collectors.toList());
        return new IndexDescriptor(table.getName().getTableName().getCanonicalName(), index.getIndexIdentifier().getCanonicalName(), true, Iterables.transform(columns, Identifier::getCanonicalName));
    }

    ColumnDescriptor describe(TableInformation table, ColumnInformation column) throws HibernateException {
        return new ColumnDescriptor(table.getName().getTableName().getCanonicalName(), column.getColumnIdentifier().getCanonicalName(), String.format("%s(%s)", column.getTypeName(), column.getColumnSize()), column.getNullable().toBoolean(true));
    }
}

