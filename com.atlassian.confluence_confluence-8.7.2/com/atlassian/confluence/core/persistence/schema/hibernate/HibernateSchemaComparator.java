/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.hibernate.HibernateException
 *  org.hibernate.boot.model.naming.Identifier
 *  org.hibernate.engine.spi.Mapping
 *  org.hibernate.mapping.Column
 *  org.hibernate.mapping.Index
 *  org.hibernate.mapping.Table
 *  org.hibernate.tool.schema.extract.spi.ColumnInformation
 *  org.hibernate.tool.schema.extract.spi.DatabaseInformation
 *  org.hibernate.tool.schema.extract.spi.IndexInformation
 *  org.hibernate.tool.schema.extract.spi.TableInformation
 */
package com.atlassian.confluence.core.persistence.schema.hibernate;

import com.atlassian.confluence.core.persistence.schema.api.SchemaComparison;
import com.atlassian.confluence.core.persistence.schema.api.SchemaElementComparison;
import com.atlassian.confluence.core.persistence.schema.api.SchemaInformationService;
import com.atlassian.confluence.core.persistence.schema.api.TableSchemaComparison;
import com.atlassian.confluence.core.persistence.schema.descriptor.ColumnDescriptor;
import com.atlassian.confluence.core.persistence.schema.descriptor.ComparedColumn;
import com.atlassian.confluence.core.persistence.schema.descriptor.ComparedIndex;
import com.atlassian.confluence.core.persistence.schema.descriptor.ComparedSchema;
import com.atlassian.confluence.core.persistence.schema.descriptor.ComparedTable;
import com.atlassian.confluence.core.persistence.schema.descriptor.IndexDescriptor;
import com.atlassian.confluence.core.persistence.schema.hibernate.HibernateDescriptorFactory;
import com.atlassian.confluence.core.persistence.schema.util.SchemaComparisonWarningFactory;
import com.atlassian.confluence.impl.core.persistence.hibernate.HibernateMetadataSource;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import org.hibernate.HibernateException;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;
import org.hibernate.tool.schema.extract.spi.DatabaseInformation;
import org.hibernate.tool.schema.extract.spi.IndexInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;

class HibernateSchemaComparator {
    private final HibernateMetadataSource hibernateMetadataSource;
    private final HibernateDescriptorFactory descriptorFactory;
    private final SchemaInformationService dbSchemaInformationService;

    public HibernateSchemaComparator(Mapping hibernateMapping, HibernateMetadataSource hibernateMetadataSource, SchemaInformationService dbSchemaInformationService) {
        this.hibernateMetadataSource = hibernateMetadataSource;
        this.dbSchemaInformationService = dbSchemaInformationService;
        this.descriptorFactory = new HibernateDescriptorFactory(hibernateMapping, dbSchemaInformationService.getDialect());
    }

    public SchemaComparison compareSchema() throws HibernateException {
        Iterable<TableSchemaComparison> comparisons = this.compareTables();
        return new ComparedSchema(comparisons, SchemaComparisonWarningFactory.buildWarnings(comparisons));
    }

    private Iterable<TableSchemaComparison> compareTables() throws HibernateException {
        SchemaInformationService.CloseableDatabaseInformation databaseInformation = this.dbSchemaInformationService.getDatabaseInformation();
        try {
            ArrayList mappedTables = Lists.newArrayList(this.hibernateMetadataSource.getTableMappings());
            ArrayList arrayList = Lists.newArrayList((Iterable)Iterables.filter((Iterable)Iterables.transform((Iterable)mappedTables, table -> this.compareTable((Table)table, databaseInformation)), Objects::nonNull));
            if (databaseInformation != null) {
                databaseInformation.close();
            }
            return arrayList;
        }
        catch (Throwable throwable) {
            try {
                if (databaseInformation != null) {
                    try {
                        databaseInformation.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            catch (SQLException ex) {
                throw new HibernateException((Throwable)ex);
            }
        }
    }

    private TableSchemaComparison compareTable(Table table, DatabaseInformation databaseInformation) throws HibernateException {
        TableInformation tableInformation = databaseInformation.getTableInformation(table.getQualifiedTableName());
        return new ComparedTable(table.getName(), this.compareIndexes(table, tableInformation), this.compareColumns(table, tableInformation));
    }

    private Iterable<SchemaElementComparison.ColumnComparison> compareColumns(Table table, TableInformation tableInformation) throws HibernateException {
        ImmutableList.Builder columnComparisons = ImmutableList.builder();
        Iterator mappedColumnIterator = table.getColumnIterator();
        while (mappedColumnIterator.hasNext()) {
            ColumnInformation columnInformation;
            Column mappedColumn = (Column)mappedColumnIterator.next();
            ColumnDescriptor mappedDescriptor = this.descriptorFactory.describe(table, mappedColumn);
            String columnName = mappedColumn.getName();
            Option actualDescriptor = tableInformation == null ? Option.none() : ((columnInformation = tableInformation.getColumn(new Identifier(columnName, false))) == null ? Option.none() : Option.some((Object)this.descriptorFactory.describe(tableInformation, columnInformation)));
            ComparedColumn comparedColumn = new ComparedColumn(columnName, (Maybe<ColumnDescriptor>)Option.option((Object)mappedDescriptor), (Maybe<ColumnDescriptor>)actualDescriptor);
            columnComparisons.add((Object)comparedColumn);
        }
        return columnComparisons.build();
    }

    private Iterable<SchemaElementComparison.IndexComparison> compareIndexes(Table table, TableInformation tableInformation) throws HibernateException {
        ImmutableList.Builder indexComparisons = ImmutableList.builder();
        Iterator mappedIndexIterator = table.getIndexIterator();
        while (mappedIndexIterator.hasNext()) {
            IndexInformation indexTableInformation;
            Index mappedIndex = (Index)mappedIndexIterator.next();
            IndexDescriptor mappedDescriptor = this.descriptorFactory.describe(table, mappedIndex);
            String indexName = mappedIndex.getName();
            Option actualDescriptor = tableInformation == null ? Option.none() : ((indexTableInformation = tableInformation.getIndex(new Identifier(indexName, false))) == null ? Option.none() : Option.some((Object)this.descriptorFactory.describe(tableInformation, indexTableInformation)));
            ComparedIndex comparedIndex = new ComparedIndex(indexName, (Maybe<IndexDescriptor>)Option.option((Object)mappedDescriptor), (Maybe<IndexDescriptor>)actualDescriptor);
            indexComparisons.add((Object)comparedIndex);
        }
        return indexComparisons.build();
    }
}

