/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 */
package com.atlassian.activeobjects.backup;

import com.atlassian.dbexporter.Column;
import com.atlassian.dbexporter.DatabaseInformation;
import com.atlassian.dbexporter.EntityNameProcessor;
import com.atlassian.dbexporter.ForeignKey;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.Table;
import com.atlassian.dbexporter.exporter.TableReader;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.java.ao.DatabaseProvider;
import net.java.ao.SchemaConfiguration;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.ddl.DDLField;
import net.java.ao.schema.ddl.DDLForeignKey;
import net.java.ao.schema.ddl.DDLTable;
import net.java.ao.schema.ddl.SchemaReader;
import net.java.ao.sql.SqlUtils;

public final class ActiveObjectsTableReader
implements TableReader {
    private final ImportExportErrorService errorService;
    private final DatabaseProvider provider;
    private final NameConverters converters;
    private final SchemaConfiguration schemaConfiguration;

    public ActiveObjectsTableReader(ImportExportErrorService errorService, NameConverters converters, DatabaseProvider provider, SchemaConfiguration schemaConfiguration) {
        this.converters = (NameConverters)Preconditions.checkNotNull((Object)converters);
        this.errorService = (ImportExportErrorService)Preconditions.checkNotNull((Object)errorService);
        this.provider = (DatabaseProvider)Preconditions.checkNotNull((Object)provider);
        this.schemaConfiguration = (SchemaConfiguration)Preconditions.checkNotNull((Object)schemaConfiguration);
    }

    @Override
    public Iterable<Table> read(DatabaseInformation databaseInformation, EntityNameProcessor entityNameProcessor) {
        DDLTable[] ddlTables;
        ArrayList tables = Lists.newArrayList();
        Connection connection = null;
        try {
            connection = this.getConnection();
            ddlTables = SchemaReader.readSchema(connection, this.provider, this.converters, this.schemaConfiguration, true);
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(null, "An error occurred reading schema information from database", e);
        }
        finally {
            SqlUtils.closeQuietly(connection);
        }
        for (DDLTable ddlTable : ddlTables) {
            tables.add(this.readTable(ddlTable, entityNameProcessor));
        }
        return tables;
    }

    private Connection getConnection() {
        try {
            return this.provider.getConnection();
        }
        catch (SQLException e) {
            throw this.errorService.newImportExportSqlException(null, "Could not get connection from provider", e);
        }
    }

    private Table readTable(DDLTable ddlTable, EntityNameProcessor processor) {
        String name = processor.tableName(ddlTable.getName());
        return new Table(name, this.readColumns(ddlTable.getFields(), processor), this.readForeignKeys(ddlTable.getForeignKeys()));
    }

    private List<Column> readColumns(DDLField[] fields, final EntityNameProcessor processor) {
        return Lists.transform((List)Lists.newArrayList((Object[])fields), (Function)new Function<DDLField, Column>(){

            public Column apply(DDLField field) {
                return ActiveObjectsTableReader.this.readColumn(field, processor);
            }
        });
    }

    private Column readColumn(DDLField field, EntityNameProcessor processor) {
        String name = processor.columnName(field.getName());
        return new Column(name, this.getType(field), field.isPrimaryKey(), field.isAutoIncrement(), this.getPrecision(field), this.getScale(field));
    }

    private int getType(DDLField field) {
        return field.getJdbcType();
    }

    private Integer getScale(DDLField field) {
        return field.getType().getQualifiers().getScale();
    }

    private Integer getPrecision(DDLField field) {
        Integer precision = field.getType().getQualifiers().getPrecision();
        return precision != null ? precision : field.getType().getQualifiers().getStringLength();
    }

    private Collection<ForeignKey> readForeignKeys(DDLForeignKey[] foreignKeys) {
        return Collections2.transform((Collection)Lists.newArrayList((Object[])foreignKeys), (Function)new Function<DDLForeignKey, ForeignKey>(){

            public ForeignKey apply(DDLForeignKey fk) {
                return ActiveObjectsTableReader.this.readForeignKey(fk);
            }
        });
    }

    private ForeignKey readForeignKey(DDLForeignKey fk) {
        return new ForeignKey(fk.getDomesticTable(), fk.getField(), fk.getTable(), fk.getForeignField());
    }
}

