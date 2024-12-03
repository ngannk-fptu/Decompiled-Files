/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dbexporter.Column
 *  com.atlassian.dbexporter.DatabaseInformation
 *  com.atlassian.dbexporter.EntityNameProcessor
 *  com.atlassian.dbexporter.Table
 *  com.atlassian.dbexporter.exporter.TableReader
 *  com.atlassian.fugue.Option
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.upgrade.recovery;

import com.atlassian.confluence.upgrade.recovery.DbDumpException;
import com.atlassian.dbexporter.Column;
import com.atlassian.dbexporter.DatabaseInformation;
import com.atlassian.dbexporter.EntityNameProcessor;
import com.atlassian.dbexporter.Table;
import com.atlassian.dbexporter.exporter.TableReader;
import com.atlassian.fugue.Option;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

public class ConfluenceTableReader
implements TableReader {
    private static final Set<String> EXCLUDED_TABLE_NAMES = ImmutableSet.of((Object)"attachmentdata", (Object)"plugindata");
    public static final Predicate<String> INCLUDED_TABLENAME = Predicates.and((Predicate)Predicates.notNull(), tableName -> !EXCLUDED_TABLE_NAMES.contains(tableName.toLowerCase(Locale.ENGLISH)));
    private final Connection connection;

    public ConfluenceTableReader(Connection connection) {
        this.connection = connection;
    }

    public Iterable<Table> read(DatabaseInformation databaseInformation, EntityNameProcessor entityNameProcessor) {
        try {
            DatabaseMetaData metaData = this.connection.getMetaData();
            CatalogAndSchema catalogAndSchema = this.parseCatalogAndSchema(metaData);
            Iterable<String> tableNames = this.parseTableNames(metaData, catalogAndSchema);
            return this.parseTables(metaData, catalogAndSchema, tableNames);
        }
        catch (SQLException e) {
            throw new DbDumpException("Failed to read database information: " + e.getMessage(), e);
        }
    }

    private CatalogAndSchema parseCatalogAndSchema(DatabaseMetaData metaData) throws SQLException {
        String confVersionTableNamePattern = this.toIdentifier(metaData, "CONFVERSION");
        try (ResultSet rs = metaData.getTables(null, null, confVersionTableNamePattern, new String[]{"TABLE"});){
            if (!rs.next()) {
                throw new DbDumpException("Could not find CONFVERSION table in the database");
            }
            CatalogAndSchema catalogAndSchema = new CatalogAndSchema();
            catalogAndSchema.catalog = rs.getString("TABLE_CAT");
            catalogAndSchema.schema = rs.getString("TABLE_SCHEM");
            CatalogAndSchema catalogAndSchema2 = catalogAndSchema;
            return catalogAndSchema2;
        }
    }

    private String toIdentifier(DatabaseMetaData metaData, String mixedCaseIdentifier) throws SQLException {
        if (metaData.storesLowerCaseIdentifiers()) {
            return mixedCaseIdentifier.toLowerCase();
        }
        if (metaData.storesUpperCaseIdentifiers()) {
            return mixedCaseIdentifier.toUpperCase();
        }
        return mixedCaseIdentifier;
    }

    private Iterable<String> parseTableNames(DatabaseMetaData metaData, CatalogAndSchema catalogAndSchema) throws SQLException {
        ArrayList<String> rs = new ArrayList<String>();
        try (ResultSet tableRs = metaData.getTables(catalogAndSchema.catalog, catalogAndSchema.schema, "%", new String[]{"TABLE"});){
            while (tableRs.next()) {
                rs.add(tableRs.getString("TABLE_NAME"));
            }
        }
        return Iterables.filter(rs, INCLUDED_TABLENAME);
    }

    private Iterable<Table> parseTables(DatabaseMetaData metaData, CatalogAndSchema catalogAndSchema, Iterable<String> tableNames) throws SQLException {
        ArrayList<Table> tables = new ArrayList<Table>();
        for (String tableName : tableNames) {
            ResultSet rs = metaData.getColumns(catalogAndSchema.catalog, catalogAndSchema.schema, tableName, "%");
            try {
                ArrayList<Column> columns = new ArrayList<Column>();
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    int columnDataType = rs.getInt("DATA_TYPE");
                    int columnSize = rs.getInt("COLUMN_SIZE");
                    int columnScale = rs.getInt("DECIMAL_DIGITS");
                    Boolean autoIncrement = (Boolean)this.isAutoIncrement(rs).getOrNull();
                    columns.add(new Column(columnName, columnDataType, null, autoIncrement, Integer.valueOf(columnSize), Integer.valueOf(columnScale)));
                }
                tables.add(new Table(tableName, columns, Collections.emptyList()));
            }
            finally {
                if (rs == null) continue;
                rs.close();
            }
        }
        return tables;
    }

    private Option<Boolean> isAutoIncrement(ResultSet rs2) throws SQLException {
        String isAutoIncrement;
        try {
            isAutoIncrement = rs2.getString("IS_AUTOINCREMENT");
        }
        catch (SQLException e) {
            isAutoIncrement = "";
        }
        return Option.option((Object)("YES".equals(isAutoIncrement) ? Boolean.TRUE : ("NO".equals(isAutoIncrement) ? Boolean.FALSE : null)));
    }

    private static class CatalogAndSchema {
        String catalog;
        String schema;

        private CatalogAndSchema() {
        }
    }
}

